package com.ifood.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Scanner;

class MqttIfoodHealthSignalClient {

    public static void main(String[] args) throws MqttException {
        while(true) {
            System.out.print("Insert the restaurant code: ");
            String restaurantCode = new Scanner(System.in).next();

            if(restaurantCode == null || restaurantCode.isEmpty()){
                System.exit(0);
            }

            sendHealthSignal(restaurantCode);
        }
    }

    private static void sendHealthSignal(String restaurantCode) throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        MqttMessage message = new MqttMessage();
        message.setPayload(restaurantCode.getBytes());
        client.publish("restaurantHealthSignalTopic", message);
        client.disconnect();
    }
}
