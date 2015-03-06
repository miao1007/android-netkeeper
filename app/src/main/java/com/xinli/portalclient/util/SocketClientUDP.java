package com.xinli.portalclient.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SocketClientUDP {
    private byte[] buffer;
    private DatagramSocket ds;

    public SocketClientUDP() throws Exception {
        this.buffer = new byte[4096];
        this.ds = null;
        this.ds = new DatagramSocket();
    }

    public final void setSoTimeout(int timeout) throws Exception {
        this.ds.setSoTimeout(timeout);
    }

    public final int getSoTimeout() throws Exception {
        return this.ds.getSoTimeout();
    }

    public final DatagramSocket getSocket() {
        return this.ds;
    }

    public final DatagramPacket send(String host, int port, byte[] bytes) throws IOException {
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host), port);
        this.ds.send(dp);
        return dp;
    }

    public final String receive(String lhost, int lport) throws Exception {
        DatagramPacket dp = new DatagramPacket(this.buffer, this.buffer.length);
        this.ds.receive(dp);
        return new String(dp.getData(), 0, dp.getLength(), "GBK");
    }

    public final void close() {
        try {
            this.ds.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static byte[] intToByteArray(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[i] = (byte) ((value >>> (((b.length - 1) - i) * 8)) & 255);
        }
        return b;
    }

    public static byte[] getAuth(int len) {
        int i = 0;
        byte[] b = new byte[len];
        for (int j = 0; j < len; j++) {
            b[i] = (byte) ((int) ((Math.random() * 100.0d) % 255.0d));
            i++;
        }
        return b;
    }
}
