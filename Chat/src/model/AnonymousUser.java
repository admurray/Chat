package model; 

public class AnonymousUser extends User
{
	private HelpDeskUser helper;
	
	public AnonymousUser(String sessionId, String name, String conversationId,
			String type, HelpDeskUser helper)
	{
		super(sessionId, name, conversationId, type);
		this.helper = helper;
	}

	public HelpDeskUser getHelper()
	{
		return helper;
	}

	public void setHelper(HelpDeskUser helper)
	{
		this.helper = helper;
	}
	
	public String toString()
	{
		String str = "NAME: "+this.getName()+" | ID: "+this.getSessionId();
		if(this.getHelper() != null)
			str = str+" HELPER: "+this.getHelper().getName();
		return str;
	}
}