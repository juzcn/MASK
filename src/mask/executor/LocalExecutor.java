/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.rununit.RunGroup;
import mask.world.IWorld;
import mask.service.MKService;

/**
 *
 * @author zj
 */
public class LocalExecutor<T extends LocalModel<? extends IWorld>> extends MasterExecutor<T> {

    public LocalExecutor(T modelConfig, IExecutorCallBack callback) {
        super(modelConfig, callback);
        service = new MKService();
        service.setup();
    }

    public LocalExecutor(T modelConfig) {
        super(modelConfig);
        service = new MKService();
        service.setup();
    }

    public RunGroup container() {
        return config.getContainer();
    }

    @Override
    protected void stopRun() {
        container().stop();
    }

    @Override
    protected void prepare() {
        container().setup();
        config.setup();
    }

    @Override
    protected void loopRun() {
        container().run();
    }

}
