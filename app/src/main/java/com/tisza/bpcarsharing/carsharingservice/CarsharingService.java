package com.tisza.bpcarsharing.carsharingservice;

import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;

public interface CarsharingService
{
	public static final CarsharingService[] CARSHARING_SERVICES = new CarsharingService[]{new GreenGo(), new MolLimo(), new Blinkee()};

	public String getID();
	public int getColor();
	public Collection<? extends VehicleCategory> getVehicleCategories();
	public Collection<Vehicle> downloadVehicles() throws IOException, JSONException;
	public List<List<LatLng>> downloadZone() throws IOException, JSONException;
	public String getAppPackage();
	public int getMenuID();
}
