function selectUser(userId){
    var url = "";
    url +="?action=SelectUser";
    url +="&userId="+escape(userId);
    url += "&token="+getToken();
    $.get(url, function(responseHtml){
		$('#buddy_name').html(responseHtml);
	});
}

function selectHelper(userId){
    var url = "";
    url +="?action=SelectHelper";
    url +="&userId="+escape(userId);
    url += "&token="+getToken();
    $.get(url, function(responseHtml){
		$('#buddy_name').html(responseHtml);
	});
}

function sendMessage()
{
	var msgVal = $('#msg').val();
	var url = "";
	url += "?action=SendMessage";
	url +=  "&message="+msgVal;
	url += "&token="+getToken();
	document.getElementById("msg").value = "";
	$.get(url, function(responseHtml){
		if(isError(responseHtml))
		{
			$("#error").fadeOut();
			$('#error').html(responseHtml).fadeIn();
			setTimeout(revertToOriginal, 5000);
		}
	});
}

function revertToOriginal() {
 
  $("#error").text("Welcome to SecureDesk").fadeIn();
}

function transferUser()
{
	var url = "";
	url +="?action=TransferUser";
	url +="&userToTransfer="+document.getElementById('select_user').value;
	url +="&newHelper="+document.getElementById('select_helper').value;
	url += "&token="+getToken();
	$.get(url, function(){})
}

function endSession()
{
	var url="";
	url += "?action=EndSession";
	url += "&token="+getToken();
	$.get(url, function(){});
}

function getMsgs()
{

	var url = "";
	url += "?action=GetMsgs";
	url += "&token="+getToken();
	$.get(url, function(responseText) {
		$('#somediv').html(responseText);
		var myDiv = document.getElementById("scrollable_panel_chat");
		myDiv.scrollTop = myDiv.scrollHeight;
	});
}

function getHelpers()
{
	 var url = "";
	 url += "?action=GetHelpers";
	 url += "&token="+getToken();
     $.get(url, function(responseText) 
     {
    	 $('#helpers').html(responseText);
     });
}

function getUsers()
{
	 var url = "";
	 url += "?action=GetUsers";
	 url += "&token="+getToken();
     $.get(url, function(responseText) 
     {
    	 $('#clients').html(responseText);
     });
}

function updateTransfers()
{
	 var url = "";
	 url += "?action=UpdateTransfers";
	 url += "&token="+getToken();
     $.get(url, function(responseText) 
     {
    	 $('#transfer_users').html(responseText);
     });
}


function updateToken()
{
	var url = "";
	 url += "?action=UpdateToken";
	 url += "&token="+getToken();
    $.get(url, function(responseText) 
    {
   	 $('#secTokValue').text(responseText);
    });
}

function getToken()
{
	 var tok = document.getElementById("secTokValue").innerHTML;
	 return tok;
}

function runHelperCalls()
{
	var runHelper = function()
	{
		getMsgs()
		getHelpers()
		getUsers()
		updateTransfers()
		setTimeout(runHelper, 3000);
	};
	runHelper();
}

function runAnonCalls()
{
	var runAnon = function () 
	{
		getMsgs()
		setTimeout(runAnon, 3000);
	};
	runAnon();
}

function newToken()
{
	var newTok = function()
	{
		updateToken()
		setTimeout(newTok, 7200000);
	};
	newTok();
}

function isError(text)
{
	return (text.indexOf("ERROR")>-1);
}