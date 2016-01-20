/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.executor.MasterExecutor.State;
import mask.logging.MKLogger;

/**
 *
 * @author zj
 */
public interface IMonitor extends MKLogger {

    public void durationCallBack(int duration);

    public void timeCallBack(int time);

    public void stateCallBack(State state);
}
