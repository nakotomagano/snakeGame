package com.snakegame.websocket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.snakegame.beans.Match;

public class MatchEncoder implements Encoder.Text<Match> {

    private static Gson gson = new Gson();

    @Override
    public String encode(Match match) throws EncodeException {
        String json = gson.toJson(match);
        return json;
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