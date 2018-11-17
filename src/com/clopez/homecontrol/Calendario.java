package com.clopez.homecontrol;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Calendario {
	
	private float [][][] dias;
	
	public Calendario() {
		dias = new float [7][24][2]; // 7 días, 24 horas, 2 tramos de media hora
	}
	
	/**
	 * @param Double The default temperature to fill up the calendar
	 */
	public Calendario(float tempDefecto) {
		new Calendario();
		for (int i = 0; i<7; i++)
			for (int j = 0; j<24; j++) {
				dias[i][j][0] = dias[i][j][1] = tempDefecto;
			}
	}
	
	public float [][][] getDias() {
		return dias;
	}
	
	/**
	 * @param dia The day of the week (0: Monday, 1: Tuesday ... , 6: Sunday)
	 * @param horainicio The initial hour:minute to program (rounded to previous half hour. Ex: 18:29 will round to 18:00)
	 * @param horafin The last hor:minute to program (rounded to previous half hour. Ex: 18:47 will round to 18:30)
	 * @param temp The temperature to program during the period
	 */
	public void setTemp(int dia, LocalTime horainicio, LocalTime horafin, float temp) {
		int inicioHora = horainicio.getHour();
		int inicioMin = horainicio.getMinute()/30; //inicioMin = 0 o 1
		int finHora = horafin.getHour();
		int finMin = horafin.getMinute()/30; //finMin = 0 o 1
		for (int i = inicioHora; i < finHora; i++) {
			// Revisar esto no está bien y quizá haya que usar otro modelo de datos más simple de manipular
			dias[dia][i][0] = dias[dia][i][1] = temp;
		}
	}
	
	/**
	 * @return The temperature programmed for the current day of the week at the current time
	 */
	public float getTempTargetNow() {
		LocalDateTime now = LocalDateTime.now(); //La fecha actual
		int diasemana = now.getDayOfWeek().getValue() - 1; //El dia de la semana actual Lunes = 0, Martes = 1, ... Domingo = 6
		int hora = now.getHour(); // Hora entre 0 y 23
		int minuto = now.getMinute();
		int min = minuto/30; // min=0 if 0<= minuto <29 or min=1 if 30<=minuto<60
		return dias[diasemana][hora][min];
	}
}
