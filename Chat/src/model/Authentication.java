package model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import dao.HelpDeskUserDao;

public class Authentication
{
	private HelpDeskUserDao dao;
	public Authentication()
	{
		try
		{
			this.dao = new HelpDeskUserDao();
		} 
		catch (NamingException e)
		{
			System.out.println("Problem creating DAO instance.");
			e.printStackTrace();
		} 
		catch (SQLException e)
		{
			System.out.println("Problem creating the DAO instance.");
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			System.out.println("Problem creating DAO instance.");
			e.printStackTrace();
		}
	}
	
	public HelpDeskUser checkCredentials(String username, String password)
	{
		//Get the result set using the dao. All we need to get is the result
		//set for the database and find the record for the specified username.
		//This should also have just one element.
		try
		{
			HelpDeskUser helper = new HelpDeskUser();
			ResultSet result = this.dao.getRecordByUsername(username);
			String name = result.getString("name");
			System.out.println("Unhashed Password : "+password);
			password = Authentication.hashPassword(password);
			System.out.println("Hashed Password : "+password);
			String pass = result.getString("password");
			if(pass.equals(password))
			{
				System.out.println("LOGIN was successfull, adding helper.");
				helper.setName(name);
				helper.setType(HelpDeskSession.hlprStr);
				helper.setUsername(username);
				return helper;
			}
			else
			{
				return null;
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}	
	}
	
	public static String hashPassword(String str)
	{
		String hash = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
		    md.update(str.getBytes("UTF-8"));
		    byte[] byteData = md.digest();
		    BigInteger number = new BigInteger(1, byteData);
	        hash = number.toString(16);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Failed to create the md5...Login must fail.");
			return hash;
		}
	    return hash;
	}

}
