package cn.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpChunkAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by liu_penghui on 2017/11/3.
 */
public class DispatcherServletChannelInitializer extends ChannelInitializer<SocketChannel> {

    private DispatcherServlet dispatcherServlet;

    public DispatcherServletChannelInitializer() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        MockServletConfig servletConfig = new MockServletConfig(servletContext);
        servletConfig.addInitParameter("contextConfigLocation","classpath:/META-INF/spring/root-context.xml");
        servletContext.addInitParameter("contextConfigLocation","classpath:/META-INF/spring/root-context.xml");
        XmlWebApplicationContext wac = new XmlWebApplicationContext();
        wac.setServletConfig(servletConfig);
        wac.setServletContext(servletContext);
        wac.setConfigLocation("classpath:/servlet-context.xml");
        wac.refresh();
        this.dispatcherServlet = new DispatcherServlet(wac);
        this.dispatcherServlet.init(servletConfig);
    }

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //pipeline.addLast("ssl", new SslHandler(engine));
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("handler", new ServletNettyHandler(this.dispatcherServlet));
    }
    @Configuration
    @EnableWebMvc
    @ComponentScan(basePackages="org.springframework.sandbox.mvc")
    static class WebConfig extends WebMvcConfigurerAdapter {

        public void addCorsMappings(CorsRegistry registry) {

    		/*registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE");
    		*/
            super.addCorsMappings(registry);
        }
    }
}
