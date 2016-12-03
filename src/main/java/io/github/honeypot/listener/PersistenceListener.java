package io.github.honeypot.listener;

import io.github.honeypot.exception.HoneypotException;
import io.github.honeypot.logger.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Observable;

import static io.github.honeypot.constants.Constants.LOG_FOLDER;

/**
 * Created by jackson on 11/30/16.
 */
public class PersistenceListener extends Observable {
    public void makeChange(Object o) {
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
