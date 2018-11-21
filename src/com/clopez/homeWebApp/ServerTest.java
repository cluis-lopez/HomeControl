package com.clopez.homeWebApp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clopez.homecontrol.GlobalVars.ModeOp;
import com.clopez.homecontrol.Globals;
import com.clopez.homecontrol.variablesExternas;
import com.clopez.raspi.Caldera;
import com.google.gson.Gson;

/**
 * Servlet implementation class SeverTest
 */
@WebServlet("/SeverTest")
public class ServerTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServerTest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// Imprime el estado
		InputStream in = getServletContext().getResourceAsStream("/WEB-INF/Properties");
		variablesExternas v = new variablesExternas(in);
		Globals g = new Globals("GLOBALS");
		// DHT11 sensor = new DHT11();
		
		boolean estado = Caldera.Estado(v.get("CalderaIP"));
		//float currentTemp = sensor.getTempHum(Integer.parseInt(v.get("SensorPIN")))[0];
		//float currentHum = sensor.getTempHum(Integer.parseInt(v.get("SensorPIN")))[1];
		
		float currentTemp = 19.2f; // Debug
		float currentHum = 0.33f; //Debug
		float tempTarget = 9999f;
		
		if (g.getModeOp() != ModeOp.APAGADO.getValue()) { // El modo es MANUAL o PROGRAMADO
			if (g.getModeOp() == ModeOp.MANUAL.getValue())
				tempTarget = g.getTempManual();
			if (g.getModeOp() == ModeOp.PROGRAMADO.getValue())
				tempTarget = g.getCalendario().getTempTargetNow();
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("modeOp", g.getModeOp());
		map.put("estado", (estado ? "ON" : "OFF"));
		map.put("currentTemp", currentTemp);
		map.put("currentHum", currentHum);
		map.put("tempTarget", tempTarget);
		map.put("calendario", g.getCalendario());

		
		
		Gson gson = new Gson();
		String json = gson.toJson(map);
				
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("cache-control", "no-cache");
		resp.getWriter().write(json);
		resp.flushBuffer();
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO
	}
}

