package cn.netty.server;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

/**
 * Created by liu_penghui on 2017/11/4.
 */
public class ServletNettyHandler extends ChannelInboundMessageHandlerAdapter<HttpRequest> {

    private final Servlet servlet;

    private final ServletContext servletContext;

    public ServletNettyHandler(Servlet servlet) {
        this.servlet = servlet;
        this.servletContext = servlet.getServletConfig().getServletContext();
    }
    @Override
    public void messageReceived(ChannelHandlerContext ctx, HttpRequest request) throws Exception {

        if (!request.getDecoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        MockHttpServletRequest servletRequest = createServletRequest(request);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        this.servlet.service(servletRequest, servletResponse);

        HttpResponseStatus status = HttpResponseStatus.valueOf(servletResponse.getStatus());
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        //response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");

        for (String name : servletResponse.getHeaderNames()) {
            for (Object value : servletResponse.getHeaderValues(name)) {
                response.addHeader(name, value);
            }
        }
        // 解决浏览器跨域问题
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Methods","POST");
        response.setHeader("Access-Control-Allow-Headers","Access-Control");
        response.setHeader("Allow","POST");

        //servletResponse.setContentType("text/plain; charset=UTF-8");

        //String content = servletResponse.getContentAsString();

        // Write the initial line and the header.
        ctx.write(response);

        InputStream contentStream = new ByteArrayInputStream(servletResponse.getContentAsByteArray());
        //InputStream contentStream = new ByteArrayInputStream(content.getBytes("UTF-8"));

        // Write the content.
        ChannelFuture writeFuture = ctx.write(new ChunkedStream(contentStream));

        writeFuture.addListener(ChannelFutureListener.CLOSE);
    }
    /**  解析前台传参
     * @param httpRequest
     * @return
     */
    private MockHttpServletRequest createServletRequest(HttpRequest httpRequest) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(httpRequest.getUri()).build();

        MockHttpServletRequest servletRequest = new MockHttpServletRequest(this.servletContext);
        servletRequest.setRequestURI(uriComponents.getPath());
        servletRequest.setPathInfo(uriComponents.getPath());
        servletRequest.setMethod(httpRequest.getMethod().getName());
        if (uriComponents.getScheme() != null) {
            servletRequest.setScheme(uriComponents.getScheme());
        }
        if (uriComponents.getHost() != null) {
            servletRequest.setServerName(uriComponents.getHost());
        }
        if (uriComponents.getPort() != -1) {
            servletRequest.setServerPort(uriComponents.getPort());
        }

        for (String name : httpRequest.getHeaderNames()) {
            for (String value : httpRequest.getHeaders(name)) {
                servletRequest.addHeader(name, value);
            }
        }

        servletRequest.setContent(httpRequest.getContent().array());

        try {
            if (uriComponents.getQuery() != null) {
                String query = UriUtils.decode(uriComponents.getQuery(), "UTF-8");
                servletRequest.setQueryString(query);
            }
            // 读取从客户端传过来的参数
            if (httpRequest.getMethod() == HttpMethod.POST) { // 处理POST请求
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
                        new DefaultHttpDataFactory(false), httpRequest);
                List<InterfaceHttpData>  postDatas = decoder.getBodyHttpDatas(); // //
                for (InterfaceHttpData postData: postDatas) {
                    String question = "",name ="";
                    if (postData.getHttpDataType() == HttpDataType.Attribute) {
                        Attribute attribute = (Attribute) postData;
                        question = attribute.getValue();
                        name = attribute.getName();

                        servletRequest.addParameter(
                                UriUtils.decode(name, "UTF-8"),
                                UriUtils.decode(question, "UTF-8"));
                        System.out.println("==key=="+name+"==value=="+question);
                    }
                }

            }
            for (Entry<String, List<String>> entry : uriComponents.getQueryParams().entrySet()) {
                for (String value : entry.getValue()) {
                    servletRequest.addParameter(
                            UriUtils.decode(entry.getKey(), "UTF-8"),
                            UriUtils.decode(value, "UTF-8"));
                    System.out.println("==key=="+entry.getKey()+"==value=="+value);
                }
            }
        }catch (Exception ex) {
            //logger.error("UnsupportedEncodingException=="+ex.getMessage());
            ex.printStackTrace();
        }

        return servletRequest;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(Unpooled.copiedBuffer(
                "Failure: " + status.toString() + "\r\n",
                CharsetUtil.UTF_8));

        // 发送消息错误关闭连接.
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }
}
