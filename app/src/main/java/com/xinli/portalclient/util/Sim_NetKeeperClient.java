package com.xinli.portalclient.util;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Sim_NetKeeperClient {
    private static final int TIMEOUT = 3000;
    private static final int dataType = 8;
    private static String encryKey = null;
    private static String key = null;
    private static final int localPort = 3311;
    private static final String serverHost = "222.177.26.12";
    private static final int serverPort = 443;
    private final transient Logger logger;
    private DatagramSocket socket;

    static {
        key = "";
        encryKey = "XINLIAPSECRET01234567891";
    }

    public Sim_NetKeeperClient() throws Exception {
        this.logger = LoggerFactory.getLogger(getClass());
        this.socket = null;
        if (this.socket == null) {
            this.socket = new DatagramSocket(3311);
        }
    }

    public final void setSoTimeout(int timeout) throws Exception {
        this.socket.setSoTimeout(timeout);
    }

    public final int getSoTimeout() throws Exception {
        return this.socket.getSoTimeout();
    }

    public final DatagramSocket getSocket() {
        return this.socket;
    }

    public void sendHeart(String username, String ip, String version) throws Exception {
        sendDataPackage(username, ip, version);
        acceptHeartResponse();
        try {
            this.socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void acceptHeartResponse() {
        byte[] buffer = new byte[200];
        try {
            DatagramPacket dataPackage = new DatagramPacket(buffer, buffer.length);
            setSoTimeout(TIMEOUT);
            this.logger.info("\u9632\u4ee3\u7406\u5fc3\u8df3\u51c6\u5907\u63a5\u53d7\u8fd4\u56de\u6570\u636e");
            this.socket.receive(dataPackage);
            this.logger.info("\u9632\u4ee3\u7406\u5fc3\u8df3\u51c6\u5907\u8fd4\u56de\u6570\u636e\u6210\u529f");
            ByteBuffer b = ByteBuffer.wrap(dataPackage.getData());
            byte[] ver = new byte[4];
            b.get(ver);
            byte reportType = b.get();
            byte[] dataByte = new byte[b.getInt()];
            b.get(dataByte);
            byte[] decryptData = ThreeDes.decryptMode(dataByte, encryKey);
            key = new String(decryptData);
            this.logger.warn(new StringBuilder("\u534f\u8bae").append(new String(ver)).append(" \u62a5\u6587\u7c7b\u578b").append(reportType).append(" \u89e3\u5bc6\u8fc7\u540e\u7684\u6570\u636e\u662f:").append(new String(decryptData)).toString());
        } catch (Exception e) {
            this.logger.error(new StringBuilder("\u9632\u4ee3\u7406\u5fc3\u8df3\u63a5\u6536\u5f02\u5e38:").append(e.getMessage()).toString());
            if (this.logger.isDebugEnabled()) {
                this.logger.info("\u670d\u52a1\u5668\u63a5\u6536\u62a5\u6587\u51fa\u9519");
            }
            e.printStackTrace();
        }
    }

    public void sendDataPackage(String username, String ip, String version) throws Exception {
        StringBuffer respBuffer = new StringBuffer();
        respBuffer.append("TYPE=").append("HEARTBEAT").append("&").append("USER_NAME=").append(username).append("&").append("IP=").append(ip).append("&").append("VERSION_NUMBER=").append(version).append("&").append("KEY=").append(key);
        try {
            this.socket.send(createPackage(respBuffer.toString(), dataType));
            this.logger.debug("\u62a5\u65878\u53d1\u9001\u6210\u529f");
        } catch (Exception e) {
            this.logger.error(new StringBuilder("\u53d1\u9001\u5fc3\u8df3\u5931\u8d25").append(e.getMessage()).toString());
            throw new Exception(e.getMessage());
        }
    }

    public DatagramPacket createPackage(String resp, int dataType) {
        byte[] enc_a = ThreeDes.encryptMode(resp.getBytes(), encryKey);
        ByteBuffer byteBuffer = ByteBuffer.allocate(enc_a.length + 9);
        byteBuffer.put("HR10".getBytes());
        byteBuffer.put((byte) dataType);
        byteBuffer.putInt(enc_a.length);
        byteBuffer.put(enc_a);
        byteBuffer.flip();
        byte[] buf = byteBuffer.array();
        InetAddress address = null;
        try {
            address = InetAddress.getByName(serverHost);
        } catch (UnknownHostException e) {
            this.logger.error("\u4e0d\u80fd\u8bc6\u522b\u7684\u8fdc\u7a0b\u4e3b\u673a:222.177.26.12");
        }
        return new DatagramPacket(buf, buf.length, address, 443);
    }

    public final void close() {
        try {
            this.socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
