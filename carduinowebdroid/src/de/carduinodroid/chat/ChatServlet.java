package de.carduinodroid.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

public class ChatServlet extends WebSocketServlet {
	
	 private static final long serialVersionUID = 1L;
	 
	 public StreamInbound createWebSocketInbound(String protocol) {
		 return new ChatMessageInbound();
		 }
	 
	 private class ChatMessageInbound extends MessageInbound {
		 
		 public void onOpen(WsOutbound outbound){
			 try {
				//Log: User connected to chat(?)
				//TODO: Get User and add him with socket connection to the list
				outbound.writeTextMessage(CharBuffer.wrap("Welcome to the Chat!"));
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		}
			  
			 @Override
			 public void onClose(int status){
				 System.out.println("Close Client.");
				//TODO: remove User and socketconnection from the list 
			 }

			 protected void onTextMessage(CharBuffer cb) throws IOException {
				 //TODO: Log chat message
				 //TODO: Send chat message to all clients in the list
			 }
			 
			 protected void onBinaryMessage(ByteBuffer bb) throws IOException{
			 }
	 }
	 	
	 	

		@Override
		protected StreamInbound createWebSocketInbound(String arg0,
				HttpServletRequest arg1) {
			// TODO Auto-generated method stub WHAT THE FUCK DOES THIS DO?
			return null;
		}

		 
}
