package operations;

import java.util.LinkedList;
import java.util.List;

public abstract class Instruction {
    protected List<Operation> operations;
    protected String assemblyInstructionName;

    Instruction() {
        this.operations = new LinkedList<Operation>();
    }

    public void addOperation(Operation operation) {
        this.operations.add(operation);
    }

    public List<Operation> getOperations() {
        return this.operations;
    }
}
