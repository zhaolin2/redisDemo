package io.zl;

import io.zl.util.ByteBuffers;

import java.nio.ByteBuffer;

public class BulkResponse implements Response {



    ByteBuffer buffer;

    public BulkResponse(byte[] bytes){
        this.buffer=ByteBuffer.wrap(bytes);
    }
    @Override
    public String toString() {
        return ByteBuffers.toStrQuietly(buffer);
    }

    @Override
    public ResponseType type() {
        return ResponseType.BULK;
    }
}
