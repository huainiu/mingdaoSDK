package com.mingdao.api.common;

import com.mingdao.api.entity.ResponseObject;
import com.mingdao.api.htf.core.NeteaseEncrypt;
import com.mingdao.api.utils.HttpClientUtil;
import com.mingdao.api.utils.HttpURLConnectionUtil;
import com.mingdao.api.utils.HttpsUtil;
import com.mingdao.api.utils.RequestType;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhenjiaWang
 * Date: 13-5-15TimeUtils
 * Time: 下午5:46
 * To change this template use File | Settings | File Templates.
 */
public class CommonSupport {
    public static ResponseObject requestAPI(Map<String, String> params, String URI, RequestType requestType) throws Exception {
        ResponseObject responseObject = null;
        if (requestType == RequestType.GET) {
            StringBuilder sb = new StringBuilder();
            if (params != null && !params.isEmpty()) {
                for (String key : params.keySet()) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(key + "=" + params.get(key));
                }
                URI += "?" + sb.toString();
                sb = null;
            }
            responseObject = HttpsUtil.httpByGet2RObjSSL(URI, null, null);
        } else if (requestType == RequestType.POST) {
            responseObject = HttpURLConnectionUtil.sendSSLPostRequest(URI, params);
        } else if (requestType == RequestType.POST_XML) {
            responseObject = HttpsUtil.httpByPostXMLObjSSL(URI, params, null, null);
        } else if (requestType == RequestType.POST_JSON) {
            responseObject = HttpsUtil.httpByPostJSONObjSSL(URI, params, null, null);
        }
        if (responseObject != null) {
            responseObject.setError(true);
            if (responseObject.getStatusCode() == 200) {
                String result = responseObject.getResult();
                if (StringUtils.isNotBlank(result)) {
                    JSONObject rootObject = null;
                    try {
                        rootObject = JSONObject.fromObject(result);
                        responseObject.setJsonObject(rootObject);
                    } catch (Exception eee) {
                        String resText = result;
                        if (resText.indexOf("sign=") != -1 && resText.indexOf("&msg=") != -1) {
                            String msg = resText.substring(resText.indexOf("&msg=") + "&msg=".length());
                            if (StringUtils.isNotBlank(msg)) {
                                result = NeteaseEncrypt.base64Decoder(msg);
                                if (StringUtils.isNotBlank(result)) {
                                    responseObject.setResult(result);
                                    rootObject = JSONObject.fromObject(result);
                                    responseObject.setJsonObject(rootObject);
                                }
                            }
                        }
                    }
                    if (rootObject != null) {
                        String error_code = null;
                        try {
                            if (rootObject.has("error_code")) {
                                error_code = rootObject.getString("error_code");
                                if (StringUtils.isNotBlank(error_code)) {
                                    responseObject.setErrorCode(error_code);
                                    responseObject.setError(true);
                                    if (params != null && !params.isEmpty()) {
                                        StringBuilder sb = new StringBuilder();
                                        for (String key : params.keySet()) {
                                            if (sb.length() > 0) {
                                                sb.append("&");
                                            }
                                            sb.append(key + "=" + params.get(key));
                                        }
                                        URI += "?" + sb.toString();
                                        sb = null;
                                    }
                                    System.out.println("request:method[" + requestType.toString() + "]" + URI + "  错误代码:" + responseObject.getErrorCode());
                                }
                            }else if(rootObject.has("retcode")){
                                error_code = rootObject.getString("retcode");
                                if (StringUtils.isNotBlank(error_code)) {
                                    if(error_code.equals("0000")){
                                        error_code=null;
                                    }else{
                                        System.out.println("request:method[" + requestType.toString() + "]" + URI + "  错误代码:" + error_code+" "+rootObject.getString("retmsg"));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            error_code = null;
                        }
                        if (StringUtils.isBlank(error_code)) {
                            responseObject.setError(false);
                        }
                    }
                }
            }else{
                System.out.println("request:method[" + requestType.toString() + "]" + URI + "  STATUS_CODE:" + responseObject.getStatusCode());
            }
        }
        return responseObject;
    }
}
