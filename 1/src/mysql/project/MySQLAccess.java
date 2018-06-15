package mysql.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class MySQLAccess {
//    private Connection connect = null;
    private Statement statement = null;
//    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public void readDataBase() throws Exception {

//
        // This will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.cj.jdbc.Driver");
//        PreparedStatement stmt = null;

        // Setup the connection with the DB
        try (Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?verifyServerCertificate=false&useSSL=true&user=sqluser&password=sqluserpw&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC")){

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO movies (NAME, LENGTH) VALUES (?, ?)")) {
                stmt.setString(1, "Titanic");
                stmt.setInt(2, 269);
                stmt.executeUpdate();
            }

            // stmt is auto closed here, even if SQLException is thrown

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO theatres (PHONE_NUMBER) VALUES (?)")) {
                 stmt.setString(1, "08773623333");
                 stmt.executeUpdate();
            }
            // stmt is auto closed here, even if SQLException is thrown

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO auditoriums (THEATRE_ID) VALUES (?)")) {
                 stmt.setInt(1, 1);
                stmt.executeUpdate();
            }
            // stmt is auto closed here, even if SQLException is thrown

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO movie_showtimes (START_TIME, MOVIE_ID, AUDITORIUM_ID, THEATRE_ID) VALUES (?,?,?,?)")) {
                 stmt.setString(1, "17:55");
                 stmt.setInt(2, 1);
                 stmt.setInt(3, 1);
                 stmt.setInt(4, 1);
                 stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connect.prepareStatement("DELETE FROM movie_showtimes WHERE LENGTH = ?;")) {
                 stmt.setInt(1, 269);
                 stmt.executeUpdate();

               }

        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO movies (NAME, LENGTH) VALUES (?, ?)")) {
                stmt.setString(1, "Iron Man");
                stmt.setInt(2, 140);
                stmt.executeUpdate();
            }


            try (PreparedStatement stmt = connect.prepareStatement("UPDATE movies SET NAME = ?, LENGTH = ? WHERE NAME = ?")) {
                stmt.setString(1, "Iron Man");
                stmt.setInt(2, 140);
                stmt.setString(3, "Adventures");
                stmt.executeUpdate();
            }
        }

    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            String name = resultSet.getString("NAME");
            Integer length = resultSet.getInt("LENGTH");
            System.out.println("Name: " + name);
            System.out.println("Length: " + length);
        }
    }


}
