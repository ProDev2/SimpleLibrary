package com.simplelib.events;

import android.os.Bundle;

public interface IEvent {
    boolean invoke(String key, Bundle args);
}
