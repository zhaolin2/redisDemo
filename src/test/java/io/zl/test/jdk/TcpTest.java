package io.zl.test.jdk;

import io.vertx.redis.client.Command;
import io.zl.Request;
import io.zl.util.CharBuffers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class TcpTest {

   public static Logger logger = LoggerFactory.getLogger(TcpTest.class);

    public static final String host = "localhost";
    ;
    public static final Integer port = 6379;

    static SocketChannel socketChannel;

    @BeforeAll
    public static void init() throws IOException, InterruptedException {

        // 创建 SocketChannel 对象
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        // 连接到服务器
        socketChannel.connect(new InetSocketAddress(host, port));

        // 等待连接完成
        while (!socketChannel.finishConnect()) {
            TimeUnit.SECONDS.sleep(1);
        }

        initShakeHand();

    }

    public static void initShakeHand() throws IOException {
        socketChannel.write(helloRequestBuffer().flip());
        read();
    }

    @Test
    public void write() throws IOException {
        // 连接完成后发送数据
        ByteBuffer buffer = setRequestBuffer();
        logger.info("charBuffer:{}", CharBuffers.toStr(buffer));

        Integer write = socketChannel.write(setRequestBuffer().flip());
        logger.info("write:{} ", write);
        read();

        socketChannel.close();

    }

    public static void read() throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(2048);

        int bytesRead;
        while ((bytesRead = socketChannel.read(buffer)) != -1) {
            if (bytesRead == 0) {
                continue;
            }

            logger.info("response:{}", CharBuffers.toStr(buffer));

            buffer.clear();
            break;
        }



    }

    public ByteBuffer setRequestBuffer() {
        Request request = new Request("set");
        request.arg("key").arg("value");

        io.vertx.redis.client.Request setRequest = io.vertx.redis.client.Request.cmd(Command.SET).arg("key").arg("value");
        byte[] bytes = setRequest.toString().getBytes();

        ByteBuffer encode = request.encode();

        return request.encode();
    }

    static public ByteBuffer helloRequestBuffer() throws UnsupportedEncodingException {

//        io.vertx.redis.client.Request req = io.vertx.redis.client.Request.cmd(Command.HELLO).arg(3);
//        byte[] bytes = req.toString().getBytes("UTF-8");
//        return ByteBuffer.wrap(bytes);
        Request request = new Request("hello");
        request.arg(3L);

        return request.encode();
    }
}
