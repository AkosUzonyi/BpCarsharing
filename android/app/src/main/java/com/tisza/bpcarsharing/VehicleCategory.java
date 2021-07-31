package com.tisza.bpcarsharing;

import android.util.*;
import com.google.android.gms.maps.model.*;

public enum VehicleCategory
{
	GREENGO(CarsharingService.GREENGO, BitmapDescriptorFactory.HUE_GREEN, Fuel.ELECTRICITY),
	MOL_LIMO_PETROL(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_BLUE, Fuel.PETROL),
	MOL_LIMO_ELECTRIC(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_AZURE - 15, Fuel.ELECTRICITY),
	BLINKEE(CarsharingService.BLINKEE, BitmapDescriptorFactory.HUE_ORANGE, Fuel.ELECTRICITY),
	OGRE_CO(CarsharingService.OGRE_CO, BitmapDescriptorFactory.HUE_VIOLET + 20, Fuel.ELECTRICITY),
	;

	private static final String TAG = "VehicleCategory";

	private final CarsharingService carsharingService;
	private final float hue;
	private final Fuel fuel;

	VehicleCategory(CarsharingService carsharingService, float hue, Fuel fuel)
	{
		this.carsharingService = carsharingService;
		this.hue = hue;
		this.fuel = fuel;
	}

	public CarsharingService getCarsharingService()
	{
		return carsharingService;
	}

	public float getHue()
	{
		return hue;
	}

	public Fuel getFuel()
	{
		return fuel;
	}

	public static VehicleCategory fromModel(CarsharingService carsharingService, String model)
	{
		switch (carsharingService)
		{
			case GREENGO: return VehicleCategory.GREENGO;
			case MOLLIMO:
				switch (model)
				{
					case "Up":
					case "Kia Picanto":
					case "Fiat 500":
					case "Hyundai Kona":
					case "Mercedes A":
					case "Mercedes CLA":
					case "Mercedes CLA SB":
						return VehicleCategory.MOL_LIMO_PETROL;
					case "eUp":
					case "Smart 2":
					case "BMW i3":
						return VehicleCategory.MOL_LIMO_ELECTRIC;
					default:
						Log.w(TAG, "unknown mol limo vehicle: " + model);
						return MOL_LIMO_PETROL;
				}
			case BLINKEE: return VehicleCategory.BLINKEE;
			case OGRE_CO: return VehicleCategory.OGRE_CO;
			default:
				Log.w(TAG, "unknown carsharing service: " + carsharingService);
				return null;
		}
	}
}
