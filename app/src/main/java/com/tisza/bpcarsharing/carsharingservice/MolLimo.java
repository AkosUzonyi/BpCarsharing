package com.tisza.bpcarsharing.carsharingservice;

import android.graphics.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class MolLimo implements CarsharingService
{
	@Override
	public int getNameResource()
	{
		return 0;
	}

	@Override
	public int getColor()
	{
		return Color.BLUE;
	}

	private int getColorFromModel(String model)
	{
		switch (model)
		{
			case "Up": return Color.rgb(0, 0, 150);
			case "eUp": return Color.rgb(0, 0, 200);
		}

		System.err.println("unkown model: " + model);
		return Color.MAGENTA;
	}

	@Override
	public Collection<Vehicle> downloadVehicles()
	{
		Collection<Vehicle> vehicles = new ArrayList<>();

		try
		{
			String jsonText = Utils.downloadText("https://www.mollimo.hu/data/cars.js?R3gE8PLjKk");
			JSONArray jsonArray = new JSONArray(jsonText.substring(jsonText.indexOf("[")));

			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject vehicleJSON = jsonArray.getJSONObject(i);

				JSONObject positionJSON = vehicleJSON.getJSONObject("location").getJSONObject("position");
				JSONObject descriptionJSON = vehicleJSON.getJSONObject("description");
				JSONObject statusJSON = vehicleJSON.getJSONObject("status");

				double gps_lat = positionJSON.getDouble("lat");
				double gps_long = positionJSON.getDouble("lon");
				String plate_number = descriptionJSON.getString("name");
				int battery_level = 50;
				int estimated_km = statusJSON.getInt("energyLevel");
				int color = getColorFromModel(descriptionJSON.getString("model"));

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
		return "com.vulog.carshare.mol";
	}
}
