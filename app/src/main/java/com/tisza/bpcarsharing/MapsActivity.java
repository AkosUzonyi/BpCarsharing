package com.tisza.bpcarsharing;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.location.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MapsActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, NetworkStateReceiver.NetworkStateListener
{
	private static final String SP_NAME_SWITCH = "switch";
	private static final String SP_KEY_CAR = "car";
	private static final String SP_KEY_ZONE = "zone";

	private static final int DOWNLOAD_INTERVAL = 40;
	private static final LatLng BP_CENTER = new LatLng(47.495225, 19.045508);
	private static final LatLngBounds BP_BOUNDS = new LatLngBounds(new LatLng(47.463008, 18.983644), new LatLng(47.550324, 19.157741));
	private static final int BP_ZOOM = 12, MY_LOCATION_ZOOM = 15;

	private NetworkStateReceiver networkStateReceiver;
	private DrawerLayout drawerLayout;
	private NavigationView navigationView;
	private GoogleMap map;

	private ProgressBarHandler progressBarHandler;

	private VehicleMarkerManager vehicleMarkerManager;
	private VehicleDownloader vehicleDownloader;
	private ZoneDownloader zoneDownloader;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		networkStateReceiver = new NetworkStateReceiver(this);
		drawerLayout = findViewById(R.id.drawer_layout);
		navigationView = findViewById(R.id.nav_view);

		ProgressBar progressBar = findViewById(R.id.progress_bar);
		progressBarHandler = new ProgressBarHandler(progressBar);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

		MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(this, this::onLocationFound);

		setActionBar(findViewById(R.id.toolbar));
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

		SharedPreferences sharedPreferences = getSharedPreferences(SP_NAME_SWITCH, Context.MODE_PRIVATE);
		vehicleMarkerManager = new VehicleMarkerManager();
		vehicleDownloader = new VehicleDownloader(getMainLooper(), DOWNLOAD_INTERVAL, vehicleMarkerManager::setVehicles, progressBarHandler);
		zoneDownloader = new ZoneDownloader(progressBarHandler);
		for (CarsharingService carsharingService : CarsharingService.values())
		{
			Switch carSwitch = navigationView.getMenu().findItem(carsharingService.getMenuID()).getActionView().findViewById(R.id.car_switch);
			carSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> vehicleMarkerManager.setCarsharingServiceVisible(carsharingService, isChecked));
			carSwitch.setChecked(sharedPreferences.getBoolean(SP_KEY_CAR + carsharingService.getID(), true));

			Switch zoneSwitch = navigationView.getMenu().findItem(carsharingService.getMenuID()).getActionView().findViewById(R.id.zone_switch);
			zoneSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> zoneDownloader.setCarsharingServiceVisible(carsharingService, isChecked));
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

		boolean locationEnabled = tryEnableMapLocation();
		//if location is enabled, wait for it, so marker placement can start with the near ones
		if (!locationEnabled)
			vehicleMarkerManager.setMap(map);

		zoneDownloader.setMap(map);
	}

	private boolean tryEnableMapLocation()
	{
		boolean hasPermission = map != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

		if (hasPermission)
			map.setMyLocationEnabled(true);

		return hasPermission;
	}

	private void onLocationFound(Location location)
	{
		if (location == null)
			return;

		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

		if (BP_BOUNDS.contains(latLng))
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MY_LOCATION_ZOOM));

		vehicleMarkerManager.setMap(map);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		unregisterReceiver(networkStateReceiver);
		setDownloadingActive(false);

		SharedPreferences sharedPreferences = getSharedPreferences(SP_NAME_SWITCH, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		NavigationView navigationView = findViewById(R.id.nav_view);
		for (CarsharingService carsharingService : CarsharingService.values())
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

		Intent launchIntent = getPackageManager().getLaunchIntentForPackage(vehicle.getCategory().getCarsharingService().getAppPackage());
		if (launchIntent == null)
		{
			Toast.makeText(this, R.string.app_not_installed, Toast.LENGTH_LONG).show();
		}
		else
		{
			startActivity(launchIntent);
		}
	}

	@Override
	public void networkConnectionStateChanged(boolean isConnected)
	{
		setDownloadingActive(isConnected);

		if (isConnected)
			if (!zoneDownloader.isReady())
				zoneDownloader.download();
	}

	private void setDownloadingActive(boolean active)
	{
		if (active)
			vehicleDownloader.start();
		else
			vehicleDownloader.stop();
	}
}
