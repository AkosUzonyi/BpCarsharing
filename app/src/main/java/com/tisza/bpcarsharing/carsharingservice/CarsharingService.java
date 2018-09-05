package com.tisza.bpcarsharing.carsharingservice;

import java.util.*;

public interface CarsharingService
{
	public static final CarsharingService[] CARSHARING_SERVICES = new CarsharingService[]{new GreenGo(), new MolLimo(), new Blinkee()};

	public int getNameResource();
	public int getColor();
	public Collection<Vehicle> downloadVehicles();
	public String getAppPackage();
}
