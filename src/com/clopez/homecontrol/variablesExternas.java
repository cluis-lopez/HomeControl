package com.clopez.homecontrol;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class variablesExternas {
	Properties props;
	
	public variablesExternas(String filename) {
		props = new Properties();
		BufferedInputStream fd;

		try {
            fd=new BufferedInputStream(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
            System.err.println("Fichero de propiedades no encontrado");
            return;
		}
		try {
            props.load(fd);
		} catch (IOException e) {
            System.err.println("No puedo acceder al fichero de propiedades");
            return;
		}
	}
	
	public String get (String propiedad) {
		return props.getProperty(propiedad);
	}
}
