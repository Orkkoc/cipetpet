package com.badlogic.gdx.net;

import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dConstants;

public class SocketHints {
    public int connectTimeout = 5000;
    public boolean keepAlive = true;
    public boolean linger = false;
    public int lingerDuration = 0;
    public int performancePrefBandwidth = 0;
    public int performancePrefConnectionTime = 0;
    public int performancePrefLatency = 1;
    public int receiveBufferSize = G3dConstants.STILL_MODEL;
    public int sendBufferSize = G3dConstants.STILL_MODEL;
    public boolean tcpNoDelay = true;
    public int trafficClass = 20;
}
