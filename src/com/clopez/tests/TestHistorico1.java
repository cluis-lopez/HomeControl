package com.clopez.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.clopez.homecontrol.Historico;

public class TestHistorico1 {
	private static final Logger log = Logger.getLogger(TestRegistro.class.getName());

	public static void main(String[] args) {
		Historico h = new Historico("Historico.log", log);
		List<String> lineas = new ArrayList<String>();
		
		lineas = h.leeLastLineas(5);
		
		for (int i=0; i<lineas.size(); i++) {
			System.out.println(lineas.get(i));
		}

	}

}
