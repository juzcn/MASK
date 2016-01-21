/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import java.util.logging.Level;
import java.util.logging.Logger;
import mask.rununit.RunGroup;
import mask.world.IWorld;

/**
 *
 * @author zj
 */
public abstract class LocalModel<T extends IWorld> extends Model<T> implements IContainer {

    private RunGroup container;

    public LocalModel(Class<T> beanClass) {
        try {
            world = (T) beanClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(LocalModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        world.setup();
    }

    /**
     * @return the conatainer
     */
    public RunGroup getContainer() {
        if (container == null) {
            container = createContainer();
        }
        return container;
    }

}
