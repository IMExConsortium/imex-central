YAHOO.namespace("imex");

YAHOO.mbi.login = {
    sendUrlFragment: function() {
        if(document.location.hash != "")
        {
            console.log("fragment = " + document.location.hash);
            window.location.href = "/icentral/user?fragment=" + encodeURIComponent(document.location.hash); 
        }
        else
        {
            console.log("clicked but no fragment");
            window.location.href = "/icentral/user";
        }
    },
}
