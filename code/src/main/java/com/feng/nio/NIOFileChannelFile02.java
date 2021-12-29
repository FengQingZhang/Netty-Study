package com.feng.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannelFile02 {

    public static void main(String[] args) throws IOException {

        File file = new File("d:\\file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();
        //
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        //从管道读取数据到缓冲区；
        fileChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();
    }
}
