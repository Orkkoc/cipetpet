package com.badlogic.gdx;

import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public interface Net {

    public interface HttpMethods {
        public static final String GET = "GET";
        public static final String POST = "POST";
    }

    public interface HttpResponse {
        byte[] getResult();

        InputStream getResultAsStream();

        String getResultAsString();

        HttpStatus getStatus();
    }

    public interface HttpResponseListener {
        void failed(Throwable th);

        void handleHttpResponse(HttpResponse httpResponse);
    }

    public enum Protocol {
        TCP
    }

    Socket newClientSocket(Protocol protocol, String str, int i, SocketHints socketHints);

    ServerSocket newServerSocket(Protocol protocol, int i, ServerSocketHints serverSocketHints);

    void openURI(String str);

    void sendHttpRequest(HttpRequest httpRequest, HttpResponseListener httpResponseListener);

    public static class HttpStatus {
        int statusCode;

        public int getStatusCode() {
            return this.statusCode;
        }

        public HttpStatus(int statusCode2) {
            this.statusCode = statusCode2;
        }
    }

    public static class HttpRequest {
        private String content;
        private long contentLength;
        private InputStream contentStream;
        private Map<String, String> headers;
        private final String httpMethod;
        private int timeOut = 0;
        private String url;

        public HttpRequest(String httpMethod2) {
            this.httpMethod = httpMethod2;
            this.headers = new HashMap();
        }

        public void setUrl(String url2) {
            this.url = url2;
        }

        public void setHeader(String name, String value) {
            this.headers.put(name, value);
        }

        public void setContent(String content2) {
            this.content = content2;
        }

        public void setContent(InputStream contentStream2, long contentLength2) {
            this.contentStream = contentStream2;
            this.contentLength = contentLength2;
        }

        public void setTimeOut(int timeOut2) {
            this.timeOut = timeOut2;
        }

        public int getTimeOut() {
            return this.timeOut;
        }

        public String getMethod() {
            return this.httpMethod;
        }

        public String getUrl() {
            return this.url;
        }

        public String getContent() {
            return this.content;
        }

        public InputStream getContentStream() {
            return this.contentStream;
        }

        public long getContentLength() {
            return this.contentLength;
        }

        public Map<String, String> getHeaders() {
            return this.headers;
        }
    }
}
