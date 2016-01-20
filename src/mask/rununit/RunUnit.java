/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.rununit;

import mask.service.SimpleMessaging;

/**
 *
 * @author zj
 */
public abstract class RunUnit implements Runnable {

    public static SimpleMessaging simpleMessaging;

    private transient RunGroup group;
    protected transient ThreadUnit threadRUnit;
    protected transient int threadMailBox;

    public static SimpleMessaging simpleMessaging() {
        if (simpleMessaging == null) {
            simpleMessaging = new SimpleMessaging();
        }
        return simpleMessaging;
    }

    public RunGroup getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(RunGroup group) {
        this.group = group;
    }

    public void setup() {
    }

    public void stop() {
    }

}
