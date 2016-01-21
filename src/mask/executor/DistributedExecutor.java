/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.service.IService;
import mask.world.IWorld;
import mask.logging.FileLogger;
import mask.utils.Utils;

/**
 *
 * @author zj
 */
public class DistributedExecutor<T extends DistributedConfig<? extends IWorld>> extends MasterExecutor<T> {

    public DistributedExecutor(T config, Monitor monitor, FileLogger... loggers) {
        super(config, monitor, loggers);
        service = (IService) Utils.getBean("java:global.MASKBeans.MASKBeans-ejb.ServiceBean");
        service.setup();
    }

    public DistributedExecutor(T config) {
        super(config);
        service = (IService) Utils.getBean("java:global.MASKBeans.MASKBeans-ejb.ServiceBean");
        service.setup();
    }

    @Override
    protected void stopRun() {
        service.sendToExecutorGroup("Stop");
        service.executorWait(config.getnRemotes());
    }

    public int nRemotes() {
        return config.getnRemotes();
    }

    @Override
    protected void prepare() {
        System.out.println("Distributed Executor Ready");
        // step 1 receive connection request
        service.executorWait(nRemotes()); // R-D Receive1
        // step 6: annonce world ready
        service.sendToExecutorGroup(config); //D-R Send1
        // step 7: receive remote ready
        service.executorWait(nRemotes()); // R-D Receive2
        // step 8: annonce world ready
        service.sendToExecutorGroup("Go"); //D-R Send2
        // step 9: wait for go
        service.executorWait(nRemotes()); //R-D Receive3
    }

    @Override
    protected void stepRun() {
        service.sendToExecutorGroup(time + "," + step);
        service.executorWait(nRemotes());
    }

}
