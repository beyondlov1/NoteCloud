package com.beyond.mq;

import com.beyond.f.Config;

import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

public class UnInvokedMethodHandler {
    private MessageQueue messageQueue;
    private Timer timer;

    public UnInvokedMethodHandler() {
        this.messageQueue = new MessageQueue();
        timer= new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handleMessage();
            }
        };
        timer.schedule(timerTask,1000,Config.UN_INVOKED_METHOD_HANDLE_PERIOD);
    }

    public void addMessage(UnHandleMethodData unHandleMethodData) {
        messageQueue.getQueue().add(unHandleMethodData);
    }

    private void handleMessage() {
        while (messageQueue != null && messageQueue.getQueue() != null && messageQueue.getQueue().size() > 0) {
            UnHandleMethodData unHandleMethodData = messageQueue.getQueue().poll();
            try {
                if (unHandleMethodData != null) {
                    unHandleMethodData.getMethod().invoke(unHandleMethodData.getSource(), unHandleMethodData.getArgs());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    public void setMessageQueue(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void stop() {
        timer.cancel();
    }

    class ConsumerRunnable implements Runnable {
        @Override
        public void run() {

                handleMessage();
        }
    }
}


