/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.executor;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
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

    protected IMonitor callback;
    private int maxTime = 100;
    private int pauseAt = 0;
    private int duration = 0;

    protected BlockingQueue<Command> commands = new LinkedBlockingQueue<>();
    private State state;

    public static LocalExecutor newLocalExecutor(LocalModel config) {
        executor = new LocalExecutor(config);
        return (LocalExecutor) executor;
    }

    public static DistributedExecutor newDistributedExecutor(DistributedModel config) {
        executor = new DistributedExecutor(config);
        return (DistributedExecutor) executor;
    }

    public static LocalExecutor newLocalExecutor(LocalModel config, IMonitor callback) {
        executor = new LocalExecutor(config, callback);
        return (LocalExecutor) executor;
    }

    public static DistributedExecutor newDistributedExecutor(DistributedModel config, IMonitor callback) {
        executor = new DistributedExecutor(config, callback);
        return (DistributedExecutor) executor;
    }

    public MasterExecutor(T config, IMonitor callback) {
        super(config);
        this.callback = callback;
    }

    public MasterExecutor(T config) {
        super(config);
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

    public State getState() {
        return state;
    }

    protected abstract void loopRun();

    protected abstract void prepare();

    protected abstract void stopRun();

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

    private void setState(State state) {
        this.state = state;
        if (callback != null) {
            callback.onState(state);
        }
    }

    @Override
    public void run() {
        setState(State.Running);
        prepare();

        forLabel:
        for (time = 1; time <= maxTime; time++) {
            System.out.println("Time =" + time);

            if (callback != null) {
                callback.onTime(time);
            }

            if (this.isLoggingEnabled() && world() != null) {
                world().logging();
            }
            if (duration > 0) {
                beginMills = System.currentTimeMillis();
            }

            while (true) {
                if (time == pauseAt) {
                    setState(State.Paused);
                    try {
                        commands.offer(commands.take());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MasterExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    setState(State.Running);
                    pauseAt = 0;
                }
                if ((command = commands.poll()) == null) {
                    break;
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

            if (callback != null && world() != null) {
                callback.world(world().getWorld());
            }

            for (step = 1; step <= steps(); step++) {
                do {
                    service.setChanged(false);
                    loopRun();
                } while (service.isChanged());
            }

            if (callback != null) {
                callback.agents(service.getAgents());
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

        if (this.isLoggingEnabled() && world() != null) {
            world().logging();
        }

        if (callback != null) {
            if (world() != null) {
                callback.world(world().getWorld());
            }
            callback.agents(service.getAgents());
        }

        setState(State.Stopped);

    }

}
