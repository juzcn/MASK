/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.executor.MasterExecutor.State;
import mask.logging.ILogger;

/**
 *
 * @author zj
 */
public interface IMonitor extends ILogger {

    public void durationCallBack(int duration);

    public void timeCallBack(int time);

    public void stateCallBack(State state);
}
