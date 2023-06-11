package io.zl.handler;

import java.nio.ByteBuffer;

public class RESPHandler {

    ResponseHandler responseHandler;

    public RESPHandler(ResponseHandler handler) {
        this.responseHandler=handler;
    }

    public void handle(ByteBuffer buffer){

    }
}
