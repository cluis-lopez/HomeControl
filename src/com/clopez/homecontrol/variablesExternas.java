package com.clopez.homecontrol;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class variablesExternas {
	Properties props;

	public variablesExternas(InputStream in) {
		props = new Properties();
		BufferedInputStream fd;

		fd = new BufferedInputStream(in);

		try {
			props.load(fd);
		} catch (IOException e) {
			System.err.println("No puedo acceder al fichero de propiedades");
			return;
		}
	}

	public String get(String propiedad) {
		return props.getProperty(propiedad);
	}
}
