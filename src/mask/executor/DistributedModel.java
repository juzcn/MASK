/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import java.io.Serializable;
import mask.utils.Utils;
import mask.world.IWorld;

/**
 *
 * @author zj
 * @param <T>
 */
public class DistributedModel<T extends IWorld> extends Model<T> implements Serializable {

    private final RemoteConfig[] remotes;

    public DistributedModel(String beanName, RemoteConfig... remotes) {
        world = (T) Utils.getBean(beanName);
        world.setup();
        process = "DistributedMask";
        for (RemoteConfig remote : remotes) {
            remote.setBeanName(beanName);
        }
        this.remotes = remotes;
    }

    public int getnRemotes() {
        return remotes.length;
    }

    public RemoteConfig getRemoteConfig(String process) {
        for (RemoteConfig config : remotes) {
            if (config.getProcess().equalsIgnoreCase(process)) {
                return config;
            }
        }
        return null;
    }

    @Override
    public void disableLogging() {
        super.disableLogging();
        for (RemoteConfig r : remotes) {
            r.disableLogging();
        }
    }
}
