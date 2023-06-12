package io.zl.test.proto;

import io.zl.Response;
import io.zl.handler.RESPHandler;
import io.zl.handler.ResponseHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HandlerTest {

    static RESPHandler handler;


    public static final ByteBuffer ERROR_BUFFER= ByteBuffer.wrap("-Error message\r\n".getBytes(StandardCharsets.ISO_8859_1));

    public static final ByteBuffer STRING_BUFFER= ByteBuffer.wrap("+VALUE\r\n".getBytes(StandardCharsets.ISO_8859_1));

    public static final ByteBuffer INTEGER_BUFFER= ByteBuffer.wrap(":200\r\n".getBytes(StandardCharsets.ISO_8859_1));

    public static final ByteBuffer BULK_BUFFER= ByteBuffer.wrap("$6\r\nfoobar\r\n".getBytes(StandardCharsets.ISO_8859_1));

    public static final ByteBuffer EMPTY_BULK_BUFFER= ByteBuffer.wrap("$6\r\nfoobar\r\n".getBytes(StandardCharsets.ISO_8859_1));

    public static final ByteBuffer NULL_BULK_BUFFER= ByteBuffer.wrap("$6\r\nfoobar\r\n".getBytes(StandardCharsets.ISO_8859_1));


    @BeforeAll
    static public void init(){
        handler = new RESPHandler(new ResponseHandler());
    }

    @Test
    public void stringParseTest(){
        Response response = handler.paresToResponse(STRING_BUFFER);
        System.out.println(response);
    }

    @Test
    public void errorParseTest(){
        Response response = handler.paresToResponse(ERROR_BUFFER);
        System.out.println(response);
    }

    @Test
    public void numberParseTest(){
        Response response = handler.paresToResponse(INTEGER_BUFFER);
        System.out.println(response);
    }

    @Test
    public void bulkParseTest(){
        Response response = handler.paresToResponse(BULK_BUFFER);
        System.out.println(response);
    }

}
