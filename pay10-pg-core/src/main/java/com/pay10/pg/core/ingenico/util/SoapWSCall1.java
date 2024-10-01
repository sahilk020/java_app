package com.pay10.pg.core.ingenico.util;

import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLStreamHandler;

class SoapWSCall1 extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        final URL target = new URL(url.toString());
        final URLConnection connection = target.openConnection();
        return connection;
    }
}