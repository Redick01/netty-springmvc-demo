package cn.netty.util;

import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.request.ZhimaAuthInfoAuthorizeRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditScoreGetRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaDataBatchFeedbackRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaAuthInfoAuthorizeResponse;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditScoreGetResponse;
import com.antgroup.zmxy.openplatform.api.response.ZhimaDataBatchFeedbackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by liu_penghui on 2017/12/12.
 */
public class ZhimaDataFeedbackUtil {

    private static final Logger Log = LoggerFactory.getLogger(ZhimaDataFeedbackUtil.class);

    //芝麻开放平台地址
    private String gatewayUrl = "https://zmopenapi.zmxy.com.cn/openapi.do";
    //商户应用Id
    private String appId = "1001713";
    //商户要将生成的公私密钥对提供给芝麻，芝麻也会提供他们生成的密钥对给商户
    //商户RSA私钥
    private String privateKey = "/rsa_key/rsa_private_key_pkcs8.pem";
    //芝麻RSA公钥..芝麻信用生成的公钥
    private String zhimaPublicKey = "";

    /**
     * 获取芝麻ID
     * @param uname 姓名
     * @param certno 身份证号
     * @return
     */
    public String zhimaAuthInfoAuthquery(String uname, String certno) {
        ZhimaAuthInfoAuthorizeRequest request = new ZhimaAuthInfoAuthorizeRequest();
        request.setChannel("apppc");
        request.setPlatform("zmop");
        request.setIdentityType("2");// 必要参数
        request.setIdentityParam("{\"name\":\""+uname+"\",\"certType\":\"IDENTITY_CARD\",\"certNo\":\""+certno+"\"}");// 必要参数
        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
        try {
            ZhimaAuthInfoAuthorizeResponse response = client.execute(request);
            if (response.isSuccess()) {
                Log.info("芝麻ID为：" + response.getOpenId());
                System.out.println("芝麻ID为：" + response.getOpenId());
                return response.getOpenId();
            }
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取芝麻分
     * @param openId 芝麻ID
     * @return
     */
    public String getZhimaScore(String openId) {
        ZhimaCreditScoreGetRequest request = new ZhimaCreditScoreGetRequest();
        //创建业务流水凭证，以当前时间作为前缀，uuid为后缀
        String transactionId = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
                + UUID.randomUUID().toString();
        request.setTransactionId(transactionId);// 必要参数，业务流水凭证
        request.setProductCode("w1010100100000000001");// 必要参数，这个值对于芝麻分产品是固定的，无需修改
        request.setOpenId(openId);// 必要参数，授权获得的openid
        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
        try {
            ZhimaCreditScoreGetResponse response = client.execute(request);
            // TODO 将业务流水凭证与响应内容持久化到DB，便于后续对账
            System.out.println("transactionId=" + transactionId + ";请求完整响应=" + response.getBody());
            if (response.isSuccess()) {
                // 打印芝麻分
                Log.info(openId + "芝麻分为：" + response.getZmScore());
                System.out.println(openId + "用户芝麻信用评分=" + response.getZmScore());
                return response.getZmScore();
            } else {
                Log.error("错误代码：" + response.getErrorCode() + "--" + "错误信息" + response.getErrorMessage());
                System.out.println("错误代码：" + response.getErrorCode() + "--" + "错误信息" + response.getErrorMessage());
            }
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void testZhimaDataFeedback() {
        ZhimaDataBatchFeedbackRequest req = new ZhimaDataBatchFeedbackRequest();
        req.setChannel("apppc");
        req.setPlatform("zmop");
        req.setFileType("json_data");
        req.setFileCharset("UTF-8");
        req.setRecords("100");
        req.setColumns("");
        req.setPrimaryKeyColumns("order_no,pay_month");
        req.setFileDescription("文件描述信息");
        req.setTypeId("100003-xxxxx-test、100003-xxxxx-order");
        req.setBizExtParams("{\"extparam1\":\"value1\"}");
        //req.setFile(new FileItem("C:\test_file"));// 必要参数
        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
        try {
            ZhimaDataBatchFeedbackResponse response = client.execute(req);
            System.out.println(response.isSuccess());
            System.out.println(response.getErrorCode());
            System.out.println(response.getErrorMessage());

        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
    }
}
