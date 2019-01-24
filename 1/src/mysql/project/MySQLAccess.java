package mysql.project;

import java.sql.*;
import java.util.Scanner;

// Find name by by id an delete theatre id from show movietimes
// search
// edit

public class MySQLAccess {

    private ResultSet resultSet = null;

    // create a scanner so we can read the command-line input
    Scanner scanner = new Scanner(System.in);
    String movie_name = null;
    String wanna_go = null;
    Integer movie_id = null;
    Integer movie_length = null;
    Integer theatre_id = null;
    Integer movie_showtime_id = null;
    Boolean no_such_id = false;
    Boolean no_audi_id = false;
    String phone_number = null;
    String start_time = null;
    String end_time = null;
    Integer seats = null;
    Integer auditorium_id = null;


    public void ifNoAuditorium(Connection connect) throws Exception {
        seats = scanner.nextInt();
        System.out.println(seats);

        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO auditoriums (SEATS_AVAILABLE, THEATRE_ID) VALUES (?,?);",
                Statement.RETURN_GENERATED_KEYS )) {
            stmt.setInt(1, seats);
            stmt.setInt(2, theatre_id);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                auditorium_id = (int)rs.getLong(1);
            }
        }
    }

    public void ifNoTheatre(Connection connect) throws Exception {

        System.out.println("No theatre with this id,add a new one, by just adding its number: ");
        phone_number = scanner.nextLine();
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO theatres (PHONE_NUMBER) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS )) {
            phone_number = scanner.nextLine();
            stmt.setString(1, phone_number);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                theatre_id = (int)rs.getLong(1);
            }
        }
        System.out.println("Thank you. Now we need an auditorium in your theatre. Enter seat count: ");
        seats = scanner.nextInt();
        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO auditoriums (THEATRE_ID, SEATS_AVAILABLE) VALUES (?,?);",
                Statement.RETURN_GENERATED_KEYS )) {
            stmt.setInt(1, theatre_id); //Figure it out
            stmt.setInt(2, seats);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                auditorium_id = (int)rs.getLong(1);
            }
        }
        System.out.println("Thank you. Now we need a showtime for your movie. Enter start and End times: ");
        start_time = scanner.nextLine();
        start_time = scanner.nextLine();
        System.out.print(start_time);
        end_time = scanner.nextLine();
        System.out.print(end_time);

        try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO movie_showtimes (START_TIME, END_TIME, MOVIE_ID, AUDITORIUM_ID) VALUES (?,?,?,?);")) {
            stmt.setString(1, start_time);
            stmt.setString(2, end_time);
            stmt.setInt(3, movie_id); // Figure it out
            stmt.setInt(4, auditorium_id); // Figure it out
            stmt.executeUpdate();
        }
    }

    public void ifThereIsTheatre(Connection connect) throws Exception {

        System.out.println("Choose its auditorium id from the list");
        try (PreparedStatement stmt = connect.prepareStatement("SELECT ID FROM auditoriums WHERE THEATRE_ID = ?")) {
            stmt.setInt(1, theatre_id);
            resultSet = stmt.executeQuery();
            if (resultSet.isBeforeFirst() ) {
                writeResultSetAuditoriumsIDS(resultSet);
            } else {
                no_audi_id = true;
            }
        }
        if(no_audi_id) {
            System.out.println("No auditoriums for this theatre, you'll add the first, by inputin seat count: ");
            ifNoAuditorium(connect);
        } else {
            auditorium_id = scanner.nextInt();
            try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM auditoriums WHERE ID = ?")) {
                stmt.setInt(1, auditorium_id);
                resultSet = stmt.executeQuery();
                if (!resultSet.isBeforeFirst()) {
                    System.out.println("Wrong auditoriums for this theatre, you'll add another one: ");
                    ifNoAuditorium(connect);
                }
            }
        }

        try (PreparedStatement stmt = connect.prepareStatement("SELECT * " +
                "FROM movie_showtimes " +
                "WHERE AUDITORIUM_ID = ?")) {
            stmt.setInt(1, auditorium_id);
            resultSet = stmt.executeQuery();
            writeResultSetShowtimesIDsAndStartTimes(resultSet);
        }
        System.out.println("Choose a showtime id from the list");
        movie_showtime_id = scanner.nextInt();
        try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM movie_showtimes WHERE ID = ?")) {
            stmt.setInt(1, movie_showtime_id);
            resultSet = stmt.executeQuery();
            if (resultSet.isBeforeFirst() ) {
                writeResultSetMovieShowtimes(resultSet);
            } else {
                System.out.print("NO SUCH MOVIE SHOWTIME");
            }
        }
    }

    public void readUserInput(Connection connect) throws Exception {


        do {
            System.out.println("Add a movie to our project :)  ");
            System.out.print("Tell us its name: ");
            movie_name = scanner.nextLine();

            System.out.print("aaand its length: ");
            movie_length = scanner.nextInt();

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO movies (NAME, LENGTH) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS )) {
                stmt.setString(1, movie_name);
                stmt.setInt(2, movie_length);
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    movie_id = (int)rs.getLong(1);
                }
            }

            try (PreparedStatement stmt = connect.prepareStatement("SELECT ID FROM theatres")) {
                resultSet = stmt.executeQuery();
                writeResultSetTheatresIDS(resultSet);
            }
            System.out.println("Choose its theatre id from the list");
            theatre_id = scanner.nextInt();
            try (PreparedStatement stmt = connect.prepareStatement("SELECT * FROM theatres WHERE ID = ?")) {
                stmt.setInt(1, theatre_id);
                resultSet = stmt.executeQuery();
                if (resultSet.isBeforeFirst() ) {
                    writeResultSetTheatres(resultSet);
                } else {
                    no_such_id = true;
                }
            }

            if (no_such_id)
                ifNoTheatre(connect);
            else
                ifThereIsTheatre(connect);

            System.out.println("Wanna go ? Y/N :)");
            wanna_go = scanner.nextLine();
            if(wanna_go.equals("Y"))
                break;

        } while (true);

    }

    public void readDataBase() throws Exception {

        // This will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.jdbc.Driver");

        // Setup the connection with the DB
        try (Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/project?verifyServerCertificate=false&useSSL=true&user=sqluser&password=sqluserpw&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC")){

            readUserInput(connect);

            // stmt is auto closed here, even if SQLException is thrown

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO theatres (PHONE_NUMBER) VALUES (?)")) {
                 stmt.setString(1, "08773623333");
                 stmt.executeUpdate();
            }
            // stmt is auto closed here, even if SQLException is thrown

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO auditoriums (THEATRE_ID, SEATS_AVAILABLE) VALUES (?,?);")) {
                 stmt.setInt(1, 1);
                 stmt.setInt(2, 10);
                stmt.executeUpdate();
            }
            // stmt is auto closed here, even if SQLException is thrown

            try (PreparedStatement stmt = connect.prepareStatement("INSERT INTO " +
                    "movie_showtimes (START_TIME, END_TIME, MOVIE_ID, AUDITORIUM_ID) " +
                    "VALUES (?,?,?,?);")) {
                 stmt.setString(1, "17:55");
                 stmt.setString(2, "18:55");
                 stmt.setInt(3, 1);
                 stmt.setInt(4, 1);
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
        System.out.println("");
        System.out.println("---------------------------------------");
        System.out.println("");
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
            System.out.print("start_time: " + start_time);
            System.out.print(" end_time: " + end_time);
            System.out.print(" movie_id: " + movie_id);
            System.out.println(" auditorium_id: " + auditorium_id);

        }
        System.out.println("");
        System.out.println("---------------------------------------");
        System.out.println("");
    }

    private void writeResultSetShowtimesIDsAndStartTimes(ResultSet resultSet) throws SQLException {
        System.out.println("");
        System.out.println("-----------------SHOWTIMES-----------------");
        System.out.println("");
        while (resultSet.next()) {
            String start_time = resultSet.getString("START_TIME");
            String id = resultSet.getString("ID");
            System.out.println("Showtime Number: " + id);
            System.out.print("Start Time: " + start_time);

        }
        System.out.println("");
        System.out.println("---------------------------------------");
        System.out.println("");
    }

    private void writeResultSetAuditoriums(ResultSet resultSet) throws SQLException {
        System.out.println("");
        System.out.println("-----------------AUDITORIUMS-----------------");
        System.out.println("");
        while (resultSet.next()) {
            Integer theatre_id = resultSet.getInt("THEATRE_ID");
            Integer seats = resultSet.getInt("SEATS_AVAILABLE");
            System.out.println("theatre_id: " + theatre_id);
            System.out.println("seats: " + seats);
        }
        System.out.println("");
        System.out.println("---------------------------------------");
        System.out.println("");
    }

    private void writeResultSetAuditoriumsIDS(ResultSet resultSet) throws SQLException {
        System.out.println("");
        System.out.println("-----------------AUDITORIUMS-----------------");
        System.out.println("");
        while (resultSet.next()) {
            String id = resultSet.getString("ID");
            System.out.println("Auditorium Number: " + id);
        }
        System.out.println("");
        System.out.println("---------------------------------------");
        System.out.println("");
    }

    private void writeResultSetTheatres(ResultSet resultSet) throws SQLException {
        System.out.println("");
        System.out.println("-----------------THEATRES-----------------");
        System.out.println("");
        while (resultSet.next()) {
            String phone_number = resultSet.getString("PHONE_NUMBER");
            System.out.println("Phone Number: " + phone_number);
        }
        System.out.println("");
        System.out.println("---------------------------------------");
        System.out.println("");
    }

    private void writeResultSetTheatresIDS(ResultSet resultSet) throws SQLException {
        System.out.println("");
        System.out.println("-----------------THEATRES-----------------");
        System.out.println("");
        while (resultSet.next()) {
            String id = resultSet.getString("ID");
            System.out.println("Theatre Number: " + id);
        }
        System.out.println("");
        System.out.println("---------------------------------------");
        System.out.println("");
    }


}
