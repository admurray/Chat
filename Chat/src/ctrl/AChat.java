package ctrl;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.AnonymousUser;
import model.Conversation;
import model.HelpDeskSession;
import model.HelpDeskUser;
import model.SecureToken;
import model.SecurityUtils;
import model.User;

/**
 * Servlet implementation class AChat
 */
@WebServlet("/AChat")
public class AChat extends HttpServlet
{
	private static final long	serialVersionUID	= 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AChat()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session = request.getSession(false);
		HelpDeskSession desk = (HelpDeskSession) this.getServletContext()
				.getAttribute("deskSession");
		SecurityUtils sanitizer = (SecurityUtils) this.getServletContext()
				.getAttribute("sanitizer");
		String action = Chat.validateInput("action", request, sanitizer);
		String sessionId = session.getId();
		String sessionUsrType = (String) session.getAttribute(Chat.sesUsrType);
		String talkingToId = (String) session.getAttribute("talkingTo");
		String talkingToType = (String) session.getAttribute("talkingToType");

		/*
		 * There are a six different types of actions that can take place
		 * GetUsers GetHelpers GetMessages SendMessages EndSession LogOut These
		 * are the different button presses that can happen when a helper/client
		 * are on their respective pages.
		 */

		// ==================UPDATE USERS=======================================
		/*
		 * Get the users, this is called only from the helpdesk user, should
		 * never run from the Anonymous Users part. Check the type of the
		 * session user. If the session user is not a helper do not execute.
		 */
		if (action.equals("GetUsers"))
		{
			this.getUsers(sessionId, desk, response, sanitizer);
		}

		if (action.equals("SelectUser"))
		{
			this.selectUser(request, desk, session, response, sanitizer);
		}

		if (action.equals("GetHelpers"))
		{
			this.getHelpers(desk, sessionId, response, sanitizer);
		}

		if (action.equals("SelectHelper"))
		{
			this.selectHelper(request, session, desk, response, sanitizer);
		}

		if (action.equals("SendMessage"))
		{
			this.sendMessage(session, sessionId, talkingToId, request,
					response, talkingToType, sessionUsrType, desk, sanitizer);
		}

		if (action.equals("GetMsgs"))
		{
			this.getMsgs(session, desk, sessionId, talkingToId, response,
					sanitizer);
		}

		if (action.equals("UpdateTransfers"))
		{
			this.updateTransfers(desk, sessionId, response, sanitizer);
		}

		if (action.equals("TransferUser"))
		{
			this.transferUser(request, desk, session, sessionId, sanitizer);
		}

		if (action.equals("EndSession"))
		{
			this.endSession(desk, sessionId, request, response, session,
					sanitizer);
		}

		if (action.equals("Logout"))
		{
			this.logout(desk, sessionId, request, response, session, sanitizer);
		}
		
		if(action.equals("UpdateToken"))
		{
			this.updateToken(response, session);
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

	public void logout(HelpDeskSession desk, String sessionId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, SecurityUtils sanitizer)
			throws ServletException, IOException
	{
		String convoId = "";
		AnonymousUser anon = null;
		HelpDeskUser helper = desk.getHelperById(sessionId);
		Iterator<AnonymousUser> iter = helper.getAnons().values().iterator();
		while (iter.hasNext())
		{
			anon = iter.next();
			convoId = Conversation.createConvoId(helper.getSessionId(),
					anon.getSessionId());
			if (desk.getConversations().containsKey(convoId))
			{
				desk.getConversations().remove(convoId);
			}
		}
		session.invalidate();
		request.setAttribute("view", "Login");
		request.getRequestDispatcher("/Master.jspx").forward(request, response);
	}

	public void endSession(HelpDeskSession desk, String sessionId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, SecurityUtils sanitizer)
			throws ServletException, IOException
	{
		String convoId = null;
		AnonymousUser anon = desk.getUserById(sessionId);
		anon.getHelper().getAnons().remove(sessionId);
		convoId = Conversation.createConvoId(anon.getSessionId(), anon
				.getHelper().getSessionId());
		if (desk.getConversations().containsKey(convoId))
		{
			desk.getConversations().remove(convoId);
		}
		desk.getUserById(sessionId).getHelper().getAnons().remove(sessionId);
		desk.getAnonUsers().remove(sessionId);

		session.invalidate();
		request.setAttribute("view", "SetName");
		request.getRequestDispatcher("/Master.jspx").forward(request, response);
	}

	public void getHelpers(HelpDeskSession desk, String sessionId,
			HttpServletResponse response, SecurityUtils sanitizer)
	{
		String html = "";
		String startButton = "<button id=\"helper_button\" name=\"user\" value=\"";
		String endButton = "</button><br/>";
		HelpDeskUser helper;
		String convoId = null;
		Conversation convo = null;
		try
		{
			if (desk.getHelpDeskUsers().size() > 1)
			{
				// Set to greater than one since this should not be possible
				// if there is just one user, the helper. How can the helper
				// select himself. Also if there is no other helper, how can
				// helper select another helper.
				// Get the session user - get it by using the session id
				Iterator<HelpDeskUser> iter = desk.getHelpDeskUsers().values()
						.iterator();
				while (iter.hasNext())
				{
					helper = iter.next();
					convoId = Conversation.createConvoId(helper.getSessionId(),
							sessionId);
					if (desk.getConversations().containsKey(convoId))
					{
						convo = desk.getConversations().get(convoId);
					}
					// ===================================================

					if (!helper.getSessionId().equals(sessionId)
							&& (convoId == null || convo == null))
					{
						html += startButton + helper.getSessionId() + "\"";
						html += " onclick=\"selectHelper('"
								+ helper.getSessionId() + "')\">"
								+ helper.getName() + endButton;
					}
					if (!helper.getSessionId().equals(sessionId)
							&& convo != null)
					{
						if (convo.getResponseId() == null)
						{
							html += startButton + helper.getSessionId() + "\"";
							html += " onclick=\"selectHelper('"
									+ helper.getSessionId() + "')\">"
									+ helper.getName() + endButton;
						} else if (convo.getResponseId().equals(sessionId))
						{
							html += startButton + helper.getSessionId() + "\"";
							html += " onclick=\"selectHelper('"
									+ helper.getSessionId() + "')\">"
									+ helper.getName()
									+ " <font color=\"red\">Waiting...</font>"
									+ endButton;
						} else
						{
							html += startButton + helper.getSessionId() + "\"";
							html += " onclick=\"selectHelper('"
									+ helper.getSessionId() + "')\">"
									+ helper.getName() + endButton;

						}
					}
				}
				if (!html.isEmpty())
				{
					response.getWriter().write(html);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Illegal cast was attempted. Ajax getHelpers");
		}
	}

	public void selectUser(HttpServletRequest request, HelpDeskSession desk,
			HttpSession session, HttpServletResponse response,
			SecurityUtils sanitizer) throws IOException
	{
		String userId = Chat.validateInput("userId", request, sanitizer);
		String html = "";
		AnonymousUser anon = desk.getUserById(userId);
		html = " <legend id=\"my_legend\">Aditya's HelpDesk| " + anon.getName()
				+ "</legend>";
		/*
		 * Once this ID is obtained it should be set as the talkingTo ID, which
		 * will be used by the helper to send the message to the person he/she
		 * has in his/her list and wishes to talk to. Also this is only set when
		 * the session owner is a helper. If the sessionUser is a Anonymous,
		 * one. There should be no selection for the user. two. The anonymous
		 * user can talk only to the helper.
		 */
		session.setAttribute("talkingTo", userId);
		session.setAttribute("talkingToType", HelpDeskSession.anonStr);
		response.getWriter().write(html);
	}

	public void getUsers(String sessionId, HelpDeskSession desk,
			HttpServletResponse response, SecurityUtils sanitizer)
	{
		String html = "";
		String startButton = "<button id=\"user_button\" name=\"user\" value=\"";
		String endButton = "</button><br/>";
		AnonymousUser anon;
		String convoId = null;
		Conversation convo = null;
		try
		{
			// Get the session user - get it by using the session id
			HelpDeskUser helper = desk.getHelpDeskUsers().get(sessionId);
			if (helper != null)
			{
				Iterator<AnonymousUser> iter = helper.getAnons().values()
						.iterator();
				while (iter.hasNext())
				{
					anon = iter.next();
					convoId = Conversation.createConvoId(sessionId,
							anon.getSessionId());
					if (desk.getConversations().containsKey(convoId))
					{
						convo = desk.getConversations().get(convoId);
					}

					// =====================================================
					if (convoId == null || convo == null)
					{
						html += startButton + anon.getSessionId() + "\"";
						html += " onclick=\"selectUser('" + anon.getSessionId()
								+ "')\">" + anon.getName()
								+ "  <font color=\"red\">New User</font>"
								+ endButton;
					} else if (convo != null)
					{
						if (convo.getResponseId() == null)
						{
							html += startButton + anon.getSessionId() + "\"";
							html += " onclick=\"selectUser('"
									+ anon.getSessionId() + "')\">"
									+ anon.getName()
									+ " <font color=\"red\">New User</font>"
									+ endButton;
						}
						if (convo.getResponseId().equals(sessionId))
						{
							html += startButton + anon.getSessionId() + "\"";
							html += " onclick=\"selectUser('"
									+ anon.getSessionId() + "')\">"
									+ anon.getName()
									+ "  <font color=\"red\">Waiting...</font>"
									+ endButton;
						} else
						{
							html += startButton + anon.getSessionId() + "\"";
							html += " onclick=\"selectUser('"
									+ anon.getSessionId() + "')\">"
									+ anon.getName() + endButton;
						}
					}

				}
				if (!html.isEmpty())
				{
					response.getWriter().write(html);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Illegal cast was attempted. Ajax getUsers");

		}
	}

	public void selectHelper(HttpServletRequest request, HttpSession session,
			HelpDeskSession desk, HttpServletResponse response,
			SecurityUtils sanitizer) throws IOException
	{
		String userId = Chat.validateInput("userId", request, sanitizer);
		String html = "";
		HelpDeskUser helper = desk.getHelperById(userId);
		html = " <legend id=\"my_legend\">Aditya's HelpDesk| "
				+ helper.getName() + "</legend>";
		session.setAttribute("talkingTo", userId);
		session.setAttribute("talkingToType", HelpDeskSession.hlprStr);
		response.getWriter().write(html);
	}

	public void sendMessage(HttpSession session, String sessionId,
			String talkingToId, HttpServletRequest request,
			HttpServletResponse response, String talkingToType,
			String sessionUsrType, HelpDeskSession desk, SecurityUtils sanitizer)
			throws IOException
	{
		String convoId = null;
		/*
		 * There are two things that matter here. One the person, sending the
		 * message and the person receiving it. The conversation id will need to
		 * be formed, via the concatenation of the two ids and then possible
		 * hashing the result. The issue is complicated in the case when the two
		 * helpdesk users are talking, what order should they be concatenated
		 * in. I could do an aphanumerical comparison and then append them,
		 * based on that.
		 */
		String html = "";
		html = this.setTalkingTo(session);
		convoId = Conversation.createConvoId(sessionId, talkingToId);
		if (convoId != null && !desk.getConversations().containsKey(convoId))
		{
			User sessionOwner;
			User talkingTo;
			if (sessionUsrType.equals(HelpDeskSession.hlprStr))
				sessionOwner = desk.getHelperById(sessionId);
			else if (sessionUsrType.equals(HelpDeskSession.anonStr)
					&& !talkingToType.equals(HelpDeskSession.anonStr))
				sessionOwner = desk.getUserById(sessionId);
			else
				sessionOwner = null;
			// =============
			if (talkingToType != null
					&& talkingToType.equals(HelpDeskSession.anonStr)
					&& !sessionUsrType.equals(HelpDeskSession.anonStr))
				talkingTo = desk.getUserById(talkingToId);
			else if (talkingToType != null
					&& talkingToType.equals(HelpDeskSession.hlprStr))
				talkingTo = desk.getHelperById(talkingToId);
			else
				talkingTo = null;
			Conversation convo = new Conversation(convoId, sessionOwner,
					talkingTo, new StringBuffer(), null);
			convo.setResponseId(talkingToId);
			desk.getConversations().put(convoId, convo);
			convo = desk.getConversations().get(convoId);
			User sender = convo.getUsrById(sessionId);
			/*
			 * There can be only three categories of messages coming in. Anon
			 * ------> Helper Helper ----> Anon Helper ----> Helper
			 */
			String message = sender.getName() + " : "
					+ Chat.validateInput("message", request, sanitizer);
			if (convo != null)
				convo.sendMessage(message);
		} else if (convoId != null
				&& desk.getConversations().containsKey(convoId))
		{
			Conversation convo = desk.getConversations().get(convoId);
			convo.setResponseId(talkingToId);
			User sender = convo.getUsrById(sessionId);
			String message = sender.getName() + " : "
					+ Chat.validateInput("message", request, sanitizer);
			if (convo != null)
				convo.sendMessage(message);
		} else
		{
			System.out
					.println("=====================================================");
			if (html == null)
			{
				response.getWriter().write("");
			} else
			{
				response.getWriter().write(html);
			}
		}
	}

	public void getMsgs(HttpSession session, HelpDeskSession desk,
			String sessionId, String talkingToId, HttpServletResponse response,
			SecurityUtils sanitizer) throws IOException
	{
		this.setTalkingTo(session);
		String convoId = Conversation.createConvoId(sessionId, talkingToId);
		if (convoId != null && desk.getConversations().containsKey(convoId))
		{
			Conversation convo = desk.getConversations().get(convoId);
			response.getWriter().write("" + convo.getConvo());
		}
	}

	public void updateTransfers(HelpDeskSession desk, String sessionId,
			HttpServletResponse response, SecurityUtils sanitizer)
			throws IOException
	{

		/*
		 * A sample of what will be the html segment pushed is given below.
		 * <select id="select_user"> <option
		 * value="sessionId">nameOfTheUser</option> </select> TO <select
		 * id="select_helper"> <option
		 * value="sessionId">nameOfTheHelper</option> </select>
		 */
		String html = "";
		String option = null;
		HelpDeskUser helper = desk.getHelpDeskUsers().get(sessionId);
		if (helper != null && helper.getAnons().size() >= 1
				&& desk.getHelpDeskUsers().size() >= 2)
		{
			AnonymousUser anon = null;
			// In this case form the well formed html.
			Iterator<AnonymousUser> anonIter = helper.getAnons().values()
					.iterator();
			html += "TRANSFER<select id=\"select_user\">\n\t";
			while (anonIter.hasNext())
			{
				anon = anonIter.next();
				option = "<option value=\"" + anon.getSessionId() + "\">"
						+ anon.getName() + "</option>";
				html += option + "\n\t";
			}
			html += "</select>\n";
		} else
		{
			html = "";
		}

		if (helper != null && desk.getHelpDeskUsers().size() >= 2
				&& helper.getAnons().size() >= 1)
		{
			HelpDeskUser friend = null;
			Iterator<HelpDeskUser> helpIter = desk.getHelpDeskUsers().values()
					.iterator();
			html += "TO<select id=\"select_helper\">\n\t";
			while (helpIter.hasNext())
			{
				friend = helpIter.next();
				if (friend.getSessionId() != sessionId)
				{
					option = "<option value=\"" + friend.getSessionId() + "\">"
							+ friend.getName() + "</option>";
					html += option + "\n\t";
				}
			}
			html += "</select>";
		} else
		{
			html = "";
		}

		if (!html.equals(""))
		{
			html += "<button id=\"transfer_submit\" onclick=\"transferUser()\">Transfer</button>";
			html = "<div id=\"form_div\">" + html + "</div>";
		}
		response.getWriter().write(html);

	}

	public void transferUser(HttpServletRequest request, HelpDeskSession desk,
			HttpSession session, String sessionId, SecurityUtils sanitizer)
	{
		String userId = Chat
				.validateInput("userToTransfer", request, sanitizer);
		String helperId = Chat.validateInput("newHelper", request, sanitizer);
		String currentHelper = sessionId;
		HelpDeskUser helper = desk.getHelperById(sessionId);
		if (desk.getHelpDeskUsers().containsKey(helperId)
				&& helper.getAnons().containsKey(userId))
		{
			HelpDeskUser newHelper = desk.getHelperById(helperId);
			AnonymousUser anon = desk.getUserById(userId);
			anon.setHelper(newHelper);
			helper.getAnons().remove(userId);
			newHelper.getAnons().put(userId, anon);
			// What can we do to a conversation. Ideally it should be stored
			// in the database, but we can just destroy it.
			String convoId = Conversation.createConvoId(userId, currentHelper);
			session.setAttribute("talkingTo", null);
			if (desk.getConversations().containsKey(convoId))
			{
				desk.getConversations().remove(convoId);
			}
		}
	}
	
	public void updateToken(HttpServletResponse response, HttpSession session) throws IOException
	{
		/*
		 * This is the method to update the value of the token, so all this
		 * does is, it returns the value of the token, just the value and sends
		 * it back to the Ajax calling function. Once that is complete it will
		 * add the sent token to a cookie. This value will be sent with each,
		 * cookie, back to the user. if this value is, incorrect the call/action
		 * does not succeed.
		 */
		SecureToken token = (SecureToken) session.getAttribute(Chat.token);
		String secTok = (token.setToken()).toString();
		System.out.println(secTok);
		response.getWriter().write(secTok);
	}
	
	public boolean checkValidity(SecureToken secToken, String siteToken)
	{
		String token = secToken.getToken();
		if(siteToken.equals(token) )
		{
			return true;
		}
		return false;
		
	}

	public String setTalkingTo(HttpSession session)
	{
		HelpDeskSession desk = (HelpDeskSession) this.getServletContext()
				.getAttribute("deskSession");
		String sessionUsrType = (String) session.getAttribute(Chat.sesUsrType);
		String talkingToId = (String) session.getAttribute("talkingTo");
		String html = "";
		if (sessionUsrType != null
				&& sessionUsrType.equals(HelpDeskSession.anonStr)
				&& talkingToId == null)
		{
			AnonymousUser anon = desk.getUserById(session.getId());
			if (anon.getHelper() != null)
				session.setAttribute("talkingTo", anon.getHelper()
						.getSessionId());
			else
				html = "<p><font color=\"red\">Please Wait for a Helper to come online</font></p>";
			talkingToId = (String) session.getAttribute("talkingTo");
		} else if (sessionUsrType != null
				&& sessionUsrType.equals(HelpDeskSession.hlprStr)
				&& talkingToId == null)
		{
			HelpDeskUser helper = desk.getHelperById(session.getId());
			if (helper.getAnons().size() >= 1)
				html = "<p><font color=\"red\">Please make a selection to talk.</font></p>";
			else
				html = "<p><font color=\"red\">Please wait for a user to come online.</font></p>";
		} else
		{
			html = null;
		}
		return html;
	}
}
