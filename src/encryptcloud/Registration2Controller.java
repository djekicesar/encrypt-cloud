/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.jfoenix.controls.JFXButton;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Cesar
 */
public class Registration2Controller implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private JFXButton back;

    @FXML
    private JFXButton dropbox;
     
    @FXML
    private ImageView oneDrive;

    @FXML
    private ImageView icloud;

    @FXML
    private ImageView owncloud;

    @FXML
    private ImageView google;
    
    User user = readUser();
    
    final String APP_KEY = "6qt0cxr0lllak0f";
    final String APP_SECRET = "5heqzjf03rb71v9";
    DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/EncryptCloud").build();
    
    
    public void writeUser(User user){
        String filePath  = "tmp.txt";
        try(FileWriter fileWriter = new FileWriter(filePath)) {
            String fileContent = user.getUsername()+" "+user.getEmail()+" "+user.getPassword()+" "+user.getEncryptionKey()+" "+user.getSuid()+" "+user.getCsp();
            fileWriter.write(fileContent);
        } catch (IOException e) {
            // exception handling
            System.err.println("File not exist");
        }
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
    void dropboxAction(ActionEvent event) throws URISyntaxException, IOException {
        user.setCsp("Dropbox");
        DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder().withNoRedirect().build();
        String authorizeUrl = webAuth.authorize(webAuthRequest);
        writeUser(user);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
             Desktop.getDesktop().browse(new URI(authorizeUrl));
                     
        }
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        Parent parent = FXMLLoader.load(getClass().getResource("Registration3.fxml"));
        Scene scene = new Scene(parent);
        stage.setTitle("Encrypt Cloud Registration 3/3");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void goback(ActionEvent event) throws IOException {
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        Parent parent = FXMLLoader.load(getClass().getResource("registration.fxml"));
        Scene scene = new Scene(parent);
        stage.setTitle("Encrypt Cloud Registration 1/3");
        stage.setScene(scene);
        stage.show();
    }
    
   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
