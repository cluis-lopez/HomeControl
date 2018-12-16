package com.clopez.homeWebApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
		int numLines = Integer.parseInt(req.getParameter("numLines"));
		String ret ="";
		Historico hist = new Historico(getServletContext().getRealPath("/")+"/WEB-INF/Historico.log", log);
		List<String> lineas = new ArrayList<String>();
		Map<String, String> map;
		List<Map<String, String>> lista = new ArrayList<Map<String, String>>();
		
		if (mode.equals("last")) {
			lineas = hist.leeLastLineas(numLines);
			ret = "OK";
		} else if (mode == "range") {
			// To be implemented
		} else {
			ret = "NOT OK";
		}
		
		for (String s: lineas) {
			log.log(Level.INFO, s);;
		}
		
		for (int i = lineas.size(); i > 0 ; i--) {
			String s = lineas.get(i-1);
			String[] token = s.split(" ");
			map = new HashMap<String, String>();
			map.put("date", token[0]);
			map.put("time", (token[1].split(":")[0]+":"+token[1].split(":")[1])); // Quitamos los segundos, que molestan
			map.put("currentTemp", token[2].split(":")[1]);
			map.put("currentHum", token[3].split(":")[1]);
			map.put("targetTemp", token[4].split(":")[1]);
			map.put("state", token[5].split(":")[1]);
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
