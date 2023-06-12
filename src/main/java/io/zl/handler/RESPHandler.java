package io.zl.handler;

import io.zl.*;
import io.zl.util.ByteBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RESPHandler {

    public static Logger logger = LoggerFactory.getLogger(RESPHandler.class);

    ResponseHandler responseHandler;

    ByteBuffer readBuffer = ByteBuffer.allocate(4096);

    public RESPHandler(ResponseHandler handler) {
        this.responseHandler = handler;
    }

    public void handle(ByteBuffer buffer) {
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
                handleBulk(eol);
                break;
            case '$':
                handleBulk(eol);
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
                logger.warn("无法识别当前type:{}",(char)type);
                return;
        }

    }


    public Response paresToResponse(ByteBuffer buffer) {

        readBuffer=buffer;
        Response response = null;
//        readBuffer.flip();

        readBuffer.mark();

        byte type = readBuffer.get();
        int position = readBuffer.position();
        int eol = findLineEnd(position);

        switch (type) {
            case '+':
                return handleSimpleString(position, eol);
            case '-':
            case '!':
               return handleError(eol);
            case ':':
            case ',':
            case '(':
                return handleNumber(type, eol);
            case '=':
                return handleBulk(eol);
            case '$':
                return handleBulk(eol);
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
                logger.warn("无法识别当前type:{}",(char)type);
        }

        return null;

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

    private Response handleBulk(int eol) {
        Long len = ByteBuffers.readLong(readBuffer, eol);
        ByteBuffers.skipEOL(readBuffer);

        byte[] bytes = ByteBuffers.readBytes(readBuffer, (int) (readBuffer.position()+len+1));
        return new BulkResponse(bytes);
    }


    private Response handleNumber(byte type, int eol) {

        Number number = null;
        byte[] bytes = ByteBuffers.readBytes(readBuffer, eol);

        switch (type) {
            case ':':
                //integer
                number = Long.parseLong(new String(bytes, StandardCharsets.US_ASCII));
                break;
            case ',':
                //decimal
                number = Double.parseDouble(new String(bytes, StandardCharsets.US_ASCII));
                break;
            case '(':
                //bigint
                bytes = ByteBuffers.readBytes(readBuffer, eol);
                number = new BigInteger(bytes);
                break;
            default:
                logger.warn("获取redis错误,[format:{}]",(char)type);
//                responseHandler.handler().fail(new NumberFormatException("Invalid REDIS format: [" + (char) type + "]"));
                break;
        }


        return new NumberResponse(number);
    }

    private Response handleError(int eol) {
        ErrorResponse response = new ErrorResponse(ByteBuffers.readString(readBuffer,eol));
        responseHandler.handler(response);

        return response;
    }

    private Response handleSimpleString(int position, int eol) {
        Response response = null;
        int len = eol - position;
        if (len == 2 && readBuffer.get(position) == 'O' && readBuffer.get(position + 1) == 'K') {
            responseHandler.handler(SimpleStringResponse.OK);
        } else {
            byte[] bytes = new byte[eol - 1  - position];
            readBuffer.get(bytes);
            response = new SimpleStringResponse(new String(bytes, StandardCharsets.ISO_8859_1));
            responseHandler.handler(response);
        }

        return response;
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
