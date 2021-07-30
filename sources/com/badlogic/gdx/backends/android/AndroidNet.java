package com.badlogic.gdx.backends.android;

import android.content.Intent;
import android.net.Uri;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.NetJavaImpl;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

public class AndroidNet implements Net {
    final AndroidApplication app;
    NetJavaImpl netJavaImpl = new NetJavaImpl();

    public AndroidNet(AndroidApplication activity) {
        this.app = activity;
    }

    public void sendHttpRequest(Net.HttpRequest httpRequest, Net.HttpResponseListener httpResponseListener) {
        this.netJavaImpl.sendHttpRequest(httpRequest, httpResponseListener);
    }

    public ServerSocket newServerSocket(Net.Protocol protocol, int port, ServerSocketHints hints) {
        return new AndroidServerSocket(protocol, port, hints);
    }

    public Socket newClientSocket(Net.Protocol protocol, String host, int port, SocketHints hints) {
        return new AndroidSocket(protocol, host, port, hints);
    }

    public void openURI(String URI) {
        final Uri uri = Uri.parse(URI);
        this.app.runOnUiThread(new Runnable() {
            public void run() {
                AndroidNet.this.app.startActivity(new Intent("android.intent.action.VIEW", uri));
            }
        });
    }
}
