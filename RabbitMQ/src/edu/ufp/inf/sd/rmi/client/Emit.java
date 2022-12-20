package edu.ufp.inf.sd.rmi.client;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ufp.inf.sd.rmi.util.RabbitUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Emit {

    private static final String EXCHANGE_NAME = "logs";

    public String message;

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            String message = args[0];

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            //System.out.println(" [x] Sent ***" + message + "***");
        }
    }

}

