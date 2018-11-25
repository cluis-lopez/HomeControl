package com.clopez.homecontrol;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class variablesExternas {
	Properties props;

	public variablesExternas(InputStream in, Logger log) {
		props = new Properties();
		BufferedInputStream fd;

		fd = new BufferedInputStream(in);

		try {
			props.load(fd);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error de entrada/salida al cargar el fichero de Propiedades");
			log.log(Level.SEVERE, e.toString(), e);
			return;
		}
	}

	public String get(String propiedad) {
		return props.getProperty(propiedad);
	}
}
