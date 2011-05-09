YAHOO.namespace("imex");

YAHOO.imex.attedit = {

    pubId: 0,
    stateButton: null,
    init: function( e, obj ) {

        // main tab panel
        //---------------

        YAHOO.imex.attedit.tabs = new YAHOO.widget.TabView("pubTab");
        
        YAHOO.imex.attedit.pubId = obj.id;
        YAHOO.imex.attedit.login = obj.login;
        
        if( obj.login === "XXXX" ){
            var commAddButton = YAHOO.util.Dom.get("cmtmgr_op_ecadd");
            commAddButton.disabled=true;
        }

        var countCallback = { cache:false, timeout: 5000, 
                              success: YAHOO.imex.attedit.attachInitList,
                              failure: YAHOO.imex.attedit.attachInitListFail
                            };   
        try{
            YAHOO.util.Connect
                .asyncRequest( 'GET', 
                               'attachmgr?op.ccnt=ccnt&id=' + YAHOO.imex.pubedit.pubId, 
                               countCallback );        
        } catch (x) {
            alert("AJAX Error:"+x);
        }
       
    },

    attachInitList: function( o ){
        
        var messages = YAHOO.lang.JSON.parse( o.responseText);
        var tbvhandle = YAHOO.util.Dom.get( "com-tbview" );

        if( tbvhandle === undefined ) return;
        
        if( messages.attachMeta.total === 0 ){
            YAHOO.util.Dom.addClass( tbvhandle, "yui-hidden" );
        } else {

            YAHOO.util.Dom.removeClass( tbvhandle, "yui-hidden" );
            
            YAHOO.imex.attedit.comDtaSrc 
                = new YAHOO.util.DataSource( "attachmgr?op.calg=calg&id=" 
                                             + YAHOO.imex.pubedit.pubId ); 
            YAHOO.imex.attedit.comDtaSrc.responseType 
                = YAHOO.util.DataSource.TYPE_JSON; 
        
            YAHOO.imex.attedit.comDtaSrc.responseSchema = {
                resultsList: "attach",
                fields: ["id","root","subject","body","date","author"]    
            };

            
            this.mySubFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("body") === "" ){ 
                    elLiner.innerHTML = oRecord.getData("subject");
                } else {
                    /*
                    elLiner.innerHTML = oRecord.getData("subject") 
                        + ' (<a href="attachmgr?op.cidg=cidg'
                        + '&opp.cid=' + oRecord.getData("id")
                        + '&id=' + oRecord.getData("root")
                        + '">more</a>)';
                     */
                    
                    elLiner.innerHTML = oRecord.getData("subject") 
                        + ' (<a onclick="YAHOO.mbi.modal.attachment({'
                        + 'rid:' + oRecord.getData("root")
                        + ',aid:' + oRecord.getData("id")
                        + '}); return false;" href="" >more</a>)';
                    
                }
            }; 

            YAHOO.widget.DataTable.Formatter.subject = this.mySubFormatter; 

            YAHOO.imex.attedit.comColDef = [
                //{key:"id", label:"ID"},
                {key:"author",width:100,label:"Author" },
                {key:"date", width:240, label:"Date" },
                {key:"subject", width:650, label:"Subject", formatter:"subject" }
            ];
        
            YAHOO.imex.attedit.comTable 
                = new YAHOO.widget.DataTable("com-tbview",
                                             YAHOO.imex.attedit.comColDef, 
                                             YAHOO.imex.attedit.comDtaSrc,
                                             { scrollable:true, 
                                               width:"100%", 
                                               height: "10em" });
            
            YAHOO.imex.attedit.tabs.getTab(2).addListener(
                "click", 
                function(){ YAHOO.imex.attedit.comTable.onShow();} );
        }  
    },

    
    attachInitListFail: function( o ){
        alert(o);  
    },

    
    pubAttach: function( op ){
        
        var sub = YAHOO.util.Dom.get("cmtmgr_opp_encs").value;
        var bdy = YAHOO.util.Dom.get("cmtmgr_opp_encb").value;

        var attachCallback = { cache:false, timeout: 5000, 
                               success: YAHOO.imex.attedit.attachUpdate,
                               failure: YAHOO.imex.attedit.attachUpdateFail
                                };   
        try{
            var eSub=  encodeURIComponent( sub );
            var eBdy=  encodeURIComponent( bdy );
            
            var postMsg = 'op.eca=add&id=' 
                + YAHOO.imex.pubedit.pubId 
                + "&opp.encs=" + eSub 
                + "&opp.encb=" + eBdy;
            
            YAHOO.util.Connect
                .asyncRequest( 'POST', 
                               'attachmgr', attachCallback, postMsg );

        } catch (x) {
            alert("AJAX Error:"+x);
        }
        
        YAHOO.util.Dom.get("cmtmgr_opp_encs").value="";
        YAHOO.util.Dom.get("cmtmgr_opp_encb").value="";

        return false;
    },

    attachUpdate: function(){
        try{
            var dtb = YAHOO.imex.attedit.comTable;
            dtb.getDataSource().sendRequest( '',
                                             { success: dtb.onDataReturnInitializeTable,
                                               scope: dtb });
        } catch (x) {
            alert(x);
        }
    },

    attachUpdateFail: function(){
          YAHOO.imex.attedit.comTable.onShow();
    },

    comRender: function() {
        alert("comrender");
        var cpanel = YAHOO.imex.attedit.comPanel;
       
        var t = cpanel.getUnitByPosition( 'top' );

        t.set('body','<textarea rows="15" cols="80"/>');

        var c = cpanel.getUnitByPosition( 'center' );
        c.set('body','<textarea rows="5" cols="80"/>');
    }
};


