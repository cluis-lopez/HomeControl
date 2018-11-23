package com.clopez.homecontrol;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Registro {
	
	LineaRegistro[] lineas;
	int contador;
	String fileName;
	
	public Registro(String filename, int numIntervalos) {
		lineas = new LineaRegistro[numIntervalos];
		contador = 0;
		this.fileName = filename;
	}
	
	/**
	 * @param modeOp The operational mode: APAGADO (Off), MANUAL or  PROGRAMADO (programmed through calendar)
	 * @param tempActual The current meassured temperature
	 * @param humActual The current humedity meassured by the DHT11 sensor
	 * @param tempTarget The target temperature set in MANUAL mode or coming from the program (PROGRAMADO mode)
	 * @param estadoCaldera true if the relays are ON, false otherwise
	 */
	public void add(int modeOp, float tempActual, float humActual, float tempTarget, int estadoCaldera) {
		if (contador == lineas.length) {
			contador = 0;
			this.save(average(lineas));
		}
		lineas[contador] = new LineaRegistro(modeOp, tempActual, humActual, tempTarget, estadoCaldera);
		contador++;
	}

	/**
	 * @param temp Temporary array of LineasRegistro
	 * @return A formatted String with the creation date and the average values fore the metered tempt., the target tempt.
	 *  and the state of the boiler (ON/OFF)
	 */
	private String average(LineaRegistro[] temp) {
		float t1 = 0;
		float t2 = 0;
		float h = 0;
		int caldera = 0; // Por defecto, asumimos que la caldera esta apagada
		for (LineaRegistro t: temp) {
			t1 = t1 + t.tempActual;
			t2 = t2 + t.tempTarget;
			h = h + t.humActual;
			if (caldera !=0)
				caldera = t.estadoCaldera;
		}
		t1 = t1/temp.length;
		t2 = t2/temp.length;
		h = h/temp.length;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd HH-mm-ss");
		return dtf.format(LocalDateTime.now()) + " : " + String.format("%.1f", t1) + " : " + String.format("%.2f%%", h) + " : " + String.format("%.1f", t2) + " : " + caldera;
	}
	
	private void save(String linea) {
		try {
			FileWriter f = new FileWriter(fileName, true);
			BufferedWriter fd = new BufferedWriter(f);
			fd.write(linea);
			fd.newLine();
			fd.flush();
			fd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}