package com.tisza.bpcarsharing;

import com.google.android.gms.maps.model.*;

public enum VehicleCategory
{
	GREENGO(CarsharingService.GREENGO, BitmapDescriptorFactory.HUE_GREEN, 110, Fuel.ELECTRICITY),
	MOL_LIMO_UP(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_BLUE, 350, Fuel.PETROL),
	MOL_LIMO_EUP(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_AZURE - 20, 150, Fuel.ELECTRICITY),
	MOL_LIMO_MERCEDES(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_VIOLET, 350, Fuel.PETROL),
	BLINKEE(CarsharingService.BLINKEE, BitmapDescriptorFactory.HUE_ORANGE, 70, Fuel.ELECTRICITY),
	LIME_S(CarsharingService.LIME, BitmapDescriptorFactory.HUE_YELLOW, 24.1F, Fuel.ELECTRICITY),
	;

	private final CarsharingService carsharingService;
	private final float hue;
	private final float maxRange;
	private final Fuel fuel;

	VehicleCategory(CarsharingService carsharingService, float hue, float maxRange, Fuel fuel)
	{
		this.carsharingService = carsharingService;
		this.hue = hue;
		this.maxRange = maxRange;
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

	public float getMaxRange()
	{
		return maxRange;
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
					case "Up": return VehicleCategory.MOL_LIMO_UP;
					case "eUp": return VehicleCategory.MOL_LIMO_EUP;
					case "Mercedes": return VehicleCategory.MOL_LIMO_MERCEDES;
					default: return null;
				}
			case BLINKEE: return VehicleCategory.BLINKEE;
			case LIME: return VehicleCategory.LIME_S;
			default: return null;
		}
	}
}
