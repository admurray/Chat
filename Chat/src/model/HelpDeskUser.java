package model;

import java.util.HashMap;
import java.util.Iterator;

public class HelpDeskUser extends User
{
	private String username;
	private HashMap<String, AnonymousUser> anons;
	
	public HelpDeskUser(String sessionId, String name, String conversationId, 
			String type, String username, 
			HashMap<String, AnonymousUser> anons)
	{
		super(sessionId, name, conversationId, type);
		this.username = username;
		this.anons = anons;
	}
	
	public HelpDeskUser()
	{
		this(null, null, null, null, null,
				new HashMap<String, AnonymousUser>());
	}
	
	public HelpDeskUser(String sessionId, String name)
	{
		this(sessionId, name, null, null, null,
				new HashMap<String, AnonymousUser>());
	}

	public HashMap<String, AnonymousUser> getAnons()
	{
		return anons;
	}

	public void setAnons(HashMap<String, AnonymousUser> anons)
	{
		this.anons = anons;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}	
	
	public boolean addUserToHelp(AnonymousUser anon)
	{
		try
		{
			this.getAnons().put(anon.getSessionId(), anon);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public int getNumOfUsers()
	{
		return this.getAnons().size();
	}

	public void removeClient(AnonymousUser anon)
	{
		this.getAnons().remove(anon.getSessionId());	
	}
	
	public String toString()
	{
		String str = "\n======================================================\n";
		str += "NAME : "+this.getName()+" | SESSION ID : "+this.getSessionId();
		str += "\nUSERS BEING HELPED";
		Iterator<AnonymousUser> iter = this.getAnons().values().iterator();
		while(iter.hasNext())
		{
			str += "\n\t"+iter.next().toString();
		}
		str += "\n==========================================================\n";
		return str;
	}
}