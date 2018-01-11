package org.spring.springboot.util;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ServerEndpoint(value = "/api/v1/topic/min_stat")
@Component
public class WebSocket {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocket.class);
	
	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	public static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session) {
		LOGGER.info("连接建立成功调用的方法");
		this.session = session;
		webSocketSet.add(this); // 加入set中
		ConsumerKafka kafka = new ConsumerKafka();
		kafka.start();
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		LOGGER.info("连接关闭调用的方法");
		webSocketSet.remove(this); // 从set中删除
	}

	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		LOGGER.info("来自客户端的消息:" + message);
	}

	/* 发生错误时调用 */
	@OnError
	public void onError(Session session, Throwable error) {
		LOGGER.info("发生错误时调用:" + error.getMessage());
	}

	public void sendMessage(String message) throws IOException {
		// TODO 组装数据格式再发送
		
		this.session.getBasicRemote().sendText(message);
	}
}
