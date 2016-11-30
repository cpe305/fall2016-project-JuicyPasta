package io.github.honeypot;

import java.util.Observable;

/**
 * Created by jackson on 11/30/16.
 */
public class PersistanceObservable extends Observable{
    public void makeChange(Object o) {
        setChanged();
        notifyObservers(o);
    }
}
