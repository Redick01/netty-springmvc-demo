package cn.netty.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liu_penghui on 2017/11/8.
 * http参数格式化工具
 */
public class ParameterFormatUtil {

    public JSONObject formatData(HttpServletRequest request){
        String data = request.getParameter("data");
        return null;
    }
}
