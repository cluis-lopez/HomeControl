package com.clopez.tests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.clopez.homecontrol.Historico;

public class TestHistorico2 {
	private static final Logger log = Logger.getLogger(TestRegistro.class.getName());

	public static void main(String[] args) {
		Historico h = new Historico("Historico.log", log);
		List<String> lineas = new ArrayList<String>();
		LocalDateTime start = LocalDateTime.parse("2018-11-27 15:31:15", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime end = LocalDateTime.parse("2018-11-27 15:31:34", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		lineas = h.leeRangoLineas(start, end);
		
		for (int i=0; i<lineas.size(); i++) {
			System.out.println(lineas.get(i));
		}

	}

}
