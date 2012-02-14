YAHOO.namespace("imex");

YAHOO.imex.attedit = {

    pubId: 0,
    stateButton: null,
    init: function( e, obj ) {

        // main tab panel
        //---------------
        if( YAHOO.imex.attedit.conf === undefined ){
            YAHOO.imex.attedit.conf=[];
        }
        YAHOO.imex.attedit.conf[obj.aclass]=[];
     
        YAHOO.imex.attedit.conf[obj.aclass].tabs = YAHOO.imex.pubedit.tabs, 
        //new YAHOO.widget.TabView("pubTab");
        YAHOO.imex.attedit.conf[obj.aclass].tabno = obj.tabno;
        YAHOO.imex.attedit.conf[obj.aclass].pubId = obj.id;
        YAHOO.imex.attedit.conf[obj.aclass].login = obj.login;
        YAHOO.imex.attedit.conf[obj.aclass].url = obj.url;
        YAHOO.imex.attedit.conf[obj.aclass].apane = obj.apane;
        YAHOO.imex.attedit.conf[obj.aclass].cname = obj.cname;
        
        if( obj.login === "XXXX" ){
            var commAddButton = YAHOO.util.Dom.get("cmtmgr_op_ecadd");
            commAddButton.disabled=true;
        }

        var countCallback = { cache:false, timeout: 5000, 
                              success: YAHOO.imex.attedit.attachInitList,
                              failure: YAHOO.imex.attedit.attachInitListFail,
                              argument: obj.aclass
                            };   
        try{
            var url = YAHOO.imex.attedit.conf[obj.aclass].url 
                + YAHOO.imex.attedit.conf[obj.aclass].pubId;
            //alert(url);
            YAHOO.util.Connect.asyncRequest( 'GET', url, countCallback );        
        } catch( ex ) {
            alert( "AJAX Error:" + ex );
        }
    },

    attachInitList: function( o ){
        
        var aclass= o.argument;
        
        var messages = YAHOO.lang.JSON.parse( o.responseText);
        var tbvhandle = YAHOO.util.Dom.get( YAHOO.imex.attedit.conf[aclass].apane );
       
        if( tbvhandle === undefined ) return;
        if( messages.attachMeta.total === 0 ){
            YAHOO.util.Dom.addClass( tbvhandle, "yui-hidden" );
        } else {

            YAHOO.util.Dom.removeClass( tbvhandle, "yui-hidden" );
            
            var url = YAHOO.imex.attedit.conf[aclass].url 
                + YAHOO.imex.attedit.conf[aclass].pubId;
                        
            YAHOO.imex.attedit.conf[aclass].comDtaSrc 
                = new YAHOO.util.DataSource( url );
 
            YAHOO.imex.attedit.conf[aclass].comDtaSrc.maxCacheEntries=0;
 
            YAHOO.imex.attedit.conf[aclass].comDtaSrc.responseType 
                = YAHOO.util.DataSource.TYPE_JSON;

            var rsFields= ["id","root","subject","body","date","author"];             

            if(aclass === 'comment'){
                rsFields.push("flagName");
            }
            
            YAHOO.imex.attedit.conf[aclass].comDtaSrc.responseSchema = {
                resultsList: "attach",
                fields: rsFields
            };
            
          
            YAHOO.imex.attedit.conf[aclass].comColDef = [
                //{key:"id", label:"ID"},
                { key:"author",width:100,
                  label:YAHOO.imex.attedit.conf[aclass].cname["author"] },
                { key:"date", width:240, 
                  label:YAHOO.imex.attedit.conf[aclass].cname["date"] },
                { key:"subject", width:500, formatter:"subject",
                  label:YAHOO.imex.attedit.conf[aclass].cname["subject"] 
                }
            ];

            if(aclass === 'comment'){
              
                YAHOO.imex.attedit.conf[aclass]
                    .comColDef.push(
                        { key:"flagName", width:100, formatter:"flag", 
                          label:YAHOO.imex.attedit.conf[aclass].cname["flagName"] }
                    );
            }

            this.mySubFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("body") === "" ){ 
                    elLiner.innerHTML = oRecord.getData("subject");
                } else {
                    elLiner.innerHTML = oRecord.getData("subject") 
                        + ' (<a onclick="YAHOO.mbi.modal.attachment({'
                        + 'rid:' + oRecord.getData("root")
                        + ',aid:' + oRecord.getData("id")
                        + '}); return false;" href="" >more</a>)';
                }
            }; 
            
            this.myFlagFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("flagName") === undefined ||
                    oRecord.getData("flagName") === "" ){ 
                        elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center>' + oRecord.getData("flagName") + '</center>';
                    }
            }; 
            

            YAHOO.widget.DataTable.Formatter.subject = this.mySubFormatter; 
            YAHOO.widget.DataTable.Formatter.flag= this.myFlagFormatter; 

            YAHOO.imex.attedit.conf[aclass].comTable 
                = new YAHOO.widget.DataTable(YAHOO.imex.attedit.conf[aclass].apane,
                                             YAHOO.imex.attedit.conf[aclass].comColDef, 
                                             YAHOO.imex.attedit.conf[aclass].comDtaSrc,
                                             { scrollable:false,
                                               dynamicData : true,
                                               draggableColumns: true
                                             });
            
            var tabno = YAHOO.imex.attedit.conf[aclass].tabno;
            YAHOO.imex.attedit.conf[aclass].tabs.getTab(tabno).addListener(
                "click", 
                function(){
                    try{                        
                        var dtb = YAHOO.imex.attedit.conf[this].comTable;
                        dtb.getDataSource().sendRequest(
                            '',
                            {success: dtb.onDataReturnInitializeTable,
                             failure: alert,
                             scope: dtb});
                    } catch (x) {
                        alert("AJAX error:"+x);
                    }
                },
                aclass,
                true
            );
        }  
    },
    
    attachInitListFail: function( o ){
        alert( "InitListFail:" + o );  
    },
    
    pubPreview: function( aclass, op ){

        var sub = YAHOO.util.Dom.get("cmtmgr_opp_encs").value;
        var bdy = YAHOO.util.Dom.get("cmtmgr_opp_encb").value;
        var flg = YAHOO.util.Dom.get("cmtmgr_opp_encf").value;
        
        var prev = { subject: sub, body: bdy, "body-type": "WIKI",
                     author: "N/A", date: "N/A"};
 
        YAHOO.mbi.modal.attachment( {prev:prev} );
                                     
        return false;
    },

    pubAttach: function( aclass, op ){ // NOTE: hardwired submit form
        
        var sub = YAHOO.util.Dom.get("cmtmgr_opp_encs").value;
        var bdy = YAHOO.util.Dom.get("cmtmgr_opp_encb").value;
        var flg = YAHOO.util.Dom.get("cmtmgr_opp_encf").value;

        var attachCallback = { cache:false, timeout: 5000, 
                               success: YAHOO.imex.attedit.attachUpdate,
                               failure: YAHOO.imex.attedit.attachUpdateFail,
                               argument: {aclass: aclass }
                             };   
        try{
            var eSub=  encodeURIComponent( sub );
            var eBdy=  encodeURIComponent( bdy );
            
            var postMsg = 'op.eca=add&id=' 
                + YAHOO.imex.pubedit.pubId 
                + "&opp.encs=" + eSub 
                + "&opp.encb=" + eBdy 
                + "&opp.encbt=WIKI" +  
                + "&opp.encf=" + flg;
            
            YAHOO.util.Connect
                .asyncRequest( 'POST', 
                               'attachmgr', attachCallback, postMsg );

        } catch( ex ) {
            alert( "AJAX Error:" + ex );
        }
        
        YAHOO.util.Dom.get("cmtmgr_opp_encs").value="";
        YAHOO.util.Dom.get("cmtmgr_opp_encb").value="";

        return false;
    },

    attachUpdate: function( o ) {
        try{
            
            var dtb = YAHOO.imex.attedit.conf[o.argument.aclass].comTable;
            dtb.getDataSource().sendRequest( 
                '',
                { success: dtb.onDataReturnInitializeTable,
                  scope: dtb });
        }catch(ex){
            alert( "AJAX Error: " + ex );
        }
    },
    
    attachUpdateFail: function() {
        YAHOO.imex.attedit.conf[o.argument.aclass].comTable.onShow();
    },

    comRender: function() {
        //alert("comrender");
        var cpanel = YAHOO.imex.attedit.comPanel;
       
        var t = cpanel.getUnitByPosition( 'top' );
        t.set('body','<textarea rows="15" cols="80"/>');
        
        var c = cpanel.getUnitByPosition( 'center' );
        c.set('body','<textarea rows="5" cols="80"/>');
    }
};
