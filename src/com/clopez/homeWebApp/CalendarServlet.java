package com.clopez.homeWebApp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clopez.homecontrol.Calendario;
import com.clopez.homecontrol.GlobalVars;
import com.clopez.homecontrol.Globals;
import com.clopez.homecontrol.variablesExternas;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class CalendarServlet
 */
@WebServlet("/CalendarServlet")
public class CalendarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CalendarServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Logger log = Logger.getLogger(CalendarServlet.class.getName());
		InputStream in = getServletContext().getResourceAsStream("/WEB-INF/Properties");
		variablesExternas v = new variablesExternas(in, log);
		String path = getServletContext().getRealPath("/");
		Globals global = new Globals(path+"WEB-INF/GLOBALS", log);
		log.setLevel(Level.parse(v.get("LogLevel")));
		
		String program = req.getParameter("program");
		
		Gson json = new Gson();
		float dias[][][] = new float[7][24][2];
		dias = json.fromJson(program, dias.getClass());
		Calendario cl = new Calendario(dias);
		
		log.log(Level.INFO, "Ajustando el programa "+ cl.printCalendario());
		global.setCalendario(cl);
		
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("cache-control", "no-cache");
		resp.getWriter().write("OK");
		resp.flushBuffer();

	}

}
