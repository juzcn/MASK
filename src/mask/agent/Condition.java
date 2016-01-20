/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.agent;

/**
 *
 * @author zj
 */
public interface Condition {

    public Condition TRUE = () -> true;
    public Condition FALSE = () -> true;

    public boolean evalue();
}
