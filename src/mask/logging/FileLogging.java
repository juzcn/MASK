/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.logging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import mask.agent.Agent;
import mask.world.World;

/**
 *
 * @author zj
 */
public class FileLogging implements FileLogger {

    private final FileLogger loggers[];
    protected ExecutorService loggingThread;

    public FileLogging(FileLogger... loggers) {
        this.loggers = loggers;
    }

    @Override
    public void start() {
        loggingThread = Executors.newSingleThreadExecutor();
        for (FileLogger logger : loggers) {
            logger.start();
        }
    }

    @Override
    public void stop() {
        loggingThread.shutdown();
        try {
            loggingThread.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileLogging.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (FileLogger logger : loggers) {
            logger.stop();
        }
    }

    @Override
    public void process(Agent[] agents) {
        loggingThread.submit(() -> {
            for (FileLogger logger : loggers) {
                logger.process(agents);
            }
        });
    }

    @Override
    public void process(World world) {
        loggingThread.submit(() -> {
            for (FileLogger logger : loggers) {
                logger.process(world);
            }
        });
    }

}
