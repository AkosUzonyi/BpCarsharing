package com.tisza.bpcarsharing;

import android.os.*;
import com.google.android.gms.maps.*;
import com.tisza.bpcarsharing.carsharingservice.*;

import java.util.*;

public class VechicleListDownloadAsyncTask extends AsyncTask<Void, Void, Collection<Vehicle>>
{
	private final CarsharingService carsharingService;
	private final GoogleMap map;

	public VechicleListDownloadAsyncTask(CarsharingService carsharingService, GoogleMap map)
	{
		this.carsharingService = carsharingService;
		this.map = map;
	}

	@Override
	protected Collection<Vehicle> doInBackground(Void... voids)
	{
		return carsharingService.downloadVehicles();
	}

	@Override
	protected void onPostExecute(Collection<Vehicle> vehicles)
	{
		if (vehicles == null)
			return;

		for (Vehicle vehicle : vehicles)
		{
			map.addMarker(vehicle.getMarker()).setTag(new MarkerTag(carsharingService, vehicle));
		}
	}
}
