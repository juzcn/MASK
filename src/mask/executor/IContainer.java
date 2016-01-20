/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import mask.rununit.RunGroup;

/**
 *
 * @author zj
 */
public interface IContainer {

    public RunGroup createContainer();

    public void setup();
}
