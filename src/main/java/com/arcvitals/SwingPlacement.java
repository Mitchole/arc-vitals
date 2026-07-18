package com.arcvitals;

// Where the swing timer sits. TOP/BOTTOM are horizontal crescents above/below the character,
// standalone and Alt-draggable; NESTED tucks it into the flanking group on the chosen side.
// Public because it is a config return type.
public enum SwingPlacement {
    TOP("Top"),
    BOTTOM("Bottom"),
    NESTED("Nested in group");

    private final String label;

    SwingPlacement(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
