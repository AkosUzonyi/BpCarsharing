package com.tisza.bpcarsharing.carsharingservice;

import android.content.*;
import android.net.*;

public class NetworkStateReceiver extends BroadcastReceiver
{
	private final NetworkStateListener networkStateListener;

	public NetworkStateReceiver(NetworkStateListener networkStateListener)
	{
		this.networkStateListener = networkStateListener;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		boolean isConnected = networkInfo != null && networkInfo.isConnected();
		networkStateListener.networkConnectionStateChanged(isConnected);
	}

	public static interface NetworkStateListener
	{
		public void networkConnectionStateChanged(boolean isConnected);
	}
}
