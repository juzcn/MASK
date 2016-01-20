/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.logging;

import mask.executor.LocalExecutor;
import mask.world.World;
import mask.agent.Agent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 *
 * @author zj
 */
public abstract class XLSLogger implements FileLogger {

    protected String logFileName;

    private SXSSFWorkbook wb;
    private final Map<String, Sheet> sheetMap;

    public XLSLogger() {
        sheetMap = new HashMap<>();
    }

    @Override
    public void start() {

        logFileName = this.getClass().getSimpleName()+new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        wb = new SXSSFWorkbook(-1); // turn off auto-flushing and accumulate all rows in 
        wb.setCompressTempFiles(true);
    }

    @Override
    public void stop() {
        FileOutputStream out;
        try {
            out = new FileOutputStream(logFileName + ".xlsx");
            wb.write(out);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LocalExecutor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LocalExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }

        // dispose of temporary files backing this workbook on disk
        wb.dispose();

    }

    protected void createSheet(String sheetName, String... coulumnNames) {
        Sheet sheet = wb.createSheet(sheetName);

        Row row = sheet.createRow(0);
        Cell cell;
        for (int colnum = 0; colnum < coulumnNames.length; colnum++) {
            cell = row.createCell(colnum);
            cell.setCellValue(coulumnNames[colnum]);
        }
        sheetMap.put(sheetName, sheet);
    }

    protected void writeRow(String sheetName, Serializable... values) {
        Sheet sheet = sheetMap.get(sheetName);
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        Cell cell;
        for (int colnum = 0; colnum < values.length; colnum++) {
            cell = row.createCell(colnum);
            cell.setCellValue(values[colnum].toString());
        }

        if (sheet.getLastRowNum() % 100 == 0) {
            try {
                //((SXSSFSheet) sh).flushRows(100); // retain 100 last rows and flush all others

                ((SXSSFSheet) sheet).flushRows(1);
                //is a shortcut for ((SXSSFSheet)sh).flushRows(0),
                // this method flushes all rows
            } catch (IOException ex) {
                Logger.getLogger(LocalExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public abstract void process(Agent[] agents);

    @Override
    public abstract void process(World world);
}
