YAHOO.namespace("imex");

YAHOO.imex.adi = {
    pubId: 0,
    stateButton: null,
    conf: {},

    init: function( e, obj ){
              
        mys = YAHOO.imex.adi;
        mys.setFormatters();

        var adid = obj.adid; 

        
        mys.conf.tabs = YAHOO.imex.pubedit.tabs;
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

        console.log( "obj.adid: " + JSON.stringify(obj.adid));
        console.log( "mys.conf: " + mys.conf[obj.adid]);
        console.log( "mys.conf[].meta: " + mys.conf[obj.adid].meta);
                
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

             YAHOO.imex.adi.initTitleCount( adid );

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

    },

    pubAttach: function( aclass, op ){ // NOTE: hardwired submit form
        
        if( aclass !== "adata" ){
            var sub = YAHOO.util.Dom.get("cmtmgr_opp_encs").value;
            var bdy = YAHOO.util.Dom.get("cmtmgr_opp_encb").value;
            var flg = YAHOO.util.Dom.get("cmtmgr_opp_encf").value;
            
            var attachCallback = { cache:false, timeout: 5000, 
                                   success: YAHOO.imex.adi.attachUpdate,
                                   failure: YAHOO.imex.adi.attachUpdateFail,
                                   argument: {aclass: aclass }
                                 };   
            try{
                var eSub=  encodeURIComponent( sub );
                var eBdy=  encodeURIComponent( bdy );
                
                var postMsg = 'op.eca=add&id=' 
                    + YAHOO.imex.pubedit.pubId 
                    + "&opp.encs=" + eSub 
                    + "&opp.encb=" + eBdy 
                    + "&opp.encbt=WIKI"  
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
        }

        var sub = YAHOO.util.Dom.get("cmtmgr_opp_edan").value;
        var fmt = YAHOO.util.Dom.get("cmtmgr_opp_edat").value;
        var flg = YAHOO.util.Dom.get("cmtmgr_opp_edaf").value;
        var file = YAHOO.util.Dom.get("cmtmgr_opp_edafile").value;

        return false;
    },

    attachUpdate: function( o ) {
                                   
        try{
            var edit = YAHOO.imex.adi;            
            var aclass = o.argument.aclass;
            
            var dtb = edit.conf[aclass].dtaTable;
            
            dtb.getDataSource().sendRequest( 
                '',
                { success: dtb.onDataReturnInitializeTable,
                  scope: dtb });
            edit.attachReload({'argument':aclass});
            edit.addTitleCount( dtb.getRecordSet().getLength() + 1,
                                YAHOO.util.Dom.get( edit.conf[aclass].pane).id );
        }catch(ex){
            alert( "AJAX Error: " + ex );
        }
    },
    
    attachUpdateFail: function(o) {
        YAHOO.imex.adi.conf[o.argument.aclass].tbview.onShow();
    },

    attachReload: function( o ) {
        var aclass = o.argument;
        YAHOO.imex.adi.conf[aclass].dtaTable.load(); 
    },

    addTitleCount: function( count, id ){
		tabs = YAHOO.imex.pubedit.tabs.get('tabs');
		for( var i = 0;i < tabs.length; i++ ){
			var currentElement = tabs[i].get('contentEl');
			if( currentElement.querySelector('#' + id)  !== null ){
                var label = tabs[i].get('label');
				if( label.indexOf('(') > 0)
					label = label.slice(0, label.indexOf('(') - 1);
				tabs[i].set('label', label +' (' + count + ')' );
				return
			}
		}		
	},

    initTitleCount: function( aclass ){
        
        var url = YAHOO.imex.adi.conf[aclass].url +
                  YAHOO.imex.adi.conf[aclass].pubid;

        var cback = { success: YAHOO.imex.adi.itcSuccess,
                      failure: YAHOO.imex.adi.itcFailure,
                      argument: {'aclass':aclass} };
                      
        var tr = YAHOO.util.Connect.asyncRequest('GET', url, cback, null);

    },

    itcSuccess: function( o ){
       try{
          //alert(JSON.stringify(o.argument['aclass']))
          var aclass = o.argument['aclass'];
          var dta =  JSON.parse( o.responseText );
          var dom = YAHOO.util.Dom.get( YAHOO.imex.adi.conf[aclass].pane).id;
          //alert( dta.attach.length  );
          if( dta.attach.length > 0 ){
             YAHOO.imex.adi.addTitleCount( dta.attach.length, dom );
          }
       }catch( ex ){
          //alert( "AJAX Error: " + ex );
       }                             
    },
    
    itcFailure: function( o ){
      //alert( "itcFailure: " + JSON.stringify(o) );   
    }

};
