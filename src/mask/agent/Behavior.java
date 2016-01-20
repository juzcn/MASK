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
public class Behavior {

    private final Condition condition;
    private final Handler handler;

    public Behavior(Condition c, Handler h) {
        condition = c;
        handler = h;
    }

    /**
     * @return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * @return the handler
     */
    public Handler getHandler() {
        return handler;
    }
}
