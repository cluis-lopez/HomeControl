package com.clopez.homeWebApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clopez.homecontrol.Historico;
import com.google.gson.Gson;

/**
 * Servlet implementation class HistoricServlet
 */
@WebServlet("/HistoryServlet")
public class HistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HistoryServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Logger log = Logger.getLogger(ControlServlet.class.getName());
		
		String mode = req.getParameter("mode");
		String ret ="";
		Historico hist = new Historico(getServletContext().getRealPath("/")+"/WEB-INF/Historico.log", log);
		List<String> lineas = new ArrayList<String>();
		Map<String, String> map;
		List<Map<String, String>> lista = new ArrayList<Map<String, String>>();
		
		if (mode == "last") {
			lineas = hist.leeLastLineas(48);
			ret = "OK";
		} else if (mode == "range") {
			
		} else {
			ret = "NOT OK";
		}
		
		for (String s: lineas) {
			String[] token = s.split(" ");
			map = new HashMap<String, String>();
			map.put("date", token[0]);
			map.put("time", token[1]);
			map.put("currentTemp", token[2]);
			map.put("currentHum", token[3]);
			map.put("targetTemp", token[4]);
			map.put("state", token[5]);
			lista.add(map);
		}
		
		Gson gson = new Gson();
		String json = gson.toJson(lista);
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("cache-control", "no-cache");
		resp.getWriter().write(json);
		resp.flushBuffer();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}