package com.tisza.bpcarsharing;

public class Vehicle
{
	private final String id;
	private final double lat, lng;
	private final String plate;
	private final int range;
	private final VehicleCategory category;

	public Vehicle(String id, double lat, double lng, String plate, int range, VehicleCategory category)
	{
		this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.plate = plate;
		this.category = category;
		this.range = range;
	}

	public boolean isVisible()
	{
		return BuildConfig.DEBUG || category.getFuel() == Fuel.ELECTRICITY;
	}

	public String getId()
	{
		return id;
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

	public int getRange()
	{
		return range;
	}
}
