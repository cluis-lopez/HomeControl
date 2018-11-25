package com.clopez.raspi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clopez.homecontrol.ControlMonitor;
import com.clopez.homecontrol.variablesExternas;

public class Caldera {

	/**
	 * @param calderaIP IP address of the relay controller
	 * @return 0 if both relays are OFF, 1 if both relays are ON, -1 if the controller is unreachable, 2 if there's a conflict (ie. boiler ON and pump OFF)
	 */
	public static int Estado(String calderaIP, Logger log) {
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

			if (i != -1 && j != -1) {
				if (content.substring(i+18, i+20).equals("ON") && content.substring(j+16, j+18).equals("ON"))
					return 1;
				else if (content.substring(i+18, i+20).equals("OF") && content.substring(j+16, j+18).equals("OF"))
					return 0;
				else
					return 2;
			}
			in.close();
		} catch (IOException e){
			log.log(Level.SEVERE, "Imposible conectarse a la caldera");
			log.log( Level.SEVERE, e.toString(), e );
			return -1;
		}
		return -1;
	}

	public static int[] ActuaCaldera (String calderaIP, String estado, Logger log) {
		int[] result = {0, 0};
		
		log.log(Level.INFO, "Actuando sobre la caldera. Vamos a {0}", (estado =="on" ? "encender" : "apagar"));
		try {
			URL url = new URL("http://"+calderaIP+"/1/"+estado);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			result[0] =  con.getResponseCode();
		} catch (IOException e){
			log.log(Level.SEVERE, "Imposible conectarse a la caldera");
			log.log( Level.SEVERE, e.toString(), e );
		}
		
		try {
			URL url = new URL("http://"+calderaIP+"/2/"+estado);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			result[1] =  con.getResponseCode();
		} catch (IOException e){
			log.log(Level.SEVERE, "Imposible conectarse a la caldera");
			log.log( Level.SEVERE, e.toString(), e );
		}
		return result;
	}
	
	public static void main (String[] args) { // For debugging purposes
		Logger log = Logger.getLogger(ControlMonitor.class.getName());
		InputStream in = null;
		try {
			in = new FileInputStream("WebContent/WEB-INF/Properties");
		} catch (FileNotFoundException e) {
			System.err.println("No puedo abrir el fichero de propiedades");
			e.printStackTrace();
		}
		variablesExternas v = new variablesExternas(in, log);
		int est = Estado(v.get("CalderaIP"), log);
		String estado = "";
		switch (est) {
		case 0:
			estado = "off";
			break;
		case 1:
			estado = "on";
			break;
		case 2:
			estado = "WARNING";
			break;
		case -1:
			estado ="CONEXION";
			break;
	}
		String accion="";
		Scanner sc = new Scanner(System.in);
		System.out.println("El estado de la caldera es : " + estado);
		System.out.println("Pulsa para cabiar el estado o CRTL-C para salir");
		sc.nextLine();
		if (est == 0)
			accion = "on";
		if (est == 1)
			accion = "off";
			
		ActuaCaldera(v.get("CalderaIP"), accion, log);
		sc.close();
	}
}
