package io.github.honeypot.servlet;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.ClientEndpoint;


import java.util.logging.Logger;

@ClientEndpoint
public class WebClientEndpoint {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@OnOpen
	public void onOpen(Session session) {
		logger.info(session.getId());
	}
	
	@OnMessage
	public String onMessage(String message, Session session) {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        try {
            logger.info(message);
            String userInput = bufferRead.readLine();
            return userInput;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(session.getId() + " " + closeReason);
	}
	
}
