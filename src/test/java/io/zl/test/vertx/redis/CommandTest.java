package io.zl.test.vertx.redis;

import io.netty.handler.logging.ByteBufFormat;
import io.vertx.core.net.NetClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.impl.RequestImpl;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.core.net.NetClient;
import io.vertx.rxjava3.core.net.NetServer;
import io.vertx.rxjava3.core.net.NetSocket;
import io.vertx.rxjava3.core.net.SocketAddress;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

@ExtendWith(VertxExtension.class)
public class CommandTest {

    static Logger logger= LoggerFactory.getLogger(CommandTest.class);

    NetServer server;

    public static SocketAddress addr =  SocketAddress.inetSocketAddress(6379,"127.0.0.1");

    static NetSocket socket;
    Buffer buffer = Buffer.buffer("","utf-8");

    @BeforeAll
    public static void init(io.vertx.core.Vertx vertx, VertxTestContext context) throws Exception {
        Vertx rxVertx = Vertx.newInstance(vertx);
        NetClientOptions options = new NetClientOptions();
        options.setLogActivity(true).setActivityLogDataFormat(ByteBufFormat.SIMPLE);

        NetClient client = rxVertx.createNetClient();
        client.connect(addr).subscribe(conn -> {
            socket = conn;
            logger.info("成功获取到连接");
            context.completeNow();
        },context::failNow);
    }

    @Test
    public void write(io.vertx.core.Vertx vert,VertxTestContext context) throws Exception {
//        Buffer buffer = socket.toFlowable().blockingFirst();
//        System.out.println(buffer.toString());
        socket.write(buffer);

        socket.handler(buffer -> {
            String s = buffer.toString(Charset.defaultCharset());
            logger.info("获取到数据,[response:{}]",s);
            context.completeNow();
        });

        socket.exceptionHandler(context::failNow);
    }

    public static void main(String[] args) {
        RequestImpl request = new RequestImpl(Command.SET);
        request.arg("key");
        request.arg("value");

        System.out.println(request);
    }

}
