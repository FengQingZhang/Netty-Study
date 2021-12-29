package com.feng.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //使用一个hashMap管理
    public static Map<String,Channel> channels = new HashMap<String,Channel>();

    //定义channel组，管理所有的channel
    //GlobalEventExecutor instance 是全局的事件执行器，是个单例
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     *  一旦连接建立，第一个执行
     *  将当前channel加入到channelGroup
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户端加入聊天的信息推送给其他在线的客户端
        /*
            该方法会将channelGroup中所有的channel遍历，并发送
            不需要遍历
         */
        channelGroup.writeAndFlush("[客户端] "+channel.remoteAddress()+"加入聊天"+ sdf.format(new java.util.Date())+" \n");
        channelGroup.add(channel);
        
    }

    /**
     * 断开连接，将xx客户离开信息推送给当前在线客户
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+" 离开了\n");
        System.out.println("channelGroup size"+channelGroup.size());
    }

    /**
     * 表示channel 处于活动状态，提示xx上线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+" 上线了^^");
    }

    /**
     * 表示channel处于不活动状态，提示xx离线了
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+" 离线了^^");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取到当前channel
        Channel channel = ctx.channel();
        //遍历 chanelGroup 根据不同的情况，回送不同的消息。
        channelGroup.forEach(ch->{
            if (channel!=ch){
                ch.writeAndFlush("[客户]"+ channel.remoteAddress()+" 发送了消息"+msg+" \n");
            }else {
                ch.writeAndFlush("[自己]发送了消息"+ msg+"\n");
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭
        ctx.close();
    }
}
