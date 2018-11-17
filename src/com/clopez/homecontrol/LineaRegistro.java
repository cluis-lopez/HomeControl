package com.clopez.homecontrol;

public class LineaRegistro {
	int modeOp;
	float tempActual;
	float humActual;
	float tempTarget;
	boolean estadoCaldera;
	
	
	public LineaRegistro(int modeOp, float tempActual, float humActual, float tempTarget, boolean estadoCaldera) {
		this.modeOp = modeOp;
		this.tempActual = tempActual;
		this.humActual = humActual;
		this.tempTarget = tempTarget;
		this.estadoCaldera = estadoCaldera;
	}
	
}
