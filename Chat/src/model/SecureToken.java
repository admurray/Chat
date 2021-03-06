package model;

import java.util.UUID;

public class SecureToken
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
	
	public long getLatestTimestamp()
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
		return this.token;
	}

	public SecureToken setToken(String token)
	{
		this.token = token;
		return this;
	}
	
	public SecureToken setToken()
	{
		return this.setToken(SecurityUtils.hashSHA(UUID.randomUUID().toString()));
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
		long difference =  minsElapsed - this.getLatestTimestamp();
		if(difference > SecureToken.secureTokenTimeout)
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
	
	public String toString()
	{
		return "Timestamp : "+this.getLatestTimestamp()+"	Value : "+this.getToken();
	}
}
