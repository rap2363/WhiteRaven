package operations;

import nes.CPU;

/**
 * Stateless utilities class that is used in conjunction with Operations to
 * retrieve specific bytes for various addressing modes
 *
 */
public final class AddressingModeUtilities {
    private AddressingModeUtilities() {}

    /**
     * Return the unsigned integer address from applying this addressing mode
     *
     * @param addressingMode
     * @param cpu
     * @param bytes
     * @return
     */
    public static int getAddress(AddressingMode addressingMode, CPU cpu, byte[] bytes) {

        switch (addressingMode) {
            case ZeroPage:  return getAddressZeroPage(bytes);
            case ZeroPageX: return getAddressZeroPageX(cpu, bytes);
            case ZeroPageY: return getAddressZeroPageY(cpu, bytes);
            case Absolute:  return getAddressAbsolute(bytes);
            case AbsoluteX: return getAddressAbsoluteX(cpu, bytes);
            case AbsoluteY: return getAddressAbsoluteY(cpu, bytes);
            case Indirect:  return getAddressAbsolute(bytes);
            case IndirectX: return getAddressIndirectX(cpu, bytes);
            case IndirectY: return getAddressIndirectY(cpu, bytes);
        }

        try {
            throw new UnimplementedAddressingMode("Unimplemented getAddress() for: " + addressingMode.toString());
        } catch (UnimplementedAddressingMode e) {
            e.printStackTrace();
        }

        return -1;
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

        switch (addressingMode) {
            case Immediate: return getValueImmediate(cpu, bytes);
            case ZeroPage:  return getValueZeroPage(cpu, bytes);
            case ZeroPageX: return getValueZeroPageX(cpu, bytes);
            case ZeroPageY: return getValueZeroPageY(cpu, bytes);
            case Absolute:  return getValueAbsolute(cpu, bytes);
            case AbsoluteX: return getValueAbsoluteX(cpu, bytes);
            case AbsoluteY: return getValueAbsoluteY(cpu, bytes);
            case IndirectX: return getValueIndirectX(cpu, bytes);
            case IndirectY: return getValueIndirectY(cpu, bytes);
        }

        try {
            throw new UnimplementedAddressingMode("Unimplemented getValue() for: " + addressingMode.toString());
        } catch (UnimplementedAddressingMode e) {
            e.printStackTrace();
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
     * Return the address (on the zero page) according to the bytes read from a
     * zero-page addressing mode instruction
     *
     * @param bytes
     * @return
     */
    private static int getAddressZeroPage(byte[] bytes) {
        return Utilities.toUnsignedValue(bytes[0]);
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
        return cpu.memory.read(getAddressZeroPage(bytes));
    }

    /**
     * Return the zero page address with the value of X added to it.
     *
     * @param cpu
     * @param bytes
     * @return
     */
    private static int getAddressZeroPageX(CPU cpu, byte[] bytes) {
        return Utilities.toUnsignedValue((byte) (bytes[0] + cpu.X.readAsByte()));
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
        return cpu.memory.read(getAddressZeroPageX(cpu, bytes));
    }

    /**
     * Return the zero page address with the value of Y added to it.
     *
     * @param cpu
     * @param bytes
     * @return
     */
    private static int getAddressZeroPageY(CPU cpu, byte[] bytes) {
        return Utilities.toUnsignedValue((byte) (bytes[0] + cpu.Y.readAsByte()));
    }

    /**
     * ZeroPage,Y addressing mode: (e.g. ADC $04,Y --> Add accumulator to value
     * at $($04 + Y))
     *
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueZeroPageY(CPU cpu, byte[] bytes) {
        return cpu.memory.read(getAddressZeroPageY(cpu, bytes));
    }
    /**
     * Return the total 16-bit absolute address from concatenating the bytes
     *
     * @param bytes
     * @return
     */
    private static int getAddressAbsolute(byte[] bytes) {
        return Utilities.toUnsignedValue(bytes[1], bytes[0]);
    }

    /**
     * Absolute addressing mode: (e.g. JMP #$3249 --> Set PC to #$3249)
     *
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueAbsolute(CPU cpu, byte[] bytes) {
        return cpu.memory.read(getAddressAbsolute(bytes));
    }

    /**
     * Add an absolute address from the bytes to the X register value to get a
     * target address.
     *
     * @param cpu
     * @param bytes
     * @return
     */
    private static int getAddressAbsoluteX(CPU cpu, byte[] bytes) {
        return Utilities.addUnsignedByteToInt(Utilities.toUnsignedValue(bytes[1], bytes[0]), cpu.X.readAsByte());
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
        return cpu.memory.read(getAddressAbsoluteX(cpu, bytes));
    }

    /**
     * Add an absolute address from the bytes to the Y register value to get a
     * target address.
     *
     * @param cpu
     * @param bytes
     * @return
     */
    private static int getAddressAbsoluteY(CPU cpu, byte[] bytes) {
        return Utilities.addUnsignedByteToInt(Utilities.toUnsignedValue(bytes[1], bytes[0]), cpu.Y.readAsByte());
    }

    /**
     * Absolute,Y addressing mode: (e.g. STA $3000,Y --> Store accumulator byte
     * at $($3000 + Y)
     *
     * @param cpu
     * @param bytes
     * @return
     */
    private static byte getValueAbsoluteY(CPU cpu, byte[] bytes) {
        return cpu.memory.read(getAddressAbsoluteY(cpu, bytes));
    }

    /**
     * Obtain an address through an indirect call. Add the X register with a
     * byte to get a zero page address (with wrap-around), which points to a
     * LSB,MSB pair that contains the target address.
     *
     * @param cpu
     * @param bytes
     * @return
     */
    private static int getAddressIndirectX(CPU cpu, byte[] bytes) {
        final int targetAddress = Utilities.toUnsignedValue((byte) (bytes[0] + cpu.X.readAsByte()));
        final byte low = cpu.memory.read(targetAddress);
        final byte high;
        // Zero-Page wrap around
        if (targetAddress == 0x00FF) {
            high = cpu.memory.read(0x0000);
        } else {
            high = cpu.memory.read(Utilities.addUnsignedByteToInt(targetAddress, (byte) 0x01));
        }
        return Utilities.toUnsignedValue(high, low);
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
        return cpu.memory.read(getAddressIndirectX(cpu, bytes));
    }

    /**
     * Obtain an address through indirection by reading the LSB,MSB from the
     * byte, then add Y to the concatenated target to obtain the final address.
     *
     * @param cpu
     * @param bytes
     */
    private static int getAddressIndirectY(CPU cpu, byte[] bytes) {
        final int targetAddress = Utilities.toUnsignedValue(bytes[0]);
        final byte low = cpu.memory.read(targetAddress);
        final byte high;
        // Zero-Page wrap around
        if (targetAddress == 0x00FF) {
            high = cpu.memory.read(0x0000);
        } else {
            high = cpu.memory.read(Utilities.addUnsignedByteToInt(targetAddress, (byte) 0x01));
        }

        final int concatenatedTarget = Utilities.toUnsignedValue(high, low);
        return Utilities.addUnsignedByteToInt(concatenatedTarget, cpu.Y.readAsByte());
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
        return cpu.memory.read(getAddressIndirectY(cpu, bytes));
    }
}
