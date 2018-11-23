package com.clopez.homecontrol;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.clopez.homecontrol.GlobalVars.ModeOp;
import com.clopez.raspi.Caldera;
import com.clopez.raspi.DHT11;
import com.clopez.raspi.SensorPythonWrapper;

public class ControlMonitor {

	public static void main(String[] args) {
		
		InputStream in = null;
		try {
			in = new FileInputStream("WebContent/WEB-INF/Properties");
		} catch (FileNotFoundException e) {
			System.err.println("No puedo abrir el fichero de propiedades");
			e.printStackTrace();
		}
		variablesExternas v = new variablesExternas(in); // Fichero con variables externas del programa (IP Adresses, etc.)
		
		Globals globals = new Globals("WEB-INF/GLOBALS"); // Fichero que serializa el estado de la aplicación
		Registro reg = new Registro("WEB-INF/Historico.log", Integer.parseInt(v.get("numIntervalos"))); // Fichero historico
		//DHT11 sensor = new DHT11();
		
		int estado;
		float[] tempHum = new float[2];;
		float currentTemp, currentHum;
		float tempTarget=9999;
		String CalderaIP = v.get("CalderaIP");
		
		while (true) {
			/* Control starts here */
			estado = Caldera.Estado(CalderaIP);
			tempHum = SensorPythonWrapper.sensor("WEB-INF/Python", v.get("SensorPIN"));
			currentTemp = tempHum[0]; //La temperatura actual
			currentHum = tempHum[1]; // La humedad actual
			
			if (globals.getModeOp() != ModeOp.APAGADO.getValue()) { // El modo es MANUAL o PROGRAMADO
				if (globals.getModeOp() == ModeOp.MANUAL.getValue())
					tempTarget = globals.getTempManual();
				if (globals.getModeOp() == ModeOp.PROGRAMADO.getValue())
					tempTarget = globals.getCalendario().getTempTargetNow();
				
				if (currentTemp < tempTarget) {// Hay que encender la caldera si no lo está ya
					if (estado == 0) // Si la caldera esta apagada, la encendemos
						Caldera.ActuaCaldera(CalderaIP, "on");
				} else { // La tempratura medida es igual o mayor que la deseada
					if (estado == 1) // La caldera está encendida
						Caldera.ActuaCaldera(CalderaIP, "off"); // Se apaga la caldera	
				}
			} else { // El modo es apagado, comprobamos que la caldera esté apagada
				if (estado != 0) // La caldera no está apagada
					Caldera.ActuaCaldera(CalderaIP, "off");
			}
				
			
			/* Monitor starts here */
			
			reg.add(globals.getModeOp(), currentTemp, currentHum, tempTarget, estado);
			
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
