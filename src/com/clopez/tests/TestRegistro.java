package com.clopez.tests;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clopez.homecontrol.LineaRegistro;
import com.clopez.homecontrol.Registro;

public class TestRegistro {
	private static final Logger log = Logger.getLogger(TestRegistro.class.getName());

	public static void main(String[] args) {
		Registro reg = new Registro("Historico.log", 5, log);
		for (int i = 0; i <= 50; i++) { // Añadimos 50 lineas que generarán 10 lineas en el fichero historico
			Random r = new Random();
			float r1 = 5 * r.nextFloat();
			float r2 = 10 * r.nextFloat();
			reg.add(0, 18f + r1, 45f + r2, 25f, 1);
			try {
				Thread.sleep(1000/2+r.nextInt(1000)/2);
			} catch (InterruptedException e) {
				log.log(Level.SEVERE, "Interruptus");
				log.log(Level.SEVERE, e.toString(), e);
			}
		}

	}

}
