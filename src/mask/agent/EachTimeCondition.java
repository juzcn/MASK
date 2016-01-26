/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.agent;

import mask.executor.MKExecutor;

/**
 *
 * @author zj
 */
public class EachTimeCondition implements Condition {

    public int atTime;
    public int interval;

    public EachTimeCondition() {
        this(1, 1);
    }

    public EachTimeCondition(int atTime) {
        this(atTime, 1);
    }

    public EachTimeCondition(int atTime, int interval) {
        this.atTime = atTime;
        this.interval = interval;
    }

    @Override
    public boolean evalue() {
        boolean ret = (MKExecutor.getExecutor().getTime() == atTime);
        if (ret == true) {
            atTime += interval;
        }
        return ret;
    }

}
