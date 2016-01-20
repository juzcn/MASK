/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.service;

import java.io.Serializable;

/**
 *
 * @author zj
 */
public interface StringFilter extends Serializable {

    public boolean evaluate(String s);
}
