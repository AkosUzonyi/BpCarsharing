package com.tisza.bpcarsharing;

import com.tisza.bpcarsharing.carsharingservice.*;

public class Vehicle
{
	private final String id;
	private final CarsharingService carsharingService;
	private final double lat, lng;
	private final String plate;
	private final int chargePercentage, range;
	private final boolean hasChargeInfo;
	private final float hue;

	public Vehicle(String id, CarsharingService carsharingService, double lat, double lng, String plate, float hue)
	{
		this(id, carsharingService, lat, lng, plate, hue, 0, 0, false);
	}

	public Vehicle(String id, CarsharingService carsharingService, double lat, double lng, String plate, float hue, int chargePercentage, int range)
	{
		this(id, carsharingService, lat, lng, plate, hue, chargePercentage, range, true);
	}

	private Vehicle(String id, CarsharingService carsharingService, double lat, double lng, String plate, float hue, int chargePercentage, int range, boolean hasChargeInfo)
	{
		this.id = id;
		this.carsharingService = carsharingService;
		this.lat = lat;
		this.lng = lng;
		this.plate = plate;
		this.hue = hue;
		this.chargePercentage = chargePercentage;
		this.range = range;
		this.hasChargeInfo = hasChargeInfo;
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

	public float getHue()
	{
		return hue;
	}

	public boolean hasChargeInfo()
	{
		return hasChargeInfo;
	}

	public int getChargePercentage()
	{
		if (!hasChargeInfo)
			throw new UnsupportedOperationException();

		return chargePercentage;
	}

	public int getRange()
	{
		if (!hasChargeInfo)
			throw new UnsupportedOperationException();

		return range;
	}
}
