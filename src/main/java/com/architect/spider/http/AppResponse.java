package com.architect.spider.http;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 返回给客户端的Response
 */
public class AppResponse {
    public static final int RESULT_CODE_SUCCESS = 0; // 成功
    public static final int RESULT_CODE_ERROR = -1; // 失败
    public static final int RESULT_CODE_PARAMS_ERROR = -100; // 参数错误

    public static final String ERROR_MSG_SUCCESS = "OK";
    public static final String ERROR_MSG_UNKNOWN = "未知错误";
    public static final String ERROR_MSG_ILLEGAL_PARAMS = "请求参数不合法";
    public static final String ERROR_MSG_JSON_EXCEPTION = "josn解析异常";
    public static final String ERROR_MSG_GET_DATA_FAILED = "获取数据失败";
    public static final String ERROR_MSG_NO_DATA = "数据不存在";
    public static final String ERROR_MSG_DB_FAILED = "数据库操作失败";
    public static final String ERROR_MSG_HTTP_FAILED = "http接口通信异常";
    public static final String ERROR_MSG_NO_DEVICE = "当前OBD设备不存在";

    private int resultCode = RESULT_CODE_ERROR; // 接口执行结果
    private String errorMsg = ERROR_MSG_UNKNOWN; // 错误提示信息
    private Object content; // 返回的数据

    public AppResponse() {
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setFailed(int resultCode, String errorMessage) {
        this.resultCode = resultCode;
        this.errorMsg = errorMessage;
    }

    public void setSuccessed(Object responseMessage) {
        this.resultCode = RESULT_CODE_SUCCESS;
        this.errorMsg = ERROR_MSG_SUCCESS;
        this.content = responseMessage;
    }

    public JSONObject getJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", resultCode);
        jsonObject.put("errorMsg", errorMsg);
        jsonObject.put("data", content);
        return jsonObject;
    }

    public InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(getJsonObject().toString()
                    .getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

}
