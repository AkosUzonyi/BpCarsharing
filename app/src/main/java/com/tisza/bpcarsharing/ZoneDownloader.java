package com.tisza.bpcarsharing;

import android.graphics.*;
import android.os.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.carsharingservice.*;

import java.util.*;

public class ZoneDownloader extends AsyncTask<Void, Void, List<List<LatLng>>>
{
	private final GoogleMap map;
	private final CarsharingService carsharingService;

	public ZoneDownloader(GoogleMap map, CarsharingService carsharingService)
	{
		this.map = map;
		this.carsharingService = carsharingService;
	}

	@Override
	protected List<List<LatLng>> doInBackground(Void... voids)
	{
		return carsharingService.downloadZone();
	}

	@Override
	protected void onPostExecute(List<List<LatLng>> zone)
	{
		for (List<LatLng> shape : zone)
		{
			int color = carsharingService.getColor();

			PolygonOptions polygonOptions = new PolygonOptions()
					.addAll(shape)
					.fillColor(Color.argb(100, Color.red(color), Color.green(color), Color.blue(color)))
					.strokeColor(Color.BLACK)
					.strokeWidth(2)
					;
			map.addPolygon(polygonOptions);
		}
	}
}
