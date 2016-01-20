/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.service;

import java.io.Serializable;

/**
 *
 * @author zj
 * @param <T>
 */
public class MKMessage<T extends Serializable> implements Serializable {

    public MKMessage(int senderID, T content) {
        this.senderID = senderID;
        this.content = content;
    }

    private final int senderID;
    private final T content;

    /**
     * @return the senderID
     */
    public int getSenderID() {
        return senderID;
    }

    /**
     * @return the content
     */
    public T getContent() {
        return content;
    }
}
