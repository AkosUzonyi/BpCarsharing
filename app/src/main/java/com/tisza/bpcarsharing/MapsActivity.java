package com.tisza.bpcarsharing;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.widget.*;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.carsharingservice.*;

import java.util.*;

public class MapsActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{

	private static final int RELOAD_DELAY_SEC = 10;
	private static final LatLng BP_CENTER = new LatLng(47.495225, 19.045508);
	private static final LatLngBounds BP_BOUNDS = new LatLngBounds(new LatLng(47.463008, 18.983644), new LatLng(47.550324, 19.157741));
	private static final int BP_ZOOM = 12, MY_LOCATION_ZOOM = 15;

	private FusedLocationProviderClient fusedLocationClient;

	private final Runnable reloadCardsRunnable = this::reloadCars;
	private List<VehicleListDownloadAsyncTask> downloadTasks = new ArrayList<>();

	private GoogleMap mMap;
	private VehicleMarkerManager vehicleMarkerManager;
	private Handler reloadHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

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

		reloadHandler = new Handler();
		vehicleMarkerManager = new VehicleMarkerManager();
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
		vehicleMarkerManager.populateMap();
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
			vehicleMarkerManager.clearVehicles();
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
			Toast.makeText(this, "App not installed", Toast.LENGTH_LONG);
		}
		else
		{
			startActivity(launchIntent);
		}
	}

	private class VehicleListDownloadAsyncTask extends AsyncTask<Void, Void, Void>
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
		protected Void doInBackground(Void... voids)
		{
			for (Vehicle vehicle : carsharingService.downloadVehicles())
				vehicleMarkerManager.registerVehicle(vehicle);

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			downloadTasks.remove(this);

			if (mMap != null && downloadTasks.isEmpty())
				vehicleMarkerManager.populateMap();
		}

		@Override
		protected void onCancelled()
		{
			downloadTasks.remove(this);
		}
	}
}
