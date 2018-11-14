package com.clopez.homecontrol;

import java.time.LocalDateTime;

public class Registro {
	LocalDateTime d;
	int modeOp;
	Double tempActual;
	Double tempTarget;
	boolean estadoCaldera;
	
	
	public Registro(int modeOp, Double tempActual, Double tempTarget, boolean estadoCaldera) {
		d = LocalDateTime.now();
		this.modeOp = modeOp;
		this.tempActual = tempActual;
		this.tempTarget = tempTarget;
		this.estadoCaldera = estadoCaldera;
	}
	
}
