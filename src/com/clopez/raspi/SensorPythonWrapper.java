package com.clopez.raspi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SensorPythonWrapper {

	public static float[] sensor(String path, String PIN) {
		float[] tempHum = new float[2];
		try {
			System.out
					.println("Se va a ejecutar: " + "/usr/bin/python " + path + "WEB-INF/Python/DHT11Sensor.py " + PIN);
			Process p = Runtime.getRuntime().exec("/usr/bin/python " + path + "WEB-INF/Python/DHT11Sensor.py " + PIN);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String ret = in.readLine();
			in.close();
			System.out.println("Salida del python: " + ret);
			tempHum[0] = Float.valueOf((ret.substring(0, ret.indexOf(':'))));
			tempHum[1] = Float.valueOf((ret.substring(ret.indexOf(':') + 1)));

			String line = "";
			while ((line = err.readLine()) != null) {
				System.out.println("[Stderr] " + line);
			}
			err.close();
		} catch (IOException e) {
			System.err.println("Error al lanzar el subproceso Python");
			e.printStackTrace();
		}

		return tempHum;
	}
}
