package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;


public class HelpDeskUserDao
{
	private static Connection conn = null;
	private DataSource ds;
	
	public HelpDeskUserDao() throws NamingException, SQLException, ClassNotFoundException {
		 this.ds = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/EECS4481");	 
	}
	
	//Get a connection from the pool
	private void createConnection() throws SQLException
	{
		
		 System.out.println("DEBUG : Getting the connections ------------------");
		 //BasicDataSource debug_bds = (BasicDataSource) this.ds;
		 //System.out.println("DEBUG: "+debug_bds.getUrl()+"");
		 System.out.println("DEBUG : "+ds.toString());
		 conn = this.ds.getConnection();
		 System.out.println(conn.getMetaData().getURL());
		 System.out.println("===================================================");
		 System.out.println("");
		 System.out.println(conn.toString());
		 System.out.println(conn.getCatalog());
		 System.out.println(conn.getAutoCommit());
		 System.out.println(conn.getMetaData().allTablesAreSelectable());
		 System.out.println(conn.getMetaData().getDriverName());
		 System.out.println(conn.getMetaData().getMaxConnections());
		 System.out.println(conn.getMetaData().supportsStoredFunctionsUsingCallSyntax());
		 System.out.println(conn.getMetaData().supportsStoredProcedures());
		 System.out.println(conn.getMetaData().allProceduresAreCallable());
		 
		 conn.setAutoCommit(true);
	}
	
	//Return connection to the pool
	private void closeConnection() throws SQLException
	{
    	conn.close();
	}
	
	public void addHelpDeskUser(String name, String username, String password) throws IOException
	{
		PreparedStatement command = null;
		String insertCommand = "INSERT INTO main.HELPDESKUSERS (ID, NAME, USERNAME, PASSWORD) VALUES ";
		//insertCommand += "(' NEWID() ', '"+name+"', '"+username+"', '"+password+"')";
		/*
		 * One thing that needs to be done is the ID needs to be generated
		 * using something like the UID. 
		 */
		UUID dbUUID;
		try
		{
			createConnection();
			//HelpDeskUserDao dao =  new HelpDeskUserDao();
			BufferedReader br = new BufferedReader(new FileReader("/home/adityam/workspace/Chat/users.txt"));
			String line;
			String newCom;
			while((line =  br.readLine()) != null)
			{
				dbUUID = UUID.randomUUID();
				String id = dbUUID.toString();
				newCom = insertCommand;
				name = line.substring(0, line.indexOf(':')).trim();
				line = line.substring(line.indexOf(':')+1);
				username = line.substring(0, line.indexOf(':')).trim();
				line = line.substring(line.indexOf(':')+1);
				password = line.trim();
				newCom+= "('"+id+"', '"+name+"', '"+username+"', '"+password+"')";
				command = HelpDeskUserDao.conn.prepareStatement(newCom);
				command.execute();
			}
			closeConnection();
			br.close();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("Problem in adding user to DB");
		}
	}
	
	//The id here is not the same as the session id.
	public ResultSet getRecordById(String id)
	{
		PreparedStatement command = null;
		String getUserCommand = "SELECT * FROM HELPDESKUSERS WHERE ID='"+id+"'";
		
		try
		{
			command = HelpDeskUserDao.conn.prepareStatement(getUserCommand);
			createConnection();
			ResultSet result = command.executeQuery();
			closeConnection();
			
			if(!result.isLast())
			{
				System.out.println("The result mustn't have more than one element.");
				throw new SQLException("Result set returned more than one element");
			}
			return result;
		}
		catch(SQLException e)
		{
			System.out.println("Problem in getting data from DB : getUserRecord");
			return null;
		}
	} 
	
	public ResultSet getRecordByUsername(String username) throws IOException
	{
		PreparedStatement command = null;
		String getUserCommand = "SELECT * FROM HELPDESKUSERS WHERE USERNAME='"+username+"'";
		
		try
		{
			System.out.println("DEBUG : Attempting to create connection......-----------");
			createConnection();
			command = HelpDeskUserDao.conn.prepareStatement(getUserCommand);
			ResultSet result = command.executeQuery();
			closeConnection();
			return result;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			System.out.println("Problem in getting data from DB : getUserRecord");
			return null;
		}
	}
}
