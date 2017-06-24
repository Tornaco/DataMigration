package dev.tornaco.ftpserver;

import android.content.Context;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Nick on 2017/6/24 16:31
 */

public class FTPServerProxy {

    private FtpServer mFtpServer;
    private String ftpConfigDir;

    private void init(Context context) {
        ftpConfigDir = context.getCacheDir().getPath() + "/ftpConfig";
    }

    public boolean startServer(Context context) {
        init(context);
        return configAndStart(context);
    }

    public String getLocalIpAddress() {
        String strIP = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        strIP = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Logger.e(e, "Fail get inet info");
        }
        return strIP;
    }

    private boolean configAndStart(Context c) {

        FtpServerFactory serverFactory = new FtpServerFactory();

        ListenerFactory factory = new ListenerFactory();

        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();

        File dir = new File(ftpConfigDir);
        if (!dir.mkdir()) {
            Logger.e("Fail mkdir:%s", ftpConfigDir);
            return false;
        }

        String filename = ftpConfigDir + "/users.properties";
        if (!copyResourceFile(c, R.raw.ftp_user_properties, filename))
            return false;

        File files = new File(filename);

        userManagerFactory.setFile(files);
        serverFactory.setUserManager(userManagerFactory.createUserManager());
        // set the port of the listener
        factory.setPort(8899);

        // replace the default listener
        serverFactory.addListener("default", factory.createListener());

        // configAndStart the server
        FtpServer server = serverFactory.createServer();
        this.mFtpServer = server;
        try {
            server.start();
        } catch (FtpException e) {
            Logger.e(e, "Fail configAndStart server");
            return false;
        }
        return true;
    }

    private boolean copyResourceFile(Context c, int rid, String targetFile) {
        InputStream fin = c.getResources().openRawResource(rid);
        FileOutputStream fos = null;
        int length;
        try {
            fos = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            while ((length = fin.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            Logger.e(e, "Fail copy res");
            return false;
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException ignored) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
        return true;
    }

    public void stop() {
        mFtpServer.stop();
    }
}
