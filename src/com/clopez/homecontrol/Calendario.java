package com.clopez.homecontrol;

import java.time.LocalDateTime;

public class Calendario {
	
	private float [][][] dias;
	
	/**
	 * @param Double The default temperature to fill up the calendar
	 */
	public Calendario(float tempDefecto) {
		dias = new float [7][24][2];
		for (int i = 0; i<7; i++)
			for (int j = 0; j<24; j++) {
				dias[i][j][0] = tempDefecto;
				dias[i][j][1] = tempDefecto;
			}
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
	 * @param dia The day of the week (0: Monday, 1: Tuesday ... , 6: Sunday)
	 * @param horainicio The initial hour:minute to program (rounded to previous half hour. Ex: 18:29 will round to 18:00)
	 * @param horafin The last hor:minute to program (rounded to previous half hour. Ex: 18:47 will round to 18:30)
	 * @param temp The temperature to program during the period
	 */
	public void setTempRange(int dia, int horaInicio, int minutoInicio, int horaFin, int minutoFin, float temp) {
		if ((horaInicio > horaFin) || (horaInicio == horaFin && minutoInicio >= minutoFin))
				return;
		int minInicio = minutoInicio/30;
		int minFin = minutoFin/30;
		for (int i = horaInicio; i <= horaFin; i++) {
			if (i == horaInicio && minInicio == 1) {
				dias[dia][i][1] = temp;
				continue;
			} else if (i == horaFin && minFin == 0) {
				dias[dia][i][0] = temp;
				continue;
			}
			dias[dia][i][0] = dias[dia][i][1] = temp;
		}
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
	public float getTempTargetDate(LocalDateTime dt) {
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
	public void printCalendario() {
		String[] diasSemana= {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
		
		for (int i = 0; i<7; i++) {
			System.out.println(diasSemana[i]);
			for (int j=0; j<24; j++) {
				System.out.println(j+":00 "+j+":29 => "+dias[i][j][0]+"      "+j+":30 "+j+":59 => "+dias[i][j][1]);
			}
		}
	}
	
}