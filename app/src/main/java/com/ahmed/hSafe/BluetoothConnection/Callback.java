package com.ahmed.hSafe.BluetoothConnection;

public interface Callback {

    /**
     * Schedule javascript function execution represented by this {@link Callback} instance
     *
     * @param args arguments passed to javascript callback method via bridge
     */
    void invoke(Object... args);
}