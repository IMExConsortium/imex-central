YAHOO.namespace("imex");

YAHOO.imex.jnledit = {

    jid: 0,
    login: null,
    stateButton: null,
    
    init: function( e, obj ) {
        
        myself = YAHOO.imex.jnledit;
        myself.jid = obj.jid;
        myself.login = obj.jid.login;        
        
        var onSelectedMenuItemChange = function ( event ) {
            var oMenuItem = event.newValue;
            var text = oMenuItem.cfg.getProperty( "text" );
            
            for( var i = 0; i < this.my.items.length; i++ ) {
                if ( this.my.items[i].text === text ) {
                    this.my.value = this.my.items[i].value;                   
                }
            }
            
            this.my.text = oMenuItem.cfg.getProperty("text");

            this.set("label", ("<em class=\"yui-button-label\">" + 
                               oMenuItem.cfg.getProperty("text") + "</em>"));
            
        };
    },

    sync:function( o ) {
        console.log("jnlSync called");
    },
    
    update: function( op ){

        console.log("update: "+ op );
        try{
            myself = YAHOO.imex.jnledit;

            var updateCallback = { cache:false, timeout: 5000, 
                                   success: myself.updateSuccess,
                                   failure: myself.callbackFail,
                                   argument:{ id: myself.jid } };
            
            var adminCallback = { cache:false, timeout: 5000, 
                                  success: myself.adminSuccess,
                                  failure: myself.callbackFail,
                                  argument:{ id: myself.jid } };
            
            var url =  'journaledit?id=' + myself.jid;
            var setCallback = null;
            
            if( op === "detail" ) {
                
                url = url + "&op.jpup=update" 
                    + '&opp.title=' + YAHOO.util.Dom.get("jnl-det-edit_journal_title").value
                    + '&opp.nlmid=' + YAHOO.util.Dom.get("jnl-det-edit_journal_nlmid").value
                    + '&opp.issn=' + YAHOO.util.Dom.get("jnl-det-edit_journal_issn").value, 
                    + '&opp.url=' + YAHOO.util.Dom.get("jnl-det-edit_journal_websiteUrl").value, 
                console.log("url set...");
                setCallback  = updateCallback;
            }
            
            if( op === 'add-admin' ) {            

                url = url + "&op.jauadd=update"
                    + '&opp.jauadd=' + YAHOO.util.Dom.get("jnl-acc-edit_opp_eauadd").value;
                setCallback = adminCallback;
            }

            if( op === 'drop-admin' ) {
                
                var drops = YAHOO.util.Dom.getElementsByClassName("admin-user-drop");
                var eaudel = ",";
                
                for( var i = 0; i < drops.length; i++ ) {
                    //alert("val=" + drops[i].value + " chk=" + drops[i].checked);
                    if( drops[i].checked ) {
                        eaudel = eaudel + drops[i].value + ",";
                    }

                } 

                if( eaudel === "," ) {               
                    if(drops.checked) {
                        eaudel = eaudel + drops.value + ",";
                    }
                }
                if( eaudel !== "," ) {   
                    
                    url =  url + "&op.jaudel=update"
                        + '&opp.jaudel=' + eaudel;
                    setCallback = adminCallback;
                }        
            }

            if( op === 'add-admin-group' ) {
               
                url = url + "&op.jagadd=update"
                    + '&opp.jagadd=' + YAHOO.util.Dom.get("jnl-acc-edit_opp_eagadd").value;
                setCallback = adminCallback;
            } 
            
            if( op === 'drop-admin-group' ) {

                var drops = YAHOO.util.Dom.getElementsByClassName("admin-group-drop");
                var eagdel = ",";
                
                for( var i = 0; i < drops.length; i++ ) {
                    if( drops[i].checked ) {
                        eagdel = eagdel + drops[i].value + ",";
                    }
                } 

                if( eagdel === "," ) {               
                    if(drops.checked) {
                        eagdel = eagdel + drops.value + ",";
                    }
                }
                if( eagdel !== "," ) {   
                    
                    url =  url + "&op.jagdel=update"
                        + '&opp.jagdel=' + eagdel;
                    setCallback = adminCallback;
                }
            }
            
            console.log( "URL="+ url );
            
            if( setCallback!== null ){  
                YAHOO.util.Connect.asyncRequest( 'GET', encodeURI(url), setCallback ); 
            }            
            
            return false;
        } catch (x) {
            console.log(x);
        }   
        return false; 
    },
    
    updateSuccess: function( o ){

        var myself = YAHOO.imex.jnledit;
         console.log("updateSuccess");
        try {
            if( myself.aclErrorTest( o.responseText ) == false ) {
                
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                var jid = messages.jid;
                var title = messages.journal.title;
                var nlmid = messages.journal.nlmid;
                var issn= messages.journal.issn;
                var wurl = messages.journal.wurl;

                var sCode = messages.statusCode;
                var sMessage = messages.statusMessage;
                
                YAHOO.util.Dom.get("jnl-det-edit_journal_title").value = title;
                YAHOO.util.Dom.get("jnl_ttl").innerHTML = title;
                
                YAHOO.util.Dom.get("jnl-det-edit_journal_nlmid").value = nlmid;
                YAHOO.util.Dom.get("jnl-det-edit_journal_issn").value = issn;
                YAHOO.util.Dom.get("jnl-det-edit_journal_wurl").value = websiteUrl;
           
                if( sCode > 0){                    
                    // pop up error modal                    
                    YAHOO.mbi.modal.exception( sCode, sMessage );                         
                }                
            }
        } catch(x) {
            console.log( x );
        }        
    },

    adminSuccess: function( o ) {

        var myself = YAHOO.imex.jnledit;
        console.log("adminSuccess");
        
        try {
            if( myself.aclErrorTest( o.responseText ) == false ) {
            
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                
                var tau = YAHOO.util.Dom.get( "td-admin-user" );
                var nih = "";

                for( var i = 0; i < messages.journal.adminUsers.length; i++ ) {
                    
                    nih = nih + '<input type="checkbox" id="jnl-acc-edit_opp_eaudel" value="' + 
                        messages.journal.adminUsers[i].id +
                        '" name="opp.eaudel" class="admin-user-drop">';

                    nih = nih + '<input type="hidden" value="' + 
                        messages.journal.adminUsers[i].id + 
                        '" name="__checkbox_opp.eaudel" id="__checkbox_jnl-acc-edit_opp_eaudel">';

                    nih = nih + messages.journal.adminUsers[i].login;
                }              
                tau.innerHTML = nih;
                YAHOO.util.Dom.get("jnl-acc-edit_opp_eauadd").value ="";

                var tag = YAHOO.util.Dom.get( "td-admin-group" );
                var nig = "";

                for( var i = 0; i < messages.journal.adminGroups.length; i++ ) {
                    
                    nig = nig + '<input type="checkbox" id="jnl-acc-edit_opp_eagdel" value="' + 
                        messages.journal.adminGroups[i].id +
                        '" name="opp.eagdel" class="admin-group-drop">';

                    nig = nig + '<input type="hidden" value="' + 
                        messages.journal.adminGroups[i].id + 
                        '" name="__checkbox_opp.eagdel" id="__checkbox_jnl-acc-edit_opp_eagdel">';
                    
                    nig = nig + messages.journal.adminGroups[i].label;
                }              
                tag.innerHTML = nig;
                //YAHOO.util.Dom.get("jnledit_opp_eauadd").value ="";

            }
        } catch(x) {
            alert("AJAX Error: " + x );
        }
    },

    callbackFail: function( o ){
        alert( "AJAX Error update failed: id=" + o.argument.id ); 
        console.log( x );
    },
    


    watchFlag: function( op ){
        if( op == 'update' ){
            var wfl = YAHOO.util.Dom.getElementsByClassName("watch-flag");
            var watch = wfl[0].checked;

            var setWatchFlagCallback = { cache:false, timeout: 5000, 
                                         success: YAHOO.imex.jnledit.watchUpdate,
                                         failure: YAHOO.imex.jnledit.updateFail,
                                         argument:{ chkbox:wfl[0] } };
            try{
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'watchmgr?op.wflu=update' 
                                   + '&id=' + YAHOO.imex.jnledit.pubId 
                                   + '&opp.wfl=' + watch,
                                   setWatchFlagCallback );
            } catch(x){
                alert( x );
            }
        }
        return false;
    },

    watchUpdate: function( o ){
        var messages = YAHOO.lang.JSON.parse( o.responseText );        
        //alert( "watchUpdate->response=" + o.responseText );
    },

    aclErrorTest: function(responseText){
        var acl = /ACL Violation/; 
        if( acl.test( responseText ) ) {
            var aclViolation = {};
            aclViolation.title = "ACL Violation";
            
            var start = responseText.indexOf("<center>");
            var end = responseText.indexOf("</center>") + "</center>".length;
            var inject = responseText.slice(start, end );
            aclViolation.body = inject;
            YAHOO.mbi.modal.spcstat( aclViolation );
            return true;
        }
        return false;
     }
};
