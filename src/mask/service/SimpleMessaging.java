/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zj
 */
public class SimpleMessaging implements ISimpleMessaging {

    protected final transient AtomicInteger nextChannel = new AtomicInteger(0);
    protected final transient List<BlockingQueue<Serializable>> channels = Collections.synchronizedList(new ArrayList<>());

    @Override
    public int newChannel() {
        int id = nextChannel.getAndIncrement();
        channels.add(new LinkedBlockingQueue<>());
        return id;
    }

    @Override
    public <T extends Serializable> T waitReceive(int channel) {
        try {
            return (T) channels.get(channel).take();
        } catch (InterruptedException ex) {
            Logger.getLogger(MKService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public <T extends Serializable> T receive(int channel) {
        return (T) channels.get(channel).poll();
    }

    @Override
    public <T extends Serializable> List<T> receiveAll(int channel) {
        BlockingQueue<T> mailBox = (BlockingQueue<T>) channels.get(channel);
        List<T> results = new ArrayList<>();
        T message;
        while (true) {
            message = mailBox.poll();
            if (message == null) {
                break;
            }
            results.add(message);
        }
        return results;
    }

    @Override
    public boolean hasMessage(int channel) {
        return (channels.get(channel).peek() != null);
    }

    @Override
    public <T extends Serializable> void send(int channel, T message) {
        channels.get(channel).offer(message);
    }

}
