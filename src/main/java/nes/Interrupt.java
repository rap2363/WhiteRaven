package main.java.nes;

/**
 * These interrupts are ordered by priority
 */
public enum Interrupt {
    NONE, // No interrupt
    IRQ,  // Maskable interrupt request
    NMI,  // Non-maskable interrupt
    RESET // Reset
}
