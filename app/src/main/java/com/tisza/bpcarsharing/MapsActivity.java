package com.tisza.bpcarsharing;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.carsharingservice.*;

import java.util.*;

public class MapsActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{
	private static final int RELOAD_DELAY_SEC = 20;

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

		reloadHandler = new Handler();
		vehicleMarkerManager = new VehicleMarkerManager();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		tryEnableMapLocation();
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(47.495225, 19.045508), 12));
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
