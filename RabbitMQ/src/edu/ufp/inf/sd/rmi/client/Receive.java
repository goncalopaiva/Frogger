package edu.ufp.inf.sd.rmi.client;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rmi.server.State.State;
import edu.ufp.inf.sd.rmi.server.State.StateMovimento;
import edu.ufp.inf.sd.rmi.server.State.StateTransito;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class Receive {

    private static final String EXCHANGE_NAME = "logs";

    public void receive(ObserverRI observer) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("\tReceive -> message received: " + message);

            String[] part = message.split(",");

            State state = createState(message);
            if (state != null) {
                state.execute(observer);
            }
            else {
                System.out.println("State is null");
            }

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    public static State createState(String message) throws RemoteException {

        System.out.println("Receive -> createState -> " + message);
        String[] part = message.split(",");

        if (Objects.equals(part[0], "State")) {
            State state = newState(Integer.parseInt(part[1]), Integer.parseInt(part[2]), Integer.parseInt(part[3]));
            System.out.println("State -> " + state.toString());
            return state;
        }

        if (Objects.equals(part[0], "StateMovimento")) {
            StateMovimento state = newStateMovimento(Integer.parseInt(part[1]), Integer.parseInt(part[2]), Integer.parseInt(part[3]), Integer.parseInt(part[4]), Integer.parseInt(part[5]));
            System.out.println("State -> " + state.toString());
            return state;
        }

        if (Objects.equals(part[0], "StateTransito")) {
            Coordenadas position = new Coordenadas(Double.parseDouble(part[5]), Double.parseDouble(part[6]));
            Coordenadas velocity = new Coordenadas(Double.parseDouble(part[7]), Double.parseDouble(part[8]));
            StateTransito state = newStateTransito(Integer.parseInt(part[1]), Integer.parseInt(part[2]), Integer.parseInt(part[3]), part[4], position, velocity, part[9], Long.parseLong(part[10]));
            System.out.println("State -> " + state.toString());
            return state;
        }

        return null;
    }

    public static State newState(int score, int timer, int level) {
        State state = new State(score, timer, level);
        return state;
    }

    public static StateMovimento newStateMovimento(int pontuacao, int timer, int level, Integer id, int direction){
        StateMovimento state = new StateMovimento(pontuacao, timer, level, id, direction);
        return state;
    }

    public static StateTransito newStateTransito(int score, int timer, int level, String type, Coordenadas position, Coordenadas velocity, String spriteName, long deltaMs){
        StateTransito state = new StateTransito(score, timer, level, type, position, velocity, spriteName, deltaMs);
        return state;
    }

}
