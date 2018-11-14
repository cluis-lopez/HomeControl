package com.clopez.homecontrol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Globals {

		public enum ModeOp{APAGADO, MANUAL, PROGRAMADO};
		int modeOp;
		Double tempManual;
		Calendario calendario;
		
		List<Object> globals;
		
		public Globals() {
			calendario = new Calendario();
			modeOp = ModeOp.APAGADO.ordinal();
			tempManual = 0.0;
			globals = new ArrayList<Object>();
			globals.add(modeOp);
			globals.add(tempManual);
			globals.add(calendario);
		}
		
		/**
		 * @param filename The file where the object is serialized and stored
		 */
		public Globals(String filename) {
			RandomAccessFile fd;
			try {
				fd = new RandomAccessFile(filename, "r");
				byte b[] = new byte[ (int) fd.length() ];
				fd.readFully(b);
				fd.close();
				Gson gson = new Gson();
				globals = gson.fromJson(new String(b), new TypeToken<ArrayList<Object>>() {}.getType());
			} catch (FileNotFoundException e) {
				System.out.println("No existe el fichero GLOBALS");
			} catch (IOException e) {
				System.err.println("Error de entrada salida");
				e.printStackTrace();
			} 
		}
		
		/**
		 * @param filename The file where the global variables are saved
		 */
		public void saveGlobals(String filename) {
			Gson gson = new Gson();
			String json = gson.toJson(globals);
			try {
				RandomAccessFile fd = new RandomAccessFile(filename, "rw");
				fd.writeBytes(json);
				fd.close();
			} catch (IOException e) {
				System.err.println("No se pueden volcar los datos en el fichero");
				e.printStackTrace();
			}
		}
		
}
