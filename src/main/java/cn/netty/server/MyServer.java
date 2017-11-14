package cn.netty.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
//import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by liu_penghui on 2017/11/3.
 */
public class MyServer {

    //private static final Logger Log = Logger.getLogger(MyServer.class);

    private final int port;

    public MyServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        ServerBootstrap server = new ServerBootstrap();
        try {
            server.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class).localAddress(port)
                    .childHandler(new DispatcherServletChannelInitializer());
            server.bind().sync().channel().closeFuture().sync();
        }
        finally {
            server.shutdown();
        }
    }

    private static Properties properties = new Properties();
    static {
        ClassLoader classLoader = MyServer.class.getClassLoader();
        InputStream ports = classLoader.getResourceAsStream("server.properties");
        try {
            properties.load(ports);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param key
     * @return
     * 获取端口号
     */
    public static String getPort(String key) {
        return properties.getProperty(key);
    }
    public static void main(String[] args) throws Exception {
        String port = getPort("server.port");
        if(port == null || "".equals(port)) {
            port = "8081";
        }
        new MyServer(Integer.parseInt(port)).run();
        //Log.info("Server Starting...");
    }

}
