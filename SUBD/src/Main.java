
import java.sql.*;

public class Main {
	public void main(String[] args)
	{
		try{
			//System.out.println("first");
			//Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cinema?user=root&password=root");
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from Players");  
			
	        
	      //  String sql_stmt = "INSERT INTO movies (name,year) VALUES ('" + name + "','" + email + "','" + contact_number + "')";
	        
	        PreparedStatement query = con.prepareStatement( "INSERT INTO Players(Name, Number, PositionCode) VALUES (bibi,2000,11)");
	        query.setNull( 1, Types.INTEGER );
	        query.setString( 2, "ООП" );
	        int affectedRows = query.executeUpdate();



		}
		catch(Exception exc){
			System.out.println("dkskxdk");
		}
		

	}
}
