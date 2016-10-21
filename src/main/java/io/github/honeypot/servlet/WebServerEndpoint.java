package io.github.honeypot.servlet;

import java.io.IOException;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import java.util.logging.Logger;

@ServerEndpoint(value = "/logs/*")
public class WebServerEndpoint {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@OnOpen
	public void onOpen(Session session) {
		logger.info(session.getId());
	}
	
	@OnMessage
	public String onMessage(String message, Session session) {
		//try {
        //   session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Exited"));
        //} catch (IOException e) {
        //   throw new RuntimeException(e);
        //}
		return message;
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(session.getId() + " " + closeReason);
	}
	
}
