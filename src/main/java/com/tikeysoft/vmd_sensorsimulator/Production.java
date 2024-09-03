package com.tikeysoft.vmd_sensorsimulator;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Production {
    private final int delay1 = 3;
    private final int delay2 = 5;
    private final int delay3 = 10;

    private final Sensor billetSensor;
    private final Sensor billetWasteSensor;
    private final Sensor semiProfileSensor;
    private boolean isRunning = false;
    private Consumer<Boolean> runningStateListener;

    public Production(IMqttClient client) {
        this.billetSensor = new Sensor(client, "AE_01/production/billet", 0.5365f, 0.4591f);
        this.billetWasteSensor = new Sensor(client, "AE_01/production/billet_waste", 0.00429f, 0.00435f);
        this.semiProfileSensor = new Sensor(client, "AE_01/production/semi_profile", 29.38f, 31.27f);
    }

    public void call() {
        if (isRunning) {
            return;
        }
        setRunning(true);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

        executorService.schedule(() -> {
            try {
                billetSensor.call();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }, delay1, TimeUnit.SECONDS);

        executorService.schedule(() -> {
            try {
                billetWasteSensor.call();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }, delay2, TimeUnit.SECONDS);

        executorService.schedule(() -> {
            try {
                semiProfileSensor.call();
            } catch (MqttException e) {
                e.printStackTrace();
            }
            setRunning(false);
        }, delay3, TimeUnit.SECONDS);

        executorService.shutdown();
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void setRunning(boolean running) {
        isRunning = running;
        if (runningStateListener != null) {
            runningStateListener.accept(isRunning);
        }
    }

    public void setRunningStateListener(Consumer<Boolean> listener) {
        this.runningStateListener = listener;
    }
}