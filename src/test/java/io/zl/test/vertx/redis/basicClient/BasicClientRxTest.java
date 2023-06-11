package io.zl.test.vertx.redis.basicClient;

import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.rxjava3.redis.client.Redis;
import io.vertx.rxjava3.redis.client.RedisAPI;
import io.vertx.rxjava3.redis.client.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@ExtendWith(VertxExtension.class)
public class BasicClientRxTest {
    Logger logger= LoggerFactory.getLogger(BasicClientRxTest.class);

    @Test
    public void test(Vertx vertx, VertxTestContext context) throws Exception {
        io.vertx.rxjava3.core.Vertx vertx1 = new io.vertx.rxjava3.core.Vertx(vertx);
        Redis client = Redis.createClient(vertx1);
        RedisAPI api = RedisAPI.api(client);

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("key");
        arrayList.add("value");

        Single<Response> setSingle = api.set(arrayList).toSingle();
        Single<Response> getResponse = api.get(arrayList.get(0)).toSingle();


        setSingle.concatMap(res -> {
            logger.info("设置key成功");
//            throw new RuntimeException("123");
            return getResponse;
        }).subscribe(res -> {
            logger.info("获取key成功,[value:{}]", res.toString());
            context.completeNow();
        }, context::failNow);

//        disposable.dispose();

    }
}
