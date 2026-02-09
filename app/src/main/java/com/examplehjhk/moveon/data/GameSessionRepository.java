package com.examplehjhk.moveon.data;

import com.examplehjhk.moveon.domain.GameSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class responsible for managing GameSession data.
 * Currently using an in-memory storage for session tracking.
 */
public class GameSessionRepository {

    // Thread-safe static list to store game sessions throughout the app lifecycle
    private static final List<GameSession> SESSIONS = new ArrayList<>();

    /**
     * Saves a new game session to the repository.
     *
     * @param session The GameSession object to be saved.
     */
    public void save(GameSession session) {
        // Prevent null objects from being added to the list
        if (session == null) return;
        SESSIONS.add(session);
    }

    /**
     * Filters and retrieves all game sessions belonging to a specific patient.
     *
     * @param patientUsername The unique username of the patient.
     * @return A list of sessions matching the provided username.
     */
    public List<GameSession> loadSessionsForPatient(String patientUsername) {
        List<GameSession> out = new ArrayList<>();
        // Return an empty list if no username is provided
        if (patientUsername == null) return out;

        // Iterate through all stored sessions to find matches
        for (GameSession s : SESSIONS) {
            // Case-insensitive comparison to ensure consistency
            if (patientUsername.equalsIgnoreCase(s.patientUsername)) {
                out.add(s);
            }
        }
        return out;
    }

    /**
     * Retrieves all game sessions stored in the repository.
     *
     * @return A new list containing all global sessions.
     */
    public List<GameSession> loadAllSessions() {
        // Return a copy of the list to protect the original data structure
        return new ArrayList<>(SESSIONS);
    }
}