package com.clopez.homecontrol;

import com.clopez.raspi.Caldera;

public class ControlMonitor {

	public static void main(String[] args) {
		
		Globals globals = new Globals("GLOBALS");
		variablesExternas v = new variablesExternas("Properties"); // Fichero con variables externas del programa (IP Adresses, etc.)
		Registro reg = new Registro("LOG"); // Fichero historico
		
		while (true) {
			/* Control starts here */
			boolean estado = Caldera.Estado(v.get("CalderaIP"));
			
			/* Monitor starts here */
			try {
				Thread.sleep(5*60*1000);
			} catch (InterruptedException e) {
				System.err.println("Interrupted");
				e.printStackTrace();
				break;
			}
		}

	}

}
