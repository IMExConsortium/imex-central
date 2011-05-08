YAHOO.namespace("imex");

YAHOO.imex.calendar = {
    calDialog : null,
    cal : null, 
    dtf : null, 
    
    init : function(e, obj ) {

        YAHOO.imex.util.copyField( "pub-stat-edit_pub_expectedPubDateStr", "pub-stat-edit_opp_epd" );        
        YAHOO.imex.util.copyField( "pub-stat-edit_pub_pubDateStr", "pub-stat-edit_opp_pd" );
        YAHOO.imex.util.copyField( "pub-stat-edit_pub_releaseDateStr", "pub-stat-edit_opp_rd" );

        YAHOO.util.Event.on( YAHOO.util.Dom.get( "epd-show"), "click", 
                             YAHOO.imex.calendar.open, 
                             { button:"epd-show", 
                               date:"pub-stat-edit_opp_epd"});
        YAHOO.util.Event.on( YAHOO.util.Dom.get( "pd-show"), "click", 
                             YAHOO.imex.calendar.open, 
                             { button:"pd-show", 
                             date:"pub-stat-edit_opp_pd"} );
        YAHOO.util.Event.on( YAHOO.util.Dom.get( "rd-show"), "click", 
                             YAHOO.imex.calendar.open, 
                             { button:"rd-show",
                               date:"pub-stat-edit_opp_rd"});
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
