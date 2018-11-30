package com.clopez.tests;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.logging.Level;

public class FakeHistorico {
	
		//Genera un fichero Historico.log "fake" para testear
	private static final int numlineas = 100;
	
	public static String Registro(LocalDateTime dt) {
		Random r = new Random();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		float t1 = 15f + (5f * r.nextFloat());
		float h = 25f + (50f * r.nextFloat());
		float t2 = 21f;
		int caldera;
		if (dt.getHour()<23 && dt.getHour()>17)
			caldera = 1;
		else
			caldera = 0;
		
		return dtf.format(dt) + " Temp:" + String.format("%.1f", t1) + " Hum:" + String.format("%.2f", h) + " TObjetivo:" + String.format("%.1f", t2) + " Caldera:" + caldera;
		
	}

	public static void main(String[] args) {
		
		LocalDateTime d = LocalDateTime.now().minusDays(2);
		
		try {
			FileWriter f = new FileWriter("Historico.log", true);
			BufferedWriter fd = new BufferedWriter(f);
			for (int i = 0; i< numlineas; i++) {
				fd.write(Registro(d));
				fd.newLine();
				d = d.plusMinutes(30);
			}
			fd.flush();
			fd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
