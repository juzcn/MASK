/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.agent;

/**
 *
 * @author zj
 */
public abstract class SimpleAgent extends Agent {

    public static enum SingleState {
        live
    };

    public SimpleAgent() {
    }

    @Override
    public void setup() {
        super.setup();
        state = SingleState.live;
        getGlobalHehaviors().add(new Behavior(() -> step() == 1, () -> perceive()));
        getGlobalHehaviors().add(new Behavior(() -> step() == 2, () -> actuate()));
    }

    @Override
    public void run() {
        for (Behavior b : globalBehaviors) {
            if (b.getCondition().evalue()) {
                b.getHandler().handle();
            }
        }
    }

    public abstract boolean perceive();

    public abstract boolean actuate();

}
