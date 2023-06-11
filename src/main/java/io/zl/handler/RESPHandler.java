package io.zl.handler;

import io.vertx.redis.client.impl.types.ErrorType;
import io.zl.SimpleStringResponse;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RESPHandler {

    ResponseHandler responseHandler;

    ByteBuffer readBuffer=ByteBuffer.allocate(4096);
    public RESPHandler(ResponseHandler handler) {
        this.responseHandler=handler;
    }

    public void handle(ByteBuffer buffer){
        readBuffer.put(buffer);
        readBuffer.flip();

        readBuffer.mark();

        byte type = readBuffer.get(0);
        int position = readBuffer.position();
        int eol = findLineEnd(position);

        switch (type) {
            case '+':
                handleSimpleString(position, eol);
                break;
            case '-':
            case '!':
                handleError(eol);
                break;
            case ':':
            case ',':
            case '(':
                handleNumber(type, eol);
                break;
            case '=':
                handleBulk(eol, true);
                break;
            case '$':
                handleBulk(eol, false);
                break;
            case '*':
            case '%':
            case '~':
                handleMulti(type, eol);
                break;
            case '_':
                handleNull();
                break;
            case '#':
                handleBoolean();
                break;
            case '|':
                handleAttribute(eol);
                break;
            case '>':
                handlePush(eol);
                break;
            default:
                // notify
                responseHandler.fail(ErrorType.create("ILLEGAL_STATE Unknown RESP type " + (char) type));
                return;
        }

    }

    private void handlePush(int eol) {

    }

    private void handleAttribute(int eol) {

    }

    private void handleBoolean() {

    }

    private void handleNull() {


    }

    private void handleMulti(byte type, int eol) {

    }

    private void handleBulk(int eol, boolean b) {
    }


    private void handleNumber(byte type, int eol) {

    }

    private void handleError(int eol) {

    }

    private void handleSimpleString(int position, int eol) {
        int len = eol - position;
        if (len==2 && readBuffer.get(position)=='O' && readBuffer.get(position+1)=='K'){
            responseHandler.handler(SimpleStringResponse.OK);
        }else {
            byte[] bytes = new byte[eol - position];
            readBuffer.get(bytes);
            responseHandler.handler(new SimpleStringResponse(new String(bytes, StandardCharsets.ISO_8859_1)));
        }
    }


    private void handlerError(int eol) {
    }

    int findLineEnd(int offset) {
        int index = -1;
        for (int i = offset; i < readBuffer.limit(); i++) {
            if (readBuffer.get(i) == '\n') {
                index = i;
                break;
            }
        }

        return (index > 0 && readBuffer.get(index - 1) == '\r') ? index : -1;
    }
}
