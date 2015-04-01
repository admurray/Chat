package model;

public class User
{
	private String sessionId;
	private String name;
	private String conversationId;
	private String type;
	
	public User(String sessionId, String name, String conversationId, String type)
	{
		this.sessionId = sessionId;
		this.name = name;
		this.conversationId = conversationId;
		this.type = type;
	}
	
	public User()
	{
		this(null, null, null, null);
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getConversationId()
	{
		return conversationId;
	}

	public void setConversationId(String conversationId)
	{
		this.conversationId = conversationId;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}
