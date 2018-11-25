package com.clopez.homecontrol;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.clopez.homecontrol.GlobalVars.ModeOp;
import com.clopez.raspi.Caldera;
import com.clopez.raspi.SensorPythonWrapper;

public class ControlMonitor {
	
	static Logger log = Logger.getLogger(ControlMonitor.class.getName());
	static FileHandler fd;

	public static void main(String[] args) {
		
		try {
			fd = new FileHandler("ControlMonitor.log");
		} catch (SecurityException | IOException e1) {
			System.err.println("No se puede abrir el fichero de log");
			e1.printStackTrace();
		}  
		
        log.addHandler(fd);
        SimpleFormatter formatter = new SimpleFormatter();  
        fd.setFormatter(formatter); 

		String path = args[0] + "/";
		System.out.println("Path : " + path );
		InputStream in = null;
		try {
			in = new FileInputStream(path+"WEB-INF/Properties");
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "Error de entrada/salida al abrir el fichero de Propiedades");
			log.log(Level.SEVERE, e.toString(), e);
		}
		variablesExternas v = new variablesExternas(in, log); // Fichero con variables externas del programa (IP Adresses, etc.)
		
		Globals globals = new Globals(path+"WEB-INF/GLOBALS", log); // Fichero que serializa el estado de la aplicación
		Registro reg = new Registro(path+"WEB-INF/Historico.log", Integer.parseInt(v.get("numIntervalos"))); // Fichero historico
		
		int estado;
		float[] tempHum = new float[2];;
		float currentTemp, currentHum;
		float tempTarget=9999;
		String calderaIP = v.get("CalderaIP");
		
		while (true) { //NOSONAR
			/* Control starts here */
			estado = Caldera.Estado(calderaIP, log);
			tempHum = SensorPythonWrapper.sensor(path, v.get("SensorPIN"), log);
			currentTemp = tempHum[0]; //La temperatura actual
			currentHum = tempHum[1]; // La humedad actual
			
			if (globals.getModeOp() != ModeOp.APAGADO.getValue()) { // El modo es MANUAL o PROGRAMADO
				if (globals.getModeOp() == ModeOp.MANUAL.getValue())
					tempTarget = globals.getTempManual();
				if (globals.getModeOp() == ModeOp.PROGRAMADO.getValue())
					tempTarget = globals.getCalendario().getTempTargetNow();
				
				log.log(Level.INFO, "Modo de operacion "+globals.getModeOp()+" , Temperatura ambiente: "+currentTemp+" ,Temperatura Objetivo: "+tempTarget);
				
				if (currentTemp < tempTarget) {// Hay que encender la caldera si no lo está ya
					if (estado == 0) // Si la caldera esta apagada, la encendemos
						Caldera.ActuaCaldera(calderaIP, "on", log);
				} else { // La tempratura medida es igual o mayor que la deseada
					if (estado == 1) // La caldera está encendida
						Caldera.ActuaCaldera(calderaIP, "off", log); // Se apaga la caldera	
				}
			} else { // El modo es apagado, comprobamos que la caldera esté apagada
				if (estado != 0) // La caldera no está apagada
					Caldera.ActuaCaldera(calderaIP, "off", log);
			}
				
			
			/* Monitor starts here */
			
			reg.add(globals.getModeOp(), currentTemp, currentHum, tempTarget, estado);
			
			try {
				Thread.sleep(60 * 1000 * Integer.parseInt(v.get("Intervalo"))); // Convertimos los minutos en milisegundos
			} catch (InterruptedException e) {
				log.log(Level.SEVERE, "Se ha interrupido la aplicación en el bucle infinito");
				log.log(Level.SEVERE, e.toString(), e);
				break;
			}
		}

	}

}
