package com.tisza.bpcarsharing;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.json.*;

import java.io.*;
import java.net.*;

public class Utils
{
	public static String downloadText(String urlString) throws IOException
	{
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
		urlConnection.setRequestProperty("Referer", url.getProtocol() + "://" + url.getAuthority() + "/");
		urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		urlConnection.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		StringBuffer response = new StringBuffer();

		String inputLine;
		while ((inputLine = in.readLine()) != null)
		{
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}
}
