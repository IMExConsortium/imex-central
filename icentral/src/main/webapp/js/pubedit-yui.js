YAHOO.namespace("imex");

YAHOO.imex.pubedit = {

    pubId: 0,
    pubPmid: null,
    pubJSpec: null,
    stateButton: null,
    prefs: null,
    aidx: -1,
    subCnt: 14,

    init: function( e, obj ){

        //console.log( obj.prefs );

        var myself = YAHOO.imex.pubedit;
        
        myself.pubPmid = obj.pmid;
        myself.pubJSpec = obj.jSpec;

        if( obj.prefs != null && obj.prefs != "" ){
            var p = obj.prefs.replace(/&quot;/g, '"'); 
            console.log( p );
            myself.prefs = YAHOO.lang.JSON.parse( p  );
            myself["curl"] 
                = myself.prefs["option-def"]["curation-tool"]["option-def"]["curation-url"].value; 
            
            myself["cpat"] 
                = myself.prefs["option-def"]["curation-tool"]["option-def"]["curation-pmid-pattern"].value; 
        }

        // curator link

        if( myself.curl != undefined ){

            var a ="";

            if(  myself.pubPmid != null ){ 
                var href = myself.curl
                    .replace( myself.cpat, myself.pubPmid );
                a = ' [<a target="icentral_outlink" href="'+href+'">Curation Tool</a>]';
            } else {
                if(  myself.pubJSpec != null ){
                    var href = myself.curl
                        .replace( myself.cpat, myself.pubJSpec );
                    a = ' [<a target="icentral_outlink" href="'+href+'">Curation Tool</a>]';
                }
            }
            
            YAHOO.util.Dom.get("pub_ttl").innerHTML += a;

        }
        
        // activate submit buttons
        
        if( obj.login != undefined  && obj.login.length > 0 ){
            
            for(var i=0; i< myself.subCnt; i++ ){

                var s = YAHOO.util.Dom.get("sub-" + i );
                if( s != undefined ){
                    s.disabled=false;
                }                
            }
        } 
        
        // main tab panel
        //---------------

        console.log( "pubedit-yui.js: pubTab" );

        YAHOO.imex.pubedit.tabs = new YAHOO.widget.TabView("pubTab");
        //this listener is for when a tab is clicked. 
        YAHOO.imex.pubedit.tabs.addListener("activeTabChange", 
                                    YAHOO.imex.pubedit.handleHistoryNavigation); 

       
        YAHOO.imex.pubedit.tabs.addListener("activeTabChange", 
                                    YAHOO.imex.pubedit.refresh); 

        YAHOO.imex.pubedit.pubId = obj.id;
        
        YAHOO.imex.util.copyField( "pubedit_pub_owner_login", "pubedit_opp_neo" );             
  
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
        
        var onImexClick = function ( event ) {
            var imexUpdate = function ( o ) {
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                var pid = messages.id;
                var imexACC = messages.pub.imexId;
                o.argument.btn.set("label",imexACC);
                o.argument.btn.set("disabled",true);        
            };
            
            var imexUpdateFail = function ( o ) {
                alert( "AJAX Error update failed: id=" + o.argument.id ); 
            };

            try{
                
                //var oMenuItem = event.newValue;
                var imexCallback = { cache:false, timeout: 5000, 
                                     success: imexUpdate,
                                     failure: imexUpdateFail,
                                     argument:{ id:obj.id, btn:imexButton } };                  
                var r = confirm("Confirm IMEx Accession request ?");
                if ( r == true ) {
                    try{
                        YAHOO.util.Connect
                            .asyncRequest( 'GET', 
                                           'pubedit?op.epix=update&id=' + obj.id, 
                                           imexCallback );        
                    } catch (x) {
                        alert("AJAX Error:"+x);
                    }
                } else {
                    // do nothing
                }   
                
            } catch (x) {
                alert(x);
            } 
        };

                
        // new status 
        //-----------

        
        var stateSel = [ 
            { text: "NEW", value: "1" },
            { text: "RESERVED", value: "2" }
            //{ text: "INPROGRESS", value: "3" },
            //{ text: "RELEASED", value: "4" }, 
            //{ text: "DISCARDED", value: "5" }, 
            //{ text: "INCOMPLETE", value: "6" } 
        ]; 
        
        YAHOO.imex.pubedit.stateButton = new YAHOO.widget.Button(
            { id: "state-button",  
              name: "state-button", 
              label: "<em class=\"yui-button-label\">"+stateSel[0].text+"</em>", 
              type: "menu",   
              menu: stateSel,  
              container: "state-button-container" }); 
        
        //var hsv = YAHOO.util.Dom.get( "nsn" );
        //var hsv = stateSel[0].text;

        YAHOO.imex.pubedit.stateButton.my = { items: stateSel, 
                                              value:stateSel[0].value,  
                                              text:stateSel[0].text};
        YAHOO.imex.pubedit.stateButton.on( "selectedMenuItemChange", 
                                           onSelectedMenuItemChange);
        
        YAHOO.imex.pubedit.setTargetStates( obj.id, 
                                            YAHOO.imex.pubedit.stateButton );
        YAHOO.imex.pubedit.pubState('set');
        
        // current status
        //---------------
        
        //var curStateButton = new YAHOO.widget.Button(
        //    { id: "state-label",  
        //      name: "state-label", 
        //      label: "<em class=\"yui-button-label\">"+stateSel[0].text+"</em>", 
        //      type: "push",   
        //      disabled: true,
        //      container: "state-label-container" }); 
        
        
        // imex id
        //---------
        
        var imexButton = new YAHOO.widget.Button(
            { id: "imex-button",  
              name: "imex-button", 
              label: "<em class=\"yui-button-label\">"+"ASSIGN"+"</em>", 
              type: "push", 
              disabled: false,  
              menu: stateSel,  
              container: "imex-button-container" }); 
        
        if( obj.imexACC  !== undefined &&  obj.imexACC.length > 0 
            && obj.imexACC !== "N/A" ) {
                imexButton.set( "label", obj.imexACC );
                imexButton.set( "disabled", true );
            }
        
        imexButton.my = { items: stateSel, value: "" };
        imexButton.on("click", onImexClick );
        YAHOO.imex.pubedit.historyInit();

    },

    refresh: function ( o ) {
       console.log( "refresh: called" );
       var idx = YAHOO.imex.pubedit.tabs.get('activeIndex');
       console.log( "reloadTabPane: active index " + idx );

       if( idx !== YAHOO.imex.pubedit.aidx ){
           YAHOO.imex.pubedit.aidx = idx;            

           var id = YAHOO.imex.pubedit.pubId;
                     
           var refreshCallback = { cache:false, timeout: 5000, 
                                   success: YAHOO.imex.pubedit.recordUpdate,
                                   failure: YAHOO.imex.pubedit.recordUpdate };        
           try{
              YAHOO.util.Connect
                .asyncRequest( 'GET', 
                               'pubedit?format=JSON&id=' + id, 
                               refreshCallback );  
           } catch (x) {
              alert("AJAX Error: " + x );
           }
       }

    },  

    recordUpdate: function ( o ) {

       try{
          var messages = YAHOO.lang.JSON.parse( o.responseText );
          
          // summary tab
          //------------

          YAHOO.util.Dom.get('rec_owner').innerHTML = 
                "<b>Requested/Submitted By:</b> " + messages.pub.owner.login;

          YAHOO.util.Dom.get('rec_state').innerHTML = 
                "<b>Curation Status:</b> " + messages.pub.stage.name + "/" +  messages.pub.state.name;
          
          YAHOO.util.Dom.get('rec_imexid').innerHTML = 
                "<b>Imex ID:</b> " + messages.pub.imexId;

          if( messages.pub.source.title.length > 0 ){

             var newCite = messages.pub.source.title;

             if(  messages.pub.volume.length > 0 ){
                 newCite += ' <b>' + messages.pub.volume + '</b>';
             }
             if(  messages.pub.issue.length > 0 ){
                 newCite += '(' + messages.pub.issue + ')';
             }
             if(  messages.pub.pages.length > 0 ){
                 newCite += ':' + messages.pub.pages;
             }
             if(  messages.pub.year.length > 0 ){
                 newCite += ', ' + messages.pub.year;
             }
          
             YAHOO.util.Dom.get('rec_src_cite').innerHTML = newCite;          
          }
                 
          if( messages.pub.pmid.length > 0 ){
             
             var newPMID = '[PUBMED:'+
                        '<a target="icentral_outlink" href="http://www.ncbi.nlm.nih.gov/pubmed/'+
                        messages.pub.pmid + '">' + messages.pub.pmid + '</a>]';
          
             YAHOO.util.Dom.get('rec_src_pmid').innerHTML =  newPMID;
          }

          YAHOO.util.Dom.get('rec_src_author').innerHTML = 
                messages.pub.author;
          
          YAHOO.util.Dom.get('rec_src_abstract').innerHTML = 
                messages.pub.abstract;

          // publication update tab
          //----------------------- 

          YAHOO.util.Dom.get('pub-det-edit_pub_pmid').value = messages.pub.pmid;
          YAHOO.util.Dom.get('pub-det-edit_pub_doi').value = messages.pub.doi;
          YAHOO.util.Dom.get('pub-det-edit_pub_journalSpecific').value = messages.pub.journalSpecific;

          // NOTE: skipped journal title selection

          YAHOO.util.Dom.get('pub-det-edit_pub_author').value = messages.pub.author;
          YAHOO.util.Dom.get('pub-det-edit_pub_title').value = messages.pub.title;
          YAHOO.util.Dom.get('pub-det-edit_pub_abstract').value = messages.pub.abstract;
        
        } catch (x) {
            alert("AJAX Error: " + x );
        }

    },

    setTargetStates: function ( id, stateButton ) {
            
        var targetStateCallback = { cache:false, timeout: 5000, 
                                    success: YAHOO.imex.pubedit.targetStateUpdate,
                                    failure: YAHOO.imex.pubedit.targetStateUpdateFail,
                                    argument:{ id:id, btn:stateButton } };        
        try{
            YAHOO.util.Connect
                .asyncRequest( 'GET', 
                               'pubedit?op.etsl=all&id=' + id, 
                               targetStateCallback );  
        } catch (x) {
            alert("AJAX Error: " + x );
        }   
    },
    
    targetStateUpdate: function ( o ) {
        try{            
            var messages = YAHOO.lang.JSON.parse( o.responseText );
                
            var tsList = [];
            for( var i = 0; i < messages.targetStates.length; i++ ) {
                tsList[i]= {text: messages.targetStates[i], value:i};
            }
            
            var btnMenu = o.argument.btn.getMenu();
            if( YAHOO.util.Dom.inDocument(btnMenu.element) ) {  //rendered   
                btnMenu.clearContent();
                btnMenu.addItems( tsList );
                btnMenu.render();
            } else {  // unrenedered
                btnMenu.itemData = tsList;
            }
        } catch ( x ) {
            alert("AJAX Error: " + x );
        }        
    },
        
    targetStateUpdateFail: function ( o ) {
        alert("AJAX Error:  Update of the target state list failed");
    },

    identUpdate: function ( o ) {
        try {
            if( YAHOO.imex.pubedit.aclErrorTest( o.responseText ) == false ) {
                
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                var pid = messages.id;
                var pmid = messages.pub.pmid;
                var doi = messages.pub.doi;
                var jsp = messages.pub.journalSpecific;

                var sCode = messages.statusCode;
                var sMessage = messages.statusMessage;
                
                YAHOO.util.Dom.get("pub-det-edit_pub_pmid").value = pmid;
                YAHOO.util.Dom.get("pub-det-edit_pub_doi").value = doi;
                YAHOO.util.Dom.get("pub-det-edit_pub_journalSpecific").value = jsp;
           
                if( sCode > 0){                    
                    // pop up error modal                    
                    YAHOO.mbi.modal.exception( sCode, sMessage );
                } else {
                  // pop up update successful
                    YAHOO.mbi.modal.success( sCode, sMessage );
                }
            }
        } catch(x) {
            alert("AJAX Error: " + x );
        }
    },
    
    identUpdateFail: function ( o ) {
        alert( "AJAX Error update failed: id=" + o.argument.id ); 
    },

    pubmedUpdate: function ( o ) {
        try {
            if( YAHOO.imex.pubedit.aclErrorTest( o.responseText ) == false ) {
                
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                var pid = messages.id;
                var pmid = messages.pub.pmid;
                var author = messages.pub.author;
                var title = messages.pub.title;
                var abst = messages.pub.abstract;

                YAHOO.util.Dom.get("pub-det-edit_pub_pmid").value = pmid;
                YAHOO.util.Dom.get("pub-det-edit_pub_author").value = author;
                YAHOO.util.Dom.get("pub-det-edit_pub_title").value = title;
                YAHOO.util.Dom.get("pub-det-edit_pub_abstract").value = abst;
            }
        } catch(x) {
            alert("AJAX Error: " + x );
        }
    },

    pubmedUpdateFail: function ( o ) {
        alert( "AJAX Error pubmed update failed: id=" + o.argument.id ); 
    },
    
    epmrUpdate: function ( o ) {
        try {
            if( YAHOO.imex.pubedit.aclErrorTest( o.responseText ) == false ) {
                
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                var pid = messages.id;
                var pmid = messages.pub.pmid;
                var doi = messages.pub.doi;
                var author = messages.pub.author;
                var title = messages.pub.title;
                var abst = messages.pub.abstract;
                var sid = messages.pub.source.id;

                YAHOO.util.Dom.get("pub-det-edit_pub_pmid").value = pmid;
                YAHOO.util.Dom.get("pub-det-edit_pub_doi").value = doi;
                YAHOO.util.Dom.get("pub-det-edit_pub_author").value = author;
                YAHOO.util.Dom.get("pub-det-edit_pub_title").value = title;
                YAHOO.util.Dom.get("pub-det-edit_pub_abstract").value = abst;
                YAHOO.util.Dom.get("pub-det-edit_opp_jid").value = sid;

                //NOTE: add new sid/text to list of options if needed

            }
        } catch(x) {
            alert("AJAX Error: " + x );
        }
    },
    
    epmrUpdateFail: function ( o ) {
        alert( "AJAX Error pmid resync failed: id=" + o.argument.id ); 
    },

    authTitleUpdate: function ( o ) {
        try {
            if( YAHOO.imex.pubedit.aclErrorTest( o.responseText ) == false ) {
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                var pid = messages.id;
                var auth = messages.pub.author;
                var title = messages.pub.title;
                var ttl = title;
                if( ttl.length >78){
                    ttl = ttl.substring(0,75);
                    ttl = ttl.replace( /\.+$/, "" );
                    ttl = ttl+"...";
                }

                YAHOO.util.Dom.get("pub-det-edit_pub_author").value = auth;
                YAHOO.util.Dom.get("pub-det-edit_pub_title").value = title;

                var icid = "<em>"+ttl+"</em>";

                if( YAHOO.imex.pubedit.curl != undefined ){
                    var href = YAHOO.imex.pubedit.curl
                        .replace( YAHOO.imex.pubedit.cpat, messages.pub.pmid );
                    icid = icid + '[<a target="icentral_outlink" href="'+href+'">Curation Tool</a>]';
                }


                YAHOO.util.Dom.get("pub_ttl").innerHTML = icid;
               
            }
        } catch(x) {
            alert("AJAX Error: " + x );
        }
    },
        
    authTitleUpdateFail: function ( o ) {
        alert( "AJAX Error update failed: id=" + o.argument.id ); 
    },
    
    pubState: function(op) {
        
        var setStateCallback = { cache:false, timeout: 5000, 
                                 success: YAHOO.imex.pubedit.stateUpdate,
                                 failure: YAHOO.imex.pubedit.stateUpdateFail,
                                 argument:{ id:YAHOO.imex.pubedit.pubId } };
        try{
            
            if( op === 'update' ) {
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'pubedit?op.esup=update' 
                                   + '&id=' + YAHOO.imex.pubedit.pubId
                                   + '&opp.nsn=' + YAHOO.imex.pubedit.stateButton.my.text, 
                                   setStateCallback );
            } 

            if( op === 'set' ) {
                YAHOO.util.Connect
                    .asyncRequest( "GET", 
                                   "pubedit?format=json"  
                                   + "&id=" + YAHOO.imex.pubedit.pubId, 
                                   setStateCallback );  
            } 

        } catch (x) {
            alert("AJAX Error: " + x );
        }   
        return false; 
    },

    pubContactMail: function(op) {
        
        var setContactMailCallback = { cache:false, timeout: 5000, 
                                 success: YAHOO.imex.pubedit.stateUpdate,
                                 failure: YAHOO.imex.pubedit.stateUpdateFail,
                                 argument:{ id:YAHOO.imex.pubedit.pubId } };
        try{
            
            if( op === 'update' ) {
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'pubedit?op.emup=update' 
                                   + '&id=' + YAHOO.imex.pubedit.pubId
                                   + '&opp.necm=' + YAHOO.util.Dom.get("pub-stat-edit_opp_necm").value, 
                                   setContactMailCallback );
            } 

        } catch (x) {
            alert("AJAX Error: " + x );
        }   
        return false; 
    },

    pubDate: function(op) {
        
        var setDateCallback = { cache:false, timeout: 5000, 
                                success: YAHOO.imex.pubedit.stateUpdate,
                                failure: YAHOO.imex.pubedit.stateUpdateFail,
                                argument:{ id:YAHOO.imex.pubedit.pubId } };
        try{
            
            if( op === 'update' ) {
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'pubedit?op.edup=update' 
                                   + '&id=' + YAHOO.imex.pubedit.pubId
                                   + '&opp.epd=' + YAHOO.util.Dom.get("pub-stat-edit_opp_epd").value
                                   + '&opp.pd=' + YAHOO.util.Dom.get("pub-stat-edit_opp_pd").value
                                   + '&opp.rd=' + YAHOO.util.Dom.get("pub-stat-edit_opp_rd").value, 
                                   setDateCallback );
            } 

        } catch (x) {
            alert("AJAX Error: " + x );
        }   
        return false; 
    },

    pubIdent: function(op) {
        
        var pubIdentCallback = { cache:false, timeout: 5000, 
                                success: YAHOO.imex.pubedit.identUpdate,
                                failure: YAHOO.imex.pubedit.identUpdateFail,
                                argument:{ id:YAHOO.imex.pubedit.pubId } };

        var pubEpmrCallback = { cache:false, timeout: 5000, 
                                success: YAHOO.imex.pubedit.epmrUpdate,
                                failure: YAHOO.imex.pubedit.epmrUpdateFail,
                                argument:{ id:YAHOO.imex.pubedit.pubId } };
        try{

            //alert("op=" + op + " id=" + YAHOO.imex.pubedit.pubId);
 
            if( op === 'update' ) {
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'pubedit?op.eidu=update' 
                                   + '&id=' + YAHOO.imex.pubedit.pubId
                                   + '&opp.pmid=' + YAHOO.util.Dom.get("pub-det-edit_pub_pmid").value
                                   + '&opp.doi=' + YAHOO.util.Dom.get("pub-det-edit_pub_doi").value
                                   + '&opp.jsp=' + YAHOO.util.Dom.get("pub-det-edit_pub_journalSpecific").value, 
                                   pubIdentCallback );
            }

            if( op === 'epmr' ) {
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'pubedit?op.epmr=update' 
                                   + '&id=' + YAHOO.imex.pubedit.pubId
                                   + '&opp.pmid=' + YAHOO.util.Dom.get("pub-det-edit_pub_pmid").value, 
                                   pubEpmrCallback );
            }

        } catch (x) {
            alert("AJAX Error: " + x );
        }   
        return false; 
    },


    pubAuthTitle: function(op) {
        var pubAuthTitleCallback = { cache:false, timeout: 5000, 
                                     success: YAHOO.imex.pubedit.authTitleUpdate,
                                     failure: YAHOO.imex.pubedit.authTitleUpdateFail,
                                     argument:{ id:YAHOO.imex.pubedit.pubId } };
        try{
            
            if( op === 'update' ) {
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'pubedit?op.eatu=update' 
                                   + '&id=' + YAHOO.imex.pubedit.pubId
                                   + '&opp.ath=' + YAHOO.util.Dom.get("pub-det-edit_pub_author").value
                                   + '&opp.ttl=' + YAHOO.util.Dom.get("pub-det-edit_pub_title").value, 
                                   pubAuthTitleCallback );
            } 

        } catch (x) {
            alert("AJAX Error: " + x );
        }   
        return false; 
    },

    pubAdminUser: function( op ) {
        
        var setAdminUserCallback = { cache:false, timeout: 5000, 
                                     success: YAHOO.imex.pubedit.adminUpdate,
                                     failure: YAHOO.imex.pubedit.updateFail,
                                     argument:{ id:YAHOO.imex.pubedit.pubId } };
        try{
            if( op === 'add' ) {
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'pubedit?op.eauadd=update' 
                                   + '&id=' + YAHOO.imex.pubedit.pubId
                                   + '&opp.eauadd=' + YAHOO.util.Dom.get("pub-acc-edit_opp_eauadd").value,
                                   setAdminUserCallback );
            } 
            
            if( op === 'drop' ) {

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
                    YAHOO.util.Connect
                        .asyncRequest( 'GET', 
                                       'pubedit?op.eaudel=update' 
                                       + '&id=' + YAHOO.imex.pubedit.pubId
                                       + '&opp.eaudel=' + eaudel,
                                       setAdminUserCallback );
                }
            } 
            
        } catch (x) {
            alert("AJAX Error: " + x );
        }   
        return false; 
    },

    pubAdminGroup: function( op ) {
        
        var setAdminGroupCallback = { cache:false, timeout: 5000, 
                                      success: YAHOO.imex.pubedit.adminUpdate,
                                      failure: YAHOO.imex.pubedit.updateFail,
                                      argument:{ id:YAHOO.imex.pubedit.pubId } };
        try{
            if( op === 'add' ) {
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'pubedit?op.eagadd=update' 
                                   + '&id=' + YAHOO.imex.pubedit.pubId
                                   + '&opp.eagadd=' + YAHOO.util.Dom.get("pub-acc-edit_opp_eagadd").value,
                                   setAdminGroupCallback );
            } 
            
            if( op === 'drop' ) {

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
                    YAHOO.util.Connect
                        .asyncRequest( 'GET', 
                                       'pubedit?op.eagdel=update' 
                                       + '&id=' + YAHOO.imex.pubedit.pubId
                                       + '&opp.eagdel=' + eagdel,
                                       setAdminGroupCallback );
                }
            } 
            
        } catch (x) {
            alert("AJAX Error: " + x );
        }   
        return false; 
    },

    adminUpdate: function( o ) {
        try {
            if( YAHOO.imex.pubedit.aclErrorTest( o.responseText ) == false ) {
            
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                
                var tau = YAHOO.util.Dom.get( "td-admin-user" );
                var nih = "";

                for( var i = 0; i < messages.pub.adminUsers.length; i++ ) {
                    
                    nih = nih + '<input type="checkbox" id="pub-acc-edit_opp_eaudel" value="' + 
                        messages.pub.adminUsers[i].id +
                        '" name="opp.eaudel" class="admin-user-drop">';

                    nih = nih + '<input type="hidden" value="' + 
                        messages.pub.adminUsers[i].id + 
                        '" name="__checkbox_opp.eaudel" id="__checkbox_pub-acc-edit_opp_eaudel">';

                    nih = nih + messages.pub.adminUsers[i].login;
                }              
                tau.innerHTML = nih;
                YAHOO.util.Dom.get("pub-acc-edit_opp_eauadd").value ="";

                var tag = YAHOO.util.Dom.get( "td-admin-group" );
                var nig = "";

                for( var i = 0; i < messages.pub.adminGroups.length; i++ ) {
                    
                    nig = nig + '<input type="checkbox" id="pub-acc-edit_opp_eagdel" value="' + 
                        messages.pub.adminGroups[i].id +
                        '" name="opp.eagdel" class="admin-group-drop">';

                    nig = nig + '<input type="hidden" value="' + 
                        messages.pub.adminGroups[i].id + 
                        '" name="__checkbox_opp.eagdel" id="__checkbox_pub-acc-edit_opp_eagdel">';

                    nig = nig + messages.pub.adminGroups[i].label;
                }              
                tag.innerHTML = nig;
                //YAHOO.util.Dom.get("pubedit_opp_eauadd").value ="";

            }
        } catch(x) {
            alert("AJAX Error: " + x );
        }
    },

    stateUpdate: function( o ) { 
        try {
            if( YAHOO.imex.pubedit.aclErrorTest( o.responseText ) == false ) {
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                var stl = YAHOO.util.Dom.get( "state-label" );
                stl.innerHTML = messages.pub.stage.name+ "/" + messages.pub.state.name;
               
                var tcm = YAHOO.util.Dom.get("td-contact-mail");
                var nih = "";
                
                if( messages.pub.contactEmail.length > 0 ) {
                    nih = '<a id="cm-link" href="mailto:' + 
                        messages.pub.contactEmail + '">' +
                        messages.pub.contactEmail +'</a>' +
                        ' <i>change to</i> ';
                }

                nih = nih + '<input type="text" id="pub-stat-edit_opp_necm" ' +
                    'value="" maxlength="32" size="32" name="opp.necm">'; 
                tcm.innerHTML = nih;                

                //var cem = YAHOO.util.Dom.get( "pub-stat-edit_opp_ecm" );
                //var cem_val = messages.pub.contactEmail;               
                //cem.value = cem_val;

                var pd = YAHOO.util.Dom.get( "pub-stat-edit_opp_pd" );
                var pd_val = messages.pub.pubDateStr;               
                pd.value = pd_val;               

                var epd = YAHOO.util.Dom.get( "pub-stat-edit_opp_epd" );
                var epd_val = messages.pub.expectedPubDateStr;               
                epd.value = epd_val;

                var rd = YAHOO.util.Dom.get( "pub-stat-edit_opp_rd" );
                var rd_val = messages.pub.releaseDateStr;               
                rd.value = rd_val;                
            }
        } catch(x) {
            alert("AJAX Error: " + x );
        }
    },

    stateUpdateFail: function( o ){
        alert(o.responseText);
    },

    updateFail: function( o ){
        alert(o.responseText);
    },

    watchFlag: function( op ){
        if( op == 'update' ){
            var wfl = YAHOO.util.Dom.getElementsByClassName("watch-flag");
            var watch = wfl[0].checked;

            var setWatchFlagCallback = { cache:false, timeout: 5000, 
                                         success: YAHOO.imex.pubedit.watchUpdate,
                                         failure: YAHOO.imex.pubedit.updateFail,
                                         argument:{ chkbox:wfl[0] } };
            try{
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'watchmgr?op.wflu=update' 
                                   + '&id=' + YAHOO.imex.pubedit.pubId 
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

    historyInit: function(){
        //initialize the default state
        var bookmarkedTabViewState = YAHOO.util.History.getBookmarkedState("pubedit");
        var initialTabViewState = bookmarkedTabViewState || "tab0";
        //get the instance of the manager
        var tabManager = YAHOO.imex.pubedit;
        //register the module
        YAHOO.util.History.register("pubedit", initialTabViewState, this.historyReadyHandler);
        //makes sure the handler is called when the DOM is ready (?)
        YAHOO.util.History.onReady( tabManager.historyReadyHandler );
        // Initialize the browser history management library.
        try {
          YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
        } catch (x) {
          // The only exception that gets thrown here is when the browser is
          // not supported (Opera, or not A-grade) Degrade gracefully.
          alert(x);
        }
    },

    //Handles page back and forward as well as refreshes
    //as you can see there is no navigate() so it wont
    //load the page, run the js and load the page again
    historyReadyHandler: function(){
        var tabManager = YAHOO.imex.pubedit;
        //gets the cuurent state in the yui history. 
        var state =  YAHOO.util.History.getCurrentState("pubedit"); 
        //sets the active tab by index number
	tabManager.tabs.set("activeIndex", state.substr(3)); 
        //hack to remove focus from the last clicked button staying highlighted on back navigation
        document.getElementsByClassName("selected")[0].firstChild.focus();

    },
    // when a tab is clicked, navigate to the correct tab
    // the new tab is also loaded into the yui history.     
    handleHistoryNavigation: function(state){
        var tabManager = YAHOO.imex.pubedit;
        var currentState, newState,newTab;
        //gets the state of the tab from the object passed from the click listener
        var newTab =  tabManager.tabs.getTabIndex(state.newValue);
        if(newTab === null)
        {
            YAHOO.util.History.navigate("pubedit", "tab0");
        }
        var newState = "tab" + newTab;
        
        try {
            currentState = YAHOO.util.History.getCurrentState("pubedit");
            // The following test is crucial. Otherwise, we end up circling forever.
            // Indeed, YAHOO.util.History.navigate will call the module onStateChange
            // callback, which will call this handler and it keeps going from here...
            if (newState != currentState && newState != "tabnull") {
                YAHOO.util.History.navigate("pubedit", newState);
            }
        } catch (e) {
            alert(e);
        } 
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
