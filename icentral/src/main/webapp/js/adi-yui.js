YAHOO.namespace("imex");

YAHOO.imex.attedit = {

    pubId: 0,

    stateButton: null,

    init: function( e, obj ) {
       
       var myself = YAHOO.imex.attedit;

        YAHOO.widget.DataTable.Formatter.adi_subject = myself.mySubFormatter; 
        YAHOO.widget.DataTable.Formatter.adi_asubject = myself.myASubFormatter; 
        YAHOO.widget.DataTable.Formatter.adi_flag = myself.myFlagFormatter; 
        YAHOO.widget.DataTable.Formatter.adi_btype = myself.myBTypeFormatter; 
        YAHOO.widget.DataTable.Formatter.adi_dll = myself.myDllFormatter; 

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
        YAHOO.imex.attedit.conf[obj.aclass].spane = obj.spane;
        
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

    adiTable: function( o ){

       var tabNo = o.tabNo;
       var dth = o.datahandle;  
       var dmh = o.domhandle;   

       var url = o.url;     
       var pubId = o.pubId; 
       
       var aclass = o.aclass;

       dth.dtaSrc = new YAHOO.util.DataSource( url + pubId );
       dth.dtaSrc.maxCacheEntries = 0;
       dth.dtaSrc.responseType = YAHOO.util.DataSource.TYPE_JSON;
       dth.dtaSrc.responseSchema = o.rschema;  // ["id","root","subject","body","date","author"];             
       dth.colDefs = o.colDefs ;
       
       if( o.setCount === True ){
           try{
               YAHOO.imex.attedit.addTitleCount( messages.attach.length, 
                                YAHOO.util.Dom.get( YAHOO.imex.attedit.conf[aclass].apane).id);
           } catch( e ) {
               console.log( e );
           }
       }      
--------------------------------------------------------------------------------
       dth.table = new YAHOO.widget.DataTable(YAHOO.imex.attedit.conf[aclass].apane,
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




    },
  
    attachInitList: function( o ){
        
        var aclass= o.argument;
        
        var messages = YAHOO.lang.JSON.parse( o.responseText);

        var conf = YAHOO.util.Dom.get( YAHOO.imex.attedit.conf[aclass];

        var tbvhandle = YAHOO.util.Dom.get( conf.apane );
        var srchandle = YAHOO.util.Dom.get( conf.spane );
       
        if( tbvhandle === undefined ) return;

        // score table
        
                
        if( messages.scoreMeta.total === 0 ){
            YAHOO.util.Dom.addClass( scorehandle, "yui-hidden" );
        } else {
        
            srchandle.innerHTML = 
                "<h3 class='pub-edit-sect'>Scores</h3>" +        
                "<div id='sdata-tbview' class='tbview'></div>";                

            YAHOO.imex.attedit.adiTable({ handle: YAHOO.util.Dom.get('score-tbview'),
                                          url: conf.surl });    
        }          

        // attachment table

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

            if(aclass === 'adata'){
                rsFields.push("bodyType");
                rsFields.push("flagName");
                rsFields.push("aid");
            }
            
            YAHOO.imex.attedit.conf[aclass].comDtaSrc.responseSchema = {
                resultsList: "attach",
                fields: rsFields
            };
            
          
            YAHOO.imex.attedit.conf[aclass].comColDef = [
                //{key:"id", label:"ID"},
                { key:"author",/*width:100,*/
                  label:YAHOO.imex.attedit.conf[aclass].cname["author"] },
                { key:"date", /*width:240,*/ 
                  label:YAHOO.imex.attedit.conf[aclass].cname["date"] },
            ];
            
            if(aclass === 'history' || aclass === 'comment' ){
              
                YAHOO.imex.attedit.conf[aclass]
                    .comColDef.push(
                        { key:"subject", /*width:500,*/ formatter:"subject", 
                          label:YAHOO.imex.attedit.conf[aclass].cname["subject"] }
                    );
            }
            
            if(aclass === 'comment'){
              
                YAHOO.imex.attedit.conf[aclass]
                    .comColDef.push(
                        { key:"flagName", /*width:100,*/ formatter:"flag", 
                          label:YAHOO.imex.attedit.conf[aclass].cname["flagName"] }
                    );
                    try{
                        YAHOO.imex.attedit.addTitleCount( messages.attach.length, 
                                                          YAHOO.util.Dom.get( YAHOO.imex.attedit.conf[aclass].apane).id);
                    } catch( e ) {
                        console.log( e );
                    }
                    
            }

            if(aclass === 'adata'){
                
                YAHOO.imex.attedit.conf[aclass]
                    .comColDef.push(
                        { key:"subject", /*width:500,*/ formatter:"asubject", 
                          label:YAHOO.imex.attedit.conf[aclass].cname["subject"] }
                    );
                
                YAHOO.imex.attedit.conf[aclass]
                    .comColDef.push(
                        { key:"bodyType", /*width:100,*/ formatter:"btype", 
                          label:YAHOO.imex.attedit.conf[aclass].cname["bodyType"] }
                    );
                YAHOO.imex.attedit.conf[aclass]
                    .comColDef.push(
                        { key:"flagName", /*width:100,*/ formatter:"flag", 
                          label:YAHOO.imex.attedit.conf[aclass].cname["flagName"] }
                    );
                YAHOO.imex.attedit.conf[aclass]
                    .comColDef.push(
                        { key:"aid", /*width:100,*/ formatter:"dll", 
                          label:YAHOO.imex.attedit.conf[aclass].cname["aid"] }
                    );
                    try{
                        YAHOO.imex.attedit.addTitleCount(messages.attach.length, YAHOO.util.Dom.get( YAHOO.imex.attedit.conf[aclass].apane).id);
                    } catch( e ) {
                        console.log( e );
                    }
            }

            this.mySubFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
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
 
            this.myASubFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
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
            
            this.myFlagFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("flagName") === undefined ||
                    oRecord.getData("flagName") === "" ){ 
                        elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center>' + oRecord.getData("flagName") + '</center>';
                    }
            }; 
         
            this.myBTypeFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("bodyType") === undefined ||
                    oRecord.getData("bodyType") === "" ){ 
                        //elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center>' + oRecord.getData("bodyType") + '</center>';
                    }
            }; 
         
            this.myDllFormatter = function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("aid") === undefined ||
                    oRecord.getData("aid") === "" ){ 
                        elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center><a href=\"\" '
                            + 'onclick=\"YAHOO.imex.attedit.attachDrop({'
                            + 'rid:' + YAHOO.imex.attedit.conf['adata'].pubId + ','
                            + 'aid:' + oRecord.getData("aid")
                            + '});return false;\">delete</a></center>';
                    }
            }; 
            

            YAHOO.widget.DataTable.Formatter.subject = this.mySubFormatter; 
            YAHOO.widget.DataTable.Formatter.asubject = this.myASubFormatter; 
            YAHOO.widget.DataTable.Formatter.flag = this.myFlagFormatter; 
            YAHOO.widget.DataTable.Formatter.btype = this.myBTypeFormatter; 
            YAHOO.widget.DataTable.Formatter.dll = this.myDllFormatter; 

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
    addTitleCount: function( count, id )
    {
		//tabs = YAHOO.imex.pubedit.tabs.get('tabs');
        tabs = YAHOO.imex.pubedit.tabs;
		for(var i = 0;i < tabs.length; i++)
		{
			var currentElement = tabs[i].get('contentEl');
			if(currentElement.querySelector('#' + id)  !== null)
			{
                var label = tabs[i].get('label');
				if( label.indexOf('(') > 0)
					label = label.slice(0, label.indexOf('(') - 1);
				tabs[i].set('label', label +' (' + count+ ')' );
				return
			}
		}
		
	}, 
    attachInitListFail: function( o ){
        alert( "InitListFail:" + o );  
    },
    nameSet: function( o ){
        
        var nameFld = YAHOO.util.Dom.get( o.nf );
        var fileFld = YAHOO.util.Dom.get( o.ff );
        //nothing in the name field
        if(typeof fileFld.files[0] != "undefined")
        {
			if( nameFld.value == null || nameFld.value == '' ){
				nameFld.value = fileFld.files[0].name;
			} 
			//same value in the name field as what will be inserted
			else if(fileFld.files[0].name == nameFld.value){}
			//insert the file name
			else {   
				nameFld.value = nameFld.value + " (" + fileFld.files[0].name +")";
			}
		}
    },

    pubPreview: function( aclass, op ){

        var sub = YAHOO.util.Dom.get("cmtmgr_opp_encs").value;
        var bdy = YAHOO.util.Dom.get("cmtmgr_opp_encb").value;
        var flg = YAHOO.util.Dom.get("cmtmgr_opp_encf").value;
        
        var prev = { subject: sub, body: bdy, "bodyType": "WIKI",
                     author: "N/A", date: "N/A"};
 
        YAHOO.mbi.modal.attachment( {prev:prev} );
                                     
        return false;
    },

    pubAttach: function( aclass, op ){ // NOTE: hardwired submit form
        
        if( aclass !== "adata" ){
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
            var edit = YAHOO.imex.attedit;
            var aclass = o.argument.aclass;
            var dtb = edit.conf[o.argument.aclass].comTable;
            
            dtb.getDataSource().sendRequest( 
                '',
                { success: dtb.onDataReturnInitializeTable,
                  scope: dtb });
            edit.attachReload({'argument':aclass});
            edit.addTitleCount(edit.conf[aclass].comTable.getRecordSet().getLength() + 1, YAHOO.util.Dom.get( edit.conf[aclass].apane).id);
        }catch(ex){
            alert( "AJAX Error: " + ex );
        }
    },
    
    attachUpdateFail: function(o) {
        YAHOO.imex.attedit.conf[o.argument.aclass].comTable.onShow();
    },

    comRender: function() {
        //alert("comrender");
        var cpanel = YAHOO.imex.attedit.comPanel;
       
        var t = cpanel.getUnitByPosition( 'top' );
        t.set('body','<textarea rows="15" cols="80"/>');
        
        var c = cpanel.getUnitByPosition( 'center' );
        c.set('body','<textarea rows="5" cols="80"/>');
    },

    attachDrop: function( o ) {
        var aid = o.aid;
        var rid = o.rid;
        
        var dropCallback = { cache:false, timeout: 5000, 
                             success: YAHOO.imex.attedit.attachDropSuccess,
                             failure: YAHOO.imex.attedit.attachDropSuccess,
                             argument: "adata" };   
        try{            
            var url = 'attachmgr?op.edda=edda&id=' + rid 
                + '&opp.daid='+aid;
            
            YAHOO.util.Connect
                .asyncRequest( 'GET', url, dropCallback );            
        } catch( ex ) {
            alert( "AJAX Error:" + ex );
        }
    },
    
    attachDropFail: function( o ){
      alert(o);      },

    attachDropSuccess: function( o ) {
        var aclass = o.argument;
        var edit = YAHOO.imex.attedit;
            edit.attachReload({'argument':aclass});
            edit.addTitleCount(edit.conf[aclass].comTable.getRecordSet().getLength() -1, YAHOO.util.Dom.get( YAHOO.imex.attedit.conf[aclass].apane).id);
    },
    attachReload: function( o ) {
        var aclass = o.argument;
        YAHOO.imex.attedit.conf[aclass].comTable.load(); 
    },
    //this upload function may make some code unnecessary. re-factor is in order  
    UploadFile: function(){
		var formElement = document.getElementById("attmgr");
		var textField = formElement.elements.namedItem('opp.edan');
		var fileField = formElement.elements.namedItem('opp.edafile');
/*
		if(fileField.files[0].size + textField.value.length < 2095000 && fileField.files.length > 0)
		{
			* 
*/
        var attachmentSuccess = function(o) 
        {
            var edit = YAHOO.imex.attedit;
            edit.addTitleCount(edit.conf['adata'].comTable.getRecordSet().getLength() + 1, YAHOO.util.Dom.get( YAHOO.imex.attedit.conf['adata'].apane).id);
            edit.attachReload({'argument':'adata'});
            formElement.reset();
        };
        var uploadSuccess = function(o) 
        {
            var edit = YAHOO.imex.attedit;
            edit.addTitleCount(edit.conf['adata'].comTable.getRecordSet().getLength() + 1, YAHOO.util.Dom.get( YAHOO.imex.attedit.conf['adata'].apane).id);
            edit.attachReload({'argument':'adata'});
            formElement.reset();
        };
        var attachmentFailure = function(o) 
        {
            alert('Error during file upload. Check if file is too large');
            formElement.reset();
        };
         
        var callback = {
          success:attachmentSuccess,
          failure:attachmentFailure,
          upload:uploadSuccess
        };
        
        if(typeof fileField.files[0] != "undefined")
        {
            var btn = YAHOO.util.Dom.get('attmgr_op_eada');
            //var form = new FormData(formElement);
            //var xhr = new XMLHttpRequest();
            
            //form.append(btn.name, btn.value);
            YAHOO.util.Connect.setForm(formElement, true);
            var sendForm = YAHOO.util.Connect.asyncRequest('POST', 'attachmgr', callback);
            //xhr.open("POST", "attachmgr", false);
            //xhr.send(form);
            /*
            if(xhr.status != 404)
            {
                var edit = YAHOO.imex.attedit;
                edit.attachReload({'argument':'adata'});
                edit.addTitleCount(edit.conf['adata'].comTable.getRecordSet().getLength() + 1, 'Attachments');
            }
            else
                alert('Error during file upload. Check if file is too large');
                */
        }
    },

    // Column formatters

    mySubFormatter: function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("body") === "" ){ 
                    elLiner.innerHTML = oRecord.getData("subject");
                } else {
                    elLiner.innerHTML = oRecord.getData("subject") 
                        + ' [<a onclick="YAHOO.mbi.modal.attachment({'
                        + 'rid:' + oRecord.getData("root")
                        + ',aid:' + oRecord.getData("id")
                        + '}); return false;" href="" >more</a>]';
                }
        },

    myASubFormatter: function( elLiner, oRecord, oColumn, oData ) { 
                
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
       }, 
            
    myFlagFormatter: function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("flagName") === undefined ||
                    oRecord.getData("flagName") === "" ){ 
                        elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center>' + oRecord.getData("flagName") + '</center>';
                    }
        }, 
         
    myBTypeFormatter: function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("bodyType") === undefined ||
                    oRecord.getData("bodyType") === "" ){ 
                        //elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center>' + oRecord.getData("bodyType") + '</center>';
                    }
        }, 
         
    myDllFormatter: function( elLiner, oRecord, oColumn, oData ) { 
                
                if( oRecord.getData("aid") === undefined ||
                    oRecord.getData("aid") === "" ){ 
                        elLiner.innerHTML = "<center>---</center>";
                    } else {
                        elLiner.innerHTML = 
                            '<center><a href=\"\" '
                            + 'onclick=\"YAHOO.imex.attedit.attachDrop({'
                            + 'rid:' + YAHOO.imex.attedit.conf['adata'].pubId + ','
                            + 'aid:' + oRecord.getData("aid")
                            + '});return false;\">delete</a></center>';
                    }
   } 
            

};
