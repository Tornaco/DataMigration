package org.newstand.datamigration.utils;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class WifiExtManager {

    private static String TAG = "WifiExtManager";

    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;
    public static final int WIFI_AP_STATE_RESTART = 15;

    public static final String WIFI_AP_STATE_CHANGED_ACTION =
            "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";

    private static final String METHOD_GET_WIFI_AP_STATE = "getWifiApState";
    private static final String METHOD_SET_WIFI_AP_ENABLED = "setWifiApEnabled";
    private static final String METHOD_GET_WIFI_AP_CONFIG = "getWifiApConfiguration";
    private static final String METHOD_IS_WIFI_AP_ENABLED = "isWifiApEnabled";
    private static final String METHOD_START_SCAN_ACTIVE = "startScanActive";

    private static final Map<String, Method> methodMap = new HashMap<String, Method>();
    private static Boolean mIsSupport;
    private static boolean mIsHtc;

    public synchronized static final boolean isSupport() {
        if (mIsSupport != null) {
            return mIsSupport;
        }

        boolean result = Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO;
        if (result) {
            try {
                Field field = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
                mIsHtc = field != null;
            } catch (Exception e) {
            }
        }

        if (result) {
            try {
                String name = METHOD_GET_WIFI_AP_STATE;
                Method method = WifiManager.class.getMethod(name);
                methodMap.put(name, method);
                result = method != null;
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException", e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException", e);
            }
        }

        if (result) {
            try {
                String name = METHOD_SET_WIFI_AP_ENABLED;
                Method method = WifiManager.class.getMethod(name, WifiConfiguration.class, boolean.class);
                methodMap.put(name, method);
                result = method != null;
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException", e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException", e);
            }
        }

        if (result) {
            try {
                String name = METHOD_GET_WIFI_AP_CONFIG;
                Method method = WifiManager.class.getMethod(name);
                methodMap.put(name, method);
                result = method != null;
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException", e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException", e);
            }
        }

        if (result) {
            try {
                String name = getSetWifiApConfigName();
                Method method = WifiManager.class.getMethod(name, WifiConfiguration.class);
                methodMap.put(name, method);
                result = method != null;
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException", e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException", e);
            }
        }

        if (result) {
            try {
                String name = METHOD_IS_WIFI_AP_ENABLED;
                Method method = WifiManager.class.getMethod(name);
                methodMap.put(name, method);
                result = method != null;
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException", e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException", e);
            }
        }

        if (result) {
            try {
                String name = METHOD_START_SCAN_ACTIVE;
                Method method = WifiManager.class.getMethod(name);
                methodMap.put(name, method);
                result = method != null;
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException", e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException", e);
            }
        }

        mIsSupport = result;
        return isSupport();
    }

    private final WifiManager mWifiManager;

    public WifiExtManager(WifiManager manager) {
        if (!isSupport()) {
            throw new RuntimeException("Unsupport Ap!");
        }
        Log.i(TAG, "Build.BRAND -----------> " + Build.BRAND);

        mWifiManager = manager;
    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

    public int getWifiApState() {
        try {
            Method method = methodMap.get(METHOD_GET_WIFI_AP_STATE);
            return (Integer) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return WIFI_AP_STATE_FAILED;
    }

    private WifiConfiguration getHtcWifiApConfiguration(WifiConfiguration standard) {
        WifiConfiguration htcWifiConfig = standard;
        try {
            Object mWifiApProfileValue = getFieldValue(standard, "mWifiApProfile");

            if (mWifiApProfileValue != null) {
                htcWifiConfig.SSID = (String) getFieldValue(mWifiApProfileValue, "SSID");
            }
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage(), e);
        }
        return htcWifiConfig;
    }

    public WifiConfiguration getWifiApConfiguration() {
        WifiConfiguration configuration = null;
        try {
            Method method = methodMap.get(METHOD_GET_WIFI_AP_CONFIG);
            configuration = (WifiConfiguration) method.invoke(mWifiManager);
            if (isHtc()) {
                configuration = getHtcWifiApConfiguration(configuration);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return configuration;
    }

    public boolean setWifiApConfiguration(WifiConfiguration netConfig) {
        boolean result = false;
        try {
            if (isHtc()) {
                setupHtcWifiConfiguration(netConfig);
            }

            Method method = methodMap.get(getSetWifiApConfigName());
            Class<?>[] params = method.getParameterTypes();
            for (Class<?> clazz : params) {
                Log.i(TAG, "param -> " + clazz.getSimpleName());
            }

            if (isHtc()) {
                int rValue = (Integer) method.invoke(mWifiManager, netConfig);
                Log.i(TAG, "rValue -> " + rValue);
                result = rValue > 0;
            } else {
                result = (Boolean) method.invoke(mWifiManager, netConfig);
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return result;
    }

    public boolean setWifiApEnabled(WifiConfiguration configuration, boolean enabled) {
        boolean result = false;
        try {
            Method method = methodMap.get(METHOD_SET_WIFI_AP_ENABLED);
            result = (Boolean) method.invoke(mWifiManager, configuration, enabled);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    public boolean isWifiApEnabled() {
        boolean result = false;
        try {
            Method method = methodMap.get(METHOD_IS_WIFI_AP_ENABLED);
            result = (Boolean) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    public boolean startScanActive() {
        boolean result = false;
        try {
            Method method = methodMap.get(METHOD_START_SCAN_ACTIVE);
            result = (Boolean) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    private void setupHtcWifiConfiguration(WifiConfiguration config) {
        try {
            Log.d(TAG, "config=  " + config);
            Object mWifiApProfileValue = getFieldValue(config, "mWifiApProfile");

            if (mWifiApProfileValue != null) {
                setFieldValue(mWifiApProfileValue, "SSID", config.SSID);
                setFieldValue(mWifiApProfileValue, "BSSID", config.BSSID);
                setFieldValue(mWifiApProfileValue, "secureType", "open");
                setFieldValue(mWifiApProfileValue, "dhcpEnable", 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage(), e);
        }
    }

    private static boolean isHtc() {
        return mIsHtc;
    }

    private static String getSetWifiApConfigName() {
        return mIsHtc ? "setWifiApConfig" : "setWifiApConfiguration";
    }

    private static Object getFieldValue(Object obj, String name) {
        Class cls = obj.getClass();
        try {
            Field fd = cls.getField(name);
            return fd.get(obj);
        } catch (Exception e) {
            Log.e(TAG, "Get field error:" + e);
        }
        return null;
    }

    private static void setFieldValue(Object obj, String name, Object field) {
        Class cls = obj.getClass();
        try {
            Field fd = cls.getField(name);
            fd.set(obj, field);
        } catch (Exception e) {
            Log.e(TAG, "Set field error:" + e);
        }
    }
}
