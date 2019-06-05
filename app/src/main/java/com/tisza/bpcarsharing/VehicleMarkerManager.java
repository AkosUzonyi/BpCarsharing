package com.tisza.bpcarsharing;

import android.os.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.*;

public class VehicleMarkerManager
{
	private Map<String, Vehicle> vehicles = new HashMap<>();
	private Map<String, Marker> markers = new HashMap<>();
	private Set<CarsharingService> visibleServices = new HashSet<>();

	private GoogleMap map;

	private Handler handler = new Handler();
	private Runnable updateAllMarkersRunnable = () -> updateMarkers(false);

	public void setMap(GoogleMap map)
	{
		this.map = map;

		for (Marker marker : markers.values())
			marker.remove();
		markers.clear();

		updateMarkers();
	}

	public void setCarsharingServiceVisible(CarsharingService carsharingService, boolean visible)
	{
		if (visible)
			visibleServices.add(carsharingService);
		else
			visibleServices.remove(carsharingService);

		for (Map.Entry<String, Marker> entry : markers.entrySet())
		{
			String id = entry.getKey();
			Marker marker = entry.getValue();

			if (vehicles.get(id).getCategory().getCarsharingService() == carsharingService)
				marker.setVisible(visible);
		}
	}

	public void setVehicles(Collection<? extends Vehicle> newVehicles)
	{
		vehicles.clear();
		for (Vehicle vehicle : newVehicles)
			vehicles.put(vehicle.getUID(), vehicle);

		updateMarkers();
	}

	private void updateMarkers()
	{
		updateMarkers(true);
		handler.removeCallbacks(updateAllMarkersRunnable);
		handler.postDelayed(updateAllMarkersRunnable, 500);
	}

	private void updateMarkers(boolean onlyVisible)
	{
		if (map == null)
			return;

		LatLngBounds visibleBounds = map.getProjection().getVisibleRegion().latLngBounds;

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

			if (!vehicle.isVisible())
				continue;

			LatLng position = new LatLng(vehicle.getLat(), vehicle.getLng());

			if (onlyVisible && !visibleBounds.contains(position))
				continue;

			if (marker == null)
			{
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(position);
				markerOptions.title(vehicle.getPlate());
				markerOptions.snippet(vehicle.getRange() + " km");
				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(vehicle.getCategory().getHue()));
				markerOptions.visible(visibleServices.contains(vehicle.getCategory().getCarsharingService()));

				marker = map.addMarker(markerOptions);
				markers.put(id, marker);
			}
			else
			{
				marker.setPosition(position);
				marker.setTitle(vehicle.getPlate());
				marker.setSnippet(vehicle.getRange() + " km");
				marker.setIcon(BitmapDescriptorFactory.defaultMarker(vehicle.getCategory().getHue()));
				marker.setVisible(visibleServices.contains(vehicle.getCategory().getCarsharingService()));
			}
			marker.setTag(vehicle);
		}
	}
}
