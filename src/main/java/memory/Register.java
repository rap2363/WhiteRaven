package main.java.memory;

interface Register {

    /**
     * Read the value as an unsigned integer from the register
     *
     * @return
     */
    int read();

    /**
     * Write an integer value to this register
     *
     * @param unsignedValue
     */
    void write(int unsignedValue);

    /**
     * Increment the register
     */
    void increment();

    /**
     * Increment the register n times
     *
     * @param n
     */
    default void incrementBy(int n) {
        for (int i = 0; i < n; i++) {
            increment();
        }
    }

    /**
     * Decrement the register
     */
    void decrement();

    /**
     * Decrement the register n times
     *
     * @param n
     */
    default void decrementBy(int n) {
        for (int i = 0; i < n; i++) {
            decrement();
        }
    }

    /**
     * Bit shift the register value left by n
     *
     * @param n
     */
    void shiftLeft(int n);

    /**
     * Bit shift the register value right by n
     *
     * @param n
     */
    void shiftRight(int n);
}
