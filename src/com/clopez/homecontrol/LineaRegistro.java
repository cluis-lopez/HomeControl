package com.clopez.homecontrol;

public class LineaRegistro {
	int modeOp;
	float tempActual;
	float humActual;
	float tempTarget;
	int estadoCaldera;
	
	
	public LineaRegistro(int modeOp, float tempActual, float humActual, float tempTarget, int estadoCaldera) {
		this.modeOp = modeOp;
		this.tempActual = tempActual;
		this.humActual = humActual;
		this.tempTarget = tempTarget;
		this.estadoCaldera = estadoCaldera;
	}
	
}
