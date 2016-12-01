package io.github.honeypot.listener;

import java.util.Observable;

/**
 * Created by jackson on 11/30/16.
 */
public class PersistanceListener extends Observable {
    public void makeChange(Object o) {
        setChanged();
        notifyObservers(o);
    }
}
