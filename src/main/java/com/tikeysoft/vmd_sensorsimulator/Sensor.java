package com.tikeysoft.vmd_sensorsimulator;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;
import java.util.concurrent.Callable;

public class Sensor implements Callable<Void> {

    public final String topic;
    private final IMqttClient client;
    private final Random rnd = new Random();
    private final float min;
    private final float max;

    public Sensor(IMqttClient client, String topic, float min, float max) {
        this.topic = topic;
        this.client = client;
        this.min = min;
        this.max = max;
    }

    @Override
    public Void call() throws MqttException {

        if (!client.isConnected()) {
            System.out.println("[I31] Client not connected.");
            return null;
        }

        MqttMessage msg = messageGenerating();
        msg.setQos(0);
        msg.setRetained(true);
        client.publish(this.topic, msg);

        return null;
    }

    /**
     * This method simulates reading the engine temperature
     *
     * @return
     */
    private MqttMessage messageGenerating() {
        double value = min + rnd.nextDouble() * (max - min);
        byte[] payload = String.format("%04.4f", value).getBytes();
        return new MqttMessage(payload);
    }
}