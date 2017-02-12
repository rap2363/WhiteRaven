package memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fixed size buffer implemented with an array and two pointers for front and back.
 * @param <T> The type of value
 */
public class CircularBuffer<T> {
    private final List<T> buffer;
    private final int size;

    private int front;
    private int back;
    private boolean atCapacity;

    public CircularBuffer(final int size) {
        buffer = new ArrayList<>(Collections.nCopies(size, null));
        this.size = size;
        clear();
    }

    /**
     * Returns and removes the first value from the buffer.
     */
    public T get() {
        final T retValue = peek();
        if (retValue == null) {
            return retValue;
        }
        front = increment(front);
        atCapacity = false;
        return retValue;
    }

    /**
     * Push a value into the buffer
     *
     * @param value
     */
    public void push(final T value) {
        buffer.set(back, value);
        back = increment(back);
        if (atCapacity) {
            front = back;
        } else {
            atCapacity = (front == back);
        }
    }

    /**
     * Return the first value of the buffer without removing it from the buffer
     *
     * @return
     */
    public T peek() {
        if (front == back && !atCapacity) {
            return null;
        }
        return buffer.get(front);
    }

    /**
     * Return whether or not the buffer is saturated
     */
    public boolean full() {
        return atCapacity;
    }

    /**
     * Return whether or not the buffer is empty
     * @return
     */
    public boolean empty() {
        return !atCapacity && front == back;
    }

    /**
     * Clear the buffer
     */
    public void clear() {
        front = 0;
        back = 0;
        atCapacity = false;
    }

    /**
     * Increment a pointer
     *
     * @param value
     * @return
     */
    private int increment(int value) {
        return (value + 1) % size;
    }
}
