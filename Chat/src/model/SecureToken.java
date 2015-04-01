package model;

import java.util.UUID;

import org.apache.tomcat.jni.Time;

public class SecureToken implements Runnable
{
	private static final long secureTokenTimeout = 5; //in minutes
	
	private long latestTimestamp;
	private String token;
	
	public SecureToken(long creationTimeStamp, String token)
	{
		this.latestTimestamp = creationTimeStamp;
		this.token = token;	
	}

	public SecureToken()
	{
		this.setToken();
		this.setLatestTimestamp();
	}
	
	public long getCreationTimeStamp()
	{
		return latestTimestamp;
	}

	private void setLatestTimestamp(long latestTimestamp)
	{
		this.latestTimestamp = latestTimestamp;
	}
	
	public void setLatestTimestamp()
	{
		this.latestTimestamp =  System.currentTimeMillis();
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}
	
	public void setToken()
	{
		this.setToken(SecurityUtils.hashSHA(UUID.randomUUID().toString()));
	}

	public static long getSecuretokentimeout()
	{
		return secureTokenTimeout;
	}
	
	public boolean isValid()
	{
		/*
		 * The token is valid iff the difference between the creation time and
		 * the curret time is less than 5 minutes.
		 */
		
		long minsElapsed = System.currentTimeMillis()/(1000 * 60);
		long difference =  minsElapsed - this.getCreationTimeStamp();
		if(difference > 5)
			return false;
		return true;
	}
	
	/*
	 * this checks to see if the token is valid. If it is not valid, in that
	 * case this will generate a new id.
	 */
	public SecureToken validate()
	{
		String secUUID;
		if(!this.isValid())
		{
			secUUID = UUID.randomUUID().toString();
			secUUID = SecurityUtils.hashSHA(secUUID);
			this.setToken(secUUID);
			this.setLatestTimestamp(System.currentTimeMillis());
		}
		return this;
	}
	

	public void run()
	{
		try
		{
			this.checkToken();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void checkToken() throws InterruptedException
	{
		while(true)
		{
			Thread.sleep((5*60*1000)+100);
			this.setToken();
			
		}
	}
}
