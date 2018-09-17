package com.tisza.bpcarsharing.carsharingservice;

import android.graphics.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class Blinkee implements CarsharingService
{
	@Override
	public int getColor()
	{
		return Color.rgb(200, 200, 0);
	}

	@Override
	public Collection<Vehicle> downloadVehicles()
	{
		Collection<Vehicle> vehicles = new ArrayList<>();

		try
		{
			String jsonText = Utils.downloadText("https://blinkee.city/api/regions/11/vehicles");
			JSONArray jsonArray = new JSONObject(jsonText).getJSONObject("data").getJSONArray("items");

			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject vehicleJSON = jsonArray.getJSONObject(i);

				JSONObject positionJSON = vehicleJSON.getJSONObject("position");

				String id = vehicleJSON.getString("id");
				double gps_lat = positionJSON.getDouble("lat");
				double gps_long = positionJSON.getDouble("lng");
				String plate_number = "blinkee";

				vehicles.add(new Vehicle(id, this, gps_lat, gps_long, plate_number, VehicleCategory.BLINKEE));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return vehicles;
	}

	@Override
	public String getAppPackage()
	{
		return "pl.blinkee.mobile";
	}
}
