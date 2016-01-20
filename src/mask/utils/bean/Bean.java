/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.utils.bean;

import java.io.Serializable;

/**
 *
 * @author zj
 */
public class Bean<T extends Serializable> implements Serializable {

    private final String[] propertyNames;
    private final T[] values;

    public Bean(String[] propertyNames, T[] values) {
        this.propertyNames = propertyNames;
        this.values = values;
    }

    /**
     * @return the values
     */
    public T[] getValues() {
        return values;
    }

    public T getValue(String propertyName) {

        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyNames[i].equalsIgnoreCase(propertyName)) {
                return values[i];
            }
        }
        return null;
    }
}
