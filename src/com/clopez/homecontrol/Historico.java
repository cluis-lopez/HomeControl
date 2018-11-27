package com.clopez.homecontrol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Historico {

	String filename;
	Logger log;
	
	public Historico(String filename, Logger log){
		this.filename = filename;
		this.log = log;
	}
	
	public List<String> leeLastLineas(int numlines) {
		List<String> lineas = new ArrayList<String>();
		RandomAccessFile fd = null;
	    try {
	        fd = new RandomAccessFile( filename, "r" );
	        long fileLength = fd.length() - 1;
	        StringBuilder sb = new StringBuilder();
	        int line = 0;
	        long p = fileLength;
	        long tp = p;
	        
			while (line <= numlines) {
				for (tp = p; tp != -1; tp--) {
					fd.seek(tp);
					int rbyte = fd.readByte();

					if (rbyte == 0xA && tp < fileLength) {
							line = line + 1;
							lineas.add(sb.reverse().toString());
							break;
					} else if (rbyte == 0xD && tp < fileLength-1) {
							line = line + 1;
							lineas.add(sb.reverse().toString());
							break;
					}
					sb.append((char) rbyte);
				}
			}
	        
	        
	    } catch (IOException e) {
			log.log(Level.SEVERE, "Error de entrada/salida al abrir el fichero de logs Historico");
			log.log(Level.SEVERE, e.toString(), e);
	    }
		return lineas;
	}
	
	public List<String> leeRangoLineas(LocalDateTime start, LocalDateTime end) {
		List<String> lineas = new ArrayList<String>();
		String temp;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while((temp = br.readLine()) != null) {
				String tokens[] = temp.split(" ");
				LocalDateTime dtlinea = LocalDateTime.parse(tokens[0]+" "+tokens[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
