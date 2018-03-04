package com.architect.spider.http;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 对HttpClient进行简单封装
 */
public class HttpClient {

    /**
     * OK: Success!
     */
    private static final int OK = 200;

    /**
     * Not Modified: There was no new data to return.
     */
    private static final int NOT_MODIFIED = 304;

    /**
     * Bad Request: The request was invalid. An accompanying error message will
     * explain why. This is the status code will be returned during rate
     * limiting.
     */
    private static final int BAD_REQUEST = 400;

    /**
     * Not Authorized: Authentication credentials were missing or incorrect.
     */
    private static final int NOT_AUTHORIZED = 401;

    /**
     * Forbidden: The request is understood, but it has been refused. An
     * accompanying error message will explain why.
     */
    private static final int FORBIDDEN = 403;

    /**
     * Not Found: The URI requested is invalid or the resource requested, such
     * as a user, does not exists.
     */
    private static final int NOT_FOUND = 404;

    /**
     * Not Acceptable: Returned by the Search API when an invalid format is
     * specified in the request.
     */
    private static final int NOT_ACCEPTABLE = 406;

    /**
     * Internal Server Error: Something is broken. Please post to the group so
     * the developer team can investigate.
     */
    private static final int INTERNAL_SERVER_ERROR = 500;

    /**
     * Bad Gateway: server is down or being upgraded.
     */
    private static final int BAD_GATEWAY = 502;

    /**
     * Service Unavailable: The servers are up, but overloaded with requests.
     * Try again later. The search and trend methods use this to indicate when
     * you are being rate limited.
     */
    private static final int SERVICE_UNAVAILABLE = 503;

    private static final boolean DEBUG = true;
    private static final Logger log = Logger.getLogger(HttpClient.class
            .getName());

    private org.apache.commons.httpclient.HttpClient client = null;

    private List<Header> headers = new ArrayList<Header>();

    /**
     * HttpClient默认构造方法，单个客户端最大连接数150，连接超时30s,读写超时30s
     */
    public HttpClient() {
        this(150, 30000, 30000);
    }

    /**
     * HttpClient带参数的构造方法
     *
     * @param maxConPerHost 单个客户端最大连接数
     * @param conTimeOutMs  连接超时
     * @param soTimeOutMs   读写超时
     */
    public HttpClient(int maxConPerHost, int conTimeOutMs, int soTimeOutMs) {
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setDefaultMaxConnectionsPerHost(maxConPerHost);
        params.setConnectionTimeout(conTimeOutMs);
        params.setSoTimeout(soTimeOutMs);

        HttpClientParams clientParams = new HttpClientParams();
        // 忽略cookie避免Cookie rejected警告
        clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        client = new org.apache.commons.httpclient.HttpClient(clientParams,
                connectionManager);
        Protocol myhttps = new Protocol("https", new SSLSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
    }

    /**
     * 添加header信息
     *
     * @param key   键
     * @param value 值
     */
    public void addHeader(String key, String value) {
        headers.add(new Header(key, value));
    }

    /**
     * 发送HttpGet请求
     *
     * @param url 请求地址
     * @return HttpResponse
     */
    public HttpResponse get(String url) {
        log("HttpGet:" + url);
        HttpResponse response = new HttpResponse();
        try {
            GetMethod getMethod = new GetMethod(url);
            response = httpRequest(getMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 发送HttpPost请求
     *
     * @param url    请求地址
     * @param params 参数
     * @return HttpResponse
     */
    public HttpResponse post(String url, NameValuePair[] params) {
        log("HttpPost" + url);
        HttpResponse response = new HttpResponse();
        try {
            PostMethod postMethod = new UTF8PostMethod(url);
            postMethod.addParameters(params);
            response = httpRequest(postMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 图片上传(multipart)
     *
     * @param url    请求地址
     * @param params 参数
     * @param item   图片
     * @return HttpResponse
     */
    public HttpResponse multPartURL(String url, NameValuePair[] params,
                                    ImageItem item) {
        HttpResponse response = new HttpResponse();
        PostMethod postMethod = new PostMethod(url);
        try {
            Part[] parts = null;
            if (params == null) {
                parts = new Part[1];
            } else {
                parts = new Part[params.length + 1];
            }
            if (params != null) {
                int i = 0;
                for (NameValuePair entry : params) {
                    parts[i++] = new StringPart(entry.getName(),
                            (String) entry.getValue());
                }
                parts[parts.length - 1] = new ByteArrayPart(item.getContent(),
                        item.getName(), item.getContentType());
            }
            postMethod.setRequestEntity(new MultipartRequestEntity(parts,
                    postMethod.getParams()));
            response = httpRequest(postMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 上传文件
     *
     * @param fileParamName 文件名
     * @param url           请求地址
     * @param params        参数
     * @param file          文件对象
     * @return HttpResponse
     */
    public HttpResponse multPartURL(String fileParamName, String url,
                                    NameValuePair[] params, File file) {
        HttpResponse response = new HttpResponse();
        PostMethod postMethod = new PostMethod(url);
        try {
            Part[] parts = null;
            if (params == null) {
                parts = new Part[1];
            } else {
                parts = new Part[params.length + 1];
            }
            if (params != null) {
                int i = 0;
                for (NameValuePair entry : params) {
                    parts[i++] = new StringPart(entry.getName(),
                            (String) entry.getValue());
                }
            }
            FilePart filePart = new FilePart(fileParamName, file.getName(),
                    file, new MimetypesFileTypeMap().getContentType(file),
                    "UTF-8");
            filePart.setTransferEncoding("binary");
            parts[parts.length - 1] = filePart;

            postMethod.setRequestEntity(new MultipartRequestEntity(parts,
                    postMethod.getParams()));
            response = httpRequest(postMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Http请求处理
     *
     * @param method HttpGet,HttpPost
     * @return HttpResponse
     */
    public HttpResponse httpRequest(HttpMethod method) {
        HttpResponse response = new HttpResponse();
        try {
            for (Header header : headers) {
                method.addRequestHeader(header);
            }
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));
            client.executeMethod(method);
            int responseCode = method.getStatusCode();
            response.setResultCode(responseCode);

            String content = inputStream2String(method
                    .getResponseBodyAsStream());
            log("HttpResponse " + content + "\n");

            if (responseCode == OK) {
                response.setResultCode(HttpResponse.RESULT_CODE_SUCCESS);
                response.setContent(content);
            } else {
                response.setErrorMsg(getCause(responseCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return response;
    }

    /**
     * multipart上传文件普通参数的处理类
     */
    private static class ByteArrayPart extends PartBase {
        private byte[] mData;
        private String mName;

        public ByteArrayPart(byte[] data, String name, String type)
                throws IOException {
            super(name, type, "UTF-8", "binary");
            mName = name;
            mData = data;
        }

        protected void sendData(OutputStream out) throws IOException {
            out.write(mData);
        }

        protected long lengthOfData() throws IOException {
            return mData.length;
        }

        protected void sendDispositionHeader(OutputStream out)
                throws IOException {
            super.sendDispositionHeader(out);
            StringBuilder buf = new StringBuilder();
            buf.append("; filename=\"").append(mName).append("\"");
            out.write(buf.toString().getBytes());
        }
    }

    // Inner class for UTF-8 support
    private static class UTF8PostMethod extends PostMethod {
        public UTF8PostMethod(String url) {
            super(url);
        }

        @Override
        public String getRequestCharSet() {
            // return super.getRequestCharSet();
            return "UTF-8";
        }
    }

    /**
     * 调试
     *
     * @param message 输出信息
     */
    private static void log(String message) {
        if (DEBUG) {
            log.debug(message);
        }
    }

    /**
     * 将InputStream转换为String(UTF-8编码)
     *
     * @param is InputStream
     * @return String
     */
    private String inputStream2String(InputStream is) {
        if (is == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4 * 1024];
        int len = 0;
        try {
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            sb.append(baos.toString("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 异常原因
     *
     * @param responseCode Http请求返回的状态
     * @return
     */
    private static String getCause(int responseCode) {
        String cause = null;
        switch (responseCode) {
            case NOT_MODIFIED:
                break;
            case BAD_REQUEST:
                cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
                break;
            case NOT_AUTHORIZED:
                cause = "Authentication credentials were missing or incorrect.";
                break;
            case FORBIDDEN:
                cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
                break;
            case NOT_FOUND:
                cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
                break;
            case NOT_ACCEPTABLE:
                cause = "Returned by the Search API when an invalid format is specified in the request.";
                break;
            case INTERNAL_SERVER_ERROR:
                cause = "Something is broken.  Please post to the group so the  developer can investigate.";
                break;
            case BAD_GATEWAY:
                cause = "Server is down or being upgraded.";
                break;
            case SERVICE_UNAVAILABLE:
                cause = "Service Unavailable: The servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.";
                break;
            default:
                cause = "Unknown";
        }
        return responseCode + ":" + cause;
    }

}
