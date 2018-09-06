package com.beyond.mq;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue {
    private ConcurrentLinkedQueue<UnHandleMethodData> queue;

    public ConcurrentLinkedQueue<UnHandleMethodData> getQueue() {
        if (queue==null){
            queue = new ConcurrentLinkedQueue<>();
        }
        return queue;
    }

    public void setQueue(ConcurrentLinkedQueue<UnHandleMethodData> queue) {
        this.queue = queue;
    }
}
