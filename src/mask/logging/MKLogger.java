/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.logging;

import mask.agent.Agent;
import mask.world.World;

/**
 *
 * @author zj
 */
public interface MKLogger {

    public void process(Agent[] agents);

    public void process(World world);

}
