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
import com.clopez.raspi.SensorPythonWrapper;
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
		String path = getServletContext().getRealPath("/");
		Globals g = new Globals(path+"/WEB-INF/GLOBALS");
		
		int estado = Caldera.Estado(v.get("CalderaIP"));
		float[] s = SensorPythonWrapper.sensor(path, v.get("SensorPIN"));
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
		String path = getServletContext().getRealPath("/");
		Globals g = new Globals(path+"WEB-INF/GLOBALS");
		req.getParameter("data");
		System.out.println("En el post");
	}
}

