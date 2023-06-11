package io.zl;


import io.vertx.codegen.annotations.DataObject;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.impl.RESPEncoder;
import io.vertx.redis.client.impl.RequestImpl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * redis的请求
 */
@DataObject
public class Request {

    private static final byte[] EMPTY_BULK = "$0\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final byte[] NULL_BULK = "$4\r\nnull\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] EOL = "\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] TRUE = new byte[]{'t'};
    private static final byte[] FALSE = new byte[]{'f'};

    String command;

    List<byte[]> args;

    public Request(String cmd) {
        this.command = cmd;
        args = new ArrayList<byte[]>();
    }

    public Request arg(Long arg) {
        return arg(numToBytes(arg));
    }

    public Request arg(String arg) {
        return arg(arg.getBytes(StandardCharsets.UTF_8));
    }

    public Request arg(byte[] bytes) {
        args.add(bytes);
        return this;
    }

    public ByteBuffer encode() {
        return encode(ByteBuffer.allocate(4096));
    }

    public ByteBuffer encode(ByteBuffer buffer) {
        buffer.put((byte) '*')
                .put(numToBytes(args.size() + 1))
                .put(EOL)
                .put(cmdBytes());

        for (byte[] arg : args) {
            buffer.put((byte) '$')
                    .put(numToBytes(arg.length))
                    .put(EOL)
                    .put(arg).put(EOL);
        }

        return buffer;
    }

    public byte[] numToBytes(long value) {
        return RESPEncoder.numToBytes(value);
    }

    public  byte[] cmdBytes(){
        return   ("$" + command.length() + "\r\n" + command + "\r\n").getBytes(StandardCharsets.ISO_8859_1);
    }

    public static Request cmd(String command) {
        return new Request(command);
    }
}
