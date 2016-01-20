/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author zj
 */
public class Utils {

    public static Context context;
    public static final String BeanClassName = "WLEnterpriseBean";

    public static Object getBean(String beanName) {
        try {
            if (context == null) {
                Hashtable env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
                env.put(Context.PROVIDER_URL, "t3://localhost:7001");
                env.put(Context.SECURITY_PRINCIPAL, "weblogic");
                env.put(Context.SECURITY_CREDENTIALS, "ez630966");
                context = new InitialContext(env);
            }
            return context.lookup(beanName);
        } catch (NamingException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static <T extends Serializable> T deepCopy(T oldObj) {
        T object = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream bos
                    = new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B
            // serialize and pass the object
            oos.writeObject(oldObj);   // C
            oos.flush();               // D
            ByteArrayInputStream bin
                    = new ByteArrayInputStream(bos.toByteArray()); // E
            // print size 

            ois = new ObjectInputStream(bin);
            object = (T) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (oos != null) {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (ois != null) {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return object; // G
    }

    public static void main(String args[]) {
    }
}
