package com.clopez.homecontrol;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Historico {

	LineaRegistro[] lineas;
	String filename;
	Logger log;
	
	public Historico(String filename, Logger log){
		this.filename = filename;
		this.log = log;
	}
	
	public ArrayList<String> leeLastLineas(int numlines) {
		ArrayList<String> lineas = new ArrayList<String>();
		RandomAccessFile fd = null;
	    try {
	        fd = new RandomAccessFile( filename, "r" );
	        long fileLength = fd.length() - 1;
	        StringBuilder sb = new StringBuilder();
	        int line = 0;
	        
	        for (long punt = fileLength; punt != -1; punt --) {
	        	fd.seek(punt);
	        	int rbyte = fd.readByte();
	        	
	             if( rbyte == 0xA ) {
	                 if (punt < fileLength) {
	                     line = line + 1;
	                 }
	             } else if( rbyte == 0xD ) {
	                 if (punt < fileLength-1) {
	                     line = line + 1;
	                 }
	             }
	             if (line >= numlines) {
	                 break;
	             }
	             sb.append((char) rbyte);
	             sb.
	        }
	        
	        
	    } catch (IOException e) {
			log.log(Level.SEVERE, "Error de entrada/salida al abrir el fichero de logs Historico");
			log.log(Level.SEVERE, e.toString(), e);
	    }
		return lineas;
	}
	
	public ArrayList<String> leeRangoLineas(LocalDateTime start, LocalDateTime end) {
		ArrayList<String> lineas = new ArrayList<String>();
		String temp;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while((temp = br.readLine()) != null) {
				String tokens[] = temp.split(" ");
				LocalDateTime dtlinea = LocalDateTime.parse(tokens[0]+" "+tokens[1], DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"));
				if ( dtlinea.isAfter(start) && dtlinea.isBefore(end) ) 
					lineas.add(temp);
			}
			br.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error de entrada/salida al abrir el fichero de logs Historico");
			log.log(Level.SEVERE, e.toString(), e);
		}
		return lineas;
	}
	
}