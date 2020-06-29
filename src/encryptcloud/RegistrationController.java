/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import security.PasswordUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import sqlite.SQLiteJDBC;

/**
 * FXML Controller class
 *
 * @author Cesar
 */
public class RegistrationController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TextField username;

    @FXML
    private JFXButton nextButton;

    @FXML
    private JFXButton backToLogin;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField passConfirmation;

    @FXML
    private Button createUser;
    
    @FXML
    private Label notification;
    
    SQLiteJDBC sqlite = new SQLiteJDBC();
    
    User user;
    
    String salt = "UXBqn2tvZB8IXZ45s6JnZxtYUX11Fb";
    
    @FXML
    void backToLoginAction(ActionEvent event) {

    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isValidate(String emailStr) {
        boolean isValid = false;
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
            if (matcher.find()){
                isValid = true;
            }
            return isValid;
    }
    
    public static boolean isPasswodValid(String password){
        String validPasswordRegEx = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).{8,15}$";
        boolean isOk = false;
        if(password.matches( validPasswordRegEx )){
            isOk = true;
        }
        return isOk;
    }
    
    public void writeUser(User user){
        String filePath  = "tmp.txt";
        try(FileWriter fileWriter = new FileWriter(filePath)) {
            String fileContent = user.getUsername()+" "+user.getEmail()+" "+user.getPassword()+" "+user.getEncryptionKey()+" "+user.getSuid();
            fileWriter.write(fileContent);
        } catch (IOException e) {
            // exception handling
            System.err.println("File not exist");
        }
    }
    
    public String generateEncryptionKey() {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 16;
    Random random = new Random();
 
    String generatedString = random.ints(leftLimit, rightLimit + 1)
      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
      .limit(targetStringLength)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
    
    return generatedString;
}
    
    @FXML
    void nextButtonAction(ActionEvent event) throws IOException {
         Window owner = nextButton.getScene().getWindow();
         if(username.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter a username");
            return;
        }
        if(email.getText().isEmpty() || !isValidate(email.getText())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter a valid email address");
            return;
        }
        if(password.getText().isEmpty() || !isPasswodValid(password.getText())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter a valid password. Must contain at least: number, upper case, lower case, special caracter and 8 caracters)");
            return;
        }
        if(passConfirmation.getText().isEmpty() || !(passConfirmation.getText().equals(password.getText()))) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please the password must match");
            return;
        }
        String salt = "UXBqn2tvZB8IXZ45s6JnZxtYUX11Fb";
        String mySecurePassword = PasswordUtils.generateSecurePassword(password.getText(), salt);
        String encryptionKey = generateEncryptionKey();
        

        user = new User(0,
                username.getText(),
                email.getText(),
                mySecurePassword,
                encryptionKey,
                salt
        );
        writeUser(user);
        //System.out.println(user.getUsername()+user.getEmail()+user.getPassword());
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        Parent parent = FXMLLoader.load(getClass().getResource("Registration2.fxml"));
        Scene scene = new Scene(parent);
        stage.setTitle("Encrypt Cloud Registration 2/3");
        stage.setScene(scene);
        stage.show();
    }   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
