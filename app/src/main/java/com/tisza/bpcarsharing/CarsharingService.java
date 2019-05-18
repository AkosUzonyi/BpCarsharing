package com.tisza.bpcarsharing;

import android.graphics.*;

public enum CarsharingService
{
	GREENGO("greengo", Color.GREEN, "com.GreenGo", R.id.greengo_switch),
	MOLLIMO("mollimo", Color.BLUE, "com.vulog.carshare.mol", R.id.mollimo_switch),
	BLINKEE("blinkee", Color.rgb(200, 200, 0), "pl.blinkee.mobile", R.id.blinkee_swicth),
	;

	private final String id;
	private final int color;
	private final String appPackage;
	private final int menuID;

	CarsharingService(String id, int color, String appPackage, int menuID)
	{
		this.id = id;
		this.color = color;
		this.appPackage = appPackage;
		this.menuID = menuID;
	}

	public String getID()
	{
		return id;
	}

	public int getColor()
	{
		return color;
	}

	public String getAppPackage()
	{
		return appPackage;
	}

	public int getMenuID()
	{
		return menuID;
	}

	public static CarsharingService fromString(String id)
	{
		for (CarsharingService carsharingService : CarsharingService.values())
			if (carsharingService.id.equals(id))
				return carsharingService;
		return null;
	}
}
