package com.soomla.soomlatest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by rimma.sukhovsky on 12/27/2016.
 */
public class Communicator {
	public static final String TAG = "Communicator";

	public static String sendQuery(String address)
	{
		URL url = null;

		try {
			url = new URL(address);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		String response = "";
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			response = readStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return response;
	}

	private static String readStream(InputStream in) {
		BufferedReader rd = new BufferedReader
				(new InputStreamReader(in));

		String line = "";
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
}
