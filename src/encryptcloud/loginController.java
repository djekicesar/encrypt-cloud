/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

import com.jfoenix.controls.JFXButton;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import security.PasswordUtils;
import sqlite.SQLiteJDBC;

/**
 *
 * @author Cesar
 */
public class loginController implements Initializable {
    
     @FXML
    private TextField username;

    @FXML
    private PasswordField password;
    
    @FXML
    private JFXButton signup;

    @FXML
    private Button signin;
    
    @FXML
    private JFXButton forgotten;

    @FXML
    private Label status;
    
    SQLiteJDBC sqlite = new SQLiteJDBC();
    
    User user;
     
    String salt = "UXBqn2tvZB8IXZ45s6JnZxtYUX11Fb";
    
    public void writeUser(User user){
        String filePath  = "tmp.txt";
        try(FileWriter fileWriter = new FileWriter(filePath)) {
            String fileContent = user.getUsername()+" "+user.getEmail()+" "+user.getPassword()+" "+user.getEncryptionKey()+" "+user.getSuid()+" "+user.getCsp()+" "+user.getAccesToken()+" "+user.getId();
            fileWriter.write(fileContent);
        } catch (IOException e) {
            // exception handling
            System.err.println("File not exist");
        }
    }
    
    @FXML
    void signup(ActionEvent event) throws IOException {
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        Parent parent = FXMLLoader.load(getClass().getResource("registration.fxml"));
        Scene scene = new Scene(parent);
        stage.setTitle("Encrypt Cloud Registration 1/3");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void login(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {
        String userName = username.getText();
        String passWord = PasswordUtils.generateSecurePassword(password.getText(), salt);
        user = sqlite.loginDatabase(userName, passWord);

        if(user != null){
            //Log to home page
            writeUser(user);
            System.out.println("Connexion successful");
            Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
            Parent parent = FXMLLoader.load(getClass().getResource("homepage.fxml"));
            Scene scene = new Scene(parent);
            stage.setTitle("Encrypt Cloud - Dashboard");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        }else{
            // login failed 'Invalid login'
            status.setText("Authentication failed. Username or password incorrect.");
           
        }
    }
    
    @FXML
    void passwordRecovery(ActionEvent event) {
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
   
    }    
    
}
