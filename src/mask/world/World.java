/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.world;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import mask.service.SimpleMessaging;
import mask.utils.Utils;
import static mask.utils.Utils.deepCopy;

/**
 *
 * @author zj
 */
public abstract class World extends SimpleMessaging implements IWorld, Serializable {

    private transient int worldMailBoxID;
    private transient boolean remote = false;
    private transient Class<World> worldClass;

    @Override
    public void setup() {
        worldMailBoxID = newChannel();
        remote = false;

        Class<?>[] classes = this.getClass().getInterfaces();
        for (Class c : classes) {
            if (c.getSimpleName().equals(Utils.BeanClassName)) {
                worldClass = (Class<World>) this.getClass().getSuperclass().getSuperclass();
                remote = true;
                break;
            }
        }
        if (remote) {
            System.out.println(" Remote world, Local Class : " + worldClass);
        } else {
            System.out.println(" local world");

        }

    }




    @Override
    public World getWorld() {
        return receive(worldMailBoxID);
    }

    private World getCopy() {
        World w = (World) this;
        if (remote) {
            try {
                w = worldClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
            }
            copyTo(w);
        }
        return deepCopy(w);
    }

    @Override
    public void logging() {
        send(worldMailBoxID, getCopy());
    }

    protected World copyTo(World w) {
        // if remote must override the method
        return w;
    }


}
