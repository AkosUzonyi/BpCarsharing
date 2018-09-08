package com.tisza.bpcarsharing;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.*;

public class VehicleMarkerManager
{
	private Map<String, Vehicle> vehicles = new HashMap<>();
	private Map<String, Marker> markers = new HashMap<>();

	private GoogleMap map;

	public synchronized void clearVehicles()
	{
		vehicles.clear();
	}

	public synchronized void registerVehicle(Vehicle vehicle)
	{
		vehicles.put(vehicle.getId(), vehicle);
	}

	public synchronized void setMap(GoogleMap map)
	{
		this.map = map;
		markers.clear();
	}

	public synchronized void populateMap()
	{
		if (map == null)
			return;

		Iterator<Map.Entry<String, Marker>> it = markers.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<String, Marker> entry = it.next();
			String id = entry.getKey();
			Marker marker = entry.getValue();

			if (!vehicles.containsKey(id))
			{
				marker.remove();
				it.remove();
			}
		}

		for (String id : vehicles.keySet())
		{
			Vehicle vehicle = vehicles.get(id);
			Marker marker = markers.get(id);

			LatLng position = new LatLng(vehicle.getLat(), vehicle.getLng());

			if (marker == null)
			{
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(position);
				marker = map.addMarker(markerOptions);
				markers.put(id, marker);
			}

			marker.setPosition(position);
			marker.setTitle(vehicle.getPlate());
			marker.setSnippet(vehicle.getChargePercentage() + "% | " + vehicle.getRange() + " km");
			//marker.setAlpha(vehicle.getChargePercentage() / 100F);
			marker.setIcon(BitmapDescriptorFactory.defaultMarker(vehicle.getHue()));
			marker.setTag(vehicle);
		}
	}
}
