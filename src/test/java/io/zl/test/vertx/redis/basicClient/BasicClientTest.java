package io.zl.test.vertx.redis.basicClient;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.redis.client.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

@ExtendWith(VertxExtension.class)
public class BasicClientTest {

    public static  final String redisUrl="redis://127.0.0.1:6379";

    public RedisConnection redisConnection;

    public RedisAPI redisAPI;
    Logger logger= LoggerFactory.getLogger(BasicClientTest.class);

    @BeforeEach
    @DisplayName("构造redis连接")
    public void init(Vertx vertx, VertxTestContext context) throws Exception {

        RedisOptions redisOperation = new RedisOptions();

        Future<RedisConnection> connectionFuture = Redis.createClient(vertx, redisOperation)
                .connect()
                .onSuccess(conn -> {
                    logger.info("获取链接成功");
                    redisConnection = conn;
                    redisAPI=RedisAPI.api(redisConnection);
                    logger.info("获取redis的连接并设置成功");
                    context.completeNow();
                }).onFailure(ex -> {
                    logger.warn("获取链接失败", ex);
                });
    }

    public Future<Response> initKeyValue(Vertx vertx, VertxTestContext context){
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("key");
        arrayList.add("value");
        return redisAPI.set(arrayList);
    }

    @Test
    public void getString(Vertx vertx,VertxTestContext context) throws InterruptedException {

        Future<Response> initFuture = initKeyValue(vertx, context);
//        Future<Response> getFuture = redisAPI.get("key");
        initFuture.onComplete(res -> {
            if (res.succeeded()){
                logger.info("设置keyValue成功,[response:{}]",res.result().toString());
                redisAPI.get("key").onComplete((getRes)->{
                    Throwable cause = getRes.cause();
                    if (Objects.nonNull(cause)){
                        logger.warn("获取key失败",cause);
                    }else {
                        String resStr = getRes.result().toString();
                        logger.info("获取key成功,[value:{}]",resStr);
                    }
                    context.completeNow();
                });
            }else {
                logger.warn("设置key错误",res.cause());
                context.failNow(res.cause());
            }

        });

    }
}
