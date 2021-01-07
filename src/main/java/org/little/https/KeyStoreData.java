package org.little.https;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.Properties;

public final class KeyStoreData {

    private final PrivateKey        key;
    private final X509Certificate[] certificateChain;

    public static final String KEYSTORE_FILEPATH  = "keystore.filepath";
    public static final String KEYSTORE_PASSWORD  = "keystore.password";
    public static final String ENTRY_ALIAS        = "entry.alias";
    public static final String ENTRY_KEY_PASSWORD = "entry.key.password";

    public static final String PKCS_12 = "PKCS12";

    /**
     * Reads data from keystore and returns a keystore data object. .
     *
     * @param securityConfigProp property list containing the parameters necessary to retrieve the keystore data
     * @return keystore data object
     */
    public static KeyStoreData from(Properties securityConfigProp)   throws CertificateException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,IOException {

        return from(Objects.requireNonNull(securityConfigProp.getProperty(KEYSTORE_FILEPATH)),
                    Objects.requireNonNull(securityConfigProp.getProperty(KEYSTORE_PASSWORD).toCharArray()),
                    Objects.requireNonNull(securityConfigProp.getProperty(ENTRY_ALIAS)),
                    Objects.requireNonNull(securityConfigProp.getProperty(ENTRY_KEY_PASSWORD).toCharArray()));
    }

    /**
     * Reads data from keystore and returns a keystore data object. .
     *
     * @param keystoreFilePath the path of the KeyStore file
     * @param keystorePassword the password used to check the integrity of the keystore, the password used to unlock the
     *                         keystore,
     * @param alias            alias of the keystore entry of interest
     * @param entryKeyPassword the password used to protect the entry of interest
     * @return keystore data object
     * @throws IOException               if there is an I/O or format problem with the keystore data, if a password is
     *                                   required but not given, or if the given password was incorrect.
     * @throws KeyStoreException         if the keystore has not been initialized (loaded).
     * @throws CertificateException      if any of the certificates in the keystore could not be loaded
     * @throws NoSuchAlgorithmException  if the algorithm used to check the integrity of the keystore cannot be found
     * @throws UnrecoverableKeyException if the entry is a {@code PrivateKeyEntry} or {@code SecretKeyEntry} and the
     *                                   specified password is wrong.
     */
    public static KeyStoreData from(String keystoreFilePath, char[] keystorePassword, String alias,char[] entryKeyPassword)throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException,UnrecoverableEntryException {

        KeyStore keyStore = KeyStore.getInstance(PKCS_12);

        try(final FileChannel keystoreFileChannel = FileChannel.open(Paths.get(keystoreFilePath), StandardOpenOption.READ)) {
            keyStore.load(Channels.newInputStream(keystoreFileChannel), keystorePassword);
        }

        PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry) keyStore.getEntry(alias, new PasswordProtection(entryKeyPassword));

        return new KeyStoreData(privateKeyEntry.getPrivateKey(),(X509Certificate[]) privateKeyEntry.getCertificateChain());
    }


    public KeyStoreData(PrivateKey key, X509Certificate[] certificateChain) {
        this.key = key;
        this.certificateChain = certificateChain;
    }

    /**
     * Gets the {@code PrivateKey}.
     *
     * @return the {@code PrivateKey}
     */
    public PrivateKey getKey() {
        return key;
    }

    /**
     * Gets the {@code Certificate} chain.
     *
     * @return an array of {@code Certificate}s corresponding to the certificate chain for the public key.
     */
    public X509Certificate[] getCertificateChain() {
        return certificateChain;
    }
}
