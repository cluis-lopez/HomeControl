package com.clopez.raspi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SensorPythonWrapper {

	public static float[] sensor(String path, String PIN, Logger log) {
		float[] tempHum = new float[2];
		try {
			Process p = Runtime.getRuntime().exec("/usr/bin/python " + path + "WEB-INF/Python/DHT11Sensor.py " + PIN);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String ret = in.readLine();
			in.close();
			log.log(Level.INFO, "Resultado de la ejecución Python [0]", ret);

			String line = "";
			String temp = "";
			while ((temp = err.readLine()) != null) {
				line += temp;
			}
			err.close();
			
			if (line != "")
				log.log(Level.WARNING, "Errores devueltos por el script Python {0}", line);
			
			tempHum[0] = Float.valueOf((ret.substring(0, ret.indexOf(':'))));
			tempHum[1] = Float.valueOf((ret.substring(ret.indexOf(':') + 1)));
			
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error al invocar el script Python");
			log.log( Level.SEVERE, e.toString(), e );
		}

		return tempHum;
	}
}
