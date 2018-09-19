package com.tisza.bpcarsharing;

import android.os.*;
import com.tisza.bpcarsharing.carsharingservice.*;

import java.util.*;

public class VehicleDownloader
{
	private final Runnable downloadCarsRunnable = this::downloadCars;
	private final Handler handler;
	private final VehiclesDownloadedListener vehiclesDownloadedListener;
	private int downloadInterval;
	private List<VehicleDownloadAsyncTask> runningDownloadTasks = new ArrayList<>();
	private Set<VehicleCategory> activeVehicleCategories = new HashSet<>(Arrays.asList(VehicleCategory.values()));
	private List<Vehicle> downloadedVehicles = new ArrayList<>();

	public VehicleDownloader(Looper looper, int downloadInterval, VehiclesDownloadedListener vehiclesDownloadedListener)
	{
		handler = new Handler(looper);
		this.downloadInterval = downloadInterval;
		this.vehiclesDownloadedListener = vehiclesDownloadedListener;
	}

	public void setVehicleCategoryActive(VehicleCategory vehicleCategory, boolean active)
	{
		if (active)
			activeVehicleCategories.add(vehicleCategory);
		else
			activeVehicleCategories.remove(vehicleCategory);
	}

	public void setDownloadInterval(int downloadInterval)
	{
		this.downloadInterval = downloadInterval;
	}

	public void start()
	{
		downloadCars();
	}

	public void stop()
	{
		cancelDownloadTasks();
		handler.removeCallbacks(downloadCarsRunnable);
	}

	private void downloadCars()
	{
		handler.removeCallbacks(downloadCarsRunnable);

		if (!runningDownloadTasks.isEmpty())
		{
			cancelDownloadTasks();
		}
		else
		{
			downloadedVehicles.clear();
			for (CarsharingService carsharingService : CarsharingService.CARSHARING_SERVICES)
			{
				if (hasAnyVehicleCategory(carsharingService.getVehicleCategories()))
					new VehicleDownloadAsyncTask(carsharingService).execute();
			}

			if (runningDownloadTasks.isEmpty())
				vehiclesDownloadedListener.onVehiclesDowloaded(Collections.EMPTY_LIST);
		}

		handler.postDelayed(downloadCarsRunnable, downloadInterval * 1000);
	}

	private boolean hasAnyVehicleCategory(Collection<? extends VehicleCategory> vehicleCategories)
	{
		for (VehicleCategory vehicleCategory : vehicleCategories)
		{
			if (activeVehicleCategories.contains(vehicleCategory))
				return true;
		}
		return false;
	}

	private void cancelDownloadTasks()
	{
		for (VehicleDownloadAsyncTask downloadTask : runningDownloadTasks)
			downloadTask.cancel(true);
	}

	private class VehicleDownloadAsyncTask extends AsyncTask<Void, Void, Collection<Vehicle>>
	{
		private final CarsharingService carsharingService;

		public VehicleDownloadAsyncTask(CarsharingService carsharingService)
		{
			this.carsharingService = carsharingService;
		}

		@Override
		protected void onPreExecute()
		{
			runningDownloadTasks.add(this);
		}

		@Override
		protected Collection<Vehicle> doInBackground(Void... voids)
		{
			return carsharingService.downloadVehicles();
		}

		@Override
		protected void onPostExecute(Collection<Vehicle> result)
		{
			for (Vehicle vehicle : result)
				if (activeVehicleCategories.contains(vehicle.getCategory()))
					downloadedVehicles.add(vehicle);

			runningDownloadTasks.remove(this);

			if (runningDownloadTasks.isEmpty())
				vehiclesDownloadedListener.onVehiclesDowloaded(downloadedVehicles);
		}

		@Override
		protected void onCancelled()
		{
			runningDownloadTasks.remove(this);
		}
	}

	public static interface VehiclesDownloadedListener
	{
		public void onVehiclesDowloaded(Collection<? extends Vehicle> vehicles);
	}
}
