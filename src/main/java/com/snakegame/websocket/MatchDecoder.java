package com.snakegame.websocket;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.snakegame.beans.Match;

public class MatchDecoder implements Decoder.Text<Match> {

    private static Gson gson = new Gson();

    @Override
    public Match decode(String s) throws DecodeException {
    	//Player player = gson.fromJson(s, Player.class);
    	Match match = gson.fromJson(s, Match.class);
        return match;
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}