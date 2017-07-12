package org.newstand.datamigration.loader.impl;

import android.net.wifi.WifiConfiguration;
import android.support.annotation.NonNull;

import com.chrisplus.rootmanager.RootManager;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.stericson.rootools.RootTools;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.WifiRecord;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.EncryptManager;
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Nick@NewStand.org on 2017/4/25 15:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WifiLoader extends BaseLoader {

    // Class for capturing a network definition delegate the wifi supplicant config file
    private static class Network {
        String ssid = "";  // equals() and hashCode() need these to be non-null
        String key_mgmt = "";
        boolean certUsed = false;
        boolean hasWepKey = false;
        boolean isEap = false;
        String psk = "";
        final ArrayList<String> rawLines = new ArrayList<>();

        static Network readFromStream(BufferedReader in) {
            final Network n = new Network();
            String line;
            try {
                while (in.ready()) {
                    line = in.readLine();
                    if (line == null || line.startsWith("}")) {
                        break;
                    }
                    n.rememberLine(line);
                }
            } catch (IOException e) {
                return null;
            }
            return n;
        }

        void rememberLine(String line) {
            // can't rely on particular whitespace patterns so strip leading/trailing
            line = line.trim();
            if (line.isEmpty()) return; // only whitespace; drop the line
            rawLines.add(line);

            // remember the ssid and key_mgmt lines for duplicate culling
            if (line.startsWith("ssid=")) {
                ssid = line;
            } else if (line.startsWith("key_mgmt=")) {
                key_mgmt = line;
                if (line.contains("EAP")) {
                    isEap = true;
                }
            } else if (line.startsWith("client_cert=")) {
                certUsed = true;
            } else if (line.startsWith("ca_cert=")) {
                certUsed = true;
            } else if (line.startsWith("ca_path=")) {
                certUsed = true;
            } else if (line.startsWith("wep_")) {
                hasWepKey = true;
            } else if (line.startsWith("eap=")) {
                isEap = true;
            } else if (line.startsWith("psk=")) {
                psk = line;
            }
        }

        public void write(Writer w) throws IOException {
            w.write("\nnetwork={\n");
            for (String line : rawLines) {
                w.write("\t" + line + "\n");
            }
            w.write("}\n");
        }

        public String dump() {
            String d = "network={\n";
            for (String line : rawLines) {
                d = d + String.format("   %s\n", line);
            }
            d = d + "}";
            return d;
        }

        @Override
        public String toString() {
            return "Network{" +
                    "ssid='" + ssid + '\'' +
                    ", key_mgmt='" + key_mgmt + '\'' +
                    ", certUsed=" + certUsed +
                    ", hasWepKey=" + hasWepKey +
                    ", isEap=" + isEap +
                    ", psk='" + psk + '\'' +
                    '}';
        }

        // Calculate the equivalent of WifiConfiguration's configKey()
        public String configKey() {
            if (ssid == null) {
                // No SSID => malformed network definition
                return null;
            }

            final String bareSsid = ssid.substring(ssid.indexOf('=') + 1);

            final BitSet types = new BitSet();
            if (key_mgmt == null) {
                // no key_mgmt specified; this is defined as equivalent to "WPA-PSK WPA-EAP"
                types.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                types.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            } else {
                // Need to parse the key_mgmt line
                final String bareKeyMgmt = key_mgmt.substring(key_mgmt.indexOf('=') + 1);
                String[] typeStrings = bareKeyMgmt.split("\\s+");

                // Parse out all the key management regimes permitted for this network.  The literal
                // strings here are the standard values permitted in wpa_supplicant.conf.
                for (final String ktype : typeStrings) {
                    switch (ktype) {
                        case "WPA-PSK":
                            Logger.v("setting WPA_PSK bit");
                            types.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                            break;
                        case "WPA-EAP":
                            Logger.v("setting WPA_EAP bit");
                            types.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                            break;
                        case "IEEE8021X":
                            Logger.v("setting IEEE8021X bit");
                            types.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                            break;
                    }
                }
            }

            // Now build the canonical config key paralleling the WifiConfiguration semantics
            final String key;
            if (types.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
                key = bareSsid + WifiConfiguration.KeyMgmt.strings[WifiConfiguration.KeyMgmt.WPA_PSK];
            } else if (types.get(WifiConfiguration.KeyMgmt.WPA_EAP) || types.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
                key = bareSsid + WifiConfiguration.KeyMgmt.strings[WifiConfiguration.KeyMgmt.WPA_EAP];
            } else if (hasWepKey) {
                key = bareSsid + "WEP";  // hardcoded this way in WifiConfiguration
            } else {
                key = bareSsid + WifiConfiguration.KeyMgmt.strings[WifiConfiguration.KeyMgmt.NONE];
            }
            return key;
        }

        // Same approach as Pair.equals() and Pair.hashCode()
        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Network)) return false;
            final Network other;
            try {
                other = (Network) o;
            } catch (ClassCastException e) {
                return false;
            }
            return ssid.equals(other.ssid) && key_mgmt.equals(other.key_mgmt);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + ssid.hashCode();
            result = 31 * result + key_mgmt.hashCode();
            return result;
        }
    }

    // Ingest multiple wifi config file fragments, looking for network={} blocks
    // and eliminating duplicates
    class WifiNetworkSettings {
        // One for fast lookup, one for maintaining ordering
        final HashSet<Network> mKnownNetworks = new HashSet<>();
        final ArrayList<Network> mNetworks = new ArrayList<>(8);

        public void readNetworks(BufferedReader in,
                                 boolean acceptEapNetworks) {
            try {
                String line;
                while (in.ready()) {
                    line = in.readLine();
                    if (line != null) {
                        // Parse out 'network=' decls so we can ignore duplicates
                        if (line.startsWith("network")) {
                            Network net = Network.readFromStream(in);
                            // Don't propagate EAP network definitions
                            if (net != null && net.isEap && !acceptEapNetworks) {
                                Logger.v("Skipping EAP network " + net.ssid + " / " + net.key_mgmt);
                                continue;
                            }
                            if (!mKnownNetworks.contains(net)) {

                                if (net != null) {
                                    Logger.v("Adding " + net.ssid + " / " + net.key_mgmt);
                                }
                                mKnownNetworks.add(net);
                                mNetworks.add(net);
                            } else {
                                if (net != null) {
                                    Logger.v("Dupe; skipped " + net.ssid + " / " + net.key_mgmt);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // whatever happened, we're done now
            }
        }

        public void write(Writer w) throws IOException {
            for (Network net : mNetworks) {
                if (net.certUsed) {
                    // Networks that use certificates for authentication can't be restored
                    // because the certificates they need don't get restored (because they
                    // are stored in keystore, and can't be restored)
                    continue;
                }

                if (net.isEap) {
                    // Similarly, omit EAP network definitions to avoid propagating
                    // controlled enterprise network definitions.
                    continue;
                }

                net.write(w);
            }
        }

        public void dump() {
            for (Network net : mNetworks) {
                net.dump();
            }
        }
    }

    @Override
    public String[] needPermissions() {
        return new String[]{"Root"};
    }

    @Override
    public Collection<DataRecord> loadFromAndroid(LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> wifiRecords = new ArrayList<>();
        if (RootManager.getInstance().obtainPermission()) {
            String tmpPath = SettingsProvider.getCommonDataDir() + File.separator + "wifi.data";
            boolean copy = RootTools.copyFile(SettingsProvider.getWifiConfigFilePath(), tmpPath, false, false);
            if (copy) {
                WifiNetworkSettings settings = new WifiNetworkSettings();
                try {
                    settings.readNetworks(Files.newReader(new File(tmpPath), Charset.defaultCharset()), true);

                    Collections.consumeRemaining(settings.mKnownNetworks, new Consumer<Network>() {
                        @Override
                        public void accept(@NonNull Network network) {
                            WifiRecord wifiRecord = new WifiRecord();
                            wifiRecord.setSsid(network.ssid);
                            wifiRecord.setPsk(network.psk);
                            wifiRecord.setDisplayName(network.ssid);
                            wifiRecord.setRawLines(network.rawLines);
                            wifiRecords.add(wifiRecord);
                        }
                    });

                } catch (FileNotFoundException e) {
                    Logger.e(e, "Fail readNetworks.");
                } finally {
                    new File(tmpPath).deleteOnExit();// Clean up.
                }
            }
        }
        return wifiRecords;
    }

    @Override
    public Collection<DataRecord> loadFromSession(LoaderSource source, Session session, LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> wifiRecords = new ArrayList<>();

        String dir = source.getParent() == LoaderSource.Parent.Received ?
                SettingsProvider.getReceivedDirByCategory(DataCategory.Wifi, session)
                : SettingsProvider.getBackupDirByCategory(DataCategory.Wifi, session);

        Iterable<File> iterable = Files.fileTreeTraverser().children(new File(dir));

        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                try {
                    String fileToParse = file.getPath();
                    boolean isEncrypted = SettingsProvider.isEncryptedFile(file.getPath());
                    Logger.i("isEncrypted %s %s", file, isEncrypted);
                    String fileToDecrypt = isEncrypted ?
                            SettingsProvider.getDecryptPath(fileToParse) : null;
                    if (isEncrypted && EncryptManager.from(getContext()).decrypt(fileToParse, fileToDecrypt)) {
                        Logger.i("Change file to parse %s", fileToDecrypt);
                        fileToParse = fileToDecrypt;
                    }
                    String content = org.newstand.datamigration.utils.Files.readString(fileToParse);
                    Gson gson = new Gson();
                    WifiRecord record = gson.fromJson(content, WifiRecord.class);
                    record.setPath(file.getPath());
                    record.setChecked(false);
                    wifiRecords.add(record);
                    // Delete decrypted file
                    if (fileToDecrypt != null) {
                        File fileToDelete = new File(fileToDecrypt);
                        if (fileToDelete.exists()) BlackHole.eat(fileToDelete.delete());
                    }
                } catch (Throwable t) {
                    Logger.e(t, "Fail to parse call log");
                }
            }
        });

        return wifiRecords;
    }
}
