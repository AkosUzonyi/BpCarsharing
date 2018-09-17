package com.tisza.bpcarsharing.carsharingservice;

import android.graphics.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class GreenGo implements CarsharingService
{
	@Override
	public int getColor()
	{
		return Color.GREEN;
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

				String id = vehicleJSON.getString("vehicle_id");
				double gps_lat = vehicleJSON.getDouble("gps_lat");
				double gps_long = vehicleJSON.getDouble("gps_long");
				String plate_number = vehicleJSON.getString("plate_number");
				int battery_level = vehicleJSON.getInt("battery_level");
				int estimated_km = vehicleJSON.getInt("estimated_km");

				vehicles.add(new Vehicle(id, this, gps_lat, gps_long, plate_number, BitmapDescriptorFactory.HUE_GREEN, battery_level, estimated_km));
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
