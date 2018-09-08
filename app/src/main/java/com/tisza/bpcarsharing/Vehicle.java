package com.tisza.bpcarsharing;

import com.tisza.bpcarsharing.carsharingservice.*;

public class Vehicle
{
	private final String id;
	private final CarsharingService carsharingService;
	private final double lat, lng;
	private final String plate;
	private final int chargePercentage, range;
	private final float hue;

	public Vehicle(String id, CarsharingService carsharingService, double lat, double lng, String plate, int chargePercentage, int range, float hue)
	{
		this.id = id;
		this.carsharingService = carsharingService;
		this.lat = lat;
		this.lng = lng;
		this.plate = plate;
		this.chargePercentage = chargePercentage;
		this.range = range;
		this.hue = hue;
	}

	public String getId()
	{
		return id;
	}

	public CarsharingService getCarsharingService()
	{
		return carsharingService;
	}

	public double getLat()
	{
		return lat;
	}

	public double getLng()
	{
		return lng;
	}

	public String getPlate()
	{
		return plate;
	}

	public int getChargePercentage()
	{
		return chargePercentage;
	}

	public int getRange()
	{
		return range;
	}

	public float getHue()
	{
		return hue;
	}
}
