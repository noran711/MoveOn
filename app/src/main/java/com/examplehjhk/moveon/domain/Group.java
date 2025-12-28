package com.examplehjhk.moveon.domain;

import java.util.List;

public class Group {

    private String groupId;
    private String groupName;
    private List<Patient> members;
    private Patient owner;

    public void addMember(Patient patient) {
        // Logic to add a member
    }

    public void removeMember(Patient patient) {
        // Logic to remove a member
    }

    public void sendInvite(Patient patient) {
        // Logic to send an invitation
    }

    // Standard Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Patient> getMembers() {
        return members;
    }

    public void setMembers(List<Patient> members) {
        this.members = members;
    }

    public Patient getOwner() {
        return owner;
    }

    public void setOwner(Patient owner) {
        this.owner = owner;
    }
}
