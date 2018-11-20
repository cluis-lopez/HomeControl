package com.clopez.raspi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.clopez.homecontrol.variablesExternas;

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
			// esto es muy cutre y habría que cambiar el servidor web en el ESP8266 para hacer algo más simple
			int i = content.indexOf("(Caldera)");
			int j = content.indexOf("(Bomba)");
			//Debug below
			//System.out.println("Caldera  " + content.substring(i+18, i+20));
			//System.out.println("Bomba  " + content.substring(j+16, j+18));
			if (i != -1 && j != -1) {
				if (content.substring(i+18, i+20).equals("ON") && content.substring(j+16, j+18).equals("ON"))
					return true;
			}
			in.close();
		} catch (IOException e){
			System.err.println("No puedo conectar con los reles de la caldera");
			e.printStackTrace();
		}
		return false;
	}

	public static int[] ActuaCaldera (String calderaIP, boolean estado) {
		int[] result = {0, 0};
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
			System.err.println("No puedo conectar con los reles de la caldera");
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main (String[] args) { // For debugging purposes
		variablesExternas v = new variablesExternas("WebContent/WEB-INF/Properties");
		boolean est = Estado(v.get("CalderaIP"));
		Scanner sc = new Scanner(System.in);
		System.out.println("El estado de la caldera es : " + (est ? "ON" : "OFF"));
		System.out.println("Pulsa para cabiar el estado o CRTL-C para salir");
		sc.nextLine();
		ActuaCaldera(v.get("CalderaIP"), !est);
		sc.close();
	}
}
