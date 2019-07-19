package com.wxt.rabbitmq.helloworld;

/**
 * @Author: weixiaotao
 * @ClassName Consumer
 * @Date: 2018/11/29 18:00
 * @Description:
 */
import com.rabbitmq.client.*;

import java.io.IOException;
public class Recv {
	private final static String QUEUE_NAME = "weixiaotao";
	public static void main(String[] argv) throws Exception {
		//建立连接和通道
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.24.128");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		//声明要消费的队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		//回调消费消息
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}
}
