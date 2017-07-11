package org.newstand.datamigration.secure;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;
import com.google.common.io.Files;

import org.newstand.datamigration.utils.BlackHole;
import org.newstand.datamigration.utils.Closer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/21 15:27
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
public class EncryptManager {

    private static EncryptManager sMe;

    private Crypto crypto;
    private Entity entity;

    private EncryptManager(Context context) {
        // Creates a new Crypto object with default implementations of a key chain
        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
        this.crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
        String secretKey = createSecretKey();
        entity = Entity.create(secretKey);
    }

    public static synchronized EncryptManager from(Context context) {
        if (sMe == null) sMe = new EncryptManager(context);
        return sMe;
    }

    @WorkerThread
    public boolean encrypt(String filePath, String toPath) throws IOException, CryptoInitializationException, KeyChainException {

        File targetFile = new File(filePath);

        if (!targetFile.exists()) {
            return false;
        }

        File encryptFile = new File(toPath);

        if (encryptFile.exists()) {
            encryptFile.deleteOnExit();
        }

        // Check for whether the crypto functionality is available
        // This might fail if Android does not load libaries correctly.
        if (!getCrypto().isAvailable()) {
            return false;
        }

        OutputStream fileStream = new BufferedOutputStream(
                new FileOutputStream(toPath));

        // Creates an output stream which encrypts the data as
        // it is written to it and writes it out to the filePath.
        OutputStream outputStream = crypto.getCipherOutputStream(
                fileStream,
                getEntity());

        BlackHole.eat(Files.asByteSource(new File(filePath)).copyTo(outputStream));

        Closer.closeQuietly(outputStream);

        return true;
    }

    public boolean decrypt(String filePath, String toPath) throws IOException, CryptoInitializationException, KeyChainException {

        File targetFile = new File(filePath);

        if (!targetFile.exists()) {
            return false;
        }

        File outFile = new File(toPath);

        if (outFile.exists()) {
            outFile.deleteOnExit();
        }

        // Check for whether the crypto functionality is available
        // This might fail if Android does not load libaries correctly.
        if (!getCrypto().isAvailable()) {
            return false;
        }

        // Get the filePath to which ciphertext has been written.
        FileInputStream fileStream = new FileInputStream(filePath);

        // Creates an input stream which decrypts the data as
        // it is read delegate it.
        InputStream inputStream = crypto.getCipherInputStream(
                fileStream,
                getEntity());


        BlackHole.eat(Files.asByteSink(new File(toPath)).writeFrom(inputStream));

        Closer.closeQuietly(inputStream);

        return true;
    }

    private static native String createSecretKey();

    static {
        System.loadLibrary("aio-lib");
    }
}
