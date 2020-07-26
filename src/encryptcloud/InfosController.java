/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

import com.jfoenix.controls.JFXCheckBox;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import security.CryptoException;
import security.CryptoUtils;
import security.FileCheckSumSHA;
import sqlite.SQLiteJDBC;

/**
 * FXML Controller class
 *
 * @author Cesar
 */
public class InfosController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private JFXCheckBox integrity;

    @FXML
    private Button openButton;

    @FXML
    private Button EcryptButton;

    @FXML
    private Button decryptButton;

    @FXML
    private Button closeButton;

    @FXML
    private JFXCheckBox encryption;

    @FXML
    private Label fileName;

    @FXML
    private Label encrytionStatut;

    @FXML
    private Label integrityStatus;
    
    @FXML
    private JFXCheckBox noButton;

    @FXML
    private JFXCheckBox yesButton;

    @FXML
    private Label notification;
    
    SQLiteJDBC sqlite; 
    
    File MyFile; 
    
    FileInfo info; 
    
    User user; 
    
    FileCheckSumSHA checksum;
    MessageDigest md = MessageDigest.getInstance("MD5");

    public InfosController() throws NoSuchAlgorithmException {
        this.checksum = new FileCheckSumSHA();
    }
    
    public String readFile(){
        String fileName  = "tampon.txt";
        String line = null;
        
        try {
            FileReader fileReader = new FileReader(fileName);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                line = bufferedReader.readLine();
            }         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return line;
    }
    
     public User readUser(){
        String fileName  = "tmp.txt";
        String line = null;
        User user1 = new User();
        
        try {
            FileReader fileReader = new FileReader(fileName);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    String[] words = line.split(" ");
                    user1.setUsername(words[0]);
                    user1.setEmail(words[1]);
                    user1.setPassword(words[2]);
                    user1.setEncryptionKey(words[3]);
                    user1.setSuid(words[4]);
                    user1.setCsp(words[5]);
                    user1.setAccesToken(words[6]);
                    user1.setId(Integer.parseInt(words[7].trim()));
                }
            }         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return user1;
    }

    @FXML
    void EcryptButtonAction(ActionEvent event) throws CryptoException {
        CryptoUtils.encrypt(user.getEncryptionKey(), MyFile, MyFile);
    }

    @FXML
    void closeButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void decryptButtonAction(ActionEvent event) throws CryptoException {
        if(decryptButton.getText()=="Encrypt"){
            CryptoUtils.encrypt(user.getEncryptionKey(), MyFile, MyFile);
            decryptButton.setText("Decrypt");
        }
        else{
           CryptoUtils.decrypt(user.getEncryptionKey(), MyFile, MyFile);
           decryptButton.setText("Encrypt");
        }
        
    }

    @FXML
    void openButtonAction(ActionEvent event) throws IOException, CryptoException {
        
        Desktop desktop = Desktop.getDesktop();
        if(MyFile.length()%16==0){
            CryptoUtils.decrypt(user.getEncryptionKey(), MyFile, MyFile);
        }
        desktop.open(MyFile);
    }
    
    @FXML
    void noButtonAction(ActionEvent event) {
//        Stage stage = (Stage) noButton.getScene().getWindow();
//        stage.close();    
          notification.setVisible(false);
          yesButton.setVisible(false);
          noButton.setVisible(false);
    }
    
     @FXML
    void yesButtonAction(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        sqlite.updateHash(checksum.checksum(MyFile.getPath(), md), user.getId(), MyFile.getName());
        notification.setVisible(false);
        yesButton.setVisible(false);
        noButton.setVisible(false);
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sqlite = new SQLiteJDBC();
        MyFile = new File(readFile());
        info = new FileInfo();
        user = readUser();
        try {
            // TODO
            info = sqlite.retrieveInfo(MyFile.getName(), user.getId());
            System.out.println(checksum.checksum(MyFile.getPath(), md));
            if(info !=null){
                fileName.setText(MyFile.getName());
                if(info.getEncryption()/*MyFile.length()%16==0*/){
                     encryption.setText("Yes");
                     encrytionStatut.setText("The file is encrypted");
                     encryption.setCheckedColor(Paint.valueOf("#008000"));
                     EcryptButton.setText("Decrypt");
                 }
                 if(info.integrity){
                     integrity.setText("Yes");
                     integrity.setCheckedColor(Paint.valueOf("#008000"));
                     if(checksum.checksum(MyFile.getPath(), md).equals(info.getContentHash())){
                         integrityStatus.setText("The File is not altered.");
                     }else{
                         integrityStatus.setText("The File is altered.");
                         integrityStatus.setTextFill(Color.web("#e1ff00"));
                         notification.setVisible(true);
                         noButton.setVisible(true);
                         yesButton.setVisible(true);
                     }
                 } 
            }else{
                fileName.setText(MyFile.getName());
                if(MyFile.length()%16==0){
                     encryption.setText("Yes");
                     encrytionStatut.setText("The file is encrypted");
                     encryption.setCheckedColor(Paint.valueOf("red"));
                     EcryptButton.setText("Decrypt");
                 }
            }
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InfosController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(InfosController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InfosController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
