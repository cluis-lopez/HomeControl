package com.clopez.homecontrol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clopez.homecontrol.GlobalVars.ModeOp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Globals {
		
		private GlobalVars gv;
		private String filename;
		private Logger log;
		
		public Globals(float tempDefecto) {
			gv = new GlobalVars(tempDefecto);
		}
		
		/**
		 * @param filename The file where the object is serialized and stored
		 */
		public Globals(String filename, Logger log) {
			this.filename = filename;
			this.log = log;
			RandomAccessFile fd;

			try {
				fd = new RandomAccessFile(filename, "r");
				byte b[] = new byte[ (int) fd.length() ];
				fd.readFully(b);
				fd.close();
				Gson gson = new Gson();
				gv = gson.fromJson(new String(b), new TypeToken<GlobalVars>() {}.getType());
			} catch (FileNotFoundException e) {
				// Si no existe el fichero es la primera vez que arranca el programa o hay un error catastrofico
				log.log(Level.SEVERE, "No se puede abrir el fichero GLOBALS");
				log.log(Level.SEVERE, e.toString(), e);
			}
			catch (IOException e) {
				log.log(Level.SEVERE, "Error de entrada/salida al abrir el fichero GLBALS");
				log.log(Level.SEVERE, e.toString(), e);
			}
		}
		
		/**
		 * @param filename The file where the global variables are saved
		 */
		private void saveGlobals() {
			Gson gson = new Gson();
			String json = gson.toJson(gv);
			try {
				RandomAccessFile fd = new RandomAccessFile(filename, "rw");
				fd.writeBytes(json);
				fd.close();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Error de entrada/salida al escribir en el fichero GLBALS");
				log.log(Level.SEVERE, e.toString(), e);
			}
		}
		
		public GlobalVars getGlobals() {
			return gv;
		}
		public int getModeOp() {
			return gv.modeOp;
		}
		
		public float getTempManual() {
			 return gv.tempManual;
		}
		
		public Calendario getCalendario() {
			return gv.calendario;
		}
		
		public void setModeOp(ModeOp mode) {
			gv.modeOp = mode.getValue();
			saveGlobals();
		}
		
		public void setTempManual(float tempManual) {
			gv.tempManual = tempManual;
			saveGlobals();
		}
		
		public void setCalendario(Calendario calendario) {
			gv.calendario = calendario;
			saveGlobals();
		}
}