package com.feng.nio;

import java.nio.ByteBuffer;

public class NIOByteBufferPutGet {
    public static void main(String[] args) {
        //创建一个Buffer
        ByteBuffer buffer = ByteBuffer.allocate(64);
        //类型化方式放入数据
        buffer.putInt(100);
        buffer.putLong(8L);
        buffer.putChar('尚');
        buffer.putShort((short)4);
        buffer.flip();
        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getChar());
        //抛出java.nio.BufferUnderflowException异常
        //System.out.println(buffer.getLong());
        System.out.println(buffer.getShort());

    }
}
