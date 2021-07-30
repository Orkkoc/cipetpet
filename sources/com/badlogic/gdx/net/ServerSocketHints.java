package com.badlogic.gdx.net;

import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dConstants;

public class ServerSocketHints {
    public int acceptTimeout = 5000;
    public int backlog = 16;
    public int performancePrefBandwidth = 0;
    public int performancePrefConnectionTime = 0;
    public int performancePrefLatency = 1;
    public int receiveBufferSize = G3dConstants.STILL_MODEL;
    public boolean reuseAddress = true;
}
