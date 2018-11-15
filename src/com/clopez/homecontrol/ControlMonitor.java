package com.clopez.homecontrol;

public class ControlMonitor {

	public static void main(String[] args) {
		
		Globals globals = new Globals();
		variablesExternas v = new variablesExternas("Properties"); // Fichero con variables externas del programa (IP Adresses, etc.)
		Registro reg = new Registro("LOG"); // Fichero historico
		
		while (true) {
			/* Control starts here */
			
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
