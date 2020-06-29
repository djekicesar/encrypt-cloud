/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.util.ProgressOutputStream;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.FullAccount;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 *
 * @author Cesar
 */
public class DropboxService {
     private static final String ACCESS_TOKEN = "#";
     private final  DbxClientV2 client;
     
    public DropboxService() throws IOException, DbxException {
        //DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/EncryptCloud").build();
        //DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        // Have the user sign in and authorize your app.
        /*String authorizeUrl = webAuth.start();
         System.out.println("1. Go to: " + authorizeUrl);
         System.out.println("2. Click Allow (you might have to log in first)");
         System.out.println("3. Copy the authorization code.");
         String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
         // This will fail if the user enters an invalid authorization code.
         DbxAuthFinish authFinish = webAuth.finish(code);
         String accessToken = authFinish.accessToken;*/
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }
    
    public FullAccount getAccount() throws DbxException{
        return client.users().getCurrentAccount();
    }
    
    public void uploadFile(String fileName) throws DbxException, IOException {       
        try (InputStream in = new FileInputStream(fileName)) {
            FileMetadata metadata = client.files().uploadBuilder(fileName).uploadAndFinish(in);
        }
    }
    
    public void downloadFile( String dropBoxFilePath , String localFileAbsolutePath) throws DownloadErrorException , DbxException , IOException {
        
	DbxDownloader <FileMetadata> dl = client.files().download(dropBoxFilePath);
				
	//FileOutputStream
	FileOutputStream fOut = new FileOutputStream(localFileAbsolutePath);
        
	//System.out.println("Downloading .... " + dropBoxFilePath);			
	//Add a progress Listener
        //	dl.download(new ProgressOutputStream(fOut, dl.getResult().getSize(), (long completed , long totalSize) -> {
        //
        //		System.out.println( ( completed * 100 ) / totalSize + " %");
        //					
        //	}));
				
    }
}
