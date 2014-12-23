package at.deder.ybr.repository;

import at.deder.ybr.channels.AbstractOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculates and stores the hashcode of a package.
 *
 * @author lycis
 */
public class PackageHash {

    private String hash = "";

    public PackageHash() {

    }

    /**
     * Create a hash for the given directory and index.
     *
     * @param targetDir
     * @param index
     */
    public PackageHash(File targetDir, PackageIndex index) {
        calculateHash(targetDir, index);
    }

    /**
     * creates a package hash with the given hash code
     *
     * @param hash
     */
    public PackageHash(String hash) {
        this.hash = hash;
    }

    /**
     * calculates a hash of all files in the package
     *
     * @param targetDir
     * @param index
     */
    public void calculateHash(File targetDir, PackageIndex index) {
        List<File> indexedFiles = index.applyTo(targetDir);

        if (indexedFiles == null) {
            return; // no valid index
        }

        if (indexedFiles.isEmpty()) {
            hash = ""; // no files empty hash
            return;
        }

        List<String> hashCodes = new ArrayList<>();
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            AbstractOutputChannel out = OutputChannelFactory.getOutputChannel();
            out.printErrLn("error: #PHx01 - " + ex.getMessage());
            return;
        }

        for (File f : indexedFiles) {
            try {
                hashCodes.add(hexHash(new FileInputStream(f)));
            } catch (NoSuchAlgorithmException | IOException ex) {
                AbstractOutputChannel out = OutputChannelFactory.getOutputChannel();
                out.printErrLn("error: #PHx02 hashing failed (" + f.getAbsolutePath() + ") - " + ex.getMessage());
                return;
            }
        }
        
        Collections.sort(hashCodes);
        StringBuilder hashSb = new StringBuilder();
        hashCodes.stream().forEach((hc) -> {
            hashSb.append(hc).append("\n");
        });
        
        
        String metaHash;
        try {
            metaHash = hexHash(new ByteArrayInputStream(hashSb.toString().getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException | IOException ex) {
             AbstractOutputChannel out = OutputChannelFactory.getOutputChannel();
             out.printErrLn("error: #PHx03 hashing failed - " + ex.getMessage());
             return;
        }
                
        hash = metaHash;
    }

    public String toString() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    private String hexHash(InputStream data) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        while ((nread = data.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };

        byte[] mdbytes = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
