package com.tisza.bpcarsharing;

import com.google.android.gms.maps.model.*;

public enum VehicleCategory
{
	GREENGO(BitmapDescriptorFactory.HUE_GREEN, 110, R.id.greengo_switch),
	MOL_LIMO_UP(BitmapDescriptorFactory.HUE_BLUE, 350, R.id.mollimo_up_switch),
	MOL_LIMO_EUP(BitmapDescriptorFactory.HUE_AZURE - 20, 150, R.id.mollimo_eup_switch),
	BLINKEE(BitmapDescriptorFactory.HUE_ORANGE, 70, R.id.blinkee_swicth),
	;

	private final float hue;
	private final float maxRange;
	private final int switchID;

	VehicleCategory(float hue, float maxRange, int switchID)
	{
		this.hue = hue;
		this.maxRange = maxRange;
		this.switchID = switchID;
	}

	public float getHue()
	{
		return hue;
	}

	public float getMaxRange()
	{
		return maxRange;
	}

	public int getSwitchID()
	{
		return switchID;
	}
}
