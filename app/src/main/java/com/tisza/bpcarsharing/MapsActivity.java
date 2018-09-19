package com.tisza.bpcarsharing;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{
	private static final int DOWNLOAD_INTERVAL = 10;
	private static final LatLng BP_CENTER = new LatLng(47.495225, 19.045508);
	private static final LatLngBounds BP_BOUNDS = new LatLngBounds(new LatLng(47.463008, 18.983644), new LatLng(47.550324, 19.157741));
	private static final int BP_ZOOM = 12, MY_LOCATION_ZOOM = 15;

	private FusedLocationProviderClient fusedLocationClient;

	private VehicleDownloader vehicleDownloader;
	private DrawerLayout drawerLayout;
	private GoogleMap map;
	private VehicleMarkerManager vehicleMarkerManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		drawerLayout = findViewById(R.id.drawer_layout);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

		MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
		fusedLocationClient.getLastLocation()
				.addOnSuccessListener(this, location ->
				{
					if (location != null)
					{
						LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
						if (BP_BOUNDS.contains(latLng))
							map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MY_LOCATION_ZOOM));
					}
				});

		setSupportActionBar(findViewById(R.id.toolbar));
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

		vehicleMarkerManager = new VehicleMarkerManager();
		vehicleDownloader = new VehicleDownloader(getMainLooper(), DOWNLOAD_INTERVAL, vehicleMarkerManager::setVehicles);

		NavigationView navigationView = findViewById(R.id.nav_view);
		for (VehicleCategory vehicleCategory : VehicleCategory.values())
		{
			Switch vehicleCategorySwitch = ((Switch)navigationView.getMenu().findItem(vehicleCategory.getSwitchID()).getActionView());
			vehicleCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> vehicleDownloader.setVehicleCategoryActive(vehicleCategory, isChecked));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				drawerLayout.openDrawer(Gravity.START);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		tryEnableMapLocation();
	}

	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		map = googleMap;
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(BP_CENTER, BP_ZOOM));
		map.setOnInfoWindowClickListener(this);
		tryEnableMapLocation();

		vehicleMarkerManager.setMap(googleMap);
	}

	private void tryEnableMapLocation()
	{
		if (map != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
			map.setMyLocationEnabled(true);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		vehicleDownloader.start();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		vehicleDownloader.stop();
	}

	@Override
	public void onInfoWindowClick(Marker marker)
	{
		Vehicle vehicle = (Vehicle)marker.getTag();

		Intent launchIntent = getPackageManager().getLaunchIntentForPackage(vehicle.getCarsharingService().getAppPackage());
		if (launchIntent == null)
		{
			Toast.makeText(this, "App not installed", Toast.LENGTH_LONG).show();
		}
		else
		{
			startActivity(launchIntent);
		}
	}
}
