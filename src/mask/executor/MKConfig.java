/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import java.io.Serializable;
import mask.service.IService;
import mask.world.IWorld;

/**
 *
 * @author zj
 */
public class MKConfig<T extends IWorld> implements Serializable {

    protected String process = "Mask";
    protected transient T world = null;

    public IService service() {
        return MKExecutor.getExecutor().getService();
    }

    /**
     * @return the process
     */
    public String getProcess() {
        return process;
    }

    /**
     * @param process the process to set
     */
    public void setProcess(String process) {
        this.process = process;
    }

    /**
     * @return the world
     */
    public T getWorld() {
        return world;
    }
}
