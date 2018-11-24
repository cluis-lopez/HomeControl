package com.clopez.homecontrol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.servlet.ServletContext;

import com.clopez.homecontrol.GlobalVars.ModeOp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Globals {
		
		private GlobalVars gv;
		private String filename;
		private String path;
		
		public Globals(float tempDefecto) {
			gv = new GlobalVars(tempDefecto);
		}
		
		/**
		 * @param filename The file where the object is serialized and stored
		 */
		public Globals(String filename) {
			this.filename = filename;
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
				System.out.println("No existe el fichero GLOBALS");
			}
			catch (IOException e) {
				System.err.println("Error de entrada salida");
				e.printStackTrace();
			}
		}
		
		/**
		 * @param filename The file where the global variables are saved
		 */
		public void saveGlobals() {
			Gson gson = new Gson();
			String json = gson.toJson(gv);
			try {
				RandomAccessFile fd = new RandomAccessFile(filename, "rw");
				fd.writeBytes(json);
				fd.close();
			} catch (IOException e) {
				System.err.println("No se pueden volcar los datos en el fichero");
				e.printStackTrace();
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