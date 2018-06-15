package mysql.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

// Find name by by id an delete theatre id from show movitimes
// search
// edit

public class MySQLAccess {

    private ResultSet resultSet = null;

    public void readDataBase() throws Exception {


        // create a scanner so we can read the command-line input
        Scanner scanner = new Scanner(System.in);

        System.out.println("Add a movie to our project :)  ");
        System.out.print("Tell us its name: ");
        String movie_name = scanner.nextLine();

        System.out.print("aaand its length: ");
        Integer movie_length = scanner.nextInt();

        System.out.println("Thank you! Have a nice day :)");


//
        // This will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.jdbc.Driver");

        // Setup the connection with the DB
        try (Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?verifyServerCertificate=false&useSSL=true&user=sqluser&password=sqluserpw&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC")){


            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO movies (NAME, LENGTH) VALUES (?, ?)")) {
                stmt.setString(1, movie_name);
                stmt.setInt(2, movie_length);
                stmt.executeUpdate();
            }

            // stmt is auto closed here, even if SQLException is thrown

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO theatres (PHONE_NUMBER) VALUES (?)")) {
                 stmt.setString(1, "08773623333");
                 stmt.executeUpdate();
            }
            // stmt is auto closed here, even if SQLException is thrown

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO auditoriums (THEATRE_ID) VALUES (?);")) {
                 stmt.setInt(1, 1);
                stmt.executeUpdate();
            }
            // stmt is auto closed here, even if SQLException is thrown

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO " +
                    "movie_showtimes (START_TIME, END_TIME, MOVIE_ID, AUDITORIUM_ID, THEATRE_ID) " +
                    "VALUES (?,?,?,?,?);")) {
                 stmt.setString(1, "17:55");
                 stmt.setString(2, "18:55");
                 stmt.setInt(3, 1);
                 stmt.setInt(4, 1);
                 stmt.setInt(5, 1);
                 stmt.executeUpdate();
            }

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO movies (NAME, LENGTH) VALUES (?, ?);")) {
                    stmt.setString(1, "Iron Man");
                    stmt.setInt(2, 140);
                    stmt.executeUpdate();
                }

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO movies (NAME, LENGTH) VALUES (?, ?);")) {
                    stmt.setString(1, "Fight Club");
                    stmt.setInt(2, 120);
                    stmt.executeUpdate();
                }


            try (PreparedStatement stmt = connect.prepareStatement("UPDATE movies SET NAME = ?, LENGTH = ? WHERE NAME = ?")) {
                stmt.setString(1, "Adventures");
                stmt.setInt(2, 160);
                stmt.setString(3, "Iron Man");
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connect.prepareStatement("UPDATE movies SET NAME = ? WHERE NAME = ?")) {
                stmt.setString(1, "Fight Club 2");
                stmt.setString(2, "Fight Club");
                stmt.executeUpdate();
                resultSet = stmt
                        .executeQuery("SELECT * FROM movies");
                writeResultSetMovies(resultSet);
                resultSet = stmt
                        .executeQuery("SELECT * FROM movie_showtimes");
                writeResultSetMovieShowtimes(resultSet);
                resultSet = stmt
                        .executeQuery("SELECT * FROM auditoriums");
                writeResultSetAuditoriums(resultSet);
                resultSet = stmt
                        .executeQuery("SELECT * FROM theatres");
                writeResultSetTheatres(resultSet);

            }
        }

    }

    private void writeResultSetMovies(ResultSet resultSet) throws SQLException {
        System.out.println("");
        System.out.println("-----------------MOVIES-----------------");
        System.out.println("");
        while (resultSet.next()) {
            String name = resultSet.getString("NAME");
            Integer length = resultSet.getInt("LENGTH");
            System.out.print("Name: " + name);
            System.out.println(" Length: " + length);
        }
    }

    private void writeResultSetMovieShowtimes(ResultSet resultSet) throws SQLException {
        System.out.println("");
        System.out.println("-----------------SHOWTIMES-----------------");
        System.out.println("");
        while (resultSet.next()) {
            String start_time = resultSet.getString("START_TIME");
            String end_time = resultSet.getString("END_TIME");
            Integer movie_id = resultSet.getInt("MOVIE_ID");
            Integer auditorium_id = resultSet.getInt("AUDITORIUM_ID");
            Integer theatre_id = resultSet.getInt("THEATRE_ID");
            System.out.print("start_time: " + start_time);
            System.out.print(" end_time: " + end_time);
            System.out.print(" movie_id: " + movie_id);
            System.out.print(" auditorium_id: " + auditorium_id);
            System.out.println(" theatre_id: " + theatre_id);

        }
    }

    private void writeResultSetAuditoriums(ResultSet resultSet) throws SQLException {
        System.out.println("");
        System.out.println("-----------------AUDITORIUMS-----------------");
        System.out.println("");
        while (resultSet.next()) {
            Integer theatre_id = resultSet.getInt("THEATRE_ID");
            System.out.println("theatre_id: " + theatre_id);
        }
    }

    private void writeResultSetTheatres(ResultSet resultSet) throws SQLException {
        System.out.println("");
        System.out.println("-----------------THEATRES-----------------");
        System.out.println("");
        while (resultSet.next()) {
            String phone_number = resultSet.getString("PHONE_NUMBER");
            System.out.println("Number: " + phone_number);
        }
    }


}
