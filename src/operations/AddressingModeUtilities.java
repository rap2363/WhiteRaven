package operations;

import snes.CPU;

/**
 * Stateless utilities class that is used in conjunction with Operations to
 * retrieve specific bytes for various addressing modes
 * 
 */
public final class AddressingModeUtilities {
    private AddressingModeUtilities() {
    }

    /**
     * Returns a byte from applying this addressing mode
     * 
     * @param addressingMode
     * @param cpu
     * @param bytes
     * @return
     */
    public static byte getValue(AddressingMode addressingMode, CPU cpu, byte[] bytes) {

        if (addressingMode == AddressingMode.Immediate) {
            return getValueImmediate(cpu, bytes);
        } else if (addressingMode == AddressingMode.ZeroPage) {
            return getValueZeroPage(cpu, bytes);
        } else if (addressingMode == AddressingMode.ZeroPageX) {
            return getValueZeroPageX(cpu, bytes);
        } else if (addressingMode == AddressingMode.Absolute) {
            return getValueAbsolute(cpu, bytes);
        } else if (addressingMode == AddressingMode.AbsoluteX) {
            return getValueAbsoluteX(cpu, bytes);
        } else if (addressingMode == AddressingMode.AbsoluteY) {
            return getValueAbsoluteY(cpu, bytes);
        } else if (addressingMode == AddressingMode.IndirectX) {
            return getValueIndirectX(cpu, bytes);
        } else if (addressingMode == AddressingMode.IndirectY) {
            return getValueIndirectY(cpu, bytes);
        }

        return 0;
    }

    /**
     * Immediate addressing mode: (e.g. LDA #$3e --> Load accumulator with 62)
     * 
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueImmediate(CPU cpu, byte[] bytes) {
        return bytes[0];
    }

    /**
     * ZeroPage addressing mode: (e.g. STA $02 --> Store accumulator into the
     * value in $02)
     * 
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueZeroPage(CPU cpu, byte[] bytes) {
        return cpu.memory.read(Utilities.toUnsignedValue(bytes[0]));
    }

    /**
     * ZeroPage,X addressing mode: (e.g. ADC $04,X --> Add accumulator to value
     * at $($04 + X))
     * 
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueZeroPageX(CPU cpu, byte[] bytes) {
        return cpu.memory.read(Utilities.toUnsignedValue((byte) (bytes[0] + cpu.X.readAsByte())));
    }

    /**
     * Absolute addressing mode: (e.g. JMP #$3249 --> Set PC to #$3249)
     * 
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueAbsolute(CPU cpu, byte[] bytes) {
        return cpu.memory.read(Utilities.toUnsignedValue(bytes[0], bytes[1]));
    }

    /**
     * Absolute,X addressing mode: (e.g. STA $3000,X --> Store accumulator byte
     * at $($3000 + X))
     * 
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueAbsoluteX(CPU cpu, byte[] bytes) {
        return cpu.memory.read(
                Utilities.addByteToUnsignedInt(Utilities.toUnsignedValue(bytes[0], bytes[1]), cpu.X.readAsByte()));
    }

    /**
     * Absolute,X addressing mode: (e.g. STA $3000,Y --> Store accumulator byte
     * at $($3000 + Y)
     * 
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueAbsoluteY(CPU cpu, byte[] bytes) {
        return cpu.memory.read(
                Utilities.addByteToUnsignedInt(Utilities.toUnsignedValue(bytes[0], bytes[1]), cpu.Y.readAsByte()));
    }

    /**
     * Indirect,X addressing mode: (e.g. ADC $30,X --> Add X to $30 to get a new
     * address which points to the LSB (low). One after it is the MSB.
     * Concatenate the bytes to obtain a new address which contains the final
     * value.)
     * 
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueIndirectX(CPU cpu, byte[] bytes) {
        int targetAddress = Utilities.toUnsignedValue((byte) (bytes[0] + cpu.X.readAsByte()));
        byte low = cpu.memory.read(targetAddress);
        byte high = cpu.memory.read(Utilities.addByteToUnsignedInt(targetAddress, (byte) 0x01));
        return cpu.memory.read(Utilities.toUnsignedValue(high, low));
    }

    /**
     * Indirect,Y addressing mode: (e.g. ADC $20,Y --> $20 and $21 contain the
     * LSB and MSB respectively. Concatenate the two bytes to obtain a new
     * address. Add the value in Y to that address and read from that area in
     * memory.)
     * 
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueIndirectY(CPU cpu, byte[] bytes) {
        int targetAddress = Utilities.toUnsignedValue(bytes[0]);
        byte low = cpu.memory.read(targetAddress);
        byte high = cpu.memory.read(Utilities.addByteToUnsignedInt(targetAddress, (byte) 0x01));
        int concatenatedTarget = cpu.memory.read(Utilities.toUnsignedValue(high, low));
        return cpu.memory.read(Utilities.addByteToUnsignedInt(concatenatedTarget, cpu.Y.readAsByte()));

    }
}
