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

	private String filename;
	private Logger log;
	
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
	        int line = 0;
	        long p = fileLength;
	        long tp = p;
	        
			while (line <= numlines) {
				StringBuilder sb = new StringBuilder();
				for (tp = p; tp != -1; tp--) {
					fd.seek(tp);
					int rbyte = fd.readByte();

					if (rbyte == 0xA) {
							line = line + 1;
							lineas.add(sb.reverse().toString());
							p = tp-1;
							break;
					}
					sb.append((char) rbyte);
				}
			}
	        
	        
	    } catch (IOException e) {
			log.log(Level.SEVERE, "Error de entrada/salida al abrir el fichero de logs Historico");
			log.log(Level.SEVERE, e.toString(), e);
	    }
	    lineas.remove(0);
		return lineas;
	}
	
	/**
	 * @param first
	 * @param last
	 * @return LocalDateTime array with the first date in the Historic.log file and the last date
	 */
	public LocalDateTime[] limits(LocalDateTime first, LocalDateTime last) {
		LocalDateTime[] ret = new LocalDateTime[2];
		List<String> lineas = leeLastLineas(1);
		BufferedReader br;
		
		if (lineas.size() != 1 || first.isAfter(last) || last.isBefore(first)) {
			ret[0] = LocalDateTime.MIN;
			ret[1] = LocalDateTime.MAX;
			return ret;
		}
		try {
			br = new BufferedReader(new FileReader(filename));
			String firstLinea = br.readLine();
			br.close();
			ret[1] = getDateTime(firstLinea);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error de entrada/salida al abrir el fichero de logs Historico");
			log.log(Level.SEVERE, e.toString(), e);
		}
		ret[0] = getDateTime(lineas.get(1));
		return ret;
	}
		
	public List<String> leeRangoLineas(LocalDateTime start, LocalDateTime end) {
		List<String> lineas = new ArrayList<String>();
		String temp;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while((temp = br.readLine()) != null) {
				LocalDateTime dtlinea = getDateTime(temp);
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
	
	private LocalDateTime getDateTime(String linea) {
		String tokens[] = linea.split(" ");
		LocalDateTime dtlinea = LocalDateTime.parse(tokens[0]+" "+tokens[1], DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
		return dtlinea;
	}
	
}
