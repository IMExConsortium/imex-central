YAHOO.namespace("imex");

YAHOO.imex.groupedit = {

  gid: 0,
  aidx: -1,
  subCnt: 14,

  init: function( e, obj ){

    var myself = YAHOO.imex.groupedit;
    myself.gid = obj.gid;
    myself.tabName = obj.tabName;
    myself.login = obj.login ;
    myself.prefs = obj.prefs;
    
    // main tab panel
    //---------------
    
    console.log( "groupedit-yui.js: pubTab" );
    
    YAHOO.imex.groupedit.tabView = new YAHOO.widget.TabView(myself.tabName);
     
    YAHOO.imex.groupedit.tabView.addListener( "activeTabChange", 
                        YAHOO.imex.groupedit.handleHistoryNavigation); 
       
    YAHOO.imex.groupedit.tabView.addListener("activeTabChange", 
                        YAHOO.imex.groupedit.refresh); 

    YAHOO.imex.groupedit.historyInit();
  },
       
  refresh: function( o ){
     console.log( "refresh: called" );
  },
    
  groupInfo: function( op ){
        
    var groupInfoCallback = { cache:false, timeout: 5000, 
                              success: YAHOO.imex.groupedit.infoUpdate,
                              failure: YAHOO.imex.groupedit.infoUpdateFail,
                              argument:{ id:YAHOO.imex.groupedit.gid } };
    try{            
      if( op === 'update' ) {

        var gname = YAHOO.util.Dom.get("groupedit_group_name").value;
        var gcomm = YAHOO.util.Dom.get("groupedit_group_comments").value 
        var galog = YAHOO.util.Dom.get("groupedit_opp_alogin").value 
        var gclog = YAHOO.util.Dom.get("groupedit_opp_clogin").value 
        var req = 'op.pup=update' 
                   + '&id=' + YAHOO.imex.groupedit.gid
                   + '&opp.alogin=' + galog 
                   + '&opp.clogin' + gclog  
                   + '&group.name=' + gname
                   + '&group.comments=' + gcomm  

        YAHOO.util.Connect.asyncRequest( 'GET', 
                                         'groupedit?' + encodeURI( req ), 
                                         groupInfoCallback );
      }    
    }catch( x ){
      alert("AJAX Error(groupInfo): " + x );
    }   
    return false; 
  },

  infoUpdate: function( o ){
    try{
      if( YAHOO.imex.groupedit.aclErrorTest( o.responseText ) == false ) {

        var messages = YAHOO.lang.JSON.parse( o.responseText );
      
        var pid = messages.id;

        var sCode = messages.statusCode;
        var sMessage = messages.statusMessage;
    
        //YAHOO.util.Dom.get("pub-det-edit_pub_pmid").value = pmid;
        //YAHOO.util.Dom.get("pub-det-edit_pub_doi").value = doi;
        //YAHOO.util.Dom.get("pub-det-edit_pub_journalSpecific").value = jsp;
   
        if( sCode > 0){         
          YAHOO.mbi.modal.exception( sCode, sMessage );
        }
      }
    }catch( x ){
      alert("AJAX Error(infoUpdate): " + x );
    }
  },
    
  infoUpdateFail: function( o ){
    alert( "AJAX Error update failed: id=" + o.argument.id ); 
  },

  groupRole: function( op ){
    
    var setRoleCallback = { cache:false, timeout: 5000, 
                            success: YAHOO.imex.groupedit.roleUpdate,
                            failure: YAHOO.imex.groupedit.roleUpdateFail,
                            argument:{ id:YAHOO.imex.groupedit.uid } };
    try{    
      if( op === 'add' ){
        var req = 'op.radd=add' + 
                  '&id=' + YAHOO.imex.groupedit.gid +
                  '&opp.radd=' + YAHOO.util.Dom.get("groupedit_opp_radd").value
        
        YAHOO.util.Connect.asyncRequest( 'GET', 
                                         'groupedit?' + encodeURI( req ),
                                         setRoleCallback );
      }
    
      if( op === 'drop' ){
        var drops = YAHOO.util.Dom.getElementsByClassName("group-role-drop");
        var rdel = ",";
    
        for( var i = 0; i < drops.length; i++ ) {
          if( drops[i].checked ) {
            rdel = rdel + drops[i].value + ",";
          }
        } 

        if( rdel === "," ){   
          if( drops.checked ){
            rdel = rdel + drops.value + ",";
          }
        }

        if( rdel !== "," ){   
          var rdel = rdel.substring(1,rdel.length-1)
          var req = 'op.rdel=update' +
                    '&id=' + YAHOO.imex.groupedit.gid +
                    '&opp.rdel=' + rdel
                    
          YAHOO.util.Connect.asyncRequest( 'GET', 
                                           'groupedit?' + encodeURI( req ),
                                            setRoleCallback );
        }
      }
    }catch( x ){
      alert( "AJAX Error(groupRole): " + x );
    }   
    return false; 
  },

  roleUpdate: function( o ){
    try{
      if( YAHOO.imex.groupedit.aclErrorTest( o.responseText ) == false ){
                
        var messages = YAHOO.lang.JSON.parse( o.responseText );

        var lgr = YAHOO.util.Dom.get( "li-group-role" );
        var nih = "";

        for( var i = 0; i < messages.group.roles.length; i++ ) {
                    
          nih = nih + '<input type="checkbox" id="groupedit_opp_rdel" value="' + 
                 messages.group.roles[i].id +
                 '" name="opp.rdel" class="group-role-drop">';
                
          nih = nih + messages.group.roles[i].name;
        }              
        lgr.innerHTML = nih;
         YAHOO.util.Dom.get("groupedit_opp_radd").value =-1;
      }
    }catch( x ){
      alert("AJAX Error(roleUpdate): " + x );
    }
  },

  roleUpdateFail: function ( o ) {
    alert( "AJAX Error roleUpdate update failed: id=" + o.argument.id ); 
  },

  historyInit: function(){
       
    var bookmarkedTabViewState = YAHOO.util.History.getBookmarkedState("groupedit");
    var initialTabViewState = bookmarkedTabViewState || "tab0";
       
    var tabManager = YAHOO.imex.groupedit;
       
    YAHOO.util.History.register( "groupedit", initialTabViewState, this.historyReadyHandler );
        
    YAHOO.util.History.onReady( tabManager.historyReadyHandler );
        
    try{
          YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
    }catch( x ){
      // The only exception that gets thrown here is when the browser is
      // not supported (Opera, or not A-grade) Degrade gracefully.
          alert(x);
    }
  },

  historyReadyHandler: function(){
    var tabManager = YAHOO.imex.groupedit;

    var state =  YAHOO.util.History.getCurrentState("groupedit"); 
	tabManager.tabView.set("activeIndex", state.substr(3)); 
    document.getElementsByClassName("selected")[0].firstChild.focus();
  },

  handleHistoryNavigation: function( state ){
    var tabManager = YAHOO.imex.groupedit;
    var currentState, newState,newTab;

    var newTab =  tabManager.tabView.getTabIndex( state.newValue );       
    if( newTab === null ){
      YAHOO.util.History.navigate("groupedit", "tab0");
    }

    var newState = "tab" + newTab;
        
    try{
      currentState = YAHOO.util.History.getCurrentState( "groupedit" );
      if( newState != currentState && newState != "tabnull" ){
        YAHOO.util.History.navigate( "groupedit", newState );
      }
    }catch( e ){
      alert( e );
    } 
  },
     
  aclErrorTest: function( responseText ){
    var acl = /ACL Violation/; 
    if( acl.test( responseText ) ){
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
