package com.tisza.bpcarsharing;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
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
import com.tisza.bpcarsharing.carsharingservice.*;

import java.util.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{
	private static final String SP_NAME_SWITCH = "switch";
	private static final String SP_KEY_CAR = "car";
	private static final String SP_KEY_ZONE = "zone";

	private static final int DOWNLOAD_INTERVAL = 10;
	private static final LatLng BP_CENTER = new LatLng(47.495225, 19.045508);
	private static final LatLngBounds BP_BOUNDS = new LatLngBounds(new LatLng(47.463008, 18.983644), new LatLng(47.550324, 19.157741));
	private static final int BP_ZOOM = 12, MY_LOCATION_ZOOM = 15;

	private FusedLocationProviderClient fusedLocationClient;
	private DrawerLayout drawerLayout;
	private NavigationView navigationView;
	private GoogleMap map;

	private Collection<VehicleMarkerManager> vehicleMarkerManagers = new ArrayList<>();
	private Collection<VehicleDownloader> activeVehicleDownloaders = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		drawerLayout = findViewById(R.id.drawer_layout);
		navigationView = findViewById(R.id.nav_view);

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

		SharedPreferences sharedPreferences = getSharedPreferences(SP_NAME_SWITCH, Context.MODE_PRIVATE);
		for (CarsharingService carsharingService : CarsharingService.CARSHARING_SERVICES)
		{
			VehicleMarkerManager vehicleMarkerManager = new VehicleMarkerManager();
			vehicleMarkerManagers.add(vehicleMarkerManager);
			VehicleDownloader vehicleDownloader = new VehicleDownloader(getMainLooper(), carsharingService, DOWNLOAD_INTERVAL, vehicleMarkerManager::setVehicles);

			Switch carSwitch = navigationView.getMenu().findItem(carsharingService.getMenuID()).getActionView().findViewById(R.id.car_switch);
			carSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
			{
				if (isChecked)
					vehicleDownloader.start();
				else
					vehicleDownloader.stop();
			});
			carSwitch.setChecked(sharedPreferences.getBoolean(SP_KEY_CAR + carsharingService.getID(), true));

			Switch zoneSwitch = navigationView.getMenu().findItem(carsharingService.getMenuID()).getActionView().findViewById(R.id.zone_switch);
			zoneSwitch.setChecked(sharedPreferences.getBoolean(SP_KEY_ZONE + carsharingService.getID(), false));
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

		for (CarsharingService carsharingService : CarsharingService.CARSHARING_SERVICES)
			new ZoneDownloader(carsharingService).execute();

		for (VehicleMarkerManager vehicleMarkerManager : vehicleMarkerManagers)
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
		for (VehicleDownloader vehicleDownloader : activeVehicleDownloaders)
			vehicleDownloader.start();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		for (VehicleDownloader vehicleDownloader : activeVehicleDownloaders)
			vehicleDownloader.stop();

		SharedPreferences sharedPreferences = getSharedPreferences(SP_NAME_SWITCH, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		NavigationView navigationView = findViewById(R.id.nav_view);
		for (CarsharingService carsharingService : CarsharingService.CARSHARING_SERVICES)
		{
			Switch carSwitch = navigationView.getMenu().findItem(carsharingService.getMenuID()).getActionView().findViewById(R.id.car_switch);
			editor.putBoolean(SP_KEY_CAR + carsharingService.getID(), carSwitch.isChecked());
			Switch zoneSwitch = navigationView.getMenu().findItem(carsharingService.getMenuID()).getActionView().findViewById(R.id.zone_switch);
			editor.putBoolean(SP_KEY_ZONE + carsharingService.getID(), zoneSwitch.isChecked());
		}
		editor.apply();
	}

	@Override
	public void onInfoWindowClick(Marker marker)
	{
		Vehicle vehicle = (Vehicle)marker.getTag();

		Intent launchIntent = getPackageManager().getLaunchIntentForPackage(vehicle.getCarsharingService().getAppPackage());
		if (launchIntent == null)
		{
			Toast.makeText(this, R.string.app_not_installed, Toast.LENGTH_LONG).show();
		}
		else
		{
			startActivity(launchIntent);
		}
	}

	private class ZoneDownloader extends AsyncTask<Void, Void, List<List<LatLng>>>
	{
		private final CarsharingService carsharingService;

		public ZoneDownloader(CarsharingService carsharingService)
		{
			this.carsharingService = carsharingService;
		}

		@Override
		protected List<List<LatLng>> doInBackground(Void... voids)
		{
			return carsharingService.downloadZone();
		}

		@Override
		protected void onPostExecute(List<List<LatLng>> zone)
		{
			List<Polygon> polygons = new ArrayList<>();

			Switch zoneSwitch = navigationView.getMenu().findItem(carsharingService.getMenuID()).getActionView().findViewById(R.id.zone_switch);
			zoneSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
			{
				for (Polygon polygon : polygons)
				{
					polygon.setVisible(isChecked);
				}
			});

			for (List<LatLng> shape : zone)
			{
				int color = carsharingService.getColor();

				PolygonOptions polygonOptions = new PolygonOptions()
						.addAll(shape)
						.fillColor(Color.argb(50, Color.red(color), Color.green(color), Color.blue(color)))
						.strokeColor(Color.BLACK)
						.strokeWidth(2)
						.visible(zoneSwitch.isChecked())
						;
				Polygon polygon = map.addPolygon(polygonOptions);
				polygons.add(polygon);
			}

		}
	}

}
