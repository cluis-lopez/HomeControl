package com.clopez.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	Logger log = Logger.getLogger(Listener.class.getName());
	
	private final long startTime = System.currentTimeMillis();
	
    public static void main(final String[] args) {
        new Listener (args);
    }
    
	public Listener (final String args[]) {
		apiKey = args.length > 0 ? args[0] : "InvalidKey";
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
        String datos;
        
        @SuppressWarnings("unchecked")
        final Map<String, String> mapa = gson.fromJson(data, Map.class);
        System.out.println(mapa); //Debug
        if (mapa.get("command").equals("GET")) {
        	Gson json = new Gson();
        	Map<String, Object> map = json.fromJson(getLocalHost("/ControlServlet"), HashMap.class);
        	List<Map<String, String>> l = json.fromJson(getLocalHost("/HistoryServlet?mode=last&numLines=24"), List.class);
        	map.put("lastLines", l);
        	System.out.println("Recibidos\n\n" + json.toJson(map));
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
    
    private void sendLocalHost(String url) {
    	
    }
}