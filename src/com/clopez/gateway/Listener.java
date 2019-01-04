package com.clopez.gateway;

import java.util.Map;

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
	private final long startTime = System.currentTimeMillis();
	
    public static void main(final String[] args) {
        new Listener (args);
    }
    
	public Listener (final String args[]) {
		apiKey = args.length > 0 ? args[0] : "InvalidKey";
		channelName = "datosToRaspi";
		eventName = "'ordenFromWebClient";
		final PusherOptions options = new PusherOptions().setEncrypted(true);
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
        final Map<String, String> jsonObject = gson.fromJson(data, Map.class);
        System.out.println(jsonObject);
    }

    @Override
    public void onSubscriptionSucceeded(final String channelName) {

        System.out.println(String.format("[%d] Subscription to channel [%s] succeeded", timestamp(), channelName));
    }

    private long timestamp() {
        return System.currentTimeMillis() - startTime;
    }
}
