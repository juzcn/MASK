/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import java.io.Serializable;
import java.util.List;
import mask.world.World;
import mask.agent.Agent;
import mask.logging.FileLogger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import mask.logging.FileLogging;
import mask.world.IWorld;

/**
 *
 * @author zj
 * @param <T>
 */
public abstract class MasterExecutor<T extends Model<? extends IWorld>> extends MKExecutor<T> {

    protected IMonitor monitor;
    private int maxTime = 100;
    private int pauseAt = 0;
    private int duration = 0;

    protected BlockingQueue<Command> commands = new LinkedBlockingQueue<>();
    protected ExecutorService monitorThread;
    private FileLogging fileLogging;
    private State state;

    public static LocalExecutor newLocalComputing(LocalModel config) {
        executor = new LocalExecutor(config);
        return (LocalExecutor) executor;
    }

    public static DistributedExecutor newDistributedComputing(DistributedModel config) {
        executor = new DistributedExecutor(config);
        return (DistributedExecutor) executor;
    }

    public MasterExecutor(T config, Monitor monitor, FileLogger... loggers) {
        super(config);
        this.monitor = monitor;
        if (loggers != null) {
            this.fileLogging = new FileLogging(loggers);
        }
    }

    public MasterExecutor(T config) {
        super(config);
        config.disableLogging();
    }

    public void start(int maxTime) {
        this.maxTime = maxTime;
        run();
    }

    public void threadStart(int maxTime) {
        this.maxTime = maxTime;
        new Thread(this).start();
    }

    public int steps() {
        return config.getSteps();
    }

    /**
     * @param maxTime the maxTime to set
     */
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * @param pauseAt the pauseAt to set
     */
    public void setPauseAt(int pauseAt) {
        this.pauseAt = pauseAt;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static enum Command {
        Run, Stop, Pause, StepRun
    }

    public static enum State {
        Running, Stopped, Paused
    }

    public void cycleRun() {
        Future<?> updateWorld1 = null;
        Future<?> updateWorld2 = null;
        Future<?> updateAgent1 = null;
        Future<?> updateAgent2 = null;

        for (time = 1; time <= maxTime; time++) {

            System.out.println("Time =" + time);

            world().logging();
            if (monitor == null) {
                if (fileLogging != null) {
                    fileLogging.process(world().getWorld());
                }
            } else {
                updateWorld2 = monitorThread.submit(() -> {
                    World w = world().getWorld();
                    if (fileLogging != null) {
                        fileLogging.process(w);
                    }
                    monitor.process(w);
                });
                if (time == 1) {
                    try {
                        updateWorld2.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(MasterExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (updateWorld1 != null) {
                        try {
                            updateWorld1.get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Logger.getLogger(MasterExecutor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    updateWorld1 = updateWorld2;
                }
            }

            for (step = 1; step <= steps(); step++) {
                do {
                    service.setChanged(false);
                    loopRun();
                } while (service.isChanged());
            }
            if (service.getAgentNumber() == 0) {
                break;
            }

            if (monitor == null) {
                if (fileLogging != null) {
                    fileLogging.process(service.getLogging());
                }
            } else {
                updateAgent2 = monitorThread.submit(() -> {
                    Agent[] agents = service.getLogging();
                    if (fileLogging != null) {
                        fileLogging.process(agents);
                    }
                    monitor.process(agents);
                });
                if (updateAgent1 != null) {
                    try {
                        updateAgent1.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(MasterExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                updateAgent1 = updateAgent2;
            }

        }
    }

    public State getState() {
        return state;
    }

    public void startLogging() {
        if (fileLogging != null) {
            fileLogging.start();
        }
        if (monitor != null) {
            monitorThread = Executors.newSingleThreadExecutor();

        }
    }

    public void stopLogging() {
        if (fileLogging != null) {
            fileLogging.stop();
        }

        if (monitor != null) {
            monitorThread.shutdown();
            try {
                monitorThread.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException ex) {
                Logger.getLogger(MasterExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected abstract void loopRun();

    protected void setup() {
        startLogging();
    }

    protected abstract void prepare();

    protected abstract void stopRun();

    protected void afterStop() {
        world().logging();
        // step 11: write last looging record
        if (monitor == null) {
            if (fileLogging != null) {
                fileLogging.process(world().getWorld());
                fileLogging.process(service.getLogging());
            }
        } else {
            monitorThread.submit(() -> {
                World w = world().getWorld();
                Agent[] agents = service.getLogging();
                if (fileLogging != null) {
                    fileLogging.process(w);
                    fileLogging.process(agents);
                }
                monitor.process(w);
                monitor.process(agents);
            });
        }
        // step 10: close world
//        world().close();
        // step 12: close trace file
        stopLogging();

    }

    public <T extends Serializable> T getResult() {
        return service.getResult();
    }

    public <T extends Serializable> List<T> getAllResults() {
        return service.getAllResults();
    }

    public void speed(int duration) {
        this.duration = duration;
        commands.add(Command.Run);
    }

    public void pauseAt(int pauseAt) {
        this.pauseAt = pauseAt;
        commands.add(Command.Run);
    }

    public void stepRun() {
        commands.offer(Command.StepRun);
    }

    public void pause() {
        commands.add(Command.Pause);
    }

    public void resume() {
        commands.add(Command.Run);
    }

    public void stop() {
        commands.add(Command.Stop);
    }

    private Command command;
    private long beginMills;
    private long waitMills;

    @Override
    public void run() {
        state = State.Running;

        if (this.isLoggingEnabled()) {
            setup();
            prepare();
            cycleRun();
            stopRun();
            afterStop();
        } else {
            prepare();
            forLabel:
            for (time = 1; time <= maxTime; time++) {
                System.out.println("Time =" + time);

                if (duration > 0) {
                    beginMills = System.currentTimeMillis();
                }

                while ((command = commands.poll()) != null) {
                    if (time == pauseAt) {
                        state = State.Paused;
                        try {
                            commands.offer(commands.take());
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MasterExecutor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        state = State.Running;
                    }
                    switch (command) {
                        case Pause:
                            pauseAt = time;
                            break;
                        case Run:
                            break;
                        case StepRun:
                            pauseAt = time + 1;
                            break;
                        case Stop:
                            break forLabel;
                    }
                }

                for (step = 1; step <= steps(); step++) {
                    do {
                        service.setChanged(false);
                        loopRun();
                    } while (service.isChanged());
                }

                if (service.getAgentNumber() == 0) {
                    break;
                }

                if (duration > 0) {
                    waitMills = duration - System.currentTimeMillis() + beginMills;
                    if (waitMills > 0) {
                        try {
                            Thread.sleep(waitMills);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MasterExecutor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            
            stopRun();
            state = State.Stopped;

        }
    }

}
