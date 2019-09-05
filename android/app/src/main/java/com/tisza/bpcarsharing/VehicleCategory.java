package com.tisza.bpcarsharing;

import com.google.android.gms.maps.model.*;

public enum VehicleCategory
{
	GREENGO(CarsharingService.GREENGO, BitmapDescriptorFactory.HUE_GREEN, Fuel.ELECTRICITY),
	MOL_LIMO_UP(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_BLUE, Fuel.PETROL),
	MOL_LIMO_EUP(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_AZURE - 15, Fuel.ELECTRICITY),
	MOL_LIMO_SMART2(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_AZURE - 30, Fuel.ELECTRICITY),
	MOL_LIMO_SMART4(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_AZURE - 15, Fuel.ELECTRICITY),
	MOL_LIMO_MERCEDES(CarsharingService.MOLLIMO, BitmapDescriptorFactory.HUE_VIOLET, Fuel.PETROL),
	BLINKEE(CarsharingService.BLINKEE, BitmapDescriptorFactory.HUE_ORANGE, Fuel.ELECTRICITY),
	LIME_S(CarsharingService.LIME, BitmapDescriptorFactory.HUE_YELLOW, Fuel.ELECTRICITY),
	OGRE_CO(CarsharingService.OGRE_CO, BitmapDescriptorFactory.HUE_VIOLET + 20, Fuel.ELECTRICITY),
	;

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
					case "Up": return VehicleCategory.MOL_LIMO_UP;
					case "eUp": return VehicleCategory.MOL_LIMO_EUP;
					case "Smart 2": return VehicleCategory.MOL_LIMO_SMART2;
					case "Smart 4": return VehicleCategory.MOL_LIMO_SMART4;
					case "Mercedes": return VehicleCategory.MOL_LIMO_MERCEDES;
					default: return null;
				}
			case BLINKEE: return VehicleCategory.BLINKEE;
			case LIME: return VehicleCategory.LIME_S;
			case OGRE_CO: return VehicleCategory.OGRE_CO;
			default: return null;
		}
	}
}
