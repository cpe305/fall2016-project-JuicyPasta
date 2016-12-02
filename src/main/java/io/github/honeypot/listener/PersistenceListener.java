package io.github.honeypot.listener;

import io.github.honeypot.logger.Log;
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
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                reader.lines().map((str)->new Log(new JSONObject(str))).forEachOrdered((log)->makeChange(log));
            }
        }
    }
    public void reloadLogs() throws IOException {
        reloadLogs(LOG_FOLDER);
    }
}
