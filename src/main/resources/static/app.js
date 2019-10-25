var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect () {
    console.log("connect function");
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/event/create', function (notification) {
            //console.log("notification received at frontend: ");
            showNotification(JSON.parse(notification.body).notificationMessage);
        });
    });
}

function disconnect() {
    if (stompClient !== null) 
    {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() 
{
    stompClient.send("/minio/test", {}, JSON.stringify({'name': $("#name").val()}));
}

function showNotification(msg)
{
    $("#greetings").append("<tr><td>" + msg + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});