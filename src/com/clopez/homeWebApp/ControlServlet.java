package com.clopez.homeWebApp;

import java.io.IOException;
import java.io.InputStream;
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
import com.clopez.homecontrol.variablesExternas;
import com.clopez.raspi.Caldera;
import com.clopez.raspi.SensorPythonWrapper;
import com.google.gson.Gson;

/**
 * Servlet implementation class SeverTest
 */
@WebServlet("/SeverTest")
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
		
		// Imprime el estado
		InputStream in = getServletContext().getResourceAsStream("/WEB-INF/Properties");
		variablesExternas v = new variablesExternas(in, log);
		String path = getServletContext().getRealPath("/");
		Globals g = new Globals(path+"/WEB-INF/GLOBALS", log);
		
		int estado = Caldera.Estado(v.get("CalderaIP"), log);
		float[] s = SensorPythonWrapper.sensor(path, v.get("SensorPIN"), log);
		float currentTemp = s[0];
		float currentHum = s[1];

		
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
		Globals g = new Globals(path+"WEB-INF/GLOBALS", log);
		
		float tempManual = Float.parseFloat(req.getParameter("clientTemp"));
		int modeOp = Integer.parseInt(req.getParameter("clientMode"));
		
		log.log(Level.INFO, "Recibido via HTTP Post "+tempManual+" , "+modeOp);
		
		if (g.getTempManual() != tempManual || modeOp != g.getModeOp()) { // Hemos cambiado la temperatura manual o el modo de operaion
			g.setTempManual(tempManual);
			switch (modeOp) {
				case 0: 
					g.setModeOp(ModeOp.APAGADO);
					break;
				case 1:
					g.setModeOp(ModeOp.MANUAL);
					break;
				case 2:
					g.setModeOp(ModeOp.PROGRAMADO);
					break;
			}
			
			//Y ajustamos el sistema a los nuevos valores
			
			String calderaIP = v.get("CalderaIP");
			float tempTarget = 0f;
			float currentTemp = SensorPythonWrapper.sensor(path, v.get("SensorPIN"), log)[0];
			int estado = Caldera.Estado(calderaIP, log);
			
			if (g.getModeOp() != ModeOp.APAGADO.getValue()) { // El modo es MANUAL o PROGRAMADO
				if (g.getModeOp() == ModeOp.MANUAL.getValue())
					tempTarget = g.getTempManual();
				if (g.getModeOp() == ModeOp.PROGRAMADO.getValue())
					tempTarget = g.getCalendario().getTempTargetNow();
				
				if (currentTemp < tempTarget) {// Hay que encender la caldera si no lo está ya
					if (estado == 0) // Si la caldera esta apagada, la encendemos
						Caldera.ActuaCaldera(calderaIP, "on", log);
				} else { // La tempratura medida es igual o mayor que la deseada
					if (estado == 1) // La caldera está encendida
						Caldera.ActuaCaldera(calderaIP, "off", log); // Se apaga la caldera	
				}
			} else { // El modo es apagado, comprobamos que la caldera esté apagada
				if (estado != 0) // La caldera no está apagada
					Caldera.ActuaCaldera(calderaIP, "off", log);
			}
				
		}
		
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("cache-control", "no-cache");
		resp.getWriter().write("OK");
		resp.flushBuffer();
	}
}

