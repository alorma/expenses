package com.alorma.expenses.data;

/**
 * Created by bernat.borras on 31/12/15.
 */
public abstract class Presenter<T> {

    private T callback;

    public void start(T t) {
        if (t == null) {
            t = getNullCallback();
        }

        callback = t;

        start();
    }

    protected abstract void start();

    public void stop() {
        callback = getNullCallback();
    }

    public abstract T getNullCallback() ;

    public T getCallback() {
        return callback;
    }
}
