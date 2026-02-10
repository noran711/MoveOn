package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;import java.util.UUID;

/**
 * Represents a social or therapy group within the system.
 * This class stores the group identity, name, owner, and a list of member usernames.
 */
public class Group implements Serializable {

    // Unique identifier for the group, automatically generated
    private final String groupId;

    // The display name of the group
    private String groupName;

    // List of usernames belonging to this group
    private final List<String> members;

    // The username of the user who created/manages the group
    private final String ownerUsername;

    /**
     * Constructor to create a new group.
     *
     * @param groupName     The name to assign to the group.
     * @param ownerUsername The username of the group creator.
     */
    public Group(String groupName, String ownerUsername) {
        // Generate a random unique ID for the session
        this.groupId = UUID.randomUUID().toString();
        this.groupName = groupName;
        this.ownerUsername = ownerUsername;
        // Initialize an empty list for members
        this.members = new ArrayList<>();
    }

    /**
     * @return The unique ID of the group.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @return The display name of the group.
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @return A list containing the usernames of all group members.
     */
    public List<String> getMembers() {
        return members;
    }

    /**
     * Adds a new member to the group list after validation.
     * Corresponds to the UML operation: addMember(Patient).
     *
     * @param memberName The username of the member to add.
     */
    public void addMember(String memberName) {
        if (memberName == null) return;

        String trimmed = memberName.trim();
        // Only add the member if the name is not empty or just whitespace
        if (!trimmed.isEmpty()) {
            members.add(trimmed);
        }
    }

    /**
     * Removes a member from the group list.
     * Corresponds to the UML operation: removeMember(Patient).
     *
     * @param memberName The username of the member to remove.
     */
    public void removeMember(String memberName) {
        members.remove(memberName);
    }
}