var chanceVal = 50, bet = 1, myHash="ADEFA3A646CDAF1D7A51626997EF74DE34CA17124A3F51244ECAA1969C478281", clientHash="CEB25E042BB9623CD4709A8E2BFBA51F26B523C42712A3EE899BEC2CDBC39D95";

function httpGetAsync(theUrl, callback) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(xmlHttp.responseText);
    }
    xmlHttp.open("GET", theUrl, true); // true for asynchronous 
    xmlHttp.send(null);
}

function openTab(evt, tabname) {
    var i, tabcontent, tablinks;

    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    document.getElementById(tabname).style.display = "block";
    evt.currentTarget.className += " active";
}

function roll() {
    var sum = (parseInt(myHash.substr(0,4), 16)) + (parseInt(clientHash.substr(0, 4), 16)),
        roll = sum%100+1;
    console.log(sum + " and " + roll);
}

window.onload = function(e) {    
    
    var chance = document.getElementById("chance"),
        rollUnder = document.getElementById("number-display"),
        wager = document.getElementById("wager"),
        profit = document.getElementById("profit"),
        username = document.getElementById("username"),
        balance = document.getElementById("balance");
    
    if (document.cookie != null) {
        
    } else {
        username.innerHTML = "Sign In";
        balance.parentNode.removeChild(balance);
    }
    
    document.getElementById("play").style.display = "block";
    document.getElementById("default").className += " active";
    
    document.getElementById("range").oninput = function() {
        chanceVal = parseFloat(this.value);
        chance.innerHTML = chanceVal + "%";
        rollUnder.innerHTML = (chanceVal+0.1).toFixed(2);
        var noHouseEdge = bet/(chanceVal/100) - bet,
            houseEdge = noHouseEdge * 0.988;
        profit.innerHTML = houseEdge.toFixed(10);
        wager.innerHTML = bet.toFixed(10);
    }
    
    document.getElementById("bet").onchange = function() {
        var DOM = document.getElementById("bet");
        console.log(this.value);
        bet = parseFloat(this.value);
        DOM.setAttribute("value", bet.toFixed(8));
        var noHouseEdge = bet/(chanceVal/100) - bet,
            houseEdge = noHouseEdge * 0.99;
        profit.innerHTML = houseEdge.toFixed(10);
        wager.innerHTML = bet.toFixed(10);
    }
}

function fetchInformation() {
    var cookie = document.cookie;
    console.log(cookie);
    if (cookie != null && cookie != "null") { // they're signed in
        httpGetAsync("http://localhost:81/api/fetchinfo?token=" + cookie, function(response) {
            console.log(response);
            if (response == -2) {
                
            }
        });
    }
}
fetchInformation();