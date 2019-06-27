package com.tisza.bpcarsharing;

import android.arch.lifecycle.*;
import android.os.*;
import org.json.*;

import java.util.*;

public class VehicleLiveData extends LiveData<Collection<Vehicle>>
{
	private final Runnable downloadCarsRunnable = this::downloadCars;
	private final Handler handler;
	private final ProgressBarHandler progressBarHandler;

	private int downloadInterval;
	private VehicleDownloadAsyncTask currentDownloadTask = null;
	private boolean newDownloadRequestPending = false;

	public VehicleLiveData(Looper looper, int downloadInterval, ProgressBarHandler progressBarHandler)
	{
		handler = new Handler(looper);
		this.downloadInterval = downloadInterval;
		this.progressBarHandler = progressBarHandler;
	}

	public void setDownloadInterval(int downloadInterval)
	{
		this.downloadInterval = downloadInterval;
	}

	@Override
	protected void onActive()
	{
		downloadCars();
	}

	@Override
	protected void onInactive()
	{
		if (currentDownloadTask != null)
			currentDownloadTask.cancel(true);
	}

	private void downloadCars()
	{
		handler.removeCallbacks(downloadCarsRunnable);
		newDownloadRequestPending = false;

		if (!hasActiveObservers())
			return;

		if (currentDownloadTask != null)
		{
			newDownloadRequestPending = true;
			return;
		}

		new VehicleDownloadAsyncTask().execute();

		handler.postDelayed(downloadCarsRunnable, downloadInterval * 1000);
	}

	private class VehicleDownloadAsyncTask extends AsyncTask<Void, Void, Collection<Vehicle>>
	{
		@Override
		protected void onPreExecute()
		{
			currentDownloadTask = this;
			progressBarHandler.startProcess();
		}

		@Override
		protected Collection<Vehicle> doInBackground(Void... voids)
		{
			try
			{
				Collection<Vehicle> vehicles = new ArrayList<>();

				String jsonText = Utils.downloadText("http://akos0.ddns.net/carsharing/vehicles");
				JSONArray jsonArray = new JSONArray(jsonText);

				for (int i = 0; i < jsonArray.length(); i++)
				{
					JSONObject vehicleJSON = jsonArray.getJSONObject(i);

					String provider = vehicleJSON.getString("service");
					String id = vehicleJSON.optString("id", provider + new Random().nextInt());
					double lat = vehicleJSON.getDouble("lat");
					double lng = vehicleJSON.getDouble("lng");
					String plate = vehicleJSON.optString("plate", provider);
					int range = vehicleJSON.optInt("range", 0);
					int charge = vehicleJSON.optInt("charge", 0);
					String model = vehicleJSON.optString("model", null);

					vehicles.add(new Vehicle(id, lat, lng, plate, range, VehicleCategory.fromModel(CarsharingService.fromString(provider), model)));
				}

				return vehicles;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return Collections.EMPTY_LIST;
			}
		}

		@Override
		protected void onPostExecute(Collection<Vehicle> downloadedVehicles)
		{
			setValue(downloadedVehicles);
			onFinished();
		}

		@Override
		protected void onCancelled()
		{
			setValue(Collections.EMPTY_LIST);
			onFinished();
		}

		private void onFinished()
		{
			currentDownloadTask = null;
			progressBarHandler.endProcess();

			if (newDownloadRequestPending)
				downloadCars();
		}
	}
}
