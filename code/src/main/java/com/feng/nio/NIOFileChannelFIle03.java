package com.feng.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannelFIle03 {

    public static void main(String[] args) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream("2.txt");
            FileChannel inputChannel = fileInputStream.getChannel();
            FileOutputStream fileOutputStream = new FileOutputStream("1.txt");
            FileChannel outChannel = fileOutputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(512);
            while (true){//循环读取
                /*
                   public Buffer clear() {
                        position = 0;
                        limit = capacity;
                        mark = -1;
                        return this;
                    }
                */
                byteBuffer.clear();//清空
                int read = inputChannel.read(byteBuffer);
                System.out.println("read="+ read);
                if (read==-1){//表示读完
                    break;
                }
                //buffer中的数据写入到outChannel
                byteBuffer.flip();
                outChannel.write(byteBuffer);

            }
            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cp() throws IOException {
        File file = new File("1.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel01 = fileInputStream.getChannel();
        FileOutputStream fileOutputStream = new FileOutputStream("3.txt");
        FileChannel fileChannel02 = fileOutputStream.getChannel();
        //如何文件很大，内存该溢出了吧
        ByteBuffer byteBuffer =ByteBuffer.allocate((int) file.length());
        fileChannel01.read(byteBuffer);
        byteBuffer.flip();
        fileChannel02.write(byteBuffer);
        fileInputStream.close();
        fileOutputStream.close();

    }
}
