package com.tisza.bpcarsharing.carsharingservice;

import android.graphics.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class Blinkee implements CarsharingService
{
	@Override
	public int getNameResource()
	{
		return 0;
	}

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

				double gps_lat = positionJSON.getDouble("lat");
				double gps_long = positionJSON.getDouble("lng");
				String plate_number = "semmi";
				int battery_level = 100;
				int estimated_km = 0;
				int color = getColor();

				vehicles.add(new Vehicle(gps_lat, gps_long, plate_number, battery_level, estimated_km, color));
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
