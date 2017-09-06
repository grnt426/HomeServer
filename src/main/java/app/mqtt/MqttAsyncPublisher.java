package app.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MqttAsyncPublisher implements Runnable {

	@Data
	@AllArgsConstructor
	private class Message {
		public String topic;
		public String message;
	}

	private MqttHandler mqttHandler;

	public MqttAsyncPublisher(MqttHandler mqttHandler) {
		this.mqttHandler = mqttHandler;
	}

	private final List<Message> queue = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void run() {

		while (true) {
			if (queue.size() != 0) {
				synchronized (queue) {
					for (Message m : queue) {
						mqttHandler.publishMessage(m.topic, m.message);
					}
					queue.clear();
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException ignored) {
			}
		}
	}

	public void addMessage(String topic, String message) {
		queue.add(new Message(topic, message));
	}
}
