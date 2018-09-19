package com.tisza.bpcarsharing.carsharingservice;

import android.graphics.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class MolLimo implements CarsharingService
{
	private static VehicleCategory getVehicleCategoryFromModelName(String model)
	{
		switch (model)
		{
			case "Up": return VehicleCategory.MOL_LIMO_UP;
			case "eUp": return VehicleCategory.MOL_LIMO_EUP;
		}
		return null;
	}

	@Override
	public int getColor()
	{
		return Color.BLUE;
	}

	@Override
	public Collection<? extends VehicleCategory> getVehicleCategories()
	{
		return Arrays.asList(VehicleCategory.MOL_LIMO_UP, VehicleCategory.MOL_LIMO_EUP);
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

				VehicleCategory vehicleCategory = getVehicleCategoryFromModelName(descriptionJSON.getString("model"));
				if (vehicleCategory == null)
					continue;

				String id = descriptionJSON.getString("id");
				double gps_lat = positionJSON.getDouble("lat");
				double gps_long = positionJSON.getDouble("lon");
				String plate_number = descriptionJSON.getString("name");
				int estimated_km = statusJSON.getInt("energyLevel");

				vehicles.add(new Vehicle(id, this, gps_lat, gps_long, plate_number, estimated_km, vehicleCategory));
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
	public List<List<LatLng>> downloadZone()
	{
		List<List<LatLng>> zone = new ArrayList<>();

		try
		{
			String text = Utils.downloadText("https://www.mollimo.hu/data/homezone.js?Isg7gJs12R");

			for (String jsonArrayText : text.split(";"))
			{
				List<LatLng> shape = new ArrayList<>();

				JSONArray shapeJSONArray = new JSONArray(jsonArrayText.substring(jsonArrayText.indexOf("[")));
				for (int i = 0; i < shapeJSONArray.length(); i++)
				{
					JSONObject coordsJSON = shapeJSONArray.getJSONObject(i);
					double lat = coordsJSON.getDouble("lat");
					double lng = coordsJSON.getDouble("lng");
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

	@Override
	public String getAppPackage()
	{
		return "com.vulog.carshare.mol";
	}
}
