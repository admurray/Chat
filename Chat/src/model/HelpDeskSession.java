package model;

import java.util.HashMap;
import java.util.Iterator;

import ctrl.Chat;
import nl.captcha.Captcha;
import dao.HelpDeskUserDao;

/*
 * The session class will keep a list of all the active users,
 * This would also have threads to keep track of the taking individuals
 */
public class HelpDeskSession
{
	private HashMap<String, HelpDeskUser>	helpDeskUsers;
	private HashMap<String, AnonymousUser>	anonUsers;
	private HashMap<String, Conversation>	conversations;

	public HelpDeskSession(HashMap<String, HelpDeskUser> helpDeskUser,
			HashMap<String, AnonymousUser> anonUsers,
			HashMap<String, Conversation> conversations,
			HelpDeskUserDao helpDeskDao)
	{
		this.helpDeskUsers = helpDeskUser;
		this.anonUsers = anonUsers;
		this.conversations = conversations;
	}

	public HelpDeskSession()
	{
		this.helpDeskUsers = new HashMap<String, HelpDeskUser>();
		this.anonUsers = new HashMap<String, AnonymousUser>();
		this.conversations = new HashMap<String, Conversation>();
	}

	public HashMap<String, HelpDeskUser> getHelpDeskUsers()
	{
		return helpDeskUsers;
	}

	public void setHelpDeskUsers(HashMap<String, HelpDeskUser> helpDeskUsers)
	{
		this.helpDeskUsers = helpDeskUsers;
	}

	public HashMap<String, AnonymousUser> getAnonUsers()
	{
		return anonUsers;
	}

	public void setAnonUsers(HashMap<String, AnonymousUser> anonUsers)
	{
		this.anonUsers = anonUsers;
	}

	public HashMap<String, Conversation> getConversations()
	{
		return conversations;
	}

	public void setConversations(HashMap<String, Conversation> conversations)
	{
		this.conversations = conversations;
	}

	public boolean addAnonymousUser(String sessionId, String anonName)
	{
		// Check the name for special characters.
		AnonymousUser anon = new AnonymousUser(sessionId, anonName, null,
				Chat.anonymousUser, null);
		this.assignHelper(anon);
		this.getAnonUsers().put(anon.getSessionId(), anon);
		return true;
	}

	// Returns id of the user added to the session.
	public boolean addHelpDeskUser(String username, String password)
	{
		return false;
	}

	public boolean assignHelper(AnonymousUser anonUser)
	{
		try
		{
			HelpDeskUser leastBusy = this.getLeastBusy();
			if (leastBusy != null)
			{
				anonUser.setHelper(leastBusy);
				leastBusy.addUserToHelp(anonUser);
				return true;
			}
			else
			{
				System.out
						.println("No Helper present, please try again later.");
				System.out.println("Or wait until a helper is online. ");
				return false;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;

		}
	}

	private HelpDeskUser getLeastBusy()
	{
		HelpDeskUser leastBusy = null;
		HelpDeskUser temp_user = null;
		Iterator<HelpDeskUser> iter;
		iter = this.helpDeskUsers.values().iterator();
		int smallest = Integer.MAX_VALUE; // Set to the MAX possible.
		while (iter.hasNext())
		{
			temp_user = iter.next();
			if (temp_user.getNumOfUsers() < smallest)
			{
				smallest = temp_user.getNumOfUsers();
				leastBusy = temp_user;
			}
		}
		return leastBusy;
	}

	// Should be run when a new helper comes online, this basically checks each
	// Anonymous User for missing helper,and the helper is assigned, this method
	// is only significant when there are many anonymous users and no helper.
	// Bulk helper assignment will never take place. If one helper is present.
	// then everyone is assigned to him. If another helper comes online, he is
	// not assigned any new user until new ones come online.
	public boolean assignHelpers()
	{

		AnonymousUser noHelper = null;
		Iterator<AnonymousUser> iter = this.anonUsers.values().iterator();
		// This should run only when there is one anonymous user
		// waiting, at other times, it should automatically assign the
		// user when the user is created.
		// If there are no users, no need to do anything.
		if (this.anonUsers.size() > 0)
		{
			/*
			 * iterate over the list of anonymous users, and check if the user
			 * has a helper, if not assign the least busy helper to this user.
			 * However I think the least busy part is redundant as this would be
			 * required only when the first user comes in. For any other
			 * Anonymous User we should set the helper when the user logs in, if
			 * a helper is present, and if more than one user is present in that
			 * case all we need is the leastBusy User.
			 */

			while (iter.hasNext())
			{
				noHelper = iter.next();
				if (noHelper.getHelper() == null)
				{
					System.out.println("Assigning helpers");
					this.assignHelper(noHelper);
				}
				else
				{
					// No user waiting.
					return true;
				}
			}
			return true;
		}
		else
			return false;
	}

	public void printHelpers()
	{
		Iterator<HelpDeskUser> iter = this.helpDeskUsers.values().iterator();
		while (iter.hasNext())
		{
			System.out.print(iter.next().toString() + " |");
		}
		System.out.println();
	}

	public void printUsers()
	{
		Iterator<AnonymousUser> iter = this.anonUsers.values().iterator();
		while (iter.hasNext())
		{
			System.out.println(iter.next().toString() + " |");
		}
		System.out.println();
	}

	public AnonymousUser getUserById(String userId)
	{
		return this.anonUsers.get(userId);
	}

	public HelpDeskUser getHelperById(String currentHelperId)
	{
		return this.helpDeskUsers.get(currentHelperId);
	}

	public boolean transferUserToHelper(String currentHelperId, String userId,
			String helperId)
	{
		AnonymousUser anon = this.getUserById(userId);
		HelpDeskUser hduser = this.getHelperById(helperId);
		HelpDeskUser currentHelper = this.getHelperById(currentHelperId);
		System.out.println(anon.toString());
		anon.setHelper(hduser);
		hduser.addUserToHelp(anon);
		currentHelper.removeClient(anon);
		// anon.setAnswered(false); This needs to be managed in the convo
		System.out.println(anon.toString());
		return true;
	}

	public boolean addHelpDeskUser(String sessionId, String username,
			String password, String captcha_ans, Captcha captcha)
	{
		// This is where I need to check the username and password. I need to
		// create a class that deals with the Authentication, this will also
		// be used to hash the data that will be go into the database,
		// I would also like to encrypt my username. ENCRYPT not HASH, as one
		// hash and one encryption makes deciphering things much harder.
		if (captcha_ans != null && captcha != null)
		{
			if (captcha.isCorrect(captcha_ans))
			{
				System.out.println("Captcha entered was correct...");
				System.out.println("Perfect this method can carry on....");
			}
			else
			{
				return false;
			}
		}
		else
		{
			// No login should be possible without captcha acceptance.
			return false;
		}

		HelpDeskUser helper = (new Authentication()).checkCredentials(username,
				password);
		if (helper != null)
		{
			helper.setSessionId(sessionId);
			this.helpDeskUsers.put(sessionId, helper);
			/*
			 * Now that a helper is in, lets check if there are any clients who
			 * have not been assigned a helper as yet, if so assign this user to
			 * them.
			 */
			this.assignHelpers();
			// @TODO : Find a better way to do this, since effectively this
			// needs
			// to run just once.
			return true;
			// Add him/her to the list of helpers present and also run the
			// method
			// to assign the users.
			// Set the session immediately.
		}
		else
		{
			return false;
		}
	}
}
