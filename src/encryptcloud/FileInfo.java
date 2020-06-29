/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

/**
 *
 * @author Cesar
 */
public class FileInfo {
    String name;
    boolean encryption;
    boolean  integrity;
    String contentHash;
    int hashCode;

    public FileInfo() {
    }

    
    public FileInfo(String name, boolean encryption, boolean integrity, String contentHash, int hashCode) {
        this.name = name;
        this.encryption = encryption;
        this.integrity = integrity;
        this.contentHash = contentHash;
        this.hashCode = hashCode;
    }

    public boolean getEncryption() {
        return encryption;
    }

    public int getHashCode() {
        return hashCode;
    }

    public String getName() {
        return name;
    }

    public boolean getIntegrity() {
        return integrity;
    }

    
    public String getContentHash() {
        return contentHash;
    }
    
    
    
}
