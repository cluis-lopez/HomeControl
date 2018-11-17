package com.clopez.homecontrol;

import com.clopez.homecontrol.Globals.ModeOp;
import com.clopez.raspi.Caldera;
import com.clopez.raspi.DHT11;

public class ControlMonitor {

	public static void main(String[] args) {
		
		Globals globals = new Globals("GLOBALS"); // Fichero que serializa el estado de la aplicación
		variablesExternas v = new variablesExternas("Properties"); // Fichero con variables externas del programa (IP Adresses, etc.)
		Registro reg = new Registro("Historico.log", Integer.parseInt(v.get("numIntervalos"))); // Fichero historico
		DHT11 sensor = new DHT11();
		
		boolean estado;
		float currentTemp, currentHum;
		
		while (true) {
			/* Control starts here */
			estado = Caldera.Estado(v.get("CalderaIP"));
			currentTemp = sensor.getTempHum(Integer.parseInt(v.get("SensorPIN")))[0];
			currentHum = sensor.getTempHum(Integer.parseInt(v.get("SensorPIN")))[1];
			if (globals.modeOp != ModeOp.APAGADO.ordinal()) {
				if (globals.modeOp == ModeOp.MANUAL.ordinal()) {
					
				}
				
			}
				
			
			/* Monitor starts here */
			try {
				Thread.sleep(60 * 1000 * Integer.parseInt(v.get("Intervalo"))); // Convertimos los minutos en milisegundos
			} catch (InterruptedException e) {
				System.err.println("Interrupted");
				e.printStackTrace();
				break;
			}
		}

	}

}
