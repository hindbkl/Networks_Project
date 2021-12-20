/*
Source of this file : BA3 INFO-H303 Project (authors : Sami Abdul Sater, Hind Bakkali, Luka Giaprakis)
Aim : connect to a database based on a configuration file
*/

package Model;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class Database {

    private String dbName, user, password, url;
    private Connection conn ;

    /**
     * Constructor that receives a file of configuration to fetch, which includes dbName, user, password
     * @param configFile
     */
    public Database(File configFile){
        getConfig(configFile) ;
        this.url = "jdbc:postgresql://localhost/" + dbName;
        conn = connect() ;
    }

    /**
     * Assign the parameters of the connection to the given file input
     * @param configFile
     */
    public void getConfig(File configFile){
        try {
            BufferedReader configReader = new BufferedReader(new FileReader(configFile));
            dbName = configReader.readLine() ;
            user = configReader.readLine() ;
            password = configReader.readLine() ;
            configReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found.");
        } catch (IOException e) {
            System.err.println("User name or password could not be read.");
        }
        return ;
    }

    /**
     * Connects to DB according to the previously set inputs.
     * @return
     */
    private Connection connect() {
        Connection connection = null ;
        try {
            System.out.println("Attempting connection to "+ dbName + "...");

            // Main line
            connection = DriverManager.getConnection(url, user, password);

            System.out.println("Connection to "+ dbName + " successful.");

        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }
        return connection ;
    }

    /**
     * Executes the given SQL query on database.
     * @param query
     * @return
     */
    public ArrayList<String> executeQuery(String query){

        // String that will contain the query result
        ArrayList<String> queryResult = new ArrayList<>() ;
        int nbCol ;

        // Each query : one statement, which creation must be surrounded by try/catch
        try {
            Statement stmt = conn.createStatement();

            // getting query results
            ResultSet res = stmt.executeQuery(query);

            // fetching metadata to retreive number of columns returned by the query (may be 1, 2, ... according to the query)
            ResultSetMetaData resultSetMetaData = res.getMetaData();

            nbCol = resultSetMetaData.getColumnCount();

            // iteration over the returned rows (may be 1 row, 2 rows, ... according to the query)
            while (res.next()){
                // iteration over columns, starting from column 1 !!
                String rowResult = "" ;
                for (int i =1 ; i <= nbCol ; i ++){
                    // append the value of the column "i" at the current row
                    rowResult += res.getString(i) + "\t" ;
                }

                // for clear formatting, adding a enter between each line
                queryResult.add(rowResult) ;
            }
            res.close();

        } catch (SQLException throwables) {
            System.err.println("");//throwables.getMessage());
        }
        return queryResult;
    }

    // if input is a file containing a query, reading it and storing it to a String
    public String getQuery(File queryFile) {
        String query = "" ;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(queryFile)) ;
            String line = reader.readLine() ;
            while (line != null){
                query += line + "\n" ;
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Query file not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return query;
    }

    public ArrayList<String> getMessages(String user, String contact) {
        ArrayList<String> result = executeQuery("SELECT sender, timestamp, content FROM messages WHERE (sender = '" + user
                + "' AND receiver = '" + contact + "') OR (receiver = '" + user + "' AND sender = '" + contact + "')"); //TODO : décrypter
        ArrayList<String> messages = new ArrayList<>();
        for (String s1 : result){
            String s2 = s1.replace("\t","\n");
            messages.add(s2);
        }
        return messages;
    }

    public ArrayList<String[]> getContacts(String user){
        ArrayList<String> contacts = executeQuery("SELECT username, connected FROM users WHERE username <> '" + user + "'");
        ArrayList<String[]> formatted = new ArrayList<>();
        for (String res : contacts){
            String[] s = res.split("\t");
            formatted.add(s);
        }

        return formatted;
    }
}