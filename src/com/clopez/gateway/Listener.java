package com.clopez.gateway;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clopez.homecontrol.variablesExternas;
import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

public class Listener implements ConnectionEventListener, ChannelEventListener {
	
	final String apiKey;
	final String channelName;
	final String eventName;
	final Pusher pusher;
	final String GoogleEndPoint; //Ojo ... cambiar a Google
	Logger log = Logger.getLogger(Listener.class.getName());
	
	private final long startTime = System.currentTimeMillis();
	
    public static void main(final String[] args) {
        new Listener (args);
    }
    
	public Listener (final String args[]) {
		String path = args[0] + "/";
		System.out.println("Path : " + path );
		InputStream in = null;
		try {
			in = new FileInputStream(path+"WEB-INF/Properties");
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "Error de entrada/salida al abrir el fichero de Propiedades");
			log.log(Level.SEVERE, e.toString(), e);
		}
		variablesExternas v = new variablesExternas(in, log);
		log.setLevel(Level.parse(v.get("LogLevel")));
		
		apiKey = v.get("Pusher_ApiKey");
		GoogleEndPoint = v.get("Google_EndPoint");
		channelName = "datosToRaspi";
		eventName = "ordenFromWebClient";
		final PusherOptions options = new PusherOptions().setEncrypted(true);
		options.setCluster("eu");
        pusher = new Pusher(apiKey, options);
        pusher.connect();
        pusher.subscribe(channelName, this, eventName);
        
        while (true) { //NOSONAR
            try {
                Thread.sleep(1000);
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	
	@Override
    public void onConnectionStateChange(final ConnectionStateChange change) {

        System.out.println(String.format("[%d] Connection state changed from [%s] to [%s]", timestamp(),
                change.getPreviousState(), change.getCurrentState()));
    }

    @Override
    public void onError(final String message, final String code, final Exception e) {

        System.out.println(String.format("[%d] An error was received with message [%s], code [%s], exception [%s]",
                timestamp(), message, code, e));
    }

    /* ChannelEventListener implementation */

    @Override
    public void onEvent(final String channelName, final String eventName, final String data) {

        System.out.println(String.format("[%d] Received event [%s] on channel [%s] with data [%s]", timestamp(),
                eventName, channelName, data));

        final Gson gson = new Gson();
        
        @SuppressWarnings("unchecked")
        final Map<String, String> mapa = gson.fromJson(data, Map.class);
        log.log(Level.INFO, "Recibidos desde RaspiGateway: "+mapa.toString());
        
        if (mapa.get("command").equals("GET")) {
        	Gson json = new Gson();
        	Map<String, Object> map = json.fromJson(getLocalHost("/ControlServlet"), HashMap.class);
        	List<Map<String, String>> l = json.fromJson(getLocalHost("/HistoryServlet?mode=last&numLines=24"), List.class);
        	map.put("chart", l);
        	// System.out.println("Recibidos\n\n" + json.toJson(map));
        	
        	//Send data to Raspi Gateway servlet
        	
        	try {
				URLConnection con = new URL(GoogleEndPoint).openConnection();
				con.setDoOutput(true); //POST request
				con.setRequestProperty("Accept-Charset", "UTF-8");
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
				try (OutputStream output = con.getOutputStream()) {
		        	String query = String.format("user=%s&password=%s&json=%s",
		        			mapa.get("user"),
		        			"myInternalPassw0rd",
		        			json.toJson(map));
				    output.write(query.getBytes("UTF-8"));
				}
				
				InputStream resp = con.getInputStream();
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(resp));
	    		StringBuilder result = new StringBuilder();
	    		String line;
	    		while((line = reader.readLine()) != null) {
	    		    result.append(line);
	    		}
	    		
	    		log.log(Level.INFO, "RaspiGateway: "+result.toString());
				
			} catch (IOException e) {
	    		log.log(Level.SEVERE, "Error conectando con Google Servlet");
				log.log(Level.SEVERE, e.toString(), e);
			}
        	
        }
        
        if (mapa.get("command").equals("CONTROL")) {
        	triggerLocalHost("ControlServlet", mapa.get("data"));
        }
    }

    @Override
    public void onSubscriptionSucceeded(final String channelName) {

        System.out.println(String.format("[%d] Subscription to channel [%s] succeeded", timestamp(), channelName));
    }

    private long timestamp() {
        return System.currentTimeMillis() - startTime;
    }
    
    private String getLocalHost(String url) {
    	String ret = "";
    	try {
    		URLConnection con = new URL("http://localhost:8080/HomeControl"+url).openConnection();
    		con.setRequestProperty("Accept-Charset", "UTF-8");
    		InputStream resp = con.getInputStream();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(resp));
    		StringBuilder result = new StringBuilder();
    		String line;
    		while((line = reader.readLine()) != null) {
    		    result.append(line);
    		}
    		ret = result.toString();
    	} catch (IOException e) {
    		log.log(Level.SEVERE, "Error conectando con: " + url);
			log.log(Level.SEVERE, e.toString(), e);
    	}
    	return ret;
    }
    
    private void triggerLocalHost(String url, String data) {
    	Gson json = new Gson();
    	Map<String, String> map = json.fromJson(data, HashMap.class); 
    	try {
			URLConnection con = new URL("http://localhost:8080/HomeControl/"+url).openConnection();
			con.setDoOutput(true); //POST request
			con.setRequestProperty("Accept-Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			try (OutputStream output = con.getOutputStream()) {
	        	String query = String.format("clientMode=%s&clientTemp=%s",
	        			map.get("clientMode"),
	        			map.get("clientTemp"));
			    output.write(query.getBytes("UTF-8"));
			}
			
			InputStream resp = con.getInputStream();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(resp));
    		StringBuilder result = new StringBuilder();
    		String line;
    		while((line = reader.readLine()) != null) {
    		    result.append(line);
    		}
    		
    		log.log(Level.INFO, "ControlServlet: " + result.toString());
			
		} catch (IOException e) {
    		log.log(Level.SEVERE, "Error conectando con Google Servlet");
			log.log(Level.SEVERE, e.toString(), e);
		}
    }
}
