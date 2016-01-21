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
public abstract class MasterExecutor<T extends MasterConfig<? extends IWorld>> extends MKExecutor<T> {

    protected IMonitor monitor;
    protected int maxTime = 100;
    protected int duration;
    protected int stopAt = 100;
    protected BlockingQueue<Command> commands = new LinkedBlockingQueue<>();
    protected ExecutorService monitorThread;
    private FileLogging fileLogging;
    private State state;

    public static LocalExecutor newLocalComputing(LocalConfig config) {
        executor = new LocalExecutor(config);
        return (LocalExecutor) executor;
    }

    public static DistributedExecutor newDistributedComputing(DistributedConfig config) {
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

    public int steps() {
        return config.getSteps();
    }

    public static enum Command {
        Run, Stop, Pause, Goto, Speed, FastForward, SlowForward, StepRun
    }

    public static enum State {
        Loaded, Running, Stopped, Paused
    }

    public void command(Command c) {
        commands.offer(c);
    }

    public void start(int maxTime, int duration, int stopAt) {
        this.stopAt = stopAt;
        this.maxTime = maxTime;
        this.duration = duration;
        if (monitor != null) {
            new Thread(this).start();
        } else {
            run();
        }
    }

    public void setRunParams(int maxTime, int duration, int stopAt) {
        this.stopAt = stopAt;
        this.maxTime = maxTime;
        this.duration = duration;
    }

    public void start(int maxTime, int duration) {
        start(maxTime, duration, maxTime);
    }

    private Command take() {
        try {
//            commands.clear();
            return commands.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(MasterExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean stepRun;

    private boolean processCommand() {

        boolean exit = false;

        if (time > stopAt) {
            setState(State.Paused);
        }

        MasterExecutor.Command command;

        if (state == State.Running) {
            nonblocking:
            do {
                command = commands.poll();
                if (command == null) {
                    break nonblocking;
                }
                switch (command) {
                    case Run:
                        break;
                    case Stop:
                        exit = true;
                        break nonblocking;
                    case Speed:
                        break;
                    case SlowForward:
                        if (duration > 0) {
                            duration = duration * 2;
                        } else {
                            duration = 1000;
                        }
                        monitor.durationCallBack(duration);
                        break;
                    case FastForward:
                        duration = duration / 2;
                        monitor.durationCallBack(duration);
                        break;
                    case Pause:
                        setState(State.Paused);
                        break;
                    case StepRun:
                        stepRun = true;
                        setState(State.Paused);
                        break;
                }
            } while (true);
        }

        if (exit) {
            return exit;
        }
        if (state == State.Loaded || state == State.Paused) {
            blockingWhile:
            do {
                command = take();
                switch (command) {
                    case Run:
                        if (time <= stopAt) {
                            setState(State.Running);
                            stepRun = false;
                            break blockingWhile;
                        }
                        break;
                    case Stop:
                        exit = true;
                        break blockingWhile;
                    case Speed:
                        break;
                    case SlowForward:
                        if (duration > 0) {
                            duration = duration * 2;
                        } else {
                            duration = 1000;
                        }
                        monitor.durationCallBack(duration);
                        break;
                    case FastForward:
                        duration = duration / 2;
                        monitor.durationCallBack(duration);
                        break;
                    case Pause:
                        break;
                    case StepRun:
                        if (!stepRun) {
                            setState(State.Paused);
                            stepRun = true;
                        }
                        break blockingWhile;
                }

            } while (true);
        }
        monitor.timeCallBack(time);
        return exit;
    }

    public void cycleRun() {
        long beginMills = 0L;
        long waitMills;
        Future<?> updateWorld1 = null;
        Future<?> updateWorld2 = null;
        Future<?> updateAgent1 = null;
        Future<?> updateAgent2 = null;

        setState(State.Loaded);
        for (time = 1; time <= maxTime; time++) {

            world().timeTicked();
            System.out.println("timeTick =" + time);

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
                if (processCommand()) {
                    break;
                }
            }

            if (duration > 0) {
                beginMills = System.currentTimeMillis();
            }

            for (step = 1; step <= steps(); step++) {
                do {
                    world().setChanged(false);
                    stepRun();
                } while (world().isChanged());
            }
            if (service.getAgentNumber() == 0) {
                break;
            }

            if (monitor == null) {
                if (fileLogging != null) {
                    fileLogging.process(world().getAgents());
                }
            } else {
                updateAgent2 = monitorThread.submit(() -> {
                    Agent[] agents = world().getAgents();
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
    }

    public void setState(State state) {
        this.state = state;
        if (monitor != null) {
            monitor.stateCallBack(state);
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

    protected abstract void stepRun();

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
                fileLogging.process(world().getAgents());
            }
        } else {
            monitorThread.submit(() -> {
                World w = world().getWorld();
                Agent[] agents = world().getAgents();
                if (fileLogging != null) {
                    fileLogging.process(w);
                    fileLogging.process(agents);
                }
                monitor.process(w);
                monitor.process(agents);
            });
        }
        // step 10: close world
        world().close();
        // step 12: close trace file
        stopLogging();
        setState(State.Stopped);

    }

    public <T extends Serializable> T getResult() {
        return service.getResult();
    }

    public <T extends Serializable> List<T> getAllResults() {
        return service.getAllResults();
    }

    @Override
    public void run() {
        if (this.isLoggingEnabled()) {
            setup();
            prepare();
            cycleRun();
            stopRun();
            afterStop();
        } else {
            prepare();
            for (time = 1; time <= maxTime; time++) {
                world().timeTicked();
                System.out.println("timeTick =" + time);
                for (step = 1; step <= steps(); step++) {
                    do {
                        world().setChanged(false);
                        stepRun();
                    } while (world().isChanged());
                }
                if (service.getAgentNumber() == 0) {
                    break;
                }
            }
            stopRun();
            world().close();
        }
    }

}
