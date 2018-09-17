package com.tisza.bpcarsharing;

import com.tisza.bpcarsharing.carsharingservice.*;

public class Vehicle
{
	private final String id;
	private final CarsharingService carsharingService;
	private final double lat, lng;
	private final String plate;
	private final int range;
	private final boolean hasChargeInfo;
	private final VehicleCategory category;

	public Vehicle(String id, CarsharingService carsharingService, double lat, double lng, String plate, VehicleCategory category)
	{
		this(id, carsharingService, lat, lng, plate, 0, category, false);
	}

	public Vehicle(String id, CarsharingService carsharingService, double lat, double lng, String plate, int range, VehicleCategory category)
	{
		this(id, carsharingService, lat, lng, plate, range, category, true);
	}

	private Vehicle(String id, CarsharingService carsharingService, double lat, double lng, String plate, int range, VehicleCategory category, boolean hasChargeInfo)
	{
		this.id = id;
		this.carsharingService = carsharingService;
		this.lat = lat;
		this.lng = lng;
		this.plate = plate;
		this.category = category;
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

	public VehicleCategory getCategory()
	{
		return category;
	}

	public boolean hasChargeInfo()
	{
		return hasChargeInfo;
	}

	public int getChargePercentage()
	{
		return (int)(getRange() / category.getMaxRange() * 100);
	}

	public int getRange()
	{
		if (!hasChargeInfo)
			throw new UnsupportedOperationException();

		return range;
	}
}
