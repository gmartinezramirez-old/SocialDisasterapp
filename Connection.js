
var CHAT_FRIENDS = "CHAT_FRIENDS";


function sendRequest(requestType, token, fbId){
  checkNetwork(function(isConnected){
    switch(requestType){
      case CHAT_FRIENDS:
        requestFriends(isConnected, token, fbId);
        break;
    }
  });
}

function checkNetwork(callback){
  NetInfo.isConnected.fetch().then(isConnected => { 
    console.log('First, is ' + (isConnected ? 'online' : 'offline')); 
    callback(isConnected);
  });
}



function loginChat(fbId, email, passwd){

  if(isConnected){
    //normal request
    
    var json = JSON.stringify({
      encriptedJSON: {
        fbId: fbId,
        fbEmail: email,
        fbPasswd: passwd
      }
    });

    fetch("http://hackaton.cloudapp.net/chat/login", {
      method: 'POST', 
      headers: { 'Accept': 'application/json', 'Content-Type': 'application/json', },
      body: json
    }).then((response) => {
      console.log(response);
    });

  }else{
    // There is nothing to do in this request
  }
}



function requestChats(isConnected, token, fbId){

  if(isConnected){
    //normal request
    
    var json = JSON.stringify({
      encriptedJSON: {
        fbId: fbId
      }
    });

    fetch("http://hackaton.cloudapp.net/chat/chats", {
      method: 'POST', 
      headers: { 'Accept': 'application/json', 'Content-Type': 'application/json', },
      body: json
    }).then((response) => {
      console.log(response);
    });

  }else{
    // DTN request


    var json = JSON.stringify({
      encriptedJSON: {
        tag: "ChatChatsRequest",
        contents: {
          fbId: fbId
        }
      }
    });

    // TODO: Send the request, json, in a DTN

  }
}



function requestChats(isConnected, token, fbId){

  if(isConnected){
    //normal request
    
    var json = JSON.stringify({
      encriptedJSON: {
        fbId: fbId
      }
    });

    fetch("http://hackaton.cloudapp.net/chat/chats", {
      method: 'POST', 
      headers: { 'Accept': 'application/json', 'Content-Type': 'application/json', },
      body: json
    }).then((response) => {
      console.log(response);
    });

  }else{
    // DTN request


    var json = JSON.stringify({
      encriptedJSON: {
        tag: "ChatChatsRequest",
        contents: {
          fbId: fbId
        }
      }
    });

    // TODO: Send the request, json, in a DTN

  }
}



function sendMessageChat(isConnected, token, fbId){

  if(isConnected){
    //normal request
    
    var json = JSON.stringify({
      encriptedJSON: {
        fbId: fbId,
        message: message,
        threadId: threadId
      }
    });

    fetch("http://hackaton.cloudapp.net/chat/send", {
      method: 'POST', 
      headers: { 'Accept': 'application/json', 'Content-Type': 'application/json', },
      body: json
    }).then((response) => {
      console.log(response);
    });

  }else{
    // DTN request


    var json = JSON.stringify({
      encriptedJSON: {
        tag: "ChatSendRequest",
        contents: {
          fbId: fbId,
          message: message,
          threadId: threadId
        }
      }
    });

    // TODO: Send the request, json, in a DTN

  }
}



function sendMessageChat(isConnected, token, fbId){

  if(isConnected){
    //normal request
    
    var json = JSON.stringify({
      encriptedJSON: {
        fbId: fbId,
        message: message,
        threadId: threadId
      }
    });

    fetch("http://hackaton.cloudapp.net/chat/send", {
      method: 'POST', 
      headers: { 'Accept': 'application/json', 'Content-Type': 'application/json', },
      body: json
    }).then((response) => {
      console.log(response);
    });

  }else{
    // DTN request


    var json = JSON.stringify({
      encriptedJSON: {
        tag: "ChatSendRequest",
        contents: {
          fbId: fbId,
          message: message,
          threadId: threadId
        }
      }
    });

    // TODO: Send the request, json, in a DTN

  }
}


function getNewsFeed(isConnected, token, fbId){

  if(isConnected){
    //normal request
    
    var json = JSON.stringify({
      encriptedJSON: {
        token: token,
      }
    });

    fetch("http://hackaton.cloudapp.net/chat/send", {
      method: 'POST', 
      headers: { 'Accept': 'application/json', 'Content-Type': 'application/json', },
      body: json
    }).then((response) => {
      console.log(response);
    });

  }else{
    // DTN request


    var json = JSON.stringify({
      encriptedJSON: {
        tag: "NewsRequest",
        contents: {
          token: token,
        }
      }
    });

    // TODO: Send the request, json, in a DTN

  }
}


// Usar para enviar peticiones que lleguen por la DTN 
function sendGeneralRequest(request){
  if(isConnected){
    //normal request
    
    var json = JSON.stringify({
      encriptedJSON: {
        token: token,
      }
    });

    fetch("http://hackaton.cloudapp.net/chat/send", {
      method: 'POST', 
      headers: { 'Accept': 'application/json', 'Content-Type': 'application/json', },
      body: json
    }).then((response) => {
      console.log(response);
    });

  }else{
    // DTN request


    var json = JSON.stringify({
      encriptedJSON: {
        tag: "NewsRequest",
        contents: {
          token: token,
        }
      }
    });

    // TODO: Send the request, json, in a DTN

  }
}
