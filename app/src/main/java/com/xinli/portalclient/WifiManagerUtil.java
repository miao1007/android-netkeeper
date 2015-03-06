package com.xinli.portalclient;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import com.google.code.microlog4android.Logger;
import java.util.List;
import org.dom4j.swing.XMLTableColumnDefinition;

public class WifiManagerUtil {
    private static final String TAG = Context.WIFI_SERVICE;
    Logger logger;
    Context mContext;
    int mNetworkID;
    String mSSID;
    WifiConfiguration mTargetWifiCfg;
    List<WifiConfiguration> mWifiHotSpotLists;
    WifiInfo mWifiInfo;
    WifiManager mWifiManager;
    List<ScanResult> mWifiScanResultLists;

    public WifiManagerUtil(Context c, Logger iLogger) {
        this.mContext = c;
        this.logger = iLogger;
        this.logger.debug(" Construct...");
    }

    public void openWifi() {
        this.mWifiManager = (WifiManager) this.mContext.getSystemService(TAG);
        if (this.mWifiManager.isWifiEnabled()) {
            this.logger.debug("before have setWifiEnabled ...");
        } else if (this.mWifiManager.setWifiEnabled(true)) {
            this.logger.debug(" setWifiEnabled...success");
        } else {
            this.logger.debug(" setWifiEnabled...failure");
        }
    }

    public void closeWifi() {
        if (!this.mWifiManager.isWifiEnabled()) {
            return;
        }
        if (this.mWifiManager.setWifiEnabled(false)) {
            this.logger.debug(" disableWifiEnabled...success");
        } else {
            this.logger.debug(" disableWifiEnabled...failure");
        }
    }

    public WifiInfo getWifiInfo() {
        if (this.mWifiManager == null) {
            this.mWifiManager = (WifiManager) this.mContext.getSystemService(TAG);
        }
        return this.mWifiManager.getConnectionInfo();
    }

    public List<ScanResult> getWifiScanResult() {
        this.logger.debug(" connectToTargetWifi step2=======");
        List<ScanResult> wifiScanResultLists = this.mWifiManager.getScanResults();
        this.logger.debug(" connectToTargetWifi step3=======");
        return wifiScanResultLists;
    }

    public List<WifiConfiguration> getWifiAllHotSpot() {
        List<WifiConfiguration> wifiHotSpotLists = this.mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : wifiHotSpotLists) {
            this.logger.debug(new StringBuilder(" wifiConfiguration.SSID(hotSpot):").append(wifiConfiguration.SSID).toString());
        }
        return wifiHotSpotLists;
    }

    public boolean isConfigHotSpot() {
        try {
            this.logger.debug(" connectToTargetWifi step4=======");
            this.mWifiHotSpotLists = getWifiAllHotSpot();
            for (WifiConfiguration wifiConfiguration : this.mWifiHotSpotLists) {
                this.logger.debug(new StringBuilder(" connectToTargetWifi wifiConfiguration.SSID=======").append(wifiConfiguration.SSID).append("==mSSID==").append(this.mSSID).toString());
                if (wifiConfiguration.SSID.equals(new StringBuilder("\"").append(this.mSSID).append("\"").toString())) {
                    this.logger.debug(new StringBuilder("before have cfg this hotspot:").append(wifiConfiguration.SSID).toString());
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.debug(new StringBuilder("isConfigHotSpot Exception:").append(e.toString()).toString());
            return false;
        }
    }

    public boolean isScanTargetWifi() {
        try {
            this.logger.debug(" connectToTargetWifi step1=======");
            this.mWifiScanResultLists = getWifiScanResult();
            if (this.mWifiScanResultLists == null) {
                return false;
            }
            for (ScanResult wifiScanResultList : this.mWifiScanResultLists) {
                this.logger.debug(new StringBuilder(" connectToTargetWifi====wifiScanResultList.SSID:").append(wifiScanResultList.SSID).append("==mSSID==").append(this.mSSID).toString());
                if (wifiScanResultList.SSID.equals(this.mSSID)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.debug(new StringBuilder("isScanTargetWifi Exception:").append(e.toString()).toString());
            return false;
        }
    }

    public WifiConfiguration isExistWifiConfiguration(String ssid) {
        this.mWifiHotSpotLists = getWifiAllHotSpot();
        for (WifiConfiguration exsitWifiConfiguration : this.mWifiHotSpotLists) {
            this.logger.debug(new StringBuilder(" connectToTargetWifi===wifiConfiguration.SSID:").append(exsitWifiConfiguration.SSID).append("==ssid==").append(ssid).toString());
            if (exsitWifiConfiguration.SSID.equals(new StringBuilder("\"").append(ssid).append("\"").toString())) {
                return exsitWifiConfiguration;
            }
        }
        return null;
    }

    public WifiConfiguration createWifiCfg(String ssid, String password, int method) {
        try {
            this.logger.debug(" connectToTargetWifi==createWifiCfg..........................");
            WifiConfiguration wifiCfg = new WifiConfiguration();
            wifiCfg.allowedAuthAlgorithms.clear();
            wifiCfg.allowedGroupCiphers.clear();
            wifiCfg.allowedKeyManagement.clear();
            wifiCfg.allowedPairwiseCiphers.clear();
            wifiCfg.allowedProtocols.clear();
            wifiCfg.SSID = new StringBuilder("\"").append(ssid).append("\"").toString();
            switch (method) {
                case XMLTableColumnDefinition.OBJECT_TYPE:
                    this.logger.debug(" createWifiCfg..........................no password ");
                    wifiCfg.wepKeys[0] = "";
                    wifiCfg.allowedKeyManagement.set(0);
                    wifiCfg.wepTxKeyIndex = 0;
                    break;
                case XMLTableColumnDefinition.STRING_TYPE:
                    this.logger.debug(" connectToTargetWifi===createWifiCfg..........................have password :WPA");
                    wifiCfg.preSharedKey = new StringBuilder("\"").append(password).append("\"").toString();
                    wifiCfg.hiddenSSID = true;
                    wifiCfg.allowedAuthAlgorithms.set(0);
                    wifiCfg.allowedGroupCiphers.set(XMLTableColumnDefinition.NUMBER_TYPE);
                    wifiCfg.allowedKeyManagement.set(1);
                    wifiCfg.allowedPairwiseCiphers.set(1);
                    wifiCfg.allowedProtocols.set(0);
                    wifiCfg.status = 2;
                    break;
                case XMLTableColumnDefinition.NUMBER_TYPE:
                    this.logger.debug(" createWifiCfg..........................have password :WEP");
                    wifiCfg.preSharedKey = new StringBuilder("\"").append(password).append("\"").toString();
                    wifiCfg.hiddenSSID = true;
                    wifiCfg.allowedAuthAlgorithms.set(1);
                    wifiCfg.allowedGroupCiphers.set(XMLTableColumnDefinition.NODE_TYPE);
                    wifiCfg.allowedGroupCiphers.set(XMLTableColumnDefinition.NUMBER_TYPE);
                    wifiCfg.allowedGroupCiphers.set(0);
                    wifiCfg.allowedGroupCiphers.set(1);
                    wifiCfg.allowedKeyManagement.set(0);
                    wifiCfg.wepTxKeyIndex = 0;
                    break;
                default:
                    wifiCfg = null;
                    break;
            }
            WifiConfiguration tempWifiCfg = isExistWifiConfiguration(ssid);
            if (tempWifiCfg == null) {
                return wifiCfg;
            }
            this.logger.debug("connectToTargetWifi== tempWifiCfg != null:");
            this.mWifiManager.removeNetwork(tempWifiCfg.networkId);
            return wifiCfg;
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.debug(new StringBuilder("createWifiCfg Exception:").append(e.toString()).toString());
            return null;
        }
    }

    public synchronized void connectToTargetWifi(String ssid, String password, int method) {
        try {
            this.mSSID = ssid;
            if (isScanTargetWifi()) {
                this.logger.debug(" connectToTargetWifi success=======");
                this.logger.debug(new StringBuilder(" connectToTargetWifi step5=====password==").append(password).append("==ssid==").append(ssid).toString());
                this.mTargetWifiCfg = createWifiCfg(ssid, password, method);
                this.logger.debug(" connectToTargetWifi step6=====");
                this.mNetworkID = this.mWifiManager.addNetwork(this.mTargetWifiCfg);
                this.logger.debug(new StringBuilder("connectToTargetWifi==addNetwork:mTargetWifiCfg->").append(this.mTargetWifiCfg).toString());
                this.logger.debug(new StringBuilder("connectToTargetWifi==addNetwork:mNetworkID->").append(this.mNetworkID).toString());
                this.mWifiManager.enableNetwork(this.mNetworkID, true);
                this.logger.debug(" connectToTargetWifi step7=====");
                this.mWifiManager.reassociate();
                this.logger.debug(" connectToTargetWifi step8=====");
                this.mWifiManager.reconnect();
                this.logger.debug(" connectToTargetWifi step9=====");
                this.logger.debug(new StringBuilder(" connectToTargetWifi step9===dhcpinfo.ipAddress==").append(Formatter.formatIpAddress(this.mWifiManager.getDhcpInfo().ipAddress)).toString());
            } else {
                this.logger.debug(" connectToTargetWifi fail=======");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.debug(new StringBuilder("connectToTargetWifi Exception:").append(e.toString()).toString());
        }
    }
}
