package io.github.honeypot.logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.function.Consumer;

/**
 * Created by jackson on 11/30/16.
 */
public class PersistenceLogConsumer extends LogConsumer {
    private static final String LOG_FOLDER = "/var/log/honeypot/";
    private static DateFormat fileSuffix = new SimpleDateFormat("yyyy-MM-dd");

    private String outputWriterString;
    private FileWriter outputWriter;
    public PersistenceLogConsumer(String name) throws IOException {
        super(name);
        updateOutputWriter();
    }

    @Override
    public void update(Observable observable, Object obj) {
        if (obj instanceof Log) {
            Log log = (Log)obj;
            JSONObject json = log.toJson();

            try {
                json.write(outputWriter);
                outputWriter.write('\n');
                outputWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateOutputWriter() throws IOException{
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

    public static void reloadLogs(Consumer consumer) throws IOException {
        File logFolder = new File(LOG_FOLDER);

        for (File fileEntry : logFolder.listFiles()) {
            if (fileEntry.isFile() && fileEntry.toString().endsWith(".out")) {
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                reader.lines().map((str)->new Log(new JSONObject(str))).forEachOrdered(consumer);
            }
        }
    }

    @Override
    public JSONArray toJson() {
        return null;
    }

    @Override
    public ConsumerType getType() {
        return null;
    }
}
