import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;
	ResultSet results = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE kdomin_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), start_date DATE, end_date DATE, ticket_status VARCHAR(6))";
		final String createUsersTable = "CREATE TABLE kdomin_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)
		HashMap<Integer, String> ExistingUserList = new HashMap<>(); // hashmap for existing users

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}
		
		try {
		
			sql = "SELECT uname FROM kdomin_users";
			PreparedStatement chkUsers = getConnection().prepareStatement(sql);
			ResultSet rs = chkUsers.executeQuery();
			
			int j =0;
			while(rs.next()) {
				String userName = rs.getString(1);
				if(!ExistingUserList.containsValue(rs.getString(1))) { // not exists, add to hashmap
					ExistingUserList.put(j, rs.getString(1));
					j++;
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}

		try {
			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {
				// add user if it doesnt exists inside hashmap
				if(!(ExistingUserList.containsValue(rowData.get(0)))){
					System.out.println("New user found, adding to database..");
					
					PreparedStatement addUser = getConnection().prepareStatement("insert into kdomin_users(uname,upass,admin) " + "values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
					
					addUser.setString(1, rowData.get(0));
					addUser.setString(2, rowData.get(1));
					addUser.setInt(3, Integer.parseInt(rowData.get(2)));
					addUser.executeUpdate();

					
				}
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int insertRecords( String ticketName, String ticketDesc) {
		String sql = "INSERT INTO kdomin_tickets (ticket_issuer, ticket_description, start_date, ticket_status) VALUES(?, ?,CURDATE(),'Open')";
		
		try {
			
			PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			//LocalDate getDate = LocalDate.now();
			//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyy-MM-dd");
			stmt.setString(1, ticketName);
			stmt.setString(2, ticketDesc);
			//stmt.setString(3, getDate.format(dtf));
			stmt.executeUpdate();
			
			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = stmt.getGeneratedKeys();
			//resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				return resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}


	public ResultSet readRecords() {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM kdomin_tickets")	;			

			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
	// continue coding for updateRecords implementation
	public int updateRecords(int ticketNum, String newDesc, String ticketStatus) {
		int row = 0;
		String sql = "UPDATE kdomin_tickets SET ticket_description = ?, ticket_status = ? WHERE ticket_id =?";

		try {
			PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, newDesc);
			stmt.setString(2, ticketStatus);
			stmt.setInt(3, ticketNum);
			
			row = stmt.executeUpdate();
		
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return ticketNum;
	}

	
	
	// continue coding for deleteRecords implementation
	public int deleteRecords(int ticketNum) {
		// only admins can access
		try {
			PreparedStatement delete = getConnection().prepareStatement("DELETE FROM kdomin_tickets WHERE ticket_id = ?", Statement.RETURN_GENERATED_KEYS);
			delete.setInt(1, ticketNum);
			delete.executeUpdate();
		}catch(SQLException e) {
			System.err.println("Error deleting ticket: " + e.getMessage());
		}
		return ticketNum;
		
	}

	
	
}
