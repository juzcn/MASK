/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.logging;

import mask.world.World;
import mask.agent.Agent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zj
 */
public abstract class CSVLogger implements FileLogger {

    protected String logFileName;

    private BufferedWriter writer;

    protected void writeHeader(String... columnNames) {
        writeRow((Serializable[]) columnNames);
    }

    protected void writeRow(Serializable... values) {
        StringBuilder sb = new StringBuilder();
        sb.append(values[0].toString());
        for (int i = 1; i < values.length; i++) {
            sb.append(',');
            sb.append(values[i]);
        }
        try {
            writer.write(sb.toString());
            writer.newLine();
        } catch (IOException ex) {
            Logger.getLogger(CSVLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start() {

        logFileName = this.getClass().getSimpleName()+new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        File logFile = new File(logFileName + ".csv");
        try {
            writer = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException ex) {
            Logger.getLogger(CSVLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void stop() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(CSVLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
