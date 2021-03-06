YAHOO.namespace("imex");

YAHOO.imex.recordedit = {

    parent: null,
    curateUrl: "",
    curatePat: "",
    pubmedUrl: "http://www.ncbi.nlm.nih.gov/pubmed/%%pmid%%",
    pubmedPat: "%%pmid%%",
    imexUrl: "imex/rec/%%imex%%",
    imexPat: "%%imex%%",
    
    init: function( parent ) { 
        try{	    
	    var RE = YAHOO.imex.recordedit;
	    RE.parent = parent;
            
            RE.bodyMenuInit( parent );
            
            // table row popup
            //----------------
            
            if( parent.loginId != null && parent.loginId> 0 ){
                parent.myDataTable.subscribe( "rowClickEvent", 
					      RE.rowPopup, {parent: parent} );
            }
            
        } catch (x) {
            console.log("recordedit error:" + x);
        }
    },
    
    bodyMenuInit: function( parent ){
        try{
            var RE = YAHOO.imex.recordedit;

	    console.log("bodyMenuInit: parent.loginId=== " + parent.loginId);

             var uid = parent.loginId;
             var dFlag = parent.loginId > 0 ? false : true;

            // table row context
            //------------------
            
            var oSupMenu = [ [{text:"Discard", 
			       onclick: {fn: RE.statusUpdate, obj: { id: 5, parent:parent} }}],
                             [{text:"Queue", 
			       onclick: {fn: RE.statusUpdate, obj: { id: 12, parent: parent} }}],
                             [{text:"Reserve", 
			       onclick: {fn: RE.statusUpdate, obj: { id: 2, parent: parent} }}]
                           ];

	    console.log("bodyMenuInit: parent.loginId= = " + parent.loginId);

	    
            var supmenu = new YAHOO.widget.Menu( "supmenu" );

            supmenu.addItems( oSupMenu );
            
	    console.log("  disabled:: " + uid + " ::: " + YAHOO.lang.JSON.stringify(dFlag));
            
            var oRowMenu = [ [ { text: "Status Update", 
				 disabled: dFlag,
				 submenu: supmenu
                               }],
                             [ { text:"Curate", 
				 disabled: dFlag, 
                                 onclick: { fn: RE.gotoExtUrl, 
                                            obj: { parent: parent,
						   url: parent.curateUrl,
                                                   idp: parent.curatePat,
                                                   idt: "pmid" } 
                                          }
                               }],
                             [ { text:"To PubMed", disabled: false,
                                 onclick: { fn: RE.gotoExtUrl, 
                                            obj: { parent: parent,
						   url: parent.pubmedUrl,
                                                   idp: parent.pubmedPat,
                                                   idt: "pmid" } 
                                          }
                               
                               }],
                             [ { text:"To IMEx", disabled: false,
                                 onclick: { fn: RE.gotoExtUrl, 
                                            obj: { parent: parent, 
						   url: parent.imexUrl,
                                                   idp: parent.imexPat,
                                                   idt: "imex" } 
                                          } 
                               }]
                           ];        
           console.log("  disabled:: " + uid + " ::: " + YAHOO.lang.JSON.stringify(dFlag));
            

            parent.myDataTable.my.bodymenu = 
		new YAHOO.widget.ContextMenu( "bodymenu", 
					      {trigger: parent.myDataTable.getTbodyEl()}); 

	    //console.log("menu: " + YAHOO.lang.JSON.stringify(oRowMenu[0].submenu));

            parent.myDataTable.my.bodymenu.addItems( oRowMenu );
            parent.myDataTable.my.bodymenu.render( "pubtab" );
	    
            console.log("menu: set trigger");   

            parent.myDataTable.my.bodymenu.subscribe( "triggerContextMenu", 
						      RE.onBodyMenuTrigger,
						      {parent: parent});

            console.log("menu: set hide");   
            parent.myDataTable.my.bodymenu.subscribe( "hide", 
						      RE.onBodyMenuHide,
						      {parent: parent}); 
        }catch( x ){
            console.error ("bodyMenuInit: error=" + x );
        }        
    },
    
    onBodyMenuTrigger: function( e, o, a ){

        console.log("onBodyMenuTrigger:");         

        try{
            var parent = a.parent;
	    var RE = YAHOO.imex.recordedit;
	    
            var tgt = this.contextEventTarget;
            
            var [record, el] = RE.getContextRecord( tgt, parent );
            
            Dom.addClass( el, "selected"); 

            //console.log("r, el " + YAHOO.lang.JSON.stringify(record) 
            // + "  " + el + " " + parent.loginId );              

            var menu = parent.myDataTable.my.bodymenu;
            var table = parent.myDataTable;
            
            parent.myDataTable.my.bodyrec = record;
            parent.myDataTable.my.bodysel = el;

            console.log("onBodyMenuTrigger: DONE");
        }catch( x ){
            console.error( "BodyMenuTrigger: error= " + x );
        }
        
    },

    onBodyMenuHide: function( e, o, a ){

        console.log("onBodyMenuHide:");         
          
        try{
	    var parent = a.parent;
            var bodysel = parent.myDataTable.my.bodysel;
            if ( bodysel != null ){
                Dom.removeClass( bodysel, "selected"); 
            } 
        }catch( x ){
            console.error( "BodyMenuHide: error=" + x );
        }
    }, 
    
    getContextRecord: function( tgt, parent ){
        try{
            var elem = tgt;
            var record = null;
            var elRow = null;
            if( tgt != undefined ){
		for( var p=5; p>0; p--){
                    elRow = parent.myDataTable.getTrEl( tgt ); 
                    record =  parent.myDataTable.getRecord( elRow );
                    if( record == null){
			tgt = tgt.parentElement;
                    } else{
			p = 0;
                    }
		}
            }        
	    
            return [ record, elRow ];
	}catch( x ){
	    consoloe.error( "getContextRecord: error=" + x)
	}   
	return [ null,null ];
    },

    statusUpdate: function( e, o, a ){

        console.log("statusUpdate:");
        try{
            var parent = a.parent;

            var record = parent.myDataTable.my.bodyrec;  
            var rid = record.getData("id");
            var url = "pubedit?id="+rid+"&op.esup=esup&opp.nsn=" + a.id;
            
            var urlcallback = {
                success: function(o) {                    
                    var parent = o.argument.parent;
		    
                    if( parent.myDataTable != null ){                        
                        try{
                            console.log("reloading...");
                            setTimeout( function() {
                                           console.log("delayed reloading...");
                                           parent.tableReload( {}, parent.myDataTable );
                                           console.log("delayed reloading... DONE");
                                         }, 500);
                        } catch( x ){
                            console.log(x);
                        }
                    } else {
			console.log( "parent.myDataTable: null");
		    }
                },
                failure: function(o) {
		    console.error( "StatusUpdate: error=" + o );
                },
		argument: {parent: parent }
            };
            
            var cObj = YAHOO.util.Connect.asyncRequest( 'GET', url, urlcallback );
	                
        } catch (x) {
            console.error( x );
        }
    },

    gotoExtUrl: function( e, o, a ){
       
	try{	   
            var names = [];
            for( name in a){
                names.push(name);
            }

            console.log("gotoExtUrl: a.names =" + YAHOO.lang.JSON.stringify(names));
            console.log("gotoExtUrl: o =" + YAHOO.lang.JSON.stringify(o));

            console.log("gotoExtUrl: url =" + a.url);
            console.log("gotoExtUrl: idp =" + a.idp);
            console.log("gotoExtUrl: idt =" + a.idt);

	    var parent = a.parent;
	    
            var record = parent.myDataTable.my.bodyrec;
            var icid = record.getData( "id" );
            var iid = record.getData( "imexId" );
            var pmid = record.getData( "pmid" );
	    
            console.log("gotoExtUrl: icid =" + YAHOO.lang.JSON.stringify(icid));
            console.log("gotoExtUrl: iid =" + YAHOO.lang.JSON.stringify(iid));
            console.log("gotoExtUrl: pmid =" + YAHOO.lang.JSON.stringify(pmid));

            var url = a.url;
            if( a.idt == "pmid" ){
               url=url.replace(a.idp, pmid);
	    }

            if( a.idt == "imex" ){
               url=url.replace(a.idp, imex);
	    }

	    if( a.idt == "icid" ){
               url=url.replace(a.idp, icid);
	    }

	    window.open(url,"icentral-tab");
            
        } catch (x) {
            console.error( x );
        }        
    },
    
    rowPopup: function( e, a ){
	try{
	    var t = e.target;
            var parent = a.parent;

            var trel = parent.myDataTable.getTrEl( t );
            var rec = parent.myDataTable.getRecord( trel );
            var l = 0;

            if( rec !== null ) return;

            while( rec == null && l++ <10 ){
                trel = trel.parentElement;
                rec = parent.myDataTable.getRecord( trel );
            }
            var  rdat =  parent.myDataTable.getRecord(trel).getData("id");
            
            var url = "pubedit?id="+rdat+"&op.popup=status";
            var dUrl = "pubedit?id="+rdat+"&op.esup=esup&opp.nsn=5";
            var qUrl = "pubedit?id="+rdat+"&op.esup=esup&opp.nsn=12";
            var rUrl = "pubedit?id="+rdat+"&op.esup=esup&opp.nsn=2";
            
            YAHOO.mbi.modal.dialog( {mtitle:"Article", 
                                     table: parent.myDataTable,
				     parent: parent,
                                     url: url,
                                     button:[{label:"Discard", id:"discard-btn", url:dUrl},
                                             {label:"ToQueue", id:"queue-btn", url:qUrl},
                                             {label:"Reserve", id:"reserve-btn", url:rUrl} ]
                                    });     
        }catch(x){
            console.error( "rowPopup: error=" + x );
        }        
    }   
};

