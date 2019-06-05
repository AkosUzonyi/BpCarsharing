package com.tisza.bpcarsharing;

import android.view.*;

public class ProgressBarHandler
{
	private final View progressBar;
	private int processes = 0;

	public ProgressBarHandler(View progressBar)
	{
		this.progressBar = progressBar;
	}

	private void updateProgressBarVisibility()
	{
		progressBar.setVisibility(processes == 0 ? View.GONE : View.VISIBLE);
	}

	public void startProcess()
	{
		processes++;
		updateProgressBarVisibility();
	}

	public void endProcess()
	{
		processes--;
		updateProgressBarVisibility();
	}
}
