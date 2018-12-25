package com.clopez.homeWebApp;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clopez.homecontrol.GlobalVars.ModeOp;
import com.clopez.homecontrol.Globals;
import com.clopez.homecontrol.Historico;
import com.clopez.homecontrol.variablesExternas;
import com.clopez.raspi.Caldera;
import com.clopez.raspi.SensorPythonWrapper;
import com.google.gson.Gson;

/**
 * Servlet implementation class SeverTest
 */
@WebServlet("/ControlServlet")
public class ControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ControlServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Logger log = Logger.getLogger(ControlServlet.class.getName()); 
		
		InputStream in = getServletContext().getResourceAsStream("/WEB-INF/Properties");
		variablesExternas v = new variablesExternas(in, log);
		Historico hist = new Historico("Historico.log", log);
		String path = getServletContext().getRealPath("/");
		Globals g = new Globals(path+"/WEB-INF/GLOBALS", log);
		log.setLevel(Level.parse(v.get("LogLevel")));
		
		// Lee la temperatura y humedad desde el sensor DHT11
		int estado = Caldera.Estado(v.get("CalderaIP"), log);
		float[] s = SensorPythonWrapper.sensor(path, v.get("SensorPIN"), log);
		float currentTemp = s[0];
		float currentHum = s[1];
		
		/* Para la página de históricos buscamos la primera y última fecha que tenemos en el 
		fichero de Historicos */
		
		LocalDateTime[] limits = hist.limits();

		
		//float currentTemp = 19.2f; // Debug
		//float currentHum = 0.33f; //Debug
		float tempTarget = 9999f;
		
		if (g.getModeOp() != ModeOp.APAGADO.getValue()) { // El modo es MANUAL o PROGRAMADO
			if (g.getModeOp() == ModeOp.MANUAL.getValue())
				tempTarget = g.getTempManual();
			if (g.getModeOp() == ModeOp.PROGRAMADO.getValue())
				tempTarget = g.getCalendario().getTempTargetNow();
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("modeOp", g.getModeOp());
		map.put("currentTemp", currentTemp);
		map.put("currentHum", currentHum);
		map.put("tempTarget", tempTarget);
		map.put("calendario", g.getCalendario());
		map.put("firstDate", limits[0]);
		map.put("lastDate", limits[1]);
		switch (estado) {
			case 0:
				map.put("estado", "OFF");
				break;
			case 1:
				map.put("estado", "ON");
				break;
			case 2:
				map.put("estado", "WARNING");
				break;
			case -1:
				map.put("estado", "CONEXION");
				break;
		}

		
		
		Gson gson = new Gson();
		String json = gson.toJson(map);
				
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("cache-control", "no-cache");
		resp.getWriter().write(json);
		resp.flushBuffer();
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Logger log = Logger.getLogger(ControlServlet.class.getName());
		InputStream in = getServletContext().getResourceAsStream("/WEB-INF/Properties");
		variablesExternas v = new variablesExternas(in, log);
		String path = getServletContext().getRealPath("/");
		Globals global = new Globals(path+"WEB-INF/GLOBALS", log);
		log.setLevel(Level.parse(v.get("LogLevel")));
		
		String ret = "NOT OK";
		String clientMode = req.getParameter("clientMode");
		String clientTemp = req.getParameter("clientTemp");
		float tempManual;
		int modeOp;
		
		log.log(Level.FINE, "Recibido via HTTP Post MODO: "+clientMode+", Temperatura: "+clientTemp);
		
		if ((clientMode.equals("0") || clientMode.equals("1") || clientMode.equals("2")) && isNumeric(clientTemp)) {
			
			tempManual = Float.parseFloat(clientTemp);
			modeOp = Integer.parseInt(clientMode);
			ret = "OK";
		
			if (global.getTempManual() != tempManual || modeOp != global.getModeOp()) { // Hemos cambiado la temperatura manual o el modo de operaion
				global.setTempManual(tempManual);
				switch (modeOp) {
					case 0: 
						global.setModeOp(ModeOp.APAGADO);
					break;
					case 1:
						global.setModeOp(ModeOp.MANUAL);
						break;
					case 2:
						global.setModeOp(ModeOp.PROGRAMADO);
						break;
				}
			
				//Y ajustamos el sistema a los nuevos valores
			
				String calderaIP = v.get("CalderaIP");
				float tempTarget = 0f;
				float currentTemp = SensorPythonWrapper.sensor(path, v.get("SensorPIN"), log)[0];
				int estado = Caldera.Estado(calderaIP, log);
			
				if (global.getModeOp() != ModeOp.APAGADO.getValue()) { // El modo es MANUAL o PROGRAMADO
					if (global.getModeOp() == ModeOp.MANUAL.getValue())
						tempTarget = global.getTempManual();
					if (global.getModeOp() == ModeOp.PROGRAMADO.getValue())
						tempTarget = global.getCalendario().getTempTargetNow();
				
					if (currentTemp < tempTarget) {// Hay que encender la caldera si no lo está ya
						if (estado == 0) // Si la caldera esta apagada, la encendemos
							Caldera.ActuaCaldera(calderaIP, "on", log);
					} else { // La temperatura medida es igual o mayor que la deseada
						if (estado == 1) // La caldera está encendida
							Caldera.ActuaCaldera(calderaIP, "off", log); // Se apaga la caldera	
					}
				} else { // El modo es apagado, comprobamos que la caldera esté apagada
					if (estado != 0) // La caldera no está apagada
							Caldera.ActuaCaldera(calderaIP, "off", log);
				}
				
			}
		} else {
			ret = "NOT OK";
		}
		
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("cache-control", "no-cache");
		resp.getWriter().write(ret);
		resp.flushBuffer();
	}
	
	/* Check if a string could be converted to number (got from StackOverflow)
	 * 
	 */
	public static boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  
}

