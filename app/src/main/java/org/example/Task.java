package org.example;

public class Task {
    private String text;
    private boolean completed;

    public Task() {
    }

    public Task(String text) {
        this.text = text;
        this.completed = false;
    }

    public String getText() {
        return text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return text;
    }
}
