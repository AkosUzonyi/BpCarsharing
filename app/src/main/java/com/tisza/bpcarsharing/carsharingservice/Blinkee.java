package com.tisza.bpcarsharing.carsharingservice;

import android.graphics.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class Blinkee implements CarsharingService
{
	private final Random rnd = new Random();

	@Override
	public String getID()
	{
		return "blinkee";
	}

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
	public Collection<Vehicle> downloadVehicles() throws IOException, JSONException
	{
		Collection<Vehicle> vehicles = new ArrayList<>();

		String jsonText = Utils.downloadText("https://blinkee.city/api/vehicles/11");
		JSONArray jsonArray = new JSONArray(jsonText);

		for (int i = 0; i < jsonArray.length(); i++)
		{
			JSONObject vehicleJSON = jsonArray.getJSONObject(i);

			JSONObject positionJSON = vehicleJSON.getJSONObject("position");

			int id = rnd.nextInt();
			double gps_lat = positionJSON.getDouble("lat");
			double gps_long = positionJSON.getDouble("lng");
			String plate_number = "blinkee";

			vehicles.add(new Vehicle(getID() + id, this, gps_lat, gps_long, plate_number, VehicleCategory.BLINKEE));
		}

		return vehicles;
	}

	public List<Shape> downloadZone() throws IOException, JSONException
	{
		List<Shape> zone = new ArrayList<>();

		String text = Utils.downloadText("https://blinkee.city/api/regions");
		JSONArray jsonArray = new JSONArray(text);
		JSONObject bpRegionJSON = getJSONObjectFromArrayByID(jsonArray, 11);
		JSONArray zoneJSONArray = bpRegionJSON
				.getJSONArray("zones")
				.getJSONObject(0)
				.getJSONObject("area")
				.getJSONArray("coordinates")
				.getJSONArray(0);

		Shape shape = new Shape();
		for (int i = 0; i < zoneJSONArray.length(); i++)
		{
			JSONArray shapeJSONArray = zoneJSONArray.getJSONArray(i);

			List<LatLng> coords;
			if (i == 0)
			{
				coords = shape.coords;
			}
			else
			{
				coords = new ArrayList<>();
				shape.holes.add(coords);
			}

			for (int j = 0; j < shapeJSONArray.length(); j++)
			{
				JSONArray coordsJSON = shapeJSONArray.getJSONArray(j);
				double lat = coordsJSON.getDouble(0);
				double lng = coordsJSON.getDouble(1);
				coords.add(new LatLng(lat, lng));
			}
		}

		zone.add(shape);
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

	@Override
	public int getMenuID()
	{
		return R.id.blinkee_swicth;
	}
}
