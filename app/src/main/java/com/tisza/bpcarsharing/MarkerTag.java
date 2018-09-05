package com.tisza.bpcarsharing;

import com.tisza.bpcarsharing.carsharingservice.*;

public class MarkerTag
{
	private final CarsharingService carsharingService;
	private final Vehicle vehicle;

	public MarkerTag(CarsharingService carsharingService, Vehicle vehicle)
	{
		this.carsharingService = carsharingService;
		this.vehicle = vehicle;
	}

	public CarsharingService getCarsharingService()
	{
		return carsharingService;
	}

	public Vehicle getVehicle()
	{
		return vehicle;
	}
}
