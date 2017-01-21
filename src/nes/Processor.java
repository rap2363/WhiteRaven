package nes;

/**
 * Abstract representation of a processing unit (CPU, and PPU). A processor has a cycle count and can execute
 * steps atomically in execute().
 *
 * If cycleCount is in surplus, it means we need to wait until we're in deficit again before beginning execution.
 */
public abstract class Processor {
    public long cycleCount;

    public final void executeCycle() {
        if (cycleCount <= 0) {
            this.execute();
        }
        this.cycleCount--;
    }

    public final void executeCycles(int numCycles) {
        for (int i = 0; i < numCycles; i++) {
            this.executeCycle();
        }
    }

    /**
     * To be overwritten in subclasses. Do one atomic piece of work (instruction, pixel creation, etc.), and
     * increment cycleCount as needed.
     */
    public abstract void execute();
}
