package main.java.memory;

import main.java.operations.Utilities;

/**
 * Immutable structure to represent a sprite fetched from SPR-RAM. Use the Builder class to build sprites.
 */
public final class Sprite {
    final public int priority; // priority in SPR-RAM (lower index == higher priority)
    final public int patternTableIndex;
    final public int y; // y-coordinate of top part of sprite on the main.java.screen.
    final public int x; // x-coordinate of left part of the sprite on the main.java.screen.
    final public byte attributes;

    private Sprite(int priority, int patternTableIndex, int y, int x, byte attributes) {
        this.priority = priority;
        this.patternTableIndex = patternTableIndex;
        this.y = y;
        this.x = x;
        this.attributes = attributes;
    }

    public boolean behindBackground() {
        return Utilities.bitAt(this.attributes, 5);
    }

    public boolean flippedHorizontally() {
        return Utilities.bitAt(this.attributes, 6);
    }

    public boolean flippedVertically() {
        return Utilities.bitAt(this.attributes, 7);
    }

    public static class Builder {
        private int priority;
        private int patternTableIndex;
        private int y;
        private int x;
        private byte attributes;

        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder setPatternTableIndex(int patternTableIndex) {
            this.patternTableIndex = patternTableIndex;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setAttributes(byte attributes) {
            this.attributes = attributes;
            return this;
        }

        public Sprite build() {
            return new Sprite(priority, patternTableIndex, y, x, attributes);
        }
    }
}
