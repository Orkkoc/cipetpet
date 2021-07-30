package com.badlogic.gdx.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.StreamUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetJavaImpl {
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    static class HttpClientResponse implements Net.HttpResponse {
        private HttpURLConnection connection;
        private InputStream inputStream;
        private Net.HttpStatus status;

        public HttpClientResponse(HttpURLConnection connection2) throws IOException {
            this.connection = connection2;
            this.inputStream = connection2.getInputStream();
            try {
                this.status = new Net.HttpStatus(connection2.getResponseCode());
            } catch (IOException e) {
                this.status = new Net.HttpStatus(-1);
            }
        }

        public byte[] getResult() {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[16384];
            while (true) {
                try {
                    int nRead = this.inputStream.read(data, 0, data.length);
                    if (nRead != -1) {
                        buffer.write(data, 0, nRead);
                    } else {
                        buffer.flush();
                        return buffer.toByteArray();
                    }
                } catch (IOException e) {
                    return new byte[0];
                }
            }
        }

        public String getResultAsString() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.inputStream));
            String line = "";
            while (true) {
                try {
                    String tmp = reader.readLine();
                    if (tmp != null) {
                        line = line + tmp;
                    } else {
                        reader.close();
                        return line;
                    }
                } catch (IOException e) {
                    return "";
                }
            }
        }

        public InputStream getResultAsStream() {
            return this.inputStream;
        }

        public Net.HttpStatus getStatus() {
            return this.status;
        }
    }

    public void sendHttpRequest(Net.HttpRequest httpRequest, Net.HttpResponseListener httpResponseListener) {
        URL url;
        if (httpRequest.getUrl() == null) {
            httpResponseListener.failed(new GdxRuntimeException("can't process a HTTP request without URL set"));
            return;
        }
        try {
            final String method = httpRequest.getMethod();
            if (method.equalsIgnoreCase(Net.HttpMethods.GET)) {
                String queryString = "";
                String value = httpRequest.getContent();
                if (value != null && !"".equals(value)) {
                    queryString = "?" + value;
                }
                url = new URL(httpRequest.getUrl() + queryString);
            } else {
                url = new URL(httpRequest.getUrl());
            }
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(method.equalsIgnoreCase(Net.HttpMethods.POST));
            connection.setDoInput(true);
            connection.setRequestMethod(method);
            Map<String, String> headers = httpRequest.getHeaders();
            for (String name : headers.keySet()) {
                connection.addRequestProperty(name, headers.get(name));
            }
            connection.setConnectTimeout(httpRequest.getTimeOut());
            connection.setReadTimeout(httpRequest.getTimeOut());
            final Net.HttpRequest httpRequest2 = httpRequest;
            final Net.HttpResponseListener httpResponseListener2 = httpResponseListener;
            this.executorService.submit(new Runnable() {
                public void run() {
                    try {
                        if (method.equalsIgnoreCase(Net.HttpMethods.POST)) {
                            String contentAsString = httpRequest2.getContent();
                            InputStream contentAsStream = httpRequest2.getContentStream();
                            OutputStream outputStream = connection.getOutputStream();
                            if (contentAsString != null) {
                                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                                writer.write(contentAsString);
                                writer.flush();
                                writer.close();
                            } else if (contentAsStream != null) {
                                StreamUtils.copyStream(contentAsStream, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            }
                        }
                        connection.connect();
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                try {
                                    httpResponseListener2.handleHttpResponse(new HttpClientResponse(connection));
                                } catch (IOException e) {
                                    httpResponseListener2.failed(e);
                                } finally {
                                    connection.disconnect();
                                }
                            }
                        });
                    } catch (Exception e) {
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                connection.disconnect();
                                httpResponseListener2.failed(e);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            httpResponseListener.failed(e);
        }
    }
}
