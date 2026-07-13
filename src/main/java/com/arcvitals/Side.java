package com.arcvitals;

public enum Side {
    LEFT("Left"),
    RIGHT("Right");

    private final String label;

    Side(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
