/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import java.io.Serializable;
import mask.rununit.RunGroup;
import mask.utils.Utils;
import mask.world.IWorld;

/**
 *
 * @author zj
 */
public abstract class RemoteConfig<T extends IWorld> extends MKConfig<T> implements IContainer, Serializable {

    private transient RunGroup container;
    protected String beanName;
    protected String className;

    public RemoteConfig(String process) {
        this.process = process;
        this.className = this.getClass().getName();
    }

    /**
     * @param beanName the beanName to set
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * @return the container
     */
    public RunGroup getContainer() {
        if (container == null) {
            container = createContainer();
        }
        return container;
    }

    public void initWorld() {
        world = (T) Utils.getBean(beanName);
    }
}
