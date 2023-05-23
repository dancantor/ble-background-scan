package com.ubb.bachelor.blebackgroundscan.domain.callbacks;

public interface BleResultCallback {
    void success(int value);
    void error(String message);
}
