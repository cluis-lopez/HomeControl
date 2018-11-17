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
			// Chequear el contenido para saber si los relés están activados o no
			in.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		return true;
	}

	public static int[] ActuaCaldera (String calderaIP, boolean estado) {
		int[] result = { 0, 0};
		String accion ="";
		
		if (estado) {
			accion = "on";
		} else {
			accion = "off";
		}
		
		try {
			URL url = new URL("http://"+calderaIP+"/1/"+accion);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			result[0] =  con.getResponseCode();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		try {
			URL url = new URL("http://"+calderaIP+"/2/"+accion);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			result[1] =  con.getResponseCode();
		} catch (IOException e){
			e.printStackTrace();
		}
		return result;
	}
}
