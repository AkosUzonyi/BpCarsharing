package com.tisza.bpcarsharing;

import com.google.android.gms.maps.model.*;

public enum VehicleCategory
{
	GREENGO(BitmapDescriptorFactory.HUE_GREEN, 110),
	MOL_LIMO_UP(BitmapDescriptorFactory.HUE_BLUE, 350),
	MOL_LIMO_EUP(BitmapDescriptorFactory.HUE_AZURE - 20, 150),
	MOL_LIMO_MERCEDES(BitmapDescriptorFactory.HUE_VIOLET, 350),
	BLINKEE(BitmapDescriptorFactory.HUE_ORANGE, 70),
	;

	private final float hue;
	private final float maxRange;

	VehicleCategory(float hue, float maxRange)
	{
		this.hue = hue;
		this.maxRange = maxRange;
	}

	public float getHue()
	{
		return hue;
	}

	public float getMaxRange()
	{
		return maxRange;
	}
}
