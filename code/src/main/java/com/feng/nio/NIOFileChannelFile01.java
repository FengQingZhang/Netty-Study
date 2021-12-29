package com.feng.nio;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannelFile01 {
    public static void main(String[] args) throws IOException {
        String str="hello world";
        //创建一个输出流->channel
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");
        //通过fileOutputStream获取对应的FileChannel
        //这个fileChannel真实类型是FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();
        //创建一个缓冲区ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //将str放入byteBuffer
        byteBuffer.put(str.getBytes());
        //切换到写入模式
        byteBuffer.flip();
        //将缓冲区的数据写到通道中
        fileChannel.write(byteBuffer);
        fileOutputStream.close();
    }
}
