package com.examplehjhk.moveon.data;

import com.examplehjhk.moveon.domain.GameSession;

import java.util.ArrayList;
import java.util.List;

public class GameSessionRepository {

    // Minimal: In-Memory Speicher (für Abgabe/Prototyp)
    private static final List<GameSession> SESSIONS = new ArrayList<>();

    public void save(GameSession session) {
        if (session == null) return;
        SESSIONS.add(session);
    }

    // Patient: nur eigene Daten
    public List<GameSession> loadSessionsForPatient(String patientUsername) {
        List<GameSession> out = new ArrayList<>();
        if (patientUsername == null) return out;

        for (GameSession s : SESSIONS) {
            if (patientUsername.equalsIgnoreCase(s.patientUsername)) out.add(s);
        }
        return out;
    }

    // Therapeut: alle Daten
    public List<GameSession> loadAllSessions() {
        return new ArrayList<>(SESSIONS);
    }
}
