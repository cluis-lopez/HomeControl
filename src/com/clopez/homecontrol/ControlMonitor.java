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
		float tempTarget=9999;
		String CalderaIP = v.get("CalderaIP");
		
		while (true) {
			/* Control starts here */
			estado = Caldera.Estado(CalderaIP);
			currentTemp = sensor.getTempHum(Integer.parseInt(v.get("SensorPIN")))[0];
			currentHum = sensor.getTempHum(Integer.parseInt(v.get("SensorPIN")))[1];
			if (globals.modeOp != ModeOp.APAGADO.ordinal()) { // El modo es MANUAL o PROGRAMADO
				if (globals.modeOp == ModeOp.MANUAL.ordinal())
					tempTarget = globals.tempManual;
				if (globals.modeOp == ModeOp.PROGRAMADO.ordinal())
					tempTarget = globals.calendario.getTempTargetNow();
				if (currentTemp < tempTarget) {// Hay que encender la caldera si no lo está ya
					if (! estado) // Si la caldera esta apagada, la encendemos
						Caldera.ActuaCaldera(CalderaIP, true);
				} else { // La tempratura medida es igual o mayor que la deseada
					if (estado) // La caldera está encendida
						Caldera.ActuaCaldera(CalderaIP, false); // Se apaga la caldera	
				}
			} else { // El modo es apagado, comprobamos que la caldera esté apagada
				if (estado) // La caldera está encendida
					Caldera.ActuaCaldera(CalderaIP, false);
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
