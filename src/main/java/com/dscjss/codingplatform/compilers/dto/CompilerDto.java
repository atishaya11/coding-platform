package com.dscjss.codingplatform.compilers.dto;

public class CompilerDto {

    private int id;

    private String name;

    private String version;

    private int timeLimit;

    private String aceEditorMode;

    private boolean allowed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getAceEditorMode() {
        return aceEditorMode;
    }

    public void setAceEditorMode(String aceEditorMode) {
        this.aceEditorMode = aceEditorMode;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
