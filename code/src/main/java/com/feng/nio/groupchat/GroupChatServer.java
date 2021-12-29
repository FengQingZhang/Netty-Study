package com.feng.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    //定义属性
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;
    //构造器
    //初始化工作
    public GroupChatServer(){
        try {
            //得到选择器
            selector = Selector.open();
            //ServerSocketChannel注册
            listenChannel = ServerSocketChannel.open();
            //绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            listenChannel.configureBlocking(false);
            //将该listenChannel注册到Selector
            listenChannel.register(selector,SelectionKey.OP_ACCEPT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){
        try {
            //循环处理
            while (true){
                int count = selector.select();
                if (count>0){//有事件处理
                    //遍历得到selectionKey集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        //取出selectionKey
                        SelectionKey key = iterator.next();
                        //监听到accept
                        if (key.isAcceptable()){
                            SocketChannel sc = listenChannel.accept();
                            sc.configureBlocking(false);
                            //将该sc注册到selector
                            sc.register(selector,SelectionKey.OP_READ);
                            //提示
                            System.out.println(sc.getRemoteAddress()+" 上线");
                        }
                        if (key.isReadable()){//通到发送read事件，即通道是可读的态度。
                            //处理读（专门）
                            readData(key);
                        }
                        iterator.remove();
                    }
                }else {
                    System.out.println("等待.....");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //发生异常处理...
        }
    }
    //读取客户端消息
    private void readData(SelectionKey key){
        //定义一个SocketChannel
        SocketChannel channel = null;
        try {
            //得到channel
            channel =(SocketChannel)key.channel();
            //创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            //根据count的值做处理
            if (count>0){
                //把缓存区的数据转为字符串
                String msg = new String(buffer.array()).trim();
                //输出该消息
                System.out.println("form 客户端:"+msg);
                //向其它的客户端转发消息(要去掉自己)，专门写一个方法来处理
                sendInfoToClients(msg,channel);
            }
        }catch (IOException e){
            try {
                System.out.println(channel.getRemoteAddress()+" 离线了..");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            ;
        }
    }

    //转发消息给其它客户（通道）
    private void sendInfoToClients(String msg,SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中...");
        //遍历 所有注册到selector上的SocketChannel，并排除self
        for (SelectionKey key:selector.keys()) {
            //通过key 取出对应的SocketChannel
            Channel targetChannel = key.channel();
            //排除自己
            if (targetChannel instanceof SocketChannel&&targetChannel!=self){
                //转型
                SocketChannel dest =(SocketChannel) targetChannel;
                //将msg存储到buffer
                ByteBuffer buffer =ByteBuffer.wrap(msg.getBytes());
                //将buffer的数据写入通道
                dest.write(buffer);
            }
        }

    }

    public static void main(String[] args) {
        //创建服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
