package com.tisza.bpcarsharing;

import com.google.android.gms.maps.model.*;

public enum VehicleCategory
{
	GREENGO(BitmapDescriptorFactory.HUE_GREEN, 110, Fuel.ELECTRICITY),
	MOL_LIMO_UP(BitmapDescriptorFactory.HUE_BLUE, 350, Fuel.PETROL),
	MOL_LIMO_EUP(BitmapDescriptorFactory.HUE_AZURE - 20, 150, Fuel.ELECTRICITY),
	MOL_LIMO_MERCEDES(BitmapDescriptorFactory.HUE_VIOLET, 350, Fuel.PETROL),
	BLINKEE(BitmapDescriptorFactory.HUE_ORANGE, 70, Fuel.ELECTRICITY),
	;

	private final float hue;
	private final float maxRange;
	private final Fuel fuel;

	VehicleCategory(float hue, float maxRange, Fuel fuel)
	{
		this.hue = hue;
		this.maxRange = maxRange;
		this.fuel = fuel;
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
}
