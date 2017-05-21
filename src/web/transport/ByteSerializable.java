package web.transport;

import java.io.Serializable;

/**
 * Objects that implement this interface can serialize/deserialize into an array of bytes. This is used for building
 * custom network protocols.
 */
public interface ByteSerializable<T> extends Serializable {
    /**
     * Serialize the object into a byte array
     *
     * @return
     */
    byte[] serialize();
}
