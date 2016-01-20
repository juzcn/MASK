/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.utils.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zj
 */
public class BeanDef {

    public static abstract class Getter<B,T extends Serializable> {

        private String propertyName;

        public Getter(String propertyName) {
            this.propertyName = propertyName;
        }

        public abstract T get(B bean);
    }
    private String[] propertyNames;
    private Method[] methods;
    private Getter[] getters;

    public BeanDef(Getter... getters) {
        this.getters = getters;
        propertyNames = new String[getters.length];
        for (int i = 0; i < getters.length; i++) {
            propertyNames[i] = getters[i].propertyName;
        }
    }

    public BeanDef(Class<?> beanClass, String... propertyNames) {
        this.propertyNames = propertyNames;
        this.methods = new Method[propertyNames.length];

        try {
            BeanInfo info = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for (int i = 0; i < propertyNames.length; i++) {
                for (int j = 0; j < pds.length; j++) {
                    if (propertyNames[i].equalsIgnoreCase(pds[j].getName())) {
                        methods[i] = pds[j].getReadMethod();
                        break;
                    }
                }
                assert methods[i] != null;
            }

        } catch (IntrospectionException ex) {
            Logger.getLogger(BeanDef.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public <B, T extends Serializable> Bean getBean(B bean) {
        Serializable[] values = new Serializable[methods.length];
        if (methods != null) {
            for (int i = 0; i < methods.length; i++) {
                try {
                    values[i] = (Serializable) methods[i].invoke(bean);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(BeanDef.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (int i = 0; i < methods.length; i++) {
                values[i] = (T) getters[i].get(bean);
            }

        }
        return new Bean<>(propertyNames, (T[]) values);
    }
}
