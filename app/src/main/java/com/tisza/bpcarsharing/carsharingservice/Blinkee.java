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
	public Collection<? extends VehicleCategory> getVehicleCategories()
	{
		return Arrays.asList(VehicleCategory.BLINKEE);
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

	public List<List<LatLng>> downloadZone()
	{
		List<List<LatLng>> zone = new ArrayList<>();

		try
		{
			String text = Utils.downloadText("https://blinkee.city/api/regions");
			JSONArray jsonArray = new JSONObject(text).getJSONObject("data").getJSONArray("items");
			JSONObject bpRegionJSON = getJSONObjectFromArrayByID(jsonArray, 11);
			JSONArray zoneJSONArray = bpRegionJSON
					.getJSONArray("zones")
					.getJSONObject(0)
					.getJSONObject("area")
					.getJSONArray("coordinates")
					.getJSONArray(0);

			for (int i = 0; i < zoneJSONArray.length(); i++)
			{
				JSONArray shapeJSONArray = zoneJSONArray.getJSONArray(i);
				List<LatLng> shape = new ArrayList<>();

				for (int j = 0; j < shapeJSONArray.length(); j++)
				{
					JSONArray coordsJSON = shapeJSONArray.getJSONArray(j);
					double lat = coordsJSON.getDouble(1);
					double lng = coordsJSON.getDouble(0);
					shape.add(new LatLng(lat, lng));
				}

				zone.add(shape);
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

		return zone;
	}

	private JSONObject getJSONObjectFromArrayByID(JSONArray jsonArray, int id) throws JSONException
	{
		for (int i = 0; i < jsonArray.length(); i++)
			if (jsonArray.getJSONObject(i).getInt("id") == id)
				return jsonArray.getJSONObject(i);

		return null;
	}

	@Override
	public String getAppPackage()
	{
		return "pl.blinkee.mobile";
	}
}
