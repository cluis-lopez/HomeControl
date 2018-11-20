package com.clopez.homeWebApp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clopez.homecontrol.GlobalVars;
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
		variablesExternas v = new variablesExternas("Properties");
		Globals g = new Globals("GLOBALS");
		GlobalVars gv = g.getGlobals();
		boolean estado = Caldera.Estado(v.get("CalderaIP"));
		Gson gson = new Gson();
		String json = gson.toJson(gv);
				
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("cache-control", "no-cache");
		resp.getWriter().write(json);
		resp.flushBuffer();
	}

}
