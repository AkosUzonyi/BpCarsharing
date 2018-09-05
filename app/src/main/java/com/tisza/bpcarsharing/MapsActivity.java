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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		for (int i = 0; i < permissions.length; i++)
		{
			if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
			{
				if (mMap != null)
				{
					mMap.setMyLocationEnabled(true);
				}
			}
		}
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
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
			mMap.setMyLocationEnabled(true);

		for (CarsharingService carsharingService : CarsharingService.CARSHARING_SERVICES)
		{
			new VechicleListDownloadAsyncTask(carsharingService, mMap).execute();
		}
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
