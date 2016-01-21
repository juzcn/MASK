/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.rununit.RunGroup;
import mask.service.IService;
import mask.utils.Utils;
import mask.world.IWorld;

/**
 *
 * @author zj
 */
public class RemoteExecutor<T extends RemoteConfig<? extends IWorld>> extends MKExecutor<T> {

    private int remoteID;
    private final String process;

    public RemoteExecutor(String process) {
        super(null);
        this.process = process;
    }

    public RunGroup container() {
        return config.getContainer();
    }

    @Override
    public void run() {
        service = (IService) Utils.getBean("java:global.MASKBeans.MASKBeans-ejb.ServiceBean");
        remoteID = service.newChannel();
        service.remoteJoinExecutorGroup(remoteID);
        // step 1: 连接到服务器,报告Ready
        service.sendToExecutor("Ready");  // R-D Send1
        // step2: 接受服务器配置
        DistributedModel dconfig = service.waitReceive(remoteID); //D-R Receive 1
        config = (T) dconfig.getRemoteConfig(process);
        config.initWorld();
        // step 3  container setup
        container().setup();
        // step 4: 连接到服务器,报告Ready
        service.sendToExecutor("Ready"); // R-D Send2
        // setp 5: config setup  
        String message = service.waitReceive(remoteID); //D-R Receive2
        // step 6:  setupLogging
        config.setup();
        // step 9: loop run
        do {
            service.sendToExecutor("Ready"); // R-D Send3
            message = service.waitReceive(remoteID);
            if (message.equals("Stop")) {
                time++;
                break;
            }
            String strs[] = message.split(",");
            time = Integer.parseInt(strs[0]);
            step = Integer.parseInt(strs[1]);
            System.out.println("Remote time " + time + " step " + step);
            container().run();
        } while (true);
        // step 10: stop agents
        container().stop();
        service.sendToExecutor("Stopped");

    }

}
