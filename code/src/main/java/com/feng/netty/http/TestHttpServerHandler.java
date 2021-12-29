package com.feng.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * ps:
 * 1. SimpleChannelInboundHandler是ChannelInboundHandlerAdapter的子类
 * 2. HttpObject：客户端和服务器端互相通讯的数据被封装成HttpObject
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    /**
     * 读取客户端数据
     * @param ctx 上下文
     * @param msg 消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        //判断msg是不是HttpRequest请求
        if (msg instanceof HttpRequest){
            System.out.println("pipeline hashcode="+ctx.pipeline().hashCode());
            System.out.println("msg 类型="+msg.getClass());
            System.out.println("客户端地址"+ctx.channel().remoteAddress());
            //获取到
            HttpRequest httpRequest = (HttpRequest) msg;
            //获取uri，过滤指定资源
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())){
                System.out.println("请求了 favicon.ico,不做相应响应");
                return;
            }
            //回复信息给浏览器【http协议】
            ByteBuf content = Unpooled.copiedBuffer("hello,我是服务器", CharsetUtil.UTF_8);
            //构造一个http的响应。即HttpResponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, content);
            //设置内容类型
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            //将构建好 response 返回
            ctx.writeAndFlush(response);


        }
    }
}
