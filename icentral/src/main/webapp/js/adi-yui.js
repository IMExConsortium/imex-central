YAHOO.namespace("imex");

YAHOO.imex.adi = {
    pubId: 0,
    stateButton: null,
    conf: {},

    init: function( e, obj ){
              
        mys = YAHOO.imex.adi;
        mys.setFormatters();

        var adid = obj.adid; 
        
        mys.conf[obj.adid] = {};
        mys.conf[obj.adid].pane = obj.adipane;
        mys.conf[obj.adid].tbview = obj.aditable;
        mys.conf[obj.adid].tabno = obj.tabno;
        mys.conf[obj.adid].pubid = obj.id;
        mys.conf[obj.adid].login = obj.login;
        mys.conf[obj.adid].url = obj.url;
        mys.conf[obj.adid].meta = obj.meta;
        mys.conf[obj.adid].rschema = obj.rschema;
        mys.conf[obj.adid].coldefs = obj.coldefs;

        console.log("obj.adid: " + JSON.stringify(obj.adid));
        console.log("mys.conf: " + mys.conf[obj.adid]);
        console.log("mys.conf[].meta: " + mys.conf[obj.adid].meta);
                
        var countCallback = { cache:false, timeout: 5000, 
                              success: YAHOO.imex.adi.initList,
                              failure: YAHOO.imex.adi.initListFail,
                              argument: { "adid": adid }
                            };   
        try{
            var url = mys.conf[adid].url + mys.conf[adid].pubid;
            YAHOO.util.Connect.asyncRequest( 'GET', url, countCallback );        
        } catch( ex ) {
            alert( "AJAX Error:" + ex );
        }
    },

    initList: function( o ){
       try{
          
          console.log("initList: o.argument " + JSON.stringify(o.argument) );
          console.log("initList: o.argument.adid: " + o.argument.adid);

          var adid = o.argument.adid;
          var myconf = YAHOO.imex.adi.conf[adid];
        
          var meta = myconf.meta; 
          var pane = myconf.pane
          var opane = YAHOO.util.Dom.get( pane );

          //console.log( "o.responseText=" + o.responseText );
          
    	  try{
             var messages = YAHOO.lang.JSON.parse( o.responseText );

             console.log( "initList: meta=" + meta );
             console.log( "initList: pane=" + pane );
             console.log( "initList: opane=" + opane );
             console.log( "initList: total=" + messages[meta].total );
               
             if( messages[meta].total === 0 ){
                YAHOO.util.Dom.addClass( pane, "yui-hidden" );
                return;
             } else {
                YAHOO.util.Dom.removeClass( pane, "yui-hidden" );
             }                             

             myconf.dtaSrc 
                = new YAHOO.util.DataSource( myconf.url + myconf.pubid );
 
             myconf.dtaSrc.maxCacheEntries = 0;
 
             myconf.dtaSrc.responseType 
                = YAHOO.util.DataSource.TYPE_JSON;

             myconf.dtaSrc.responseSchema = myconf.rschema; 

             myconf.dtaTable 
                = new YAHOO.widget.DataTable( myconf.tbview,
                       myconf.coldefs, 
                       myconf.dtaSrc,
                       { scrollable:false,
                         dynamicData : true,
                         draggableColumns: true
                       });

             myconf.pubtab = YAHOO.imex.pubedit.tabs.getTab(myconf.tabno);
             console.log("myconf.pubtab: " + myconf.pubtab);

             if( myconf.pubtab !== undefined){
                console.log("adi: setting up tab listener");
                myconf.pubtab.addListener( "click", 
                   function(){
                      console.log("adi: calling up tab listener");
                      try{                        
                         var dtb = myconf.dtaTable;
                         dtb.getDataSource().sendRequest(
                            '',
                            {success: dtb.onDataReturnInitializeTable,
                             failure: alert,
                             scope: dtb});
                       }catch(x){
                          alert("AJAX error:"+x);
                       }
                   },
                   myconf,
                   true
                );
             }
          }catch( x ){
             console.log(x);
          }

       }catch( x ){
          console.log(x);
       }
    },
                       
    initListFail: function( o ){
        console.log( "initListFail:" + o );
    },

    setFormatters: function( o ){

       var myNameFormatter = function( elLiner, oRecord, oColumn, oData ) { 
             elLiner.innerHTML = oRecord.getData("name");
          };

       var myValueFormatter = function( elLiner, oRecord, oColumn, oData ) { 
             //elLiner.innerHTML = "<center>" + oRecord.getData("value") + "</center>";
             var val = parseFloat( Math.round(oRecord.getData("value") * 100) / 100).toFixed(2);
               elLiner.innerHTML = "<center>" + val + "</center>";
          };

       var mySubFormatter = function( elLiner, oRecord, oColumn, oData ) { 
             if( oRecord.getData("body") === "" ){ 
                    elLiner.innerHTML = oRecord.getData("subject");
             } else {
                    elLiner.innerHTML = oRecord.getData("subject") 
                        + ' [<a onclick="YAHOO.mbi.modal.attachment({'
                        + 'rid:' + oRecord.getData("root")
                        + ',aid:' + oRecord.getData("id")
                        + '}); return false;" href="" >more</a>]';
             }
          };
 
       var myASubFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
             if( oRecord.getData("body") === "" ){ 
                    elLiner.innerHTML = oRecord.getData("subject");
             } else {
                    elLiner.innerHTML = oRecord.getData("subject") 
                        + ' [<a onclick="YAHOO.mbi.modal.attachment({'
                        + 'atp:\'adata\', rid:' + oRecord.getData("root")
                        + ',aid:' + oRecord.getData("id")
                        + '}); return false;" href="" >view</a>]'
                        + '[<a href="attachmgr?op.aidg=aidg'
                        + '&id='+ oRecord.getData("root") 
                        + '&opp.aid='+ oRecord.getData("id")
                        + '&opp.rt=data">download</a>]';
             }
          }; 
            
       var myFlagFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("flagName") === undefined ||
                    oRecord.getData("flagName") === "" ){ 
                        elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center>' + oRecord.getData("flagName") + '</center>';
                    }
            }; 
         
       var myBTypeFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("bodyType") === undefined ||
                    oRecord.getData("bodyType") === "" ){ 
                        //elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center>' + oRecord.getData("bodyType") + '</center>';
                    }
            }; 
         
       var myDllFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("aid") === undefined ||
                    oRecord.getData("aid") === "" ){ 
                        elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center><a href=\"\" '
                            + 'onclick=\"YAHOO.imex.attedit.attachDrop({'
                            + 'rid:' + oRecord.getData("root") + ','
                            + 'aid:' + oRecord.getData("aid")
                            + '});return false;\">delete</a></center>';
                    }
            }; 

       var myTestFormatter = function( elLiner, oRecord, oColumn, oData ) { 
          console.log("data: " + JSON.stringify(oRecord) );
          elLiner.innerHTML = oData;
       };
       
       YAHOO.widget.DataTable.Formatter.adi_test = myTestFormatter; 
       YAHOO.widget.DataTable.Formatter.adi_name = myNameFormatter; 
       YAHOO.widget.DataTable.Formatter.adi_value = myValueFormatter; 

       YAHOO.widget.DataTable.Formatter.adi_subject = mySubFormatter; 
       YAHOO.widget.DataTable.Formatter.adi_asubject = myASubFormatter; 
       YAHOO.widget.DataTable.Formatter.adi_flag = myFlagFormatter; 
       YAHOO.widget.DataTable.Formatter.adi_btype = myBTypeFormatter; 
       YAHOO.widget.DataTable.Formatter.adi_dll = myDllFormatter; 

    }

};
