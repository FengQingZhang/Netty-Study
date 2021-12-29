package com.feng.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;


public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取数据（这里我们可以读取客户端发送的消息）
     * @param ctx 上下文对象，含有管道pipeline，管道channel，地址
     * @param msg 就是客户端发送的数据，默认Object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //比如这里我们有一个非常耗时长的业务->异步执行->提交该channel对应的
        //NIOEventLoop 的taskQueue中，
        //解决方法1 用户程序自定义的普通任务
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(10*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端，汪1",CharsetUtil.UTF_8));
                    System.out.println("channel code=" +ctx.channel().hashCode());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        //用户自定义定义任务-> 该任务是提交到scheduleTaskQueue中
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(10*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端，汪2",CharsetUtil.UTF_8));
                    System.out.println("channel code=" +ctx.channel().hashCode());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },5, TimeUnit.SECONDS);
        System.out.println("go on ...");
        /*
        System.out.println("服务器读取线程："+Thread.currentThread().getName());
        System.out.println("server ctx="+ctx);
        ChannelPipeline pipeline =ctx.pipeline();//本质是一个双向链表，出站入站
        Channel channel = ctx.channel();
        //ByteBuf是Netty提供的，不是NIO的ByteBuffer
        ByteBuf byteBuf =(ByteBuf) msg;
        System.out.println("客户端发送的消息："+byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址："+channel.remoteAddress());
        */
    }

    /**
     * 数据读取完毕
     * @param ctx 上下文对象，含有管道pipeline，管道channel，地址
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将数据写人到缓存，并刷新
        //一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端(>^ω^<)喵",CharsetUtil.UTF_8));
    }

    /**
     * 处理异常，一般是需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
