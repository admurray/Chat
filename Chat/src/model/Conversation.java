package model;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.util.NoSuchElementException;

public class Conversation
{
	private String			id;
	private User			user1;
	private User			user2;
	private StringBuffer	convo;
	private String			responseId; // Will be set to the if of the person
										// who needs a response.

	public Conversation()
	{
		this(null, null, null, new StringBuffer(), null);
	}

	public Conversation(String convoId, User user1, User user2)
	{
		this(convoId, user1, user2, new StringBuffer(), null);
	}

	public Conversation(String id, User user1, User user2, StringBuffer convo,
			String responseId)
	{
		this.id = id;
		this.user1 = user1;
		this.user2 = user2;
		this.convo = convo;
		this.responseId = null;
	}

	public User getUser1()
	{
		return user1;
	}

	public void setUser1(User user1)
	{
		this.user1 = user1;
	}

	public User getUser2()
	{
		return user2;
	}

	public void setUser2(User user2)
	{
		this.user2 = user2;
	}

	public StringBuffer getConvo()
	{
		return convo;
	}

	public void setConvo(StringBuffer convo)
	{
		this.convo = convo;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getResponseId()
	{
		return responseId;
	}

	public void setResponseId(String responseId)
	{
		this.responseId = responseId;
	}

	public User getUsrById(String id)
	{
		if (this.getUser1().getSessionId().equals(id))
			return this.getUser1();
		else if (this.getUser2().getSessionId().equals(id))
			return this.getUser2();
		else
			return null;
	}

	public String sendMessage(String message)
	{
		//message = message.trim();
		this.getConvo().append(message + "<br/>");
		return "" + this.getConvo();
	}

	// Gets the id of the other participant.
	public User getParticipant(String id)
	{
		// first make sure that you do have the right conversation
		// So the given id is a part of the convo;
		if (this.user1.getSessionId() == id)
		{
			return this.user2;
		} else if (this.user2.getSessionId() == id)
		{
			return this.user1;
		} else
		{
			throw new NoSuchElementException("User id not a part of the convo.");
		}
	}

	public String toString()
	{
		String str = this.id + " : " + this.user1.toString() + " - "
				+ this.user2.toString() + "\n";
		str = str + " CONVO : " + this.convo;
		return str;
	}

	public static String createConvoId(String id1, String id2)
	{
		String convoId = null;
		
		if (id1 != null && id2 != null)
		{
			int comp = id1.compareTo(id2);
			if (comp < 0)
			{
				convoId = id1 + id2;
				convoId = Conversation.md5Hash(convoId);
				return convoId;
			} else if (comp > 0)
			{
				convoId = id2 + id1;
				convoId = Conversation.md5Hash(convoId);
				return convoId;
			} else
			{	
				return null;
			}
		} else
			return null;

	}

	private static String md5Hash(String str)
	{
		String hash = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] byteData = md.digest();
			BigInteger number = new BigInteger(1, byteData);
			hash = number.toString(16);
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out
					.println("Failed to create the md5 hash.... No Convo possible");
			return hash;
		}
		return hash;
	}
}
