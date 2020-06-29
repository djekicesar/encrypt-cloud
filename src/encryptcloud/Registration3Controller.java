/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.DbxClientV2;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import sqlite.SQLiteJDBC;

/**
 * FXML Controller class
 *
 * @author Cesar
 */
public class Registration3Controller implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TextField code;

    @FXML
    private JFXButton finish;

    @FXML
    private JFXCheckBox licence;
    
    SQLiteJDBC sqlite = new SQLiteJDBC();
   
    User user = readUser();
    
    final String APP_KEY = "6qt0cxr0lllak0f";
    final String APP_SECRET = "5heqzjf03rb71v9";
    DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/EncryptCloud").build();
    
    
    
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
//                    System.out.println(user.getUsername()+" "+user.getEmail()+" "+user.getPassword());
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
    
    public void writeUser(User user){
        String filePath  = "tmp.txt";
        try(FileWriter fileWriter = new FileWriter(filePath)) {
            String fileContent = "";
            fileWriter.write(fileContent);
        } catch (IOException e) {
            // exception handling
            System.err.println("File not exist");
        }
    }
    
    @FXML
    void finishAction(ActionEvent event) throws IOException {
         Window owner = finish.getScene().getWindow();
        DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
        String code1;
        code1 = code.getText().trim();
        DbxAuthFinish authFinish;
        try {
            authFinish = webAuth.finishFromCode(code1);
            user.setAccesToken(authFinish.getAccessToken());            
            int i = sqlite.insertion(user);
            if(i==1){
                writeUser(user);
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, owner, "Form Succes!", "Account created succesfully");
                Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
                Parent parent = FXMLLoader.load(getClass().getResource("login.fxml"));
                Scene scene = new Scene(parent);
                stage.setTitle("Encrypt Cloud Login");
                stage.setScene(scene);
                stage.show();
            }           
        } catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.authorize: " + ex.getMessage());
            System.exit(1);
        }
        
    }

    @FXML
    void licenceAction(ActionEvent event) {

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
