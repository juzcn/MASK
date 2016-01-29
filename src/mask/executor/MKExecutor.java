/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.world.IWorld;
import mask.service.IService;
import mask.logging.FileLogger;

/**
 *
 * @author zj
 * @param <T>
 */
public abstract class MKExecutor<T extends MKConfig<? extends IWorld>> implements Runnable {

    protected T config;
    protected int step;
    protected int time;
    protected static MKExecutor executor;
    protected IService service;

    public static MKExecutor getExecutor() {
        return executor;
    }

    public static RemoteExecutor newRemoteExecutor(String process) {
        executor = new RemoteExecutor(process);
        return (RemoteExecutor) executor;
    }

    protected MKExecutor(T config) {
        this.config = config;

    }

    public int getStep() {
        return step;
    }

    public IWorld world() {
        return config.world;
    }

    /**
     * @return the process
     */
    public String process() {
        return config.process;
    }

    public int getTime() {
        return time;
    }

    /**
     * @return the service
     */
    public IService getService() {
        return service;
    }

    public boolean isLoggingEnabled() {
        return config.isLoggingEnabled();
    }
}
