/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptcloud;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.files.Metadata;
import com.jfoenix.controls.JFXButton;
import java.awt.Desktop;
import static java.awt.SystemColor.desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.fx.ui.controls.filesystem.DirectoryTreeView;
import org.eclipse.fx.ui.controls.filesystem.DirectoryView;
import org.eclipse.fx.ui.controls.filesystem.IconSize;
import org.eclipse.fx.ui.controls.filesystem.ResourceItem;
import org.eclipse.fx.ui.controls.filesystem.ResourcePreview;
import org.eclipse.fx.ui.controls.filesystem.RootDirItem;

import java.nio.file.Paths;
 
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import org.eclipse.fx.ui.controls.filesystem.ResourceEvent;
import security.CryptoException;
import security.CryptoUtils;

/**
 * FXML Controller class
 *
 * @author Cesar
 */
public class HomepageController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    private static RootDirItem rootDirItem;
    
    User user = readUser();
    
    
    private final String ACCESS_TOKEN = user.getAccesToken(); /*"zCsY2ScOJ4AAAAAAAAAAP5WEkzTMXaX9q5UVvamnBpm8oAZS0BYp6mx380QUNcJf"*/;
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/EncryptCloud").build();
    DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
  
    @FXML
    private SplitPane p;

    @FXML
    private JFXButton createFolder;

  
    @FXML
    private Label currentUser;

     @FXML
    private AnchorPane master;
   
    @FXML
    private Button upLoadFile;

    @FXML
    private Button uploadFolder;
    
    public void writeFile(String path){
        String filePath  = "tampon.txt";
        try(FileWriter fileWriter = new FileWriter(filePath)) {
            String fileContent = path;
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
    void upLoadFileAction(ActionEvent event) {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(EncryptCloud.class.getResourceAsStream("/images/logo2.png")));
        Parent root = null;
        try {
            root = FXMLLoader.load(
                    UploadFileController.class.getResource("UploadFile.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        stage.setScene(new Scene(root));
        stage.setTitle("Upload File To Your Cloud");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node)event.getSource()).getScene().getWindow() );
        stage.show();
            
    }

    @FXML
    void uploadFolderAction(ActionEvent event) {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(EncryptCloud.class.getResourceAsStream("/images/logo2.png")));
        Parent root = null;
        try {
            root = FXMLLoader.load(
                    UploadFileController.class.getResource("UploadFolder.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        stage.setScene(new Scene(root));
        stage.setTitle("Upload Folder To Your Cloud");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node)event.getSource()).getScene().getWindow() );
        stage.show();
    }
    
    public void uploadFile(String fileName) throws DbxException, IOException {       
        try (InputStream in = new FileInputStream(fileName)) {
            FileMetadata metadata = client.files().uploadBuilder(fileName).uploadAndFinish(in);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
         /*try {
            // TODO
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/EncryptCloud").build();
            DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
           
            ListFolderResult result = client.files().listFolder("/test");
            
            
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    DbxDownloader <FileMetadata> downloader = client.files().download(metadata.getPathLower());
                    FileOutputStream out = new FileOutputStream("C:\\DropBox\\"+metadata.getName());
                    try {
                        FileMetadata response = downloader.download(out);
                        System.out.println(response);
                    } catch (IOException ex) {
                         Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
                     } finally {
                        try {
                            out.close();
                        } catch (IOException ex) {
                            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    System.out.println(metadata.getPathLower());
                }

                if (!result.getHasMore()) {
                    break;
                }

            result = client.files().listFolderContinue(result.getCursor());
            }
            
        } catch (DbxException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        }*/
         
        rootDirItem = ResourceItem.createObservedPath(Paths.get("C:\\Users\\Cesar\\Dropbox"));
        
        DirectoryTreeView tv = new DirectoryTreeView();
        tv.setIconSize(IconSize.MEDIUM);
        tv.setRootDirectories(FXCollections.observableArrayList(rootDirItem));

        DirectoryView v = new DirectoryView();
        v.setIconSize(IconSize.MEDIUM);
       
        
        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }      
        
        v.setOnOpenResource((ResourceEvent<ResourceItem> e) -> {
            File file = new File(e.getResourceItems().get(0).getUri().substring(6).replace("%20", " "));
            writeFile(file.getPath());
            if(file.exists()) {
//                desktop.open(file);
                Stage stage = new Stage();
                stage.getIcons().add(new Image(EncryptCloud.class.getResourceAsStream("/images/logo2.png")));
                Parent root = null;
                try {
                    root = FXMLLoader.load(
                    InfosController.class.getResource("Infos.fxml"));
                } catch (IOException ex) {
                    Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
                }
                stage.setScene(new Scene(root));
                stage.setTitle("File Informations");
                stage.setResizable(false);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node)e.getSource()).getScene().getWindow() );
                stage.show();
             } else{
                System.err.println("File not exist");
             }
        });

        tv.getSelectedItems().addListener( (Observable o) -> {
          if( ! tv.getSelectedItems().isEmpty() ) {
            v.setDir(tv.getSelectedItems().get(0));
          } else {
            v.setDir(null);
          }
        });
     

        ResourcePreview prev = new ResourcePreview();
        v.getSelectedItems().addListener((Observable o) -> {
          if( v.getSelectedItems().size() == 1 ) {
            prev.setItem(v.getSelectedItems().get(0));
          } else {
            prev.setItem(null);
          }
        });
        p.getItems().addAll(tv, v, prev);               
        p.setDividerPositions(0.2,0.8);
        rootDirItem.dispose();
    }    
    
}
