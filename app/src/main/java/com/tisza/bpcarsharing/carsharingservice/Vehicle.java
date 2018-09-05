package com.tisza.bpcarsharing.carsharingservice;

import com.google.android.gms.maps.model.*;

public class Vehicle
{
	private final double lat, lng;
	private final String plate;
	private final int chargePercentage, range;
	private final int color;

	public Vehicle(double lat, double lng, String plate, int chargePercentage, int range, int color)
	{
		this.lat = lat;
		this.lng = lng;
		this.plate = plate;
		this.chargePercentage = chargePercentage;
		this.range = range;
		this.color = color;
	}

	public MarkerOptions getMarker()
	{
		MarkerOptions marker = new MarkerOptions();
		marker.position(new LatLng(lat, lng));
		marker.title(plate);
		marker.snippet(chargePercentage + "% | " + range + " km");
		marker.alpha(chargePercentage / 100F);
		//marker.icon(BitmapDescriptor)
		return marker;
	}
}
