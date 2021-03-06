package com.wxt.rabbitmq.rpc;

/*********************************
 * @author weixiaotao1992@163.com
 * @date 2018/12/1 13:20
 * QQ:1021061446
 *
 *********************************/
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClient {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String replyQueueName;

    /**
     * 定义一个RPC客户端
     * @throws IOException
     * @throws TimeoutException
     */
    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.24.128");
        factory.setUsername("admin");
        factory.setPassword("admin");
        connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
    }

    /**
     * 真正地请求
     * @param message
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(corrId)) {
                    System.out.println("[client current time] : " + System.currentTimeMillis());
                    response.offer(new String(body, "UTF-8"));
                }
            }
        });

        return response.take();
    }

    /**
     * 关闭连接
     * @throws IOException
     */
    public void close() throws IOException {
        connection.close();
    }

    public static void main(String[] argv) {
        RPCClient fibonacciRpc = null;
        String response = null;
        try {
            //创建一个RPC客户端
            fibonacciRpc = new RPCClient();
            System.out.println(" [x] Requesting fib(30)");
            //RPC客户端发送调用请求，并等待影响，直到接收到
            response = fibonacciRpc.call("30");
            System.out.println(" [.] Got '" + response + "'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fibonacciRpc != null) {
                try {
                    fibonacciRpc.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

