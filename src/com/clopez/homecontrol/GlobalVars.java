package com.clopez.homecontrol;

public class GlobalVars {
	
	public enum ModeOp{
		APAGADO(0), MANUAL(1), PROGRAMADO(2);
		ModeOp (int v){
		 value = v;
		}
		 private int value;
		 public int getValue() {
		    return value;
		  }
		}

	public int modeOp;
	public float tempManual;
	public Calendario calendario;
	
	public GlobalVars (float tempDefecto) {
		calendario = new Calendario(tempDefecto);
		modeOp = ModeOp.APAGADO.getValue();
		tempManual = 0f;
	}
}