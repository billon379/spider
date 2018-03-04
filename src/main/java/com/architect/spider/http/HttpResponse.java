package com.architect.spider.http;

/**
 * Http请求的返回结果
 */
public class HttpResponse {

    public static final int RESULT_CODE_SUCCESS = 0; // 成功
    public static final int RESULT_CODE_ERROR = -1; // 失败

    private int resultCode = RESULT_CODE_ERROR; // 接口执行结果
    private String errorMsg; // 错误提示信息
    private String content; // 返回的数据

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.resultCode = RESULT_CODE_ERROR;
        this.errorMsg = errorMsg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
