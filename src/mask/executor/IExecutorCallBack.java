/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.agent.Agent;
import mask.executor.MasterExecutor.State;
import mask.world.World;

/**
 *
 * @author zj
 */
public interface IExecutorCallBack {

    public void time(int time);

    public void state(State state);

    public void agents(Agent[] agents);

    public void world(World world);

}
