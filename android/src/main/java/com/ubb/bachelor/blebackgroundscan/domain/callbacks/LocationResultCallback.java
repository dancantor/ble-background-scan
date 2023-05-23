package com.ubb.bachelor.blebackgroundscan.domain.callbacks;

import android.location.Location;

public interface LocationResultCallback {
    void success(Location location);
    void error(String message);
}
