import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

public class TestNIO1 {

    /**
     * 通过测试可知rewind方法置适用于读模式下进行重读，写模式下进行重写
     * 通过测试可知flip方法用于读切换成写
     * 通过测试可知clear方法用于写切换成读
     *
     * @throws IOException
     */

    @Test
    public void Server() throws IOException {
        //1.创建一个通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2.切换成非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //3.绑定连接
        serverSocketChannel.bind(new InetSocketAddress(8859));
        //4.获取选择器
        Selector selector = Selector.open();
        //5.将通道注册到监听器上，并注册监听事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6.轮询式的获取选择器上已经准备就绪的事件
        while (selector.select() > 0) {
            //7.获取选择器上所有注册的监听事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                //8.获取准备就绪的事件
                SelectionKey selectionKey = iterator.next();
                //9.判断具体是什么事件准备就绪
                if (selectionKey.isAcceptable()) {
                    //10.若接收就绪，就去获取通道
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //11.切换非阻塞模式
                    socketChannel.configureBlocking(false);
                    //12.注册通道到selector上，事件为读
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    //13.获取当前读准备就绪状态的通道
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    //14.读取数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len=0;
                    while((len=socketChannel.read(buffer))>0){
                        System.out.println(len);
                        buffer.flip();
                        System.out.println(new String(buffer.array(),0,len));
                        buffer.clear();
                    }
                    socketChannel.close();
                }
                //15.取消选择键
                iterator.remove();
            }
        }
    }

    @Test
    public void Client() throws IOException {
        //1.创建一个通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8859));
        //2.切换成非阻塞模式
        socketChannel.configureBlocking(false);
        //3.创建一个缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        //4.发送时间给服务器
        byteBuffer.put(LocalDateTime.now().toString().getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();
        socketChannel.close();
    }

    @Test
    public void buffer() throws IOException {
        FileChannel.open(Paths.get(""), StandardOpenOption.CREATE);
    }

}
