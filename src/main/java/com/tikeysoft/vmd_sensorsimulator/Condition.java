package com.tikeysoft.vmd_sensorsimulator;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Condition {
    private final Sensor billetTempSensor;
    private final Sensor dieTempSensor;
    private final Sensor rampPressureSensor;
    private boolean isRunning = false;
    private Consumer<Boolean> runningStateListener;
    private ScheduledExecutorService executorService;
    private final int delay = 1;


    public Condition(IMqttClient client) {
        this.billetTempSensor = new Sensor(client, "AE_01/condition/billet_temp", 80, 100);
        this.dieTempSensor = new Sensor(client, "AE_01/condition/die_temp", 80, 100);
        this.rampPressureSensor = new Sensor(client, "AE_01/condition/ramp_pressure", 80, 100);
    }

    public void start() {
        if (isRunning) {
            return;
        }
        setRunning(true);
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            try {
                billetTempSensor.call();
                dieTempSensor.call();
                rampPressureSensor.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, delay, TimeUnit.SECONDS);
    }

    public void stop() {
        executorService.shutdown();
        setRunning(false);
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