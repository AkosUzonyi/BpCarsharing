package com.tisza.bpcarsharing.carsharingservice;

import android.graphics.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class GreenGo implements CarsharingService
{
	@Override
	public int getNameResource()
	{
		return 0;
	}

	@Override
	public int getColor()
	{
		return Color.GREEN;
	}

	private static int getColorFromString(String color)
	{
		switch (color)
		{
			case "WHITE":   return Color.WHITE;
			case "SILVER":  return Color.GRAY;
			case "BLUE":    return Color.BLUE;
			case "PEACOCK": return Color.BLUE;
			case "YELLOW":  return Color.YELLOW;
			case "BLACK":   return Color.BLACK;
		}

		System.err.println("color not recognized: " + color);
		return Color.MAGENTA;
	}

	@Override
	public Collection<Vehicle> downloadVehicles()
	{
		Collection<Vehicle> vehicles = new ArrayList<>();

		try
		{
			String jsonText = Utils.downloadText("https://www.greengo.hu/divcontent.php?rnd=0.3300484530422363&funct=callAPI&APIname=getVehicleList&params[P_ICON_SIZE]=48&_=1528898349590");
			JSONArray jsonArray = new JSONArray(jsonText);

			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject vehicleJSON = jsonArray.getJSONObject(i);

				double gps_lat = vehicleJSON.getDouble("gps_lat");
				double gps_long = vehicleJSON.getDouble("gps_long");
				String plate_number = vehicleJSON.getString("plate_number");
				int battery_level = vehicleJSON.getInt("battery_level");
				int estimated_km = vehicleJSON.getInt("estimated_km");
				int color = getColorFromString(vehicleJSON.getString("color"));

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
		return "com.GreenGo";
	}
}
