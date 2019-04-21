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
	private final ProgressBarHandler progressBarHandler;

	private boolean visible = false;
	private GoogleMap map = null;
	private List<ShapeCoords> coordinates = null;
	private List<Polygon> polygons = new ArrayList<>();

	public ZoneDownloader(CarsharingService carsharingService, ProgressBarHandler progressBarHandler)
	{
		this.carsharingService = carsharingService;
		this.progressBarHandler = progressBarHandler;
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

		int color = carsharingService.getColor();

		for (ShapeCoords shape : coordinates)
		{
			PolygonOptions polygonOptions = new PolygonOptions()
					.addAll(shape.coords)
					.fillColor(Color.argb(50, Color.red(color), Color.green(color), Color.blue(color)))
					.strokeColor(Color.BLACK)
					.strokeWidth(2)
					.visible(visible)
					;

			for (List<LatLng> holeCoords : shape.holes)
				polygonOptions.addHole(holeCoords);

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

	private class DownloadTask extends AsyncTask<Void, Void, List<ShapeCoords>>
	{
		@Override
		protected void onPreExecute()
		{
			progressBarHandler.startProcess();
		}

		@Override
		protected List<ShapeCoords> doInBackground(Void... voids)
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
		protected void onPostExecute(List<ShapeCoords> zone)
		{
			progressBarHandler.endProcess();

			coordinates = zone;
			createPolygons();
		}

		@Override
		protected void onCancelled()
		{
			progressBarHandler.endProcess();
		}
	}
}
