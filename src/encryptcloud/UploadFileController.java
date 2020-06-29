/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.util.IOUtil.ProgressListener;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import security.CryptoException;
import security.CryptoUtils;
import sqlite.SQLiteJDBC;

/**
 * FXML Controller class
 *
 * @author Cesar
 */
public class UploadFileController implements Initializable {

    /**
     * Initializes the controller class.
     */
     @FXML
    private JFXCheckBox integrity;

    @FXML
    private JFXCheckBox encryption;

    @FXML
    private Button chooseFile;

    @FXML
    private JFXComboBox <String> parentFolder;

    @FXML
    private Button cancelButton;

    @FXML
    private Button sendFile;
    
    @FXML
    private ProgressBar progressbar;
    
    @FXML
    private Label pourcentage;
    
    @FXML
    private Label status;

    
    final FileChooser fileChooser = new FileChooser();
    
    List <File> files = null;
    
    User user = readUser();
    
    SQLiteJDBC sqlite = new SQLiteJDBC();
    
    private String ACCESS_TOKEN = user.getAccesToken();    
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/EncryptCloud").build();
    
    DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
    
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
    
    public void uploadFile(File file) throws DbxException, IOException, NoSuchAlgorithmException {
        try (InputStream in = new FileInputStream(file)) {
            ProgressListener progressListener = l -> printProgress(l, file.length());
            FileMetadata metadata = client.files().uploadBuilder("/encrypt/" +file.getName()).uploadAndFinish(in, progressListener);
            System.out.println(metadata.toStringMultiline());
        }catch (UploadErrorException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        } catch (DbxException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Error reading from file \"" + file + "\": " + ex.getMessage());
            System.exit(1);
        }
    }

    private void printProgress(long uploaded, long size) {
        System.out.printf("Uploaded %12d / %12d bytes (%5.2f%%)\n", uploaded, size, 100 * (uploaded / (double) size));
        double val = (uploaded / (double) size);
         try {
             Thread.sleep(2000);
             progressbar.setProgress(val);
             pourcentage.setText((100 * val) + "%");
         } catch (InterruptedException ex) {
             Logger.getLogger(UploadFileController.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    @FXML
    void cancelButtonAct(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void chooseFileAction(ActionEvent event) throws DbxException, IOException {
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        files = fileChooser.showOpenMultipleDialog(stage);
        String texte = " ";
        if(files!=null){
            texte = files.stream().map((file) -> file.getName() +" ").reduce(texte, String::concat);
        }else{
            texte = "Choose a File From Your Machine";
        }
        chooseFile.setText(texte);       
    }
    
    @FXML
    void sendFileAction(ActionEvent event) throws DbxException, CryptoException, NoSuchAlgorithmException, IOException, SQLException {
        
        int row;
        if (files != null) {
            for(File f: files){
                try {
                    if(encryption.isSelected()){
                        CryptoUtils.encrypt(user.getEncryptionKey(), f, f);
                    }
                    uploadFile(f);
                    row = sqlite.savefilemetada(f, user.getId(), encryption.isSelected(), integrity.isSelected());
                    status.setText("Your file has been uploaded successfully");
                } catch (IOException ex) {
                    Logger.getLogger(UploadFileController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        user = readUser();
    }    
    
}
