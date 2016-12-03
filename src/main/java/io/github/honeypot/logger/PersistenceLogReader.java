package io.github.honeypot.logger;

import io.github.honeypot.constants.Constants;
import io.github.honeypot.exception.HoneypotRuntimeException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

/**
 * Created by jackson on 11/30/16.
 */
public class PersistenceLogReader extends LogConsumer {
    private static DateFormat fileSuffix = new SimpleDateFormat("yyyy-MM-dd");

    private String outputWriterString;
    private FileWriter outputWriter;

    @Override
    public void update(Observable observable, Object obj) {
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
    }

    public void updateOutputWriter() throws IOException {
        String currentFile = Constants.LOG_FOLDER + "honeypot." + fileSuffix.format(new Date()) + ".out";
        if (outputWriter == null || outputWriterString == null || !outputWriterString.equals(currentFile)) {
            if (outputWriter != null) {
                outputWriter.flush();
                outputWriter.close();
            }

            this.outputWriterString = currentFile;
            this.outputWriter = new FileWriter(currentFile, true);
        }
    }

    @Override
    public JSONArray toJson() {
        return null;
    }
}
