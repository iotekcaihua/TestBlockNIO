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
import java.util.*;

public class TestNIO1 {

    /**
     * 通过测试可知rewind方法置适用于读模式下进行重读，写模式下进行重写
     * 通过测试可知flip方法用于读切换成写
     * 通过测试可知clear方法用于写切换成读
     *
     * @throws IOException
     */

    @Test
    public void Server() throws IOException, InterruptedException {
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
        //创建一个读队列
        LinkedList<SelectionKey> readQueue = new LinkedList<>();
        //6.轮询式的获取选择器上已经准备就绪的事件
        //只有事件处于就绪状态才会生成selectionkey，同一个通道注册的selectionkey是相同的，可以复用;
        // 当事件完成后，selectionkey状态改变，当该事件再次触发，状态再次回归到就绪
        while (selector.select() > 0) {
            //7.获取选择器上所有注册的监听事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            //通道一旦连接上就有写的事件发生，一旦client端写入数据到通道内，server端就有通道的读事件发生
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
                    //首先要判断该事件是否在读的队列中，如果在的话，就不处理，让读线程处理完后会移除该key，此时该事件也就结束了
                    if (readQueue.indexOf(selectionKey) != -1) {
                        //队列中有该事件,取消该事件
                        iterator.remove();
                        continue;
                    }
                    //队列中不存在该事件,将该事件添加进队列
                    readQueue.add(selectionKey);
                    //只要client端一旦write了数据，立马开启一个线程去读数据
                    new Thread(() -> {
                        //13.获取当前读准备就绪状态的通道
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        //14.读取数据
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = 0;
                        //只有当socketChannel断开连接的时候才会read（）到-1
                        try {
                            //只要断开连接就返回不进行读写
                          /*  if (socketChannel.read(buffer) == -1) {
                                socketChannel.close();
                                return;
                            }*/
                            while ((len = socketChannel.read(buffer)) > 0) {
                                buffer.flip();
                                System.out.println(new String(buffer.array(), 0, len));
                                buffer.clear();
                                readQueue.remove(selectionKey);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
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
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            byteBuffer.put(scanner.next().getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            socketChannel.close();
        }
    }


    public static void main(String[] args) throws IOException {
        //1.创建一个通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8859));
        //2.切换成非阻塞模式
        socketChannel.configureBlocking(false);
        //3.创建一个缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
        //4.发送时间给服务器
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String str = scanner.next();
            byteBuffer.put(str.getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        socketChannel.close();
    }


    @Test
    public void s() throws InterruptedException {
        ArrayList<String> list = new ArrayList<>();
        list.add("A");
        list.add("V");
        list.add("C");
        Iterator<String> iterator = list.iterator();
        new Thread(() -> {
            list.add("B");
            System.out.println(list);
        }).start();
        while (iterator.hasNext()) {
            Thread.sleep(1000);
            if ("V".equals(iterator.next())) {
                iterator.remove();
            }
        }
        System.out.println(list);
    }

}
