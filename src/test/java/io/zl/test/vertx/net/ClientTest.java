package io.zl.test.vertx.net;

import io.netty.handler.logging.ByteBufFormat;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
public class ClientTest {

    public static Logger logger = LoggerFactory.getLogger(ClientTest.class);

    public static NetSocket netSocket;

    public static Promise<Buffer> promise;

    public static Vertx vertxInner;

    @BeforeAll
    static public void init(io.vertx.core.Vertx vertx, VertxTestContext context) throws Exception {
        NetClient netClient = vertx.createNetClient(new NetClientOptions()
                .setLogActivity(true)
                .setActivityLogDataFormat(ByteBufFormat.SIMPLE)
        );
        vertxInner=vertx;

        netClient.connect(6379, "127.0.0.1", res -> {
            if (res.succeeded()) {
                NetSocket socket = res.result();
                netSocket = socket;
                netSocket.handler(ClientTest::read);

                context.completeNow();
            } else {
                context.failNow(res.cause());
            }
        });

    }


    @Test
    public void write(VertxTestContext context) {
        Request req = Request.cmd(Command.HELLO).arg(3);

        write(req).onSuccess (buffer -> {
            logger.info("hello成功,[buffer:{}]", buffer);
            Request setRequest = Request.cmd(Command.SET).arg("key").arg("value");
            write(setRequest).onSuccess(writeRes -> {
                context.completeNow();
            }).onFailure(context::failNow);
        }).onFailure(context::failNow);
    }

    public Future<Buffer> write(Request request){
        netSocket.write(request.toString());
        promise= Promise.promise();
        return promise.future();
    }

    static public void read(Buffer buffer) {
        logger.info("readBuffer:{}", buffer);
        promise.complete(buffer);
    }
}
