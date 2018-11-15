package com.clopez.homecontrol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Registro {
	
	LineaRegistro[] lineas;
	int contador;
	String fileName;
	
	public Registro(String filename) {
		lineas = new LineaRegistro[6];
		contador = 0;
		this.fileName = filename;
	}
	
	/**
	 * @param modeOp The operational mode: APAGADO (Off), MANUAL or  PROGRAMADO (programmed through calendar)
	 * @param tempActual The current meassured temperature
	 * @param tempTarget The target temperature set in MANUAL mode or comming from the program (PROGRAMADO mode)
	 * @param estadoCaldera ON if the relays are ON, OFF otherwise
	 */
	public void add(int modeOp, float tempActual, float tempTarget, boolean estadoCaldera) {
		if (contador <6) {
			lineas[contador] = new LineaRegistro(modeOp, tempActual, tempTarget, estadoCaldera);
			contador++;
		} else {
			contador = 0;
			this.save(average(lineas));
		}
	}
	
	/**
	 * @param temp Temporary array of LineasRegistro
	 * @return A formatted String with the creation date and the average values fore the metered tempt., the target tempt.
	 *  and the state of the boiler (ON/OFF)
	 */
	private String average(LineaRegistro[] temp) {
		float t1 = 0;
		float t2 = 0;
		boolean caldera = false;
		for (LineaRegistro t: temp) {
			t1 = t1 + t.tempActual;
			t2 = t2 + t.tempTarget;
			caldera = caldera || t.estadoCaldera;
		}
		t1 = t1/temp.length;
		t2 = t2/temp.length;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY MM HH mm");
		LocalTime d = LocalTime.now();
		return d.format(dtf) + " : " + String.format("%.1f", t1) + " : " + String.format("%.1f", t2) + " : " + String.valueOf(caldera);
	}
	
	private void save(String linea) {
		try {
			File f = new File(fileName);
		    long fileLength = f.length();
			RandomAccessFile fd = new RandomAccessFile(fileName, "w");
			fd.seek(fileLength);
			fd.writeBytes(linea);
			fd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
