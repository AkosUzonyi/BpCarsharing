package com.tisza.bpcarsharing.carsharingservice;

import android.graphics.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.*;
import org.json.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class GreenGo implements CarsharingService
{
	private static final Pattern zonePattern = Pattern.compile("var area = (\\[[^;]*)");

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
	public Collection<Vehicle> downloadVehicles() throws IOException, JSONException
	{
		Collection<Vehicle> vehicles = new ArrayList<>();

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

		return vehicles;
	}

	public List<Shape> downloadZone() throws IOException, JSONException
	{
		List<Shape> zone = new ArrayList<>();

		String pageHTML = Utils.downloadText("https://www.greengo.hu");

		Matcher matcher = zonePattern.matcher(pageHTML);
		if (!matcher.find())
			return zone;

		String jsonText = matcher.group(1);
		JSONArray jsonArray = new JSONArray(jsonText);

		for (int i = 0; i < jsonArray.length(); i++)
		{
			JSONArray shapeJSONArray = new JSONArray(jsonArray.getJSONObject(i).getString("area"));
			Shape shape = new Shape();

			for (int j = 0; j < shapeJSONArray.length(); j++)
			{
				JSONArray coordsJSON = shapeJSONArray.getJSONArray(j);
				double lat = coordsJSON.getDouble(1);
				double lng = coordsJSON.getDouble(0);
				shape.coords.add(new LatLng(lat, lng));
			}

			zone.add(shape);
		}

		return zone;
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
