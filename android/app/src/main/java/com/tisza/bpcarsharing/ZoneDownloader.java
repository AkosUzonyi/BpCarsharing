package com.tisza.bpcarsharing;

import android.graphics.*;
import android.os.*;
import android.util.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.json.*;

import java.util.*;

public class ZoneDownloader
{
	private final ProgressBarHandler progressBarHandler;

	private Set<CarsharingService> visibleServices = new HashSet<>();
	private GoogleMap map = null;
	private List<Shape> shapes = null;
	private List<Polygon> polygons = new ArrayList<>();

	public ZoneDownloader(ProgressBarHandler progressBarHandler)
	{
		this.progressBarHandler = progressBarHandler;
	}

	public void setMap(GoogleMap map)
	{
		this.map = map;

		createPolygons();
	}

	public void setCarsharingServiceVisible(CarsharingService carsharingService, boolean visible)
	{
		if (visible)
			visibleServices.add(carsharingService);
		else
			visibleServices.remove(carsharingService);

		for (Polygon polygon : polygons)
			if (polygon.getTag() == carsharingService)
				polygon.setVisible(visible);
	}

	private void createPolygons()
	{
		for (Polygon polygon : polygons)
			polygon.remove();
		polygons.clear();

		if (map == null || shapes == null)
			return;

		for (Shape shape : shapes)
		{
			int color = shape.carsharingService.getColor();
			PolygonOptions polygonOptions = new PolygonOptions()
					.addAll(shape.coords)
					.fillColor(Color.argb(50, Color.red(color), Color.green(color), Color.blue(color)))
					.strokeColor(Color.BLACK)
					.strokeWidth(2)
					.visible(visibleServices.contains(shape.carsharingService))
					;

			for (List<LatLng> holeCoords : shape.holes)
				polygonOptions.addHole(holeCoords);

			Polygon polygon = map.addPolygon(polygonOptions);
			polygon.setTag(shape.carsharingService);
			polygons.add(polygon);
		}
	}

	public boolean isReady()
	{
		return shapes != null;
	}

	public void download()
	{
		new DownloadTask().execute();
	}

	private class DownloadTask extends AsyncTask<Void, Void, List<Shape>>
	{
		@Override
		protected void onPreExecute()
		{
			progressBarHandler.startProcess();
		}

		private void parseShape(JSONArray jsonArray, List<LatLng> list) throws JSONException
		{
			for (int j = 0; j < jsonArray.length(); j++)
			{
				JSONObject coordsJSON = jsonArray.getJSONObject(j);
				double lat = coordsJSON.getDouble("lat");
				double lng = coordsJSON.getDouble("lng");
				list.add(new LatLng(lat, lng));
			}
		}

		@Override
		protected List<Shape> doInBackground(Void... voids)
		{
			try
			{
				List<Shape> zone = new ArrayList<>();

				String jsonText = Utils.downloadText("http://akos0.ddns.net/carsharing/zones");
				JSONArray jsonArray = new JSONArray(jsonText);

				for (int i = 0; i < jsonArray.length(); i++)
				{
					JSONObject shapeJSON = jsonArray.getJSONObject(i);
					Shape shape = new Shape();

					shape.carsharingService = CarsharingService.fromString(shapeJSON.getString("service"));
					parseShape(shapeJSON.getJSONArray("coords"), shape.coords);

					JSONArray holesJSON = shapeJSON.getJSONArray("holes");
					for (int j = 0; j < holesJSON.length(); j++)
					{
						List<LatLng> hole = new ArrayList<>();
						parseShape(holesJSON.getJSONArray(j), hole);
						shape.holes.add(hole);
					}

					zone.add(shape);
				}

				return zone;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Shape> result)
		{
			progressBarHandler.endProcess();

			shapes = result;
			createPolygons();
		}

		@Override
		protected void onCancelled()
		{
			progressBarHandler.endProcess();
		}
	}
}
