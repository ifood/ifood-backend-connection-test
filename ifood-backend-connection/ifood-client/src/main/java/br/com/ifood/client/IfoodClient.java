package br.com.ifood.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Scanner;

class IfoodClient {

    private MqttClient client;
    private static final String TOPIC = "ifood/backend/connection/alivesignal";

    public IfoodClient() throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        this.client = client;
    }

    public static void main(String[] args) throws MqttException {
        IfoodClient ifoodClient = new IfoodClient();

        while(true) {
            System.out.print("Enter the code to identify your restaurant: ");
            String code = new Scanner(System.in).next();

            if(code == null || code.isEmpty()){
                System.exit(0);
            }

            ifoodClient.sendKeepAliveSignal(code);
        }
    }

    private void sendKeepAliveSignal(String code) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setPayload(code.getBytes());

        client.publish(TOPIC, message);

        client.disconnect();
    }
}