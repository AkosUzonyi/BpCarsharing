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

public class MapsActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{
	private static final int RELOAD_DELAY_SEC = 20;

	private final Runnable reloadCardsRunnable = this::reloadCars;

	private GoogleMap mMap;
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
		reloadHandler.removeCallbacks(reloadCardsRunnable);
	}

	private void reloadCars()
	{
		if (mMap != null)
		{
			mMap.clear();
			for (CarsharingService carsharingService : CarsharingService.CARSHARING_SERVICES)
			{
				new VechicleListDownloadAsyncTask(carsharingService, mMap).execute();
			}
		}

		reloadHandler.postDelayed(reloadCardsRunnable, RELOAD_DELAY_SEC * 1000);
	}

	@Override
	public void onInfoWindowClick(Marker marker)
	{
		MarkerTag markerTag = (MarkerTag)marker.getTag();

		Intent launchIntent = getPackageManager().getLaunchIntentForPackage(markerTag.getCarsharingService().getAppPackage());
		if (launchIntent == null)
		{
			Toast.makeText(this, "App not installed", Toast.LENGTH_LONG);
		}
		else
		{
			startActivity(launchIntent);
		}
	}
}
