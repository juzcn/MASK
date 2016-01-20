/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.rununit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zj
 */
public class RunGroup extends RunUnit {

    private static enum RunAs {
        Loop, ThreadPool, Thread
    }

    private final List<RunUnit> rUnits = new ArrayList<>();
    private final List<RunUnit> tempAdd = new ArrayList<>();
    private final List<RunUnit> tempRemove = new ArrayList<>();

    private final RunAs runAs;
    private int nThreads;
    private ExecutorService executor = null;
    private List<Future<?>> futures = null;

    private RunGroup(RunAs runAs) {
        this.runAs = runAs;
    }

    private RunGroup(RunAs runAs, int nThreads) {
        this.runAs = runAs;
        this.nThreads = nThreads;
        this.executor = Executors.newFixedThreadPool(nThreads);
    }

    public static RunGroup newLoopGroup() {
        return new RunGroup(RunAs.Loop);
    }

    public static RunGroup newThreadGroup() {
        return new RunGroup(RunAs.Thread);
    }

    public static RunGroup newThreadPoolGroup(int nThreads) {
        return new RunGroup(RunAs.ThreadPool, nThreads);
    }

    public RunUnit add(RunUnit rUnit) {
        rUnit.setGroup(this);
        if (runAs == RunAs.Thread) {
            rUnit = new ThreadUnit(rUnit);
        }
        rUnits.add(rUnit);
        return rUnit;
    }

    public void addAll(RunUnit... rUnits) {
        for (RunUnit rUnit : rUnits) {
            add(rUnit);
        }
    }

    public void remove(RunUnit a) {
        rUnits.remove(a);
    }

    public synchronized void addTemp(RunUnit rUnit) {
        tempAdd.add(rUnit);
    }

    public synchronized void addTempAll(RunUnit... rUnits) {
        tempAdd.addAll(Arrays.asList(rUnits));
    }

    public synchronized void removeTemp(RunUnit rUnit) {
        tempRemove.add(rUnit);
    }

    private void merge() {
        for (RunUnit rUnit : tempAdd) {
            add(rUnit).setup();
        }
        for (RunUnit rUnit : tempRemove) {
            rUnit.stop();
            remove(rUnit);
        }
        tempAdd.clear();
        tempRemove.clear();
    }

    public void publish(String message) {
        for (RunUnit rUnit : rUnits) {
            simpleMessaging().send(rUnit.threadMailBox, message);
        }
    }

    @Override
    public void setup() {
        switch (runAs) {
            case ThreadPool:
                executor = Executors.newFixedThreadPool(nThreads);
                futures = new ArrayList<>();
                break;
            case Thread:
                threadMailBox = simpleMessaging().newChannel();
                break;

        }
        for (RunUnit rUnit : rUnits) {
            rUnit.setup();
        }
        if (runAs == RunAs.Thread) {
            for (RunUnit rUnit : rUnits) {
                simpleMessaging().waitReceive(threadMailBox);
            }
        }

    }

    private void loopRun() {
        for (RunUnit rUnit : rUnits) {
            rUnit.run();
        }
    }

    private void threadPoolRun() {
        for (RunUnit a : rUnits) {
            futures.add(executor.submit(a));
        }
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(RunGroup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        futures.clear();
    }

    @Override
    public void stop() {
        if (runAs == RunAs.Thread) {
//            for (RunUnit rUnit : rUnits) {
//                System.out.println("Group received 1" + receive());
//            }
            publish("Stop");
            simpleMessaging().waitReceive(threadMailBox);
        }
        for (RunUnit rUnit : rUnits) {
            rUnit.stop();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

    public void threadRun() {
        publish("Go");
        for (RunUnit rUnit : rUnits) {
            simpleMessaging().waitReceive(threadMailBox);
        }
    }

    @Override
    public void run() {
        merge();
        switch (runAs) {
            case Loop:
                loopRun();
                break;
            case ThreadPool:
                threadPoolRun();
                break;
            case Thread:
                threadRun();
                break;
        }
    }

}
