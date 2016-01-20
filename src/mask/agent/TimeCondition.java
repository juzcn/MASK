/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.agent;

import mask.executor.MKExecutor;
import java.io.Serializable;

/**
 *
 * @author zj
 */
public class TimeCondition implements Condition, Serializable  {

    private long atTime;

    public TimeCondition(long atTime) {
        this.atTime = atTime;
    }

    @Override
    public boolean evalue() {
        return (MKExecutor.getExecutor().getTime() == atTime);
    }

    public void setAtTime(long atTime) {
        this.atTime = atTime;
    }
}
