YAHOO.namespace("imex");


YAHOO.imex.util = {
  copyField : function( inField, outField ) {
        
        var h = YAHOO.util.Dom.get( inField );
        if(h) {
            var opp = YAHOO.util.Dom.get( outField );
            if( opp ) {
                opp.value = h.value; 
            }
        }
  }
};

YAHOO.imex.calendar = {
    calDialog : null,
    cal : null, 
    dtf : null, 
    
    init : function(e, obj ) {

        YAHOO.imex.util.copyField( "pubedit_pub_expectedPubDateStr", "pubedit_opp_epd" );        
        YAHOO.imex.util.copyField( "pubedit_pub_pubDateStr", "pubedit_opp_pd" );
        YAHOO.imex.util.copyField( "pubedit_pub_releaseDateStr", "pubedit_opp_rd" );

        YAHOO.util.Event.on( YAHOO.util.Dom.get( "epd-show"), "click", 
                             YAHOO.imex.calendar.open, 
                             { button:"epd-show", 
                               date:"pubedit_opp_epd"});
        YAHOO.util.Event.on( YAHOO.util.Dom.get( "pd-show"), "click", 
                             YAHOO.imex.calendar.open, 
                             { button:"pd-show", 
                             date:"pubedit_opp_pd"} );
        YAHOO.util.Event.on( YAHOO.util.Dom.get( "rd-show"), "click", 
                             YAHOO.imex.calendar.open, 
                             { button:"rd-show",
                               date:"pubedit_opp_rd"});
    },

    close : function(e) {
        YAHOO.imex.calendar.calDialog.hide();
        YAHOO.imex.calendar.dtf = null;
    },

    reset : function(e) {
        // Reset the current calendar page to the select date, or 
        // to today if nothing is selected.
        var selDates = YAHOO.imex.calendar.cal.getSelectedDates();
        var resetDate;
        
        if (selDates.length > 0) {
            resetDate = selDates[0];
        } else {
            resetDate = YAHOO.imex.calendar.cal.today;
        }
        
        YAHOO.imex.calendar.cal.cfg.setProperty( "pagedate", resetDate );
        YAHOO.imex.calendar.cal.render();
    },

    clear : function(e) {
        YAHOO.imex.calendar.cal.deselectAll();
        YAHOO.imex.calendar.cal.render();        
        if( YAHOO.imex.calendar.dtf ) {
            YAHOO.imex.calendar.dtf.value = "0000/00/00";
        }
        YAHOO.imex.calendar.calDialog.hide();
        YAHOO.imex.calendar.dtf = null;
    },

    select : function( e, par ) {        
        YAHOO.imex.calendar.calDialog.hide();        
        if ( YAHOO.imex.calendar.cal.getSelectedDates().length > 0 ) {
            var selDate = YAHOO.imex.calendar.cal.getSelectedDates()[0];
            
            var dStr = selDate.getDate();
            if ( dStr < 10 ) {
                dStr = "0" + dStr;
            }

            var mStr = selDate.getMonth()+1;
            if ( mStr < 10 ) {
                mStr = "0" + mStr;
            }
            var yStr = selDate.getFullYear();
            
            YAHOO.imex.calendar.dtf.value = yStr  + "/" + mStr + "/" + dStr;
        } else {
            YAHOO.imex.calendar.dtf.value = "0000/00/00";
        }
        YAHOO.imex.calendar.calDialog.hide();
        YAHOO.imex.calendar.dtf = null;
    },
    
    // date/calendar setup
    //--------------------
    
    open : function( e, obj ) {
        try{
            
            var btn = YAHOO.util.Dom.get( obj.button );
            var dtf = YAHOO.util.Dom.get( obj.date );
            YAHOO.imex.calendar.dtf = dtf;            
            
            if( !YAHOO.imex.calendar.calDialog ) {
                
                YAHOO.imex.calendar.calDialog = new YAHOO.widget.Dialog(
                    "container", 
                    {visible:false,
                     context:[btn, "tl", "bl"],
                     buttons:[ { text:"Clear",
                                 handler: YAHOO.imex.calendar.clear }], 
                     //          { text:"Close", isDefault:true,
                     //            handler: YAHOO.imex.calendar.close }],
                     draggable:false,
                     close:true
                    });               
                
                YAHOO.imex.calendar.calDialog.setHeader('Pick A Date');
                YAHOO.imex.calendar.calDialog.setBody('<div id="cal"></div>');
                YAHOO.imex.calendar.calDialog.render( document.body );
            
                YAHOO.imex.calendar.calDialog.showEvent.subscribe(
                    function() {
                        if (YAHOO.env.ua.ie) {
                            YAHOO.imex.calendar.calDialog.fireEvent(
                                "changeContent");
                        }
                    });
            } else {
                YAHOO.imex.calendar.calDialog.cfg.setProperty( 
                    "context", [btn,"tl", "bl"] );
            }

            if (!YAHOO.imex.calendar.cal ) {
            
                YAHOO.imex.calendar.cal = new YAHOO.widget.Calendar(
                    "cal", 
                    { iframe:false, hide_blank_weeks:true } );
                

                YAHOO.imex.calendar.cal.cfg.setProperty("DATE_FIELD_DELIMITER","/");
                YAHOO.imex.calendar.cal.cfg.setProperty("DATE_RANGE_DELIMITER","-");
                YAHOO.imex.calendar.cal.cfg.setProperty("MDY_YEAR_POSITION",1);
                YAHOO.imex.calendar.cal.cfg.setProperty("MDY_MONTH_POSITION",2);
                YAHOO.imex.calendar.cal.cfg.setProperty("MDY_DAY_POSITION",3);
                
                YAHOO.imex.calendar.cal.cfg.setProperty("MD_MONTH_POSITION",1);
                YAHOO.imex.calendar.cal.cfg.setProperty("MD_DAY_POSITION",2);
                
                YAHOO.imex.calendar.cal.render();
            
                YAHOO.imex.calendar.cal.selectEvent.subscribe( 
                    YAHOO.imex.calendar.select, "foo" );
                
            
                YAHOO.imex.calendar.cal.renderEvent.subscribe(
                    function() {
                        YAHOO.imex.calendar.calDialog.fireEvent("changeContent");
                    });
            }

            
            if ( dtf.value !== "0000/00/00" && dtf.value !== "" ) {
                YAHOO.imex.calendar.cal.cfg.setProperty(
                    "selected", dtf.value );
            } else {
                YAHOO.imex.calendar.cal.deselectAll();
                YAHOO.imex.calendar.cal.render();
            }
            
            var seldate = YAHOO.imex.calendar.cal.getSelectedDates();
        
            if (seldate.length > 0) {
                
                YAHOO.imex.calendar.cal.cfg.setProperty("pagedate", seldate[0]);
                YAHOO.imex.calendar.cal.render();
            }
        
            YAHOO.imex.calendar.calDialog.show();    

        } catch ( ex ) {
            alert("YUI Exception: " + ex );
        }        
    }   
};



YAHOO.imex.pubedit = {

    pubId: 0,
    stateButton: null,
    init: function( e, obj ) {

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
            
            var oMenuItem = event.newValue;
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
        };
        
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

        var stateUpdate = function ( o ) {
            var messages = YAHOO.lang.JSON.parse( o.responseText );
            var pid = messages.id;
            var imexACC = messages.pub.imexId;
            o.argument.btn.set("label",imexACC);
            o.argument.btn.set("disabled",true);        
        };
        
        var stateUpdateFail = function ( o ) {
            alert( "AJAX Error update failed: id=" + o.argument.id ); 
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
                                   "pubmgr?format=json"  
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
                                   + '&opp.necm=' + YAHOO.util.Dom.get("pubedit_opp_ecm").value, 
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
                                   + '&opp.epd=' + YAHOO.util.Dom.get("pubedit_opp_epd").value
                                   + '&opp.pd=' + YAHOO.util.Dom.get("pubedit_opp_pd").value
                                   + '&opp.rd=' + YAHOO.util.Dom.get("pubedit_opp_rd").value, 
                                   setDateCallback );
            } 

        } catch (x) {
            alert("AJAX Error: " + x );
        }   
        return false; 
    },


    pubAdminUser: function(op) {
        
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
                                   + '&opp.eauadd=' + YAHOO.util.Dom.get("pubedit_opp_eauadd").value,
                                   setAdminUserCallback );
            } 
            
            if( op === 'drop' ) {

                var drops = YAHOO.util.Dom.getElementsByClassName("admin-user-drop");
                var eaudel = ",";
                
                for( var i = 0; i < drops.length; i++ ) {
                    //alert("val=" + drops[i].value + "chk=" + drops[i].checked);
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

    
    adminUpdate: function( o ) {

        try {
            var acl = /ACL Violation/; 
            if( acl.test( o.responseText ) ) {
                YAHOO.mbi.modal.spcstat("ACL Violation");
            } else {
                
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                var tau = YAHOO.util.Dom.get( "td-admin-user" );
                               
                var nih = "";

                for( var i = 0; i < messages.pub.adminUsers.length; i++ ) {
                    
                    nih = nih + '<input type="checkbox" id="pubedit_opp_eaudel" value="' + 
                        messages.pub.adminUsers[i].id +
                        '" name="opp.eaudel">';

                    nih = nih + '<input type="hidden" value="' + 
                        messages.pub.adminUsers[i].id + 
                        '" name="__checkbox_opp.eaudel" id="__checkbox_pubedit_opp_eaudel">';

                    nih = nih + messages.pub.adminUsers[i].login;
                }              
                tau.innerHTML = nih;
                YAHOO.util.Dom.get("pubedit_opp_eauadd").value ="";
            }
        } catch(x) {
            alert("AJAX Error: " + x );
        }
    },

    stateUpdate: function( o ) { 
        try {
            var acl = /ACL Violation/; 
            if( acl.test(o.responseText) ) {
                YAHOO.mbi.modal.spcstat("ACL Violation");
            } else {
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                var stl = YAHOO.util.Dom.get( "state-label" );
                stl.innerHTML = messages.pub.state.name;
                
                var cem = YAHOO.util.Dom.get( "pubedit_opp_ecm" );
                var cem_val = messages.pub.contactEmail;               
                cem.value = cem_val;

                var pd = YAHOO.util.Dom.get( "pubedit_opp_pd" );
                var pd_val = messages.pub.pubDateStr;               
                pd.value = pd_val;

                var epd = YAHOO.util.Dom.get( "pubedit_opp_epd" );
                var epd_val = messages.pub.expectedPubDateStr;               
                epd.value = epd_val;

                var rd = YAHOO.util.Dom.get( "pubedit_opp_rd" );
                var rd_val = messages.pub.releaseDateStr;               
                rd.value = rd_val;                
            }
        } catch(x) {
            alert("AJAX Error: " + x );
        }
    },

    stateUpdateFail: function( o ) {
        alert(o.responseText);
    },

    updateFail: function( o ) {
        alert(o.responseText);
    }
};


