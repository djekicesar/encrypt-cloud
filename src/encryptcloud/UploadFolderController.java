/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Cesar
 */
public class UploadFolderController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private JFXCheckBox integrity;

    @FXML
    private JFXCheckBox encryption;

    @FXML
    private Button chooseFolder;

    @FXML
    private JFXComboBox<?> parentFolder;

    @FXML
    private Button cancelButton;

    @FXML
    private Button sendFolder;
    
     final DirectoryChooser directoryChooser = new DirectoryChooser();
     //configuringDirectoryChooser(directoryChooser);
 
    
    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        // Set title for DirectoryChooser
        directoryChooser.setTitle("Select Some Directories");
 
        // Set Initial Directory
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }
    
    

    @FXML
    void cancelButtonAct(ActionEvent event) {

    }

    @FXML
    void chooseFolderAction(ActionEvent event) {
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        File dir = directoryChooser.showDialog(stage);
            if (dir != null) {

            } else {

            }
    }

    @FXML
    void sendFolderAction(ActionEvent event) {

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
