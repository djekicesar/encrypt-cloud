/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlite;

import com.dropbox.core.v2.files.FileMetadata;
import encryptcloud.FileInfo;
import encryptcloud.User;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import security.FileCheckSumSHA;

/**
 *
 * @author Cesar
 */
public class SQLiteJDBC {
    Connection c = null;
    Statement stmt = null;
    private final String urlSQLite ="jdbc:sqlite:C:/Users/Cesar/Desktop/test.db" ;
    
    public Connection connect(){
        try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection(urlSQLite);
      } catch ( ClassNotFoundException | SQLException e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }
      System.out.println("Opened database successfully");
      return c;
   }
    
    public void createTable(){
        try {
         c = this.connect();
         stmt = c.createStatement();
         String sql = "CREATE TABLE IF NOT EXISTS USERS " +
                        "(ID INTEGER PRIMARY KEY     AUTOINCREMENT   NOT NULL," +
                        " USERNAME   TEXT   NOT NULL   UNIQUE, " +
                        " PASSWORD   TEXT   NOT NULL, " +
                        " EMAIL   TEXT   NOT NULL   UNIQUE, " + 
                        " CSP   TEXT   NOT NULL, " + 
                        " ENCRYPTIONKEY   TEXT   NOT NULL   UNIQUE, "+
                        " ACCESTOKEN   TEXT   NOT NULL, "+
                        " SUID   TEXT   NOT NULL   UNIQUE);"; 
         
         String sql2 = "CREATE TABLE IF NOT EXISTS FILEMETADATA" +
                        "(ID INTEGER PRIMARY KEY     AUTOINCREMENT   NOT NULL," +
                        " USERID   INTEGER   NOT NULL, " +
                        " NAME   TEXT   NOT NULL, " +
                        " CONTENTHASH   TEXT   NOT NULL, " + 
                        " HASHCODE   INTEGER   NOT NULL, " + 
                        " ENCRYPTED   BOOLEAN   NOT NULL, " +
                        " INTEGRITYCHECKED   BOOLEAN   NOT NULL);"; 
         
         stmt.executeUpdate(sql);
         stmt.execute(sql2);
         
      } catch ( SQLException e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }
      System.out.println("Table created successfully");
   }
    
    public int insertion(User user){
        int numRowsInserted = 0;
       
        try {
         Class.forName("org.sqlite.JDBC");
         c = this.connect();
         createTable();
         String sql = "INSERT INTO USERS(USERNAME, PASSWORD, EMAIL, CSP, ENCRYPTIONKEY, ACCESTOKEN, SUID) VALUES (?,?,?,?,?,?,?);";
         
            try ( Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getEmail());
                pstmt.setString(4, user.getCsp());
                pstmt.setString(5, user.getEncryptionKey());
                pstmt.setString(6, user.getAccesToken());
                pstmt.setString(7, user.getSuid());
                numRowsInserted = pstmt.executeUpdate();
                
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
      } catch ( ClassNotFoundException e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }
      System.out.println("Records created successfully");
      return numRowsInserted;
    }
    
    
    public User loginDatabase(String userName, String passWord) throws ClassNotFoundException, SQLException{
        User user = null;
        Class.forName("org.sqlite.JDBC");
        c = this.connect();
        String query = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?";
        PreparedStatement pstmt = c.prepareStatement(query);
        //Parameters
        pstmt.setString(1, userName);
        pstmt.setString(2, passWord);
        //Execute
        ResultSet res = pstmt.executeQuery();

        if(res.next()){
            //You dont need to find userName & passWord, are already exists
            int id =  res.getInt("id");
            String username =  res.getString("username");
            String email =  res.getString("email");
            String csp =  res.getString("csp");
            String encryptionKey =  res.getString("encryptionkey");
            String accestoken =  res.getString("accestoken");
            String password =  res.getString("password");
            String suid =  res.getString("suid");

            user = new User(id, username, email, csp, password, encryptionKey, accestoken, suid); 
        }
        c.close();
        return user;
        
    }
    
    public int savefilemetada(File file, int userid, boolean encryption, boolean integrity) throws NoSuchAlgorithmException, IOException, SQLException{
        int numRowsInserted = 0;
       
        try {
         Class.forName("org.sqlite.JDBC");
         c = this.connect();
         FileCheckSumSHA checksum = new FileCheckSumSHA();
         MessageDigest md = MessageDigest.getInstance("MD5");
         createTable();
         String sql = "INSERT INTO FILEMETADATA(USERID, NAME, CONTENTHASH, HASHCODE, ENCRYPTED, INTEGRITYCHECKED) VALUES (?,?,?,?,?,?);";
         
            try ( Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userid);
                pstmt.setString(2, file.getName());
                pstmt.setString(3, checksum.checksum(file.getPath(), md));
                pstmt.setInt(4, file.hashCode());
                pstmt.setBoolean(5, encryption);
                pstmt.setBoolean(6, integrity);
                numRowsInserted = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
      } catch ( ClassNotFoundException e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }
      c.close();
      System.out.println("Records created successfully");
      return numRowsInserted;
        
    }
    
    public FileInfo retrieveInfo(String filename, int userid) throws ClassNotFoundException, SQLException{
        FileInfo fileInfo = null;
        Class.forName("org.sqlite.JDBC");
        c = this.connect();
        String query = "SELECT * FROM FILEMETADATA WHERE NAME=? AND USERID=?;";
        PreparedStatement pstmt = c.prepareStatement(query);
        //Parameters
        pstmt.setString(1, filename);
        pstmt.setInt(2, userid);
        //Execute
        ResultSet res = pstmt.executeQuery();

        if(res.next()){
            //You dont need to find userName & passWord, are already exists
            String name =  res.getString("NAME");
            boolean encryption =  res.getBoolean("ENCRYPTED");
            boolean integrity =  res.getBoolean("INTEGRITYCHECKED");
            String contentHash =  res.getString("CONTENTHASH");
            int hashCode =  res.getInt("HASHCODE");

            fileInfo = new FileInfo(name, encryption, integrity, contentHash, hashCode); 
        }else{
            System.err.println("No match");
        }
        c.close();
        return fileInfo;
    }
    
    public void updateHash(String newContentHash, int userId, String filename) throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        c = this.connect();
        String query = "UPDATE FILEMETADATA SET CONTENTHASH = ? WHERE NAME=? AND USERID =?";
        PreparedStatement pstmt = c.prepareStatement(query);
        //Parameters
        pstmt.setString(1, newContentHash);
        pstmt.setString(2, filename);
        pstmt.setInt(3, userId);
        pstmt.executeUpdate();
        c.close();
    }
    
    public void selectAll(){
        try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection(urlSQLite);
      c.setAutoCommit(false);
      System.out.println("Opened database successfully");

      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS;" );
      
      while ( rs.next() ) {
         int id = rs.getInt("id");
         String  name = rs.getString("username");
//         Date creationDate  = rs.getDate("creationdate");
//         String  address = rs.getString("address");
//         float salary = rs.getFloat("salary");
         
         System.out.println( "ID = " + id );
         System.out.println( "NAME = " + name );
//         System.out.println( "AGE = " + creationDate);
//         System.out.println( "ADDRESS = " + address );
//         System.out.println( "SALARY = " + salary );
         System.out.println();
      }
      rs.close();
      stmt.close();
      c.close();
   } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
   }
   System.out.println("Operation done successfully");
    }
       
}
