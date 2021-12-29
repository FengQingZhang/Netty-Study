package com.feng.nio.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * 客户端
 */
public class NewIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",7001));
        String filename="protoc-3.6.1-win32.zip";
        //得到文件channel
        FileChannel fileChannel = new FileInputStream(filename).getChannel();
        //得到开始时间
        long startTime = System.currentTimeMillis();
        //在linux下，一次transferTo方法就可以完成传输
        //在window下，一次调用transferTo只能传送8m，就需要分段传输文件，而且要注意传输时的位置
        //transferTo 底层使用到零拷贝
        long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);
        System.out.println("发送的总的字节数="+transferCount+",耗时："+(System.currentTimeMillis()-startTime));
        //关闭
        fileChannel.close();
    }
}
