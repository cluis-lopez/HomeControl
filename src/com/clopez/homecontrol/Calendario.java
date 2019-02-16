package com.clopez.homecontrol;

import java.time.LocalDateTime;

public class Calendario {
	
	private float [][][] dias;
	
	/**
	 * @param float The default temperature to fill up the calendar
	 */
	public Calendario(float tempDefecto) {
		dias = new float [7][24][2];
		for (int i = 0; i<7; i++)
			for (int j = 0; j<24; j++) {
				dias[i][j][0] = tempDefecto;
				dias[i][j][1] = tempDefecto;
			}
	}
	
	public Calendario(float dias[][][]) {
		this.dias = dias;
	}
	
	public float [][][] getDias() {
		return dias;
	}
	
	/**
	 * @param dia The day of the week (0: Monday, 1: Tuesday ... , 6: Sunday)
	 * @param hora The hour to set the temperature (0 .. 24)
	 * @param minuto The minute to set the temp
	 * @param temp The temperature to program during the period
	 */
	public void setTemp(int dia, int hora, int minuto, float temp) {
		int min = minuto/30;// min = 0 o 1
		dias[dia][hora][min] = temp;
	}
	
	/**
	 * @return The temperature programmed for the current day of the week at the current time
	 */
	public float getTempTargetNow() {
		LocalDateTime now = LocalDateTime.now(); //La fecha actual
		return getTempTargetDate(now);
	}
	
	/**
	 * @param dt The date and time 
	 * @return The programmed temperature for that date and time
	 */
	public float getTempTargetDate(LocalDateTime dt) { // Ojo posible bug. If Domingo = 0, diasemana = -1
		int diasemana = dt.getDayOfWeek().getValue() - 1; //El dia de la semana actual Lunes = 0, Martes = 1, ... Domingo = 6
		int hora = dt.getHour(); // Hora entre 0 y 23
		int minuto = dt.getMinute();
		int min = minuto/30; // min=0 if 0<= minuto <29 or min=1 if 30<=minuto<60
		return dias[diasemana][hora][min];
	}
	
	public float getTempTarget(int diasemana, int hora, int minuto) {
		int min = minuto/30; // min=0 if 0<= minuto <29 or min=1 if 30<=minuto<60
		return dias[diasemana][hora][min];
	}
	
	/**
	 *  Just printout the calendar for debugging purposes
	 */
	public String printCalendario() {
		String[] diasSemana= {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
		String ret = "";
		
		for (int i = 0; i<7; i++) {
			ret = ret + diasSemana[i] + "\n";
			for (int j=0; j<24; j++) {
				ret = ret + (j+":00 "+j+":30 => "+dias[i][j][0]+"      "+j+":30 "+j+":59 => "+dias[i][j][1]) + "\n";
			}
		}
		return ret;
	}
	
}