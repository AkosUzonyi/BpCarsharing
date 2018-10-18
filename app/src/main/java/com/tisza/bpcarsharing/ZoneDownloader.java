package com.tisza.bpcarsharing;

import android.graphics.*;
import android.os.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.tisza.bpcarsharing.carsharingservice.*;

import java.util.*;

public class ZoneDownloader
{
	private final CarsharingService carsharingService;
	private boolean visible = false;
	private GoogleMap map = null;
	private List<List<LatLng>> coordinates = null;
	private List<Polygon> polygons = new ArrayList<>();

	public ZoneDownloader(CarsharingService carsharingService)
	{
		this.carsharingService = carsharingService;
	}

	public void setMap(GoogleMap map)
	{
		this.map = map;

		createPolygons();
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
		for (Polygon polygon : polygons)
			polygon.setVisible(visible);
	}

	private void createPolygons()
	{
		for (Polygon polygon : polygons)
			polygon.remove();
		polygons.clear();

		if (map == null || coordinates == null)
			return;

		for (List<LatLng> shape : coordinates)
		{
			int color = carsharingService.getColor();

			PolygonOptions polygonOptions = new PolygonOptions()
					.addAll(shape)
					.fillColor(Color.argb(50, Color.red(color), Color.green(color), Color.blue(color)))
					.strokeColor(Color.BLACK)
					.strokeWidth(2)
					.visible(visible)
					;
			Polygon polygon = map.addPolygon(polygonOptions);
			polygons.add(polygon);
		}
	}

	public boolean isReady()
	{
		return coordinates != null;
	}

	public void download()
	{
		new DownloadTask().execute();
	}

	private class DownloadTask extends AsyncTask<Void, Void, List<List<LatLng>>>
	{
		@Override
		protected List<List<LatLng>> doInBackground(Void... voids)
		{
			try
			{
				return carsharingService.downloadZone();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<List<LatLng>> zone)
		{
			coordinates = zone;
			createPolygons();
		}
	}
}
