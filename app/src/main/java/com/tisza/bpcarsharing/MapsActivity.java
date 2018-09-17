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
import com.tisza.bpcarsharing.carsharingservice.*;

import java.util.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{

	private static final int RELOAD_DELAY_SEC = 10;
	private static final LatLng BP_CENTER = new LatLng(47.495225, 19.045508);
	private static final LatLngBounds BP_BOUNDS = new LatLngBounds(new LatLng(47.463008, 18.983644), new LatLng(47.550324, 19.157741));
	private static final int BP_ZOOM = 12, MY_LOCATION_ZOOM = 15;

	private FusedLocationProviderClient fusedLocationClient;

	private final Runnable reloadCardsRunnable = this::reloadCars;
	private List<VehicleListDownloadAsyncTask> downloadTasks = new ArrayList<>();

	private DrawerLayout drawerLayout;
	private GoogleMap mMap;
	private List<Vehicle> vehicles = new ArrayList<>();
	private VehicleMarkerManager vehicleMarkerManager;
	private Handler reloadHandler;

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
							mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MY_LOCATION_ZOOM));
					}
				});

		setSupportActionBar(findViewById(R.id.toolbar));
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

		reloadHandler = new Handler();
		vehicleMarkerManager = new VehicleMarkerManager();
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
		mMap = googleMap;
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BP_CENTER, BP_ZOOM));
		mMap.setOnInfoWindowClickListener(this);
		tryEnableMapLocation();

		vehicleMarkerManager.setMap(googleMap);
	}

	private void tryEnableMapLocation()
	{
		if (mMap != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
			mMap.setMyLocationEnabled(true);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		reloadCars();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		cancelDownloadTasks();
		reloadHandler.removeCallbacks(reloadCardsRunnable);
	}

	private void reloadCars()
	{
		reloadHandler.removeCallbacks(reloadCardsRunnable);

		if (!downloadTasks.isEmpty())
		{
			cancelDownloadTasks();
		}
		else
		{
			vehicles.clear();
			for (CarsharingService carsharingService : CarsharingService.CARSHARING_SERVICES)
				new VehicleListDownloadAsyncTask(carsharingService).execute();
		}

		reloadHandler.postDelayed(reloadCardsRunnable, RELOAD_DELAY_SEC * 1000);
	}

	private void cancelDownloadTasks()
	{
		for (VehicleListDownloadAsyncTask downloadTask : downloadTasks)
			downloadTask.cancel(true);
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

	private class VehicleListDownloadAsyncTask extends AsyncTask<Void, Void, Collection<Vehicle>>
	{
		private final CarsharingService carsharingService;

		public VehicleListDownloadAsyncTask(CarsharingService carsharingService)
		{
			this.carsharingService = carsharingService;
		}

		@Override
		protected void onPreExecute()
		{
			downloadTasks.add(this);
		}

		@Override
		protected Collection<Vehicle> doInBackground(Void... voids)
		{
			return carsharingService.downloadVehicles();
		}

		@Override
		protected void onPostExecute(Collection<Vehicle> result)
		{
			for (Vehicle vehicle : result)
				vehicles.add(vehicle);

			downloadTasks.remove(this);

			if (mMap != null && downloadTasks.isEmpty())
				vehicleMarkerManager.setVehicles(vehicles);
		}

		@Override
		protected void onCancelled()
		{
			downloadTasks.remove(this);
		}
	}
}
