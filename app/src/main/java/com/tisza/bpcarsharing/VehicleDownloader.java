package com.tisza.bpcarsharing;

import android.os.*;
import com.tisza.bpcarsharing.carsharingservice.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class VehicleDownloader
{
	private final Runnable downloadCarsRunnable = this::downloadCars;
	private final Handler handler;
	private final CarsharingService carsharingService;
	private final VehiclesDownloadedListener vehiclesDownloadedListener;

	private boolean active = false;
	private int downloadInterval;
	private VehicleDownloadAsyncTask currentDownloadTask = null;
	private boolean newDownloadRequestPending = false;

	public VehicleDownloader(Looper looper, CarsharingService carsharingService, int downloadInterval, VehiclesDownloadedListener vehiclesDownloadedListener)
	{
		handler = new Handler(looper);
		this.carsharingService = carsharingService;
		this.downloadInterval = downloadInterval;
		this.vehiclesDownloadedListener = vehiclesDownloadedListener;
	}

	public void setDownloadInterval(int downloadInterval)
	{
		this.downloadInterval = downloadInterval;
	}

	public void start()
	{
		if (active)
			return;

		active = true;
		downloadCars();
	}

	public void stop()
	{
		if (!active)
			return;

		active = false;

		if (currentDownloadTask != null)
			currentDownloadTask.cancel(true);

		vehiclesDownloadedListener.onVehiclesDowloaded(Collections.EMPTY_LIST);
		newDownloadRequestPending = false;
		handler.removeCallbacks(downloadCarsRunnable);
	}

	private void downloadCars()
	{
		handler.removeCallbacks(downloadCarsRunnable);

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
		}

		@Override
		protected Collection<Vehicle> doInBackground(Void... voids)
		{
			try
			{
				return carsharingService.downloadVehicles();
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
			vehiclesDownloadedListener.onVehiclesDowloaded(downloadedVehicles);
			onFinished();
		}

		@Override
		protected void onCancelled()
		{
			onFinished();
		}

		private void onFinished()
		{
			currentDownloadTask = null;
			if (newDownloadRequestPending)
			{
				newDownloadRequestPending = false;
				downloadCars();
			}
		}
	}

	public static interface VehiclesDownloadedListener
	{
		public void onVehiclesDowloaded(Collection<? extends Vehicle> vehicles);
	}
}
