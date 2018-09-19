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
	public String getID()
	{
		return "greengo";
	}

	@Override
	public int getColor()
	{
		return Color.GREEN;
	}

	@Override
	public Collection<? extends VehicleCategory> getVehicleCategories()
	{
		return Arrays.asList(VehicleCategory.GREENGO);
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
				int estimated_km = vehicleJSON.getInt("estimated_km");

				vehicles.add(new Vehicle(getID() + id, this, gps_lat, gps_long, plate_number, estimated_km, VehicleCategory.GREENGO));
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
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getAppPackage()
	{
		return "com.GreenGo";
	}

	@Override
	public int getMenuID()
	{
		return R.id.greengo_switch;
	}
}
