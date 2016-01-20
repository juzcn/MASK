/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.service;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author zj
 */
public interface ISimpleMessaging {

    public boolean hasMessage(int channel);

    public int newChannel();

    public <T extends Serializable> T waitReceive(int channel);

    public <T extends Serializable> void send(int channel, T message);

    public <T extends Serializable> T receive(int channel);

    public <T extends Serializable> List<T> receiveAll(int channel);

}
