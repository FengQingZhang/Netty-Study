package com.feng.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class GroupChatClient {
    //定义相关的属性
    private final String HOST = "127.0.0.1";//服务器的ip
    private final int PORT = 6667;//服务器端口
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    //构造器，完成初始化
    public GroupChatClient() throws IOException {
        selector = Selector.open();
        //连接服务器
        socketChannel = socketChannel.open(new InetSocketAddress(HOST,PORT));
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //注册selector到socketChannel
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到username
        username = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username+" is ok");
    }

    //向服务器发生消息
    public void sendInfo(String info){
        info = username+" 说: "+info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //读取从服务器回复的消息
    public void readInfo(){
        try {
            int readChannels =selector.select(2000);
            if (readChannels>0){//有可以用的通道
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if (key.isReadable()){
                        //得到相关的通道
                        SocketChannel sc = (SocketChannel) key.channel();
                        //得到一个Buffer
                        ByteBuffer byteBuffer =ByteBuffer.allocate(1024);
                        //从通道读取数据到缓冲区
                        sc.read(byteBuffer);
                        //把读到的数据转为字符串
                        String msg = new String(byteBuffer.array());
                        System.out.println(msg.trim());
                    }
                    iterator.remove();
                }
            }else {
                //System.out.println("没有可以用的通道...");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //启动
        GroupChatClient chatClient = new GroupChatClient();
        //启动一个线程,每个3秒，读取从服务器发送数据
        new Thread(){
            @Override
            public void run() {
                while (true){
                    chatClient.readInfo();
                    try{
                        sleep(3000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        //发送数据给服务器端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }
    }
}
