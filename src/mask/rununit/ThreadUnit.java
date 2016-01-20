/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.rununit;

/**
 *
 * @author zj
 */
public class ThreadUnit extends RunUnit {

    private final RunUnit rUnit;

    public ThreadUnit(RunUnit rUnit) {
        this.rUnit = rUnit;

    }

    @Override
    public void setup() {
        threadMailBox = simpleMessaging().newChannel();
        rUnit.setup();
        new Thread(this).start();
    }

    @Override
    public void stop() {
        rUnit.stop();
    }

    @Override
    public void run() {
        do {
            simpleMessaging().send(rUnit.getGroup().threadMailBox, "Ready");
            String message = (String) simpleMessaging().waitReceive(threadMailBox);
            if (message.equals("Stop")) {
                break;
            }
            rUnit.run();
        } while (true);
        simpleMessaging().send(rUnit.getGroup().threadMailBox, "Stopped");
    }
}
