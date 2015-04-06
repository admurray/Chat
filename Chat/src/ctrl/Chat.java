package ctrl;

import nl.captcha.Captcha;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.HelpDeskSession;
import model.SecureToken;
import model.SecurityUtils;

/**
 * Servlet implementation class Start
 */
@WebServlet(
{ "/Chat", "/Chat/*" })
public class Chat extends HttpServlet
{
	private static final long				serialVersionUID	= 1L;
	private static final String				helpPage			= "/help";
	private static final String				loginPage			= "/login";
	private static final String				validKeywords[]		=
																{ "GetUsers",
			"SelectUser", "GetHelpers", "SelectHelper", "SendMessage",
			"GetMsgs", "UpdateTransfers", "TransferUser", "EndSession",
			"Logout", "UpdateToken"											};
	private static final HashSet<String>	validActions		= new HashSet<String>(
																		Arrays.asList(validKeywords));

	// All The values that will be pushed into the session.
	// sesUsrType can have only two values, either helper or anonymous.
	public static final String				sesUsrType			= "sessionUserType";
	public static final String				crntView			= "currentView";
	public static final String				token				= "secToken";
	public static final String 				secThread			= "secThread";
	public static final String 				registeredUser		= "helper";
	public static final String 				anonymousUser		= "anonymous";

	public void init() throws ServletException
	{
		this.getServletContext().setAttribute("deskSession",
				new HelpDeskSession());
		this.getServletContext().setAttribute("sanitizer", new SecurityUtils());
	}

	/**
	 * Default constructor.
	 */
	public Chat()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		RequestDispatcher rd = null;
		String path = request.getPathInfo();
		String view = null;
		HelpDeskSession desk = (HelpDeskSession) this.getServletContext()
				.getAttribute("deskSession");
		SecurityUtils sanitizer = (SecurityUtils) this.getServletContext()
				.getAttribute("sanitizer");
		HttpSession session = request.getSession(true);
		String action = Chat.validateInput("action", request, sanitizer);
		String token = Chat.validateInput("token", request, sanitizer);
		
		SecureToken secToken = (SecureToken) (session.getAttribute(Chat.token) == null?
				(new SecureToken()):
				session.getAttribute(Chat.token));
		
		session.setAttribute(Chat.token, secToken);
		request.setAttribute(Chat.token, secToken.getToken());

		/*
		 * I can always get the information about the user from the sessionId
		 * now that I am using the sessionId as the id for the users. The only
		 * user that I need to store in the session is the user that the session
		 * user is talking to.
		 */
		if (action != null && validActions.contains(action) && token != null &&
				token.equals(secToken.getToken()))
		{
			// Have a named dispatch to AChat to manage ajax
			// Consider how the action attribute could be set and passed
			// by the user.
			this.getServletContext().getNamedDispatcher("AChat")
					.forward(request, response);

		} 
		else if (action != null && !action.isEmpty() && !validActions.contains(action))
		{
			view = (String) session.getAttribute(crntView);
			if(view != null)
			{
				request.setAttribute("view", view);
				rd = request.getRequestDispatcher("/Master.jspx");
				rd.forward(request, response);
			}
			System.out.println("Invalid action...... : "+action);
		} 
		else
		{
			if (path != null && !path.isEmpty() && !path.equals("/"))
			{
				if (path.equals(helpPage))
				{
					String name = Chat.validateInput("name", request, sanitizer);
					if ((name == null || name.isEmpty())
							&& !desk.getAnonUsers()
									.containsKey(session.getId()))
					{
						// Do not set the type of the user here. Refer to the
						// session hijacking attack. Setting the user type is a
						// vuln, if the type is not checked when the username
						// and password are set.
						view = "SetName";
						session.setAttribute(crntView, view);
					}

					else if (!desk.getAnonUsers().containsKey(session.getId())
							&& name != null && !name.isEmpty())
					{
						if (desk.addAnonymousUser(session.getId(), name)) // add
																			// returns
																			// boolean
						{
							// This is required, so that we know which list to
							// access to
							// get the User instance.
							view = "AnonymousUser";
							session.setAttribute(sesUsrType,
									Chat.anonymousUser);
							session.setAttribute("talkingToType",
									Chat.registeredUser);
						} else
						{
							System.out.println("NAME was probably invalid....");
							view = "SetName";
						}
						session.setAttribute(crntView, view);
					} else
					{
						view = (String) session.getAttribute(crntView);
					}

				}

				if (path.equals(loginPage))
				{
					String username = Chat.validateInput("username", request,
							sanitizer);
					String password = Chat.validateInput("password", request,
							sanitizer);
					String captcha_ans = Chat.validateInput("captcha_answer",
							request, sanitizer);
					Captcha captcha = (Captcha) session
							.getAttribute(Captcha.NAME);

					if ((username == null || username.isEmpty()
							|| password == null || password.isEmpty())
							&& !desk.getHelpDeskUsers().containsKey(
									session.getId()))
					{
						view = "Login";
						session.setAttribute(crntView, view);
					}

					else if (!desk.getHelpDeskUsers().containsKey(
							session.getId())
							&& username != null
							&& !username.isEmpty()
							&& password != null && !password.isEmpty())
					{
						System.out.println("Trying to log the user in.....");
						if (desk.addHelpDeskUser(session.getId(), username,
								password, captcha_ans, captcha)) // add returns
																	// boolean
						{
							// This is required, so that we know which list to
							// access to
							// get the User instance.
							view = "HelpDeskUser";
							session.setAttribute(sesUsrType,
									Chat.registeredUser);
						} else
						{
							System.out
									.println("LOGIN was probably invalid....");
							view = "Login";
						}
						session.setAttribute(crntView, view);
					} else
					{
						view = (String) session.getAttribute(crntView);
					}
				}
				request.setAttribute("view", view);
				rd = request.getRequestDispatcher("/Master.jspx");
				rd.forward(request, response);
			}
			if (path == null || path.isEmpty() || path.equals("/"))
			{
				System.out.println("View set to SetName");
				// Default page is the page for the user.
				view = "SetName";
				request.setAttribute("view", view);
				rd = request.getRequestDispatcher("/Master.jspx");
				rd.forward(request, response);
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		this.doGet(request, response);
	}

	protected static String validateInput(String param,
			HttpServletRequest request, SecurityUtils sanitizer)
	{
		String value = request.getParameter(param);
		value = sanitizer.sanitize(value);
		return value;
	}
}
