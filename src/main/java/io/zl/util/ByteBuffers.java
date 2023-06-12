package io.zl.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public abstract class ByteBuffers {

    static public String readString(ByteBuffer buffer, int endPosition) {
        return new String(readBytes(buffer,endPosition), StandardCharsets.ISO_8859_1);
    }

    static public byte[] readBytes(ByteBuffer buffer, int endPosition) {
        int position = buffer.position();
        byte[] bytes = new byte[endPosition -1  - position];

        buffer.get(bytes);
        return bytes;
    }


    static public Long readLong(ByteBuffer buffer, int endPosition) {
        byte[] bytes = ByteBuffers.readBytes(buffer,endPosition);
        return Long.parseLong(new String(bytes, StandardCharsets.US_ASCII));
    }

    public static CharBuffer fromBuffer(ByteBuffer buffer) throws CharacterCodingException {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        return decoder.decode(buffer);
    }

    public static String toStr(ByteBuffer buffer) throws CharacterCodingException {
        return fromBuffer(buffer).toString();
    }

    public static String toStrQuietly(ByteBuffer buffer) {
        try {
            return fromBuffer(buffer).toString();
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void skipEOL(ByteBuffer readBuffer) {
        int position = readBuffer.position();

        for (int i = position; i < readBuffer.limit()-1; i++) {
            if (readBuffer.get(i) == '\r' && readBuffer.get(i+1) == '\n') {
                readBytes(readBuffer,i+3);
                return;
            }
        }
    }

}
