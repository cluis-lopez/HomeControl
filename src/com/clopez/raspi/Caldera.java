package com.clopez.raspi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Caldera {

	public static boolean Estado(String calderaIP) {
		try {
			URL url = new URL("http://"+calderaIP);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		return true;
	}

	public static int ActuaCaldera (String calderaIP, boolean estado) {
		int result = 0;
		try {
			URL url = new URL("http://"+calderaIP+"/1/on");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			result =  con.getResponseCode();
		} catch (IOException e){
			e.printStackTrace();
		}
		return result;
	}
}
