package model;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class SecurityUtils
{
	private PolicyFactory	policy;

	public SecurityUtils()
	{
		policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
	}

	public String sanitize(String message)
	{
		return policy.sanitize(message);
	}
	/*
	 * PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
	 * String safeHTML = policy.sanitize(untrustedHTML);
	 * 
	 * or the tests show how to configure your own policy:
	 * 
	 * PolicyFactory policy = new HtmlPolicyBuilder() .allowElements("a")
	 * .allowUrlProtocols("https") .allowAttributes("href").onElements("a")
	 * .requireRelNofollowOnLinks() .build(); String safeHTML =
	 * policy.sanitize(untrustedHTML);
	 * 
	 * or you can write custom policies to do things like changing h1s to divs
	 * with a certain class:
	 * 
	 * PolicyFactory policy = new HtmlPolicyBuilder() .allowElements("p")
	 * .allowElements( new ElementPolicy() { public String apply(String
	 * elementName, List<String> attrs) { attrs.add("class");
	 * attrs.add("header-" + elementName); return "div"; } }, "h1", "h2", "h3",
	 * "h4", "h5", "h6")) .build(); String safeHTML =
	 * policy.sanitize(untrustedHTML);
	 */
	public static String hashSHA(String input)
	{
		String output;
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
		    md.update(input.getBytes("UTF-8"));
		    byte[] byteData = md.digest();
		    BigInteger number = new BigInteger(1, byteData);
	        output = number.toString(16);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return output;
	}
}
