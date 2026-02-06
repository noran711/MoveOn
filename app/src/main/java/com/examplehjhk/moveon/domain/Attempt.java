package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Attempt implements Serializable {
    public int attemptNumber;
    public boolean success;
    public final List<SensorSample> sensorSamples = new ArrayList<>();

    public Attempt(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public void addSample(SensorSample s) {
        sensorSamples.add(s);
    }

    // UML hat sowas wie getErrorAngle() – minimal:
    public float getErrorAngle() {
        // solange du keine Sollbahn hast: 0 als Platzhalter (UML-konform vorhanden)
        return 0f;
    }
}
