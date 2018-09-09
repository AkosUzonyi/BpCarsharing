package com.tisza.bpcarsharing.carsharingservice;

import android.graphics.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class MolLimo implements CarsharingService
{
	private static final VehicleModel defaultModel = new VehicleModel(BitmapDescriptorFactory.HUE_RED, 100);
	private static final Map<String, VehicleModel> modelNameMap = new HashMap<>();

	static
	{
		modelNameMap.put("Up", new VehicleModel(BitmapDescriptorFactory.HUE_BLUE, 350));
		modelNameMap.put("eUp", new VehicleModel(BitmapDescriptorFactory.HUE_AZURE - 20, 100));
	}

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

				VehicleModel vehicleModel = modelNameMap.get(descriptionJSON.getString("model"));
				if (vehicleModel == null)
					vehicleModel = defaultModel;

				String id = descriptionJSON.getString("id");
				double gps_lat = positionJSON.getDouble("lat");
				double gps_long = positionJSON.getDouble("lon");
				String plate_number = descriptionJSON.getString("name");
				int estimated_km = statusJSON.getInt("energyLevel");
				int battery_level = (int)((float)estimated_km / vehicleModel.maxKm * 100);
				float hue = vehicleModel.hue;

				vehicles.add(new Vehicle(id, this, gps_lat, gps_long, plate_number, hue, battery_level, estimated_km));
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

	private static class VehicleModel
	{
		private final float hue;
		private final int maxKm;

		public VehicleModel(float hue, int maxKm)
		{
			this.hue = hue;
			this.maxKm = maxKm;
		}
	}
}
