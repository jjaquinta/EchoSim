// echosim.js

var endpoint = null;
var intents = null;
var utterances = null;
var userid = null;
var appid = null;

function doTalk()
{
	var text = document.getElementById('text').value;
	var url = "echosim?text="+text;
	if (endpoint != null)
		url += "&endpoint="+encodeURI(endpoint);
	if (intents != null)
		url += "&intents="+encodeURI(intents);
	if (utterances != null)
		url += "&utterances="+encodeURI(utterances);
	if (userid != null)
		url += "&userid="+encodeURI(userid);
	if (appid != null)
		url += "&appid="+encodeURI(appid);
	url += "&Accept=html";
	xhrGet(url, function(responseText){
		// add to document
		var mytitle = document.getElementById('message');
		mytitle.innerHTML = "<pre>"+text+"</pre>"+responseText+"<br/>"+mytitle.innerHTML+"<br/>";
		document.getElementById('text').focus();
		document.getElementById('text').select();
	}, function(err){
		console.log(err);
	});
}

//utilities
function createXHR(){
	if(typeof XMLHttpRequest != 'undefined'){
		return new XMLHttpRequest();
	}else{
		try{
			return new ActiveXObject('Msxml2.XMLHTTP');
		}catch(e){
			try{
				return new ActiveXObject('Microsoft.XMLHTTP');
			}catch(e){}
		}
	}
	return null;
}
function xhrGet(url, callback, errback){
	var xhr = new createXHR();
	xhr.open("GET", url, true);
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4){
			if(xhr.status == 200){
				callback(xhr.responseText);
			}else{
				errback('service not available');
			}
		}
	};
	xhr.timeout = 3000;
	xhr.ontimeout = errback;
	xhr.send();
}
function parseJson(str){
	return window.JSON ? JSON.parse(str) : eval('(' + str + ')');
}
function prettyJson(str){
	// If browser does not have JSON utilities, just print the raw string value.
	return window.JSON ? JSON.stringify(JSON.parse(str), null, '  ') : str;
}

