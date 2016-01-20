/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.world;

import mask.agent.Agent;

/**
 *
 * @author zj
 */
public interface IWorld  {

    public void timeTicked();

    public void setChanged(boolean changed);

    public boolean isChanged();

    public void close();

    public void logging(Agent agent);

    public Agent[] getAgents();

    public void logging();

    public World getWorld();

    public void setup();
}
