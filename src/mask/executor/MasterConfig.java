/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.world.IWorld;

/**
 *
 * @author zj
 */
public class MasterConfig<T extends IWorld> extends MKConfig<T> {

    private int steps = 1;

    /**
     * @return the steps
     */
    public int getSteps() {
        return steps;
    }

    /**
     * @param steps the steps to set
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }
;
}
