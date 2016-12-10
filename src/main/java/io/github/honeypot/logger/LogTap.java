package io.github.honeypot.logger;

import io.github.honeypot.constants.Constants;
import io.github.honeypot.exception.HoneypotRuntimeException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.github.honeypot.constants.Constants.LOG_FOLDER;

/**
 * Created by jackson on 12/8/16.
 */
public class LogTap extends Observable implements Observer {
    private static DateFormat fileSuffix = new SimpleDateFormat("yyyy-MM-dd");

    private String outputWriterString;
    private FileWriter outputWriter;
    private LogTap() {}

    private static LogTap INSTANCE = new LogTap();
    public static LogTap getInstance() {
        return INSTANCE;
    }

    @Override
    public void update(Observable observable, Object obj) {
        // Save to file
        try {
            updateOutputWriter();
        } catch (IOException e) {
            throw new HoneypotRuntimeException(e);
        }
        if (obj instanceof Log) {
            Log log = (Log)obj;
            JSONObject json = log.toJson();

            try {
                json.write(outputWriter);
                outputWriter.write('\n');
                outputWriter.flush();
            } catch (IOException e) {
                throw new HoneypotRuntimeException(e);
            }
        }

        makeChange(obj);
    }

    private void updateOutputWriter() throws IOException {
        String currentFile = LOG_FOLDER + "honeypot." + fileSuffix.format(new Date()) + ".out";
        if (outputWriter == null || outputWriterString == null || !outputWriterString.equals(currentFile)) {
            if (outputWriter != null) {
                outputWriter.flush();
                outputWriter.close();
            }

            this.outputWriterString = currentFile;
            this.outputWriter = new FileWriter(currentFile, true);
        }
    }


    private void makeChange(Object o) {
        setChanged();
        notifyObservers(o);
    }

    public void reloadLogs(String logFolderString) throws IOException {
        File logFolder = new File(logFolderString);
        for (File fileEntry : logFolder.listFiles()) {
            if (fileEntry.isFile() && fileEntry.toString().endsWith(".out")) {
                try (FileReader fileReader = new FileReader(fileEntry)) {
                    BufferedReader reader = new BufferedReader(fileReader);
                    reader.lines().map((str) -> new Log(new JSONObject(str))).forEachOrdered((log) -> makeChange(log));
                } catch (JSONException e) {
                    throw new IOException(e);
                }
            }
        }
    }
    public void reloadLogs() throws IOException {
        reloadLogs(LOG_FOLDER);
    }

}
