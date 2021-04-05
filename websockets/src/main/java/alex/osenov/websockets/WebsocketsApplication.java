package alex.osenov.websockets;

import alex.osenov.websockets.model.Message;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

public class WebsocketsApplication {

	public static void main(String[] args) {
		WebSocketClient client = new ReactorNettyWebSocketClient();
		Message subMessage = new Message("subscribe", "book", "tBTCUSD");
		client.execute(
				URI.create("wss://api-pub.bitfinex.com/ws/2"),
				session -> session.send(
						Mono.just(session.textMessage(subMessage.asText())))
						.thenMany(session.receive()
								.map(WebSocketMessage::getPayloadAsText)
								.log())
						.then())
				.block();
	}

}
