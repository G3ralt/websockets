package alex.osenov.websockets;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

public class WebsocketsApplication {

	public static void main(String[] args) {
		WebSocketClient client = new ReactorNettyWebSocketClient();
		client.execute(
				URI.create("wss://api-pub.bitfinex.com/ws/2"),
				session -> session.send(
						Mono.just(session.textMessage("{ \n" +
								"  \"event\": \"subscribe\", \n" +
								"  \"channel\": \"book\", \n" +
								"  \"symbol\": \"tBTCUSD\" \n" +
								"}")))
						.thenMany(session.receive()
								.map(WebSocketMessage::getPayloadAsText)
								.log())
						.then())
				.block();
	}

}
