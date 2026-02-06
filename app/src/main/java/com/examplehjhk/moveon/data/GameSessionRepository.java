package com.examplehjhk.moveon.data;

import com.examplehjhk.moveon.domain.GameSession;

import java.util.ArrayList;
import java.util.List;

public class GameSessionRepository {

    // In-Memory Speicher (für Prototyp / Demo)
    private static final List<GameSession> SESSIONS = new ArrayList<>();

    /** Speichert eine abgeschlossene GameSession */
    public void save(GameSession session) {
        if (session == null) return;
        SESSIONS.add(session);
    }

    /** Lädt alle Sessions eines Patienten (Filter später möglich) */
    public List<GameSession> loadSessions(String patientId) {
        // patientId wird aktuell noch nicht gefiltert
        return new ArrayList<>(SESSIONS);
    }

    /** Nur für Debug / Tests */
    public int getSessionCount() {
        return SESSIONS.size();
    }
}
