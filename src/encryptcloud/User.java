/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

import java.util.Date;

/**
 *
 * @author Cesar
 */
public class User {

    private int id;
    private String username;
    private String email;
    private String csp;
    private String password;
    private String encryptionKey;
    private String accesToken;
    private String suid;

    public User(int id, String username, String email, String csp, String password, String encryptionKey, String accesToken, String suid) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.csp = csp;
        this.password = password;
        this.encryptionKey = encryptionKey;
        this.accesToken = accesToken;
        this.suid = suid;
    }

    
    
    public User (){
        
    }
    public User(int id, String username, String email, String password, String encryptionKey,  String suid){
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.encryptionKey=encryptionKey;
        this.suid=suid;
    }

    public String getCsp() {
        return csp;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return encryptionKey;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getSuid() {
        return suid;
    }

    public String getAccesToken() {
        return accesToken;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }   

    public void setSuid(String suid) {
        this.suid = suid;
    }

    public void setCsp(String csp) {
        this.csp = csp;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccesToken(String accesToken) {
        this.accesToken = accesToken;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }    
    
}
