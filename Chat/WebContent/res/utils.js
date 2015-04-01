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
		$('#error').html(responseHtml);
	});
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
	setInterval(function(){
		var url = "";
		url += "?action=GetMsgs";
		url += "&token="+getToken();
		$.get(url, function(responseText) {
			$('#somediv').text(responseText);
		});
	}, 1000);
}

function getHelpers()
{
	setInterval(function()
	{
		 var url = "";
		 url += "?action=GetHelpers";
		 url += "&token="+getToken();
         $.get(url, function(responseText) 
         {
        	 $('#helpers').html(responseText);
         });
     }, 3000);
}

function getUsers()
{
	setInterval(function()
	{
		 var url = "";
		 url += "?action=GetUsers";
		 url += "&token="+getToken();
         $.get(url, function(responseText) 
         {
        	 $('#clients').html(responseText);
         });
     }, 3000);
}

function updateTransfers()
{
	setInterval(function()
	{
		 var url = "";
		 url += "?action=UpdateTransfers";
		 url += "&token="+getToken();
         $.get(url, function(responseText) 
         {
        	 $('#transfer_users').html(responseText);
         });
     }, 3000);
}


function updateToken()
{
	setInterval(function(){
		var url="";
		url += "?action=UpdateToken";
		$.get(url, function(responseText)
		{
			$('#secTok').html(responseText);
		})
	}, 1000*60)
}

function getToken()
{
	 var tok = document.getElementById("secTokValue").innerHTML;
	 return tok;
}