package com.clopez.homecontrol;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.clopez.homecontrol.GlobalVars.ModeOp;

public class fakeHistorico {
	static Logger log = Logger.getLogger(ControlMonitor.class.getName());
	static FileHandler fd;
	final static int LINEAS = 100; //Numero de lineas a escribir

	public static void main(String[] args) {
		String path = args[0] + "/";
		System.out.println("Path : " + path );
		InputStream in = null;
		try {
			in = new FileInputStream(path+"WEB-INF/Properties");
		} catch (FileNotFoundException e) {
			System.err.println("Error de entrada/salida al abrir el fichero de Propiedades");
			System.err.println(e.toString());
		}
		variablesExternas v = new variablesExternas(in, log);
		// Los segundos entre cada linea segun el ficero Properties
		int inter = Integer.parseInt(v.get("Intervalo")) * Integer.parseInt(v.get("numIntervalos"));
		
		LocalDateTime ts = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		//Registro reg = new Registro(path+"WEB-INF/Historico.log", Integer.parseInt(v.get("NumIntervalos")), log);
		
		String[] lineas = new String[LINEAS];
		
		for (int i = 1; i <= LINEAS; i++) {
			LocalDateTime ts2 = ts.minusMinutes(i * (long) inter);
			int h = ts2.getHour();
			int modeOp;
			float tempTarget;
			int caldera = 0;
			if ((h >= 7 && h<=9) || h>=19 && h<= 23) {
				modeOp = ModeOp.MANUAL.getValue();
				tempTarget = 21f;
				caldera = 1;
			} else {
				modeOp = ModeOp.APAGADO.getValue();
				tempTarget = 9999f;
				caldera = 0;
			}
			//reg.add(modeOp, 20f, 45f, tempTarget, estadoCaldera);
			float temp = 20f;
			float hum = 45f;
			String s = dtf.format(ts2) + " Temp:" + String.format("%.1f", temp) + " Hum:" + String.format("%.2f", hum) + " TObjetivo:" + String.format("%.1f", tempTarget) + " Caldera:" + caldera;
			lineas[LINEAS-i] = s;

		}
		
		// Write the lineas into the file
		FileWriter f;
		try {
			f = new FileWriter(path+"WEB-INF/Historico.log", true);
			BufferedWriter fd = new BufferedWriter(f);
			for (String s: lineas) {
				fd.write(s);
				fd.newLine();
			}
			fd.flush();
			fd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
