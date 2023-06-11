package io.zl.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public abstract class CharBuffers {

    public static CharBuffer fromBuffer(ByteBuffer buffer) throws CharacterCodingException {
        buffer.flip();
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        return decoder.decode(buffer);
    }

    public static String toStr(ByteBuffer buffer) throws CharacterCodingException {
        return fromBuffer(buffer).toString();
    }
}
