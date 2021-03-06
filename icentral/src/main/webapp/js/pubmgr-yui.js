YAHOO.namespace("imex");

YAHOO.imex.pubmgr = {

    objid: "pubmgr",

    imexUrl: "http://www.ncbi.nlm.nih.gov/pubmed/%%pmid%%",
    imexPat: "%%pmid%%",

    curateUrl: "http://www.ncbi.nlm.nih.gov/pubmed/%%pmid%%",
    curatePat: "%%pmid%%",
 
    pubmedUrl: "http://www.ncbi.nlm.nih.gov/pubmed/%%pmid%%",
    pubmedPat: "%%pmid%%",
  
    admus: "",
    owner: "",
    query: "",    
    cflag: "",
    watch: "",
    loginId: "",
    
    stageBtn: { my:{value:"",foo:"stage"} },    
    stageSel: [ { text: "---ANY---", value: "" },
                { text: "preQueue", value: "preQueue" },
                { text: "Queue", value: "Queue" } 
              ],

    stateBtn: { my:{value:"",foo:"state"} },    
    stateSel: [ { text: "---ANY---", value: "" } ],
    
    partnerBtn: { my:{value:"",foo:"partner"} },    
    partnerSel: [ { text: "---ANY---", value: "" } ],

    myDataSource: null,
    myPaginator: null,
    
    myCD: { 
        "id":{ key:"id", label:"ICID", menuLabel:"ICID",
               sortable:true, resizeable:true, hidden: true, hideable: true,
               formatter:"icid" },
        
        "pub":{ key:"pub", label:"Publication", menuLabel:"Publication",
                sortable:true, resizeable:true, hideable: false,
                formatter:"publication", maxAutoWidth:1000  },

        "pmid":{ key:"pmid", label:"PMID", menuLabel:"PMID", 
                 sortable:true, resizeable:true,  hideable: true,
                 formatter:"pmid", className:"pmid"},

        "imexId":{ key:"imexId", label:"<center>Imex<br/>Accession</center>", 
                   sortable:true, resizeable:true, hideable: true,
                   formatter:"imex",menuLabel:"Imex Accession" },

        "imexDb":{ key:"imexDb", label:"<center>Imex<br/>Partner</center>", 
                   sortable:false, resizeable:true, hideable: true,
                   formatter:"partnerList",menuLabel:"Imex Partner" },

        "stage":{ key:"stage", label:"Stage", 
                  sortable:false, resizeable:false, hideable: true, 
                  formatter:"center", menuLabel:"Stage" },

        "state":{ key:"state", label:"Status", 
                  sortable:false, resizeable:false, hideable: true, 
                  formatter:"center", menuLabel:"Status" },

        "submission":{ label:"Submission", menuLabel:"Submission", key:"submission", 
                       hideable: true, hidden: true,
                       children:[
                           { key:"date",  label:"Date", 
                             sortable:true, resizeable:false, formatter:"crt" },
                           { key:"owner", label:"Submitted By",
                             sortable:false, resizeable:false, formatter:"list" }
                       ]
                     },

        "modified":{ label:"Last Modified", menuLabel:"Last Modified", key:"modified", 
                     hideable: true, hidden: false,
                     children:[
                         { key:"modTStamp",  label:"Date", 
                           sortable:true, resizeable:false, formatter:"mts" },
                         { key:"modUser", label:"Modified By",
                           sortable:false, resizeable:false}
                     ]
                   },

        "activity":{ label:"Last Activity", menuLabel:"Last Activity", key:"activity", 
                     hideable: true, hidden: true,
                     children:[
                         { key:"actTStamp",  label:"Date", 
                           sortable:true, resizeable:false, formatter:"ats" },
                         { key:"actUser", label:"Modified By",
                           sortable:false, resizeable:false}
                     ]
                   },

        "editor":{ key:"editor", label:"Curator(s)", menuLabel:"Curator(s)",
                   sortable:false, resizeable:true, hideable: true,
                   formatter:"editorList" },
        "detail":{ key:"detail", label:"",
                   sortable:false, resizeable:true, hideable: true,
                   formatter:"elink", className:"detail" }        
    },

    
    myCP: { "date": "submission","modTStamp":"modified","actTStamp":"activity"},

    myCL: [ "id", "pub", "pmid", "imexId", "imexDb", "stage",
            "state", "submission", "modified", "activity", "editor", "detail" ],
    myColumnDefs: [],

    buildRequest: function ( state ){
      
        var PMGR = YAHOO.imex.pubmgr;

        //console.log("stge: " + PMGR.stage + " flt: >" + state.filter.stage + "<");
        //console.log("stat: " + PMGR.status + " flt: >" + state.filter.status + "<");
        //alert("buildRequest->state" +YAHOO.lang.JSON.stringify(state));
        
        var gfVal, sfVal;

        if( PMGR.stage !== undefined ){
           gfVal = PMGR.stage;
        }

        if( PMGR.status !== undefined ){
           sfVal = PMGR.status; 
        }

        //oSelf.my.stageFlt.my.value;

        if( state.filter.stage !== undefined &&
            state.filter.stage !== ""){
           gfVal = state.filter.stage;
        } 

	//oSelf.my.stateFlt.my.value;

        if( PMGR.stateBtn.my.value !== undefined &&
            PMGR.stateBtn.my.value !== "" ){
           sfVal = YAHOO.imex.pubmgr.stateBtn.my.value;
        }

        var req = "opp.off=" + state.startIndex + 
            "&opp.wfl=" + state.watch + 
            "&opp.max=" + state.pageSize + 
	    "&opp.gfv=" + gfVal +
            "&opp.sfv=" + sfVal +
            "&opp.pfv=" + state.filter.partner +
            "&opp.efv=" + state.filter.editor +
            "&opp.ofv=" + state.filter.owner +
            "&opp.ffv=" + state.filter.cflag +
            "&opp.query=" + state.query +
            "&opp.skey=" + state.scol +
            "&opp.sdir=" + state.sdir;

	req = req.replace(/=undefined/g, '=');
        
        //console.log("REQ: " + req );

        return encodeURI( req );
        
    },

    requestBuilder: function( oState, oSelf ) {

        //console.log("requestBuilder called");   
        
        var PMGR = YAHOO.imex.pubmgr;

        // get state (or use defaults)
        //----------------------------

        oState = oState || {pagination:null, sortedBy:null};

        var sort = (oState.sortedBy) ? oState.sortedBy.key : "id";
        var dir = 
            (oState.sortedBy 
             && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC)
            ? "false" : "true";
        var startIndex = (oState.pagination) 
            ? oState.pagination.recordOffset : 0;
        var results = (oState.pagination) 
            ? oState.pagination.rowsPerPage : 10;

        // LS: also get watch flag here ?
        //         
        // filters
        //--------
        
        var gfVal, sfVal;

        if( PMGR.stage !== undefined ){
           gfVal = PMGR.stage;
        }

        if( PMGR.status !== undefined ){
           sfVal = PMGR.status; 
        }

        //oSelf.my.stageFlt.my.value;

        if( PMGR.stageBtn.my.value !== undefined &&
            PMGR.stageBtn.my.value !== ""){
           gfVal = PMGR.stageBtn.my.value;
        } 

	//oSelf.my.stateFlt.my.value;

        if( PMGR.stateBtn.my.value !== undefined &&
            PMGR.stateBtn.my.value !== "" ){
           sfVal = YAHOO.imex.pubmgr.stateBtn.my.value;
        }

        // oSelf.my.partnerFlt.my.value;
        var pfVal = YAHOO.imex.pubmgr.partnerBtn.my.value;
        
        if( gfVal === undefined ){
            gfVal = "";
        }
        if( sfVal === undefined ){
            sfVal = "";
        }
        if( pfVal === undefined ){
            pfVal = "";
        }

        //alert("stateButton: name="+ YAHOO.imex.pubmgr.stateBtn.my.name 
        //      + " value=" + YAHOO.imex.pubmgr.stateBtn.my.value);
        
        var efVal = oSelf.my.admusFlt;
        var ofVal = oSelf.my.ownerFlt;
        var ffVal = oSelf.my.cflagFlt;


        // watch flag 
        
        var wtFlg = oSelf.my.watchFlg;
        if( wtFlg === undefined ){
            wtFlg = "";
        }

        // query
        //------

        var queryVal = oSelf.my.query;


        var req = "opp.skey=" + sort +
            "&opp.wfl=" + wtFlg +
	    "&opp.gfv=" + gfVal + 
            "&opp.sfv=" + sfVal +
            "&opp.pfv=" + pfVal +
            "&opp.efv=" + efVal +
            "&opp.ofv=" + ofVal +
            "&opp.ffv=" + ffVal +
            "&opp.query=" + queryVal +            
            "&opp.sdir=" + dir +
            "&opp.off=" + startIndex +
            "&opp.max=" + results; 

	req = req.replace(/=undefined/g, '=');        
        req = encodeURI(req);
        //console.log("request: " + req);
        
        // build custom request
        //---------------------

        return req;
    },

    tableReload: function( o, dt ) {
        try {
            //console.log("reloading");
            var state = dt.get('paginator').getState();            
           
            // note: should test if offset > total; if yes,
            //       decrement offset by page size and reload 
            //       once more, set pager to the last page  

            var reloadCallback = {
                success: dt.onDataReturnInitializeTable,
                failure: dt.onDataReturnInitializeTable,
                scope: dt,
                argument: dt.getState()
            };
                
            var reloadRequest = 
                dt.my.requestBuilder( dt.getState(), dt );
            //console.log("request: " + reloadRequest);    
            dt.getDataSource().sendRequest( reloadRequest, 
                                            reloadCallback );
        } catch (x) {   
            console.log(x);
        }
    },
    getDefaultCookie: function(){
        var cookie = "";
        var pubmgr = YAHOO.imex.pubmgr;
        for(var i = 0; i < pubmgr.myCL.length; i++ ){

            var hidden= false;
            if(  pubmgr.myCD[pubmgr.myCL[i]].hidden === true ){
                hidden= true;
            }
            cookie += pubmgr.myCD[pubmgr.myCL[i]].key + ":" + hidden +"|";
        }
        //alert( cookie );

        return cookie;
    },

    init: function( init ){
       console.log("PMGR:init CALLED"); 
       console.log(YAHOO.lang.JSON.stringify(init)); 

       var scrSuccess = function( response ){                                 

          var oRes = YAHOO.lang.JSON.parse( response.responseText);
          var scoreList = oRes.scoreNameList;
          console.log( "scrSuccess: scoreList: " + YAHOO.lang.JSON.stringify( scoreList ));

           var pubmgr = YAHOO.imex.pubmgr;

           pubmgr.loginId = init.loginid;
           pubmgr.scoreList = scoreList;

           if( typeof pubmgr.myDataTable != "undefined" ){
               pubmgr.myDataTable.my.configmenu.destroy();
               pubmgr.myDataTable.destroy();
               pubmgr.myColumnDefs = [];            
           } else {
               pubmgr.userTableLayoutInit( init );
           }          
        }       

        var scrCallback = { cache:false, timeout: 5000, 
                             success: scrSuccess,
                             failure: scrSuccess
                           };            
        try{
             YAHOO.util.Connect
                .asyncRequest( 'GET', 
                               'pubmgr?op.scrl=scrl',
                               scrCallback );        
        } catch (x) {
                console.log("AJAX Error:"+x);               
       }
       console.log("PMGR:init DONE"); 
    },

    //----------------------------------------------------------------
    //if a user is logged in this sets the cookie to their preferences
    //----------------------------------------------------------------

    userTableLayoutInit: function( init ){
        var pubmgr = YAHOO.imex.pubmgr;
        console.log("userTableLayoutInit: CALLED");
        

        if( pubmgr.loginId  !== undefined && pubmgr.loginId !== ""
	    && pubmgr.loginId >-1 ){

            console.log("userTableLayoutInit: pubmgr.loginId" + pubmgr.loginId);
            
            var Success = function( response ){ 
                          
                var pubmgr = YAHOO.imex.pubmgr;            
                var cookie = YAHOO.util.Cookie.get("pubmgr");
           
                if( cookie == null ){
                    cookie = pubmgr.getDefaultCookie();
                    YAHOO.util.Cookie.set( "pubmgr", cookie );
                }
                
                var responseText = YAHOO.lang.JSON.parse(response.responseText);
                var preferences = YAHOO.lang.JSON.parse(responseText.preferences);
                if (preferences.tableLayout == "null" ){
                     pubmgr.updateUserTablePref(cookie);
                } else {
                    cookie = preferences.tableLayout;
                    YAHOO.util.Cookie.set( "pubmgr", cookie );
                    pubmgr.buildCDefs( cookie, [] );
                }
            
               try{
                  var cookie = YAHOO.util.Cookie.get("pubmgr");
                  if( cookie == null ){
                      cookie = pubmgr.getDefaultCookie();
        
                      YAHOO.util.Cookie.set( "pubmgr", cookie );
                  }
            
                  if( cookie !== null ){
                     pubmgr.buildCDefs( cookie, scoreList );                
                  }
               } catch (x) {
                  console.log("INIT: ex="+ x);
               }

	       console.log( "userTableLayoutInit: callback initView call: " 
                            + YAHOO.lang.JSON.stringify(init) );
               
               pubmgr.initView( init );
               pubmgr.historyInit( init );

            };
            var Fail = function ( o ) {
                console.log( "AJAX Error update failed: id=" + o.argument.id ); 
            };
            var callback = { cache:false, timeout: 5000, 
                             success: Success,
                             failure: Fail
                             }; 
            
            try{
                console.log("userTableLayoutInit: async pref call");

                YAHOO.util.Connect
                .asyncRequest( 'GET', 
                               'userprefmgr?id=' + pubmgr.loginId +'&op.view=true',
                               callback );        
            } catch (x) {
                console.log("AJAX Error:"+x);
            }

            console.log("userTableLayoutInit: DONE");
        } else {

           console.log("userTableLayoutInit: nologin");

           try{
               var cookie = YAHOO.util.Cookie.get("pubmgr");
               if( cookie == null ){
                   cookie = pubmgr.getDefaultCookie();
        
                   YAHOO.util.Cookie.set( "pubmgr", cookie );
               }
            
               if( cookie !== null ){
                   pubmgr.buildCDefs( cookie, pubmgr.scoreList );                
               }
           } catch (x) {
               console.log("INIT: ex="+ x);
           }

	   console.log( "userTableLayoutInit: direct initView call: " 
                         + YAHOO.lang.JSON.stringify(init) );

           pubmgr.initView( init );
           pubmgr.historyInit( init );
       } 
    },
    
    updateUserTablePref: function( cookie ){
        var pubmgr = YAHOO.imex.pubmgr;
        var loginId = pubmgr.loginId;
        
        if( loginId !== undefined && loginId != ""
            && loginId > -1 ){
            try{
                YAHOO.util.Connect
                .asyncRequest( 'POST', 
                               'userprefmgr?id=' + loginId + '&op.updateTable=true',
                               null, "opp.tableLayout=" + cookie );        
            } catch (x) {
                console.log("AJAX Error:"+x);
            }
        }
    },
    
    buildCDefs: function( cookie, scrList ){

        var PMGR = YAHOO.imex.pubmgr;

        var col = cookie.split("|");
        
        if(col.length > 0){
            PMGR.myColumnDefs=[];
            
            for( var i =0; i < col.length-1; i++ ){
                try{
                
                    var cs = col[i].split(":");
                    if( PMGR.myCD[cs[0]] !== undefined ){
                        
                        PMGR.myColumnDefs.push(PMGR.myCD[cs[0]]);
                        if(cs[1] === 'true'){
                            PMGR.myColumnDefs[PMGR.myColumnDefs.length-1].hidden = true;
                        } else {
                            PMGR.myColumnDefs[PMGR.myColumnDefs.length-1].hidden = false;
                        }
                    }
                    
                } catch (x) {
                    console.log( "I:"+ i + "-> " + cs[0] + " ex=" + x );
                }
            }        
        }

        //"pmid":{ key:"pmid", label:"PMID", menuLabel:"PMID", 
        //         sortable:true, resizeable:true,  hideable: true,
        //         formatter:"pmid", className:"pmid"},


        for( var j = 0; j < scrList.length; j++ ){

           var scrName = scrList[j];

           var cDef = { key:scrName, label:scrName, menuLabel:scrName, 
                        sortable:true, resizeable:true,  hideable: true,
                        formatter:"value", className:"value", hidden: true
           }; 
          
          PMGR.myColumnDefs.push(cDef);


        }
        
    },

    buildCookie: function(){

        var PMGR = YAHOO.imex.pubmgr;
        var cookie = "";
        var ac = PMGR.myDataTable.getColumnSet().keys;
        for(var i=0; i< ac.length; i++ ){

            var key = ac[i].getKey();
            var hid = ac[i].hidden;
            if( PMGR.myCD[key] !== undefined ){
                cookie += key + ":" + hid + "|";
            } else { // nested column
                key = PMGR.myCP[key];
                if( PMGR.myCD[key] !== undefined ){
                    cookie += key + ":" + hid + "|";
                }
            }
        }
        //console.log("cookie:"+cookie);
        return cookie;
    },

    historyInit: function( init ){

        var PMGR = YAHOO.imex.pubmgr;
        
        var defstate = {
            startIndex: 0,
            pageSize: 25,
            filter:{ stage: PMGR.stage,
                     status: PMGR.status,
                     jid: PMGR.jid,
                     partner:"",
                     editor:PMGR.admus,
                     owner: PMGR.owner,
                     cflag: PMGR.cflag},
            query: PMGR.query,
            watch: PMGR.watch,
            scol: "id",
            sdir: "asc" };
                 
        if(  init !== undefined){

	   if( init.watch !== undefined ){
              defstate.watch = init.watch;
           }

           if( init.stage !== undefined ){
             defstate.filter.stage = init.stage;
           }
                    
           if( init.status !== undefined ){
              defstate.filter.status = init.status;
           }

           if( init.jid !== undefined ){
            defstate.filter.jid = init.jid;
           }
                      
        }

        var dst = YAHOO.lang.JSON.stringify( defstate );
        
        var bState = YAHOO.util.History.getBookmarkedState( "pubmgr" );
        var iState = bState || dst;
        
	    console.log("historyInit:     init: " + YAHOO.lang.JSON.stringify( init ));
	    console.log("historyInit: defstate: " + dst);
	    console.log("historyInit:   bState: " + YAHOO.lang.JSON.stringify( bState ));

        var PMGR = YAHOO.imex.pubmgr;
        
        YAHOO.util.History.register( "pubmgr", iState, 
                                     PMGR.handleHistoryNavigation ); 
        
        YAHOO.util.History.onReady( PMGR.historyReadyHandler );    
        
        try{
            YAHOO.util.History.initialize( "yui-history-field", 
                                           "yui-history-iframe" );
        } catch (x) {
            console.log(x);
        }                
    },

    handleFilter: function( ev, o ){

        var filter = o.filter;
        var newVal = ev.newValue.value;
        
        var PMGR = YAHOO.imex.pubmgr;
        
        var newState = PMGR.myDataSource.my.myState;
        
        // LS: watch flag ?
        
        newState.startIndex = 0;
        newState.filter[filter] = newVal;
        
        YAHOO.util.History
            .navigate( "pubmgr", 
                       PMGR.generateStateString( newState ) );
    },

    handlePagination: function( state, datatable ){
        
        var PMGR = YAHOO.imex.pubmgr;
        var newState = PMGR.myDataSource.my.myState;
        
        newState.startIndex = state.recordOffset;
        newState.pageSize = state.rowsPerPage;
        
        YAHOO.util.History
            .navigate( "pubmgr", 
                       PMGR.generateStateString( newState ) );
    },
    
    handleSorting: function( column ){
        
        var PMGR = YAHOO.imex.pubmgr;
        var newState = PMGR.myDataSource.my.myState;
        try{
            var sdir = this.getColumnSortDir( column );
            newState.startIndex = 0;
            //alert( "HS: col=" + column.key + " dir=" + sdir );

            if( column.key !== undefined && sdir !==undefined ){
                
                newState.scol = column.key;
                newState.sdir = sdir.substr( 7 );

                //alert( "new sort=(" + newState.scol 
                //       + "," + newState.sdir + ")" );
            }

            YAHOO.util.History
                .navigate( "pubmgr", 
                           PMGR.generateStateString( newState ) );
        } catch (x) {
            console.log(x);
        }
        
    },
    
    handleHistoryNavigation: function( state ){
        
        var PMGR = YAHOO.imex.pubmgr;
        
        var parsed = PMGR.parseStateString( state );      
        var request = PMGR.buildRequest( parsed );
        
        PMGR.myDataSource.my.myState = parsed;
        
        // update filters
        //---------------

        var flt, 
        sflt = "";
        
        // reset filter buttons/menus
        //---------------------------
                
        var stageLabel = "---ANY---";
        var statusLabel = "---ANY---";
        var partnerLabel = "---ANY---";


	// set filter buttons/menus to default
	//------------------------------------
	
        if( PMGR.stage !== undefined && PMGR.stage !== "" ){ 
           stageLabel = PMGR.stage;
        }   

        if( PMGR.status !== undefined && PMGR.status !== "" ){ 
           statusLabel = PMGR.status;
        }   
        
        // set filter buttons/menus to current state
	//------------------------------------------

        if( parsed.filter.stage !== ""){
            stageLabel = parsed.filter.stage;
        }

        if( parsed.filter.status !== ""){
            statusLabel = parsed.filter.status;
        }

        if( parsed.filter.partner !== ""){
            partnerLabel = parsed.filter.partner;
        }        

        if( PMGR.stageBtn.set !== undefined ){           
            PMGR.stageBtn.set( "label", 
                               ("<em class=\"yui-button-label\">" + 
                                stageLabel + "</em>"));
        }else{
            PMGR.stageSel[0].text = stageLabel;
        }

        if( PMGR.stateBtn.set !== undefined ){           
            PMGR.stateBtn.set( "label", 
                               ("<em class=\"yui-button-label\">" + 
                                statusLabel + "</em>"));
        }else{
            PMGR.stateSel[0].text = statusLabel;
        }
        
        if( PMGR.partnerBtn.set!== undefined ){
            PMGR.partnerBtn.set( "label", 
                                 ("<em class=\"yui-button-label\">" + 
                                  partnerLabel + "</em>"));
        }else{
            PMGR.partnerSel[0].text = partnerLabel;
        }

	// reload stage/state filter button menus
	//---------------------------------------

	console.log("HHN: status="+parsed.filter.status +
		    " stage="+ parsed.filter.stage );

        var buttonUpdateSuccess = function( o ){	    
            var messages = YAHOO.lang.JSON.parse( o.responseText );
	    var button=o.argument.btn;

	    if(button.getMenu !== undefined){

		var buttonMenu=button.getMenu();

		var items = [{ text: "---ANY---", value: "" }];
		for( var i=0; i< messages.acom.length; i++){
		    var ci = messages.acom[i];
		    items.push({value: ci["name"], text: ci["name"]});
		}

		try{
		    if (YAHOO.util.Dom.inDocument(buttonMenu.element)) {		    
			buttonMenu.clearContent(); 
			buttonMenu.addItems(items);
			buttonMenu.render();
		    } else {
			buttonMenu.itemData = items;
		    }
		} catch(x){
		    console.log(x);
		}
	    }
        };
        
	var buttonUpdateFailure = function( o ){
	    console.log("HHN: stageUpdateFailure");
	}

        var stageUpdateCallback = 
	    { cache:false, timeout: 5000, 
              success: buttonUpdateSuccess,
              failure: buttonUpdateFailure,
              argument: { btn: YAHOO.imex.pubmgr.stageBtn } }	

        var statusUpdateCallback = 
	    { cache:false, timeout: 5000, 
              success: buttonUpdateSuccess,
              failure: buttonUpdateFailure,
              argument: { btn: YAHOO.imex.pubmgr.stateBtn } };	

        try{
	    var oppstg ="";
	    if( parsed.filter.stage !== "" && parsed.filter.stage !== null){
		oppstg = "&opp.stage=" + parsed.filter.stage;		
	    }
	    console.log("HHN: oppstg: "+oppstg);

            YAHOO.util.Connect.asyncRequest( 'GET', 
                                             "acom?op.pstac=ac" + oppstg, 
                                             statusUpdateCallback );	    
	    var oppsts ="";
	    if( parsed.filter.status !== "" && parsed.filter.status !== null){
		oppsts = "&opp.status=" + parsed.filter.status;		
	    }
	    console.log("HHN: oppsts: "+oppsts);

            YAHOO.util.Connect.asyncRequest( 'GET', 
                                             "acom?op.psgac=ac"+oppsts, 
                                             stageUpdateCallback );        
        } catch (x) {
            console.log("AJAX Error:"+x);
        }
        
        // reload data
        //------------
        
        var mdt = PMGR.myDataTable;
        
        PMGR.myDataSource
            .sendRequest( request, {
                              success: mdt.onDataReturnSetRows,
                              failure: mdt.onDataReturnSetRows,
                              scope: mdt,
                              argument: {}
                          });        
    },

    generateStateString: function( state ){            
        return YAHOO.lang.JSON.stringify( state );
    },
    
    parseStateString: function( statStr ){
        return YAHOO.lang.JSON.parse(statStr);
    },

    generateLinkState: function( stage, status, partner ){
	
        //LS: watch ?
	    console.log( "****generateLinkState" );
        var filter = {stage:stage, status:status, partner:partner, editor:'', owner:'', cflag:''  };
        var state = {startIndex:0, pageSize:25,filter:filter, scol:'id', sdir:'asc' };
        return YAHOO.lang.JSON.stringify( state );
    },

    historyReadyHandler: function(){

        try{
            var cState = YAHOO.util.History.getCurrentState( "pubmgr" );
            YAHOO.imex.pubmgr.handleHistoryNavigation( cState );
        } catch (x) {
            console.log(x);
        }
    },

    initView: function( init, cord, chid ) { 

        var PMGR = YAHOO.imex.pubmgr;
        
        console.log("InitView: CALLED" );
	    console.log("InitView: init: " + YAHOO.lang.JSON.stringify( init ) );

        this.formatterInit();
        
        // NOTE: turning on filter tabs
        //-----------------------------        

        if( init !== undefined ){
            PMGR.admus = init.admus;
            PMGR.owner = init.owner;
            PMGR.cflag = init.cflag;
            PMGR.watch = init.watch;
            PMGR.jid = init.jid;

            PMGR.stage = init.stage;
            PMGR.status = init.status;

            PMGR.query = init.query;           
        }
        
        console.log("InitView: PMGR.query=" + PMGR.query  );

        var partnerSuccess = function( o ){
            var messages = YAHOO.lang.JSON.parse( o.responseText );
            
            YAHOO.imex.pubmgr.selBtnInit( 
                { pmgr: YAHOO.imex.pubmgr,
                  filter: "partner",
                  ival: "",
                  items: messages.acom,
                  selmnu: YAHOO.imex.pubmgr.partnerSel,
                  selbtn: "partnerBtn",
                  selcnt: "partner-button-container",
                  selnme: "partner-button" ,
                  seltext: YAHOO.imex.pubmgr.partnerSel[0].text});

            YAHOO.util.Dom.removeClass("pubflt", "ic-hidden");      
        };
        
        var partnerCallback = { cache:false, timeout: 5000, 
                                success: partnerSuccess,
                                failure: partnerSuccess,
                                argument:{}}; // id:obj.id, btn:imexButton } };

        var stateSuccess = function( o ){
            var messages = YAHOO.lang.JSON.parse(o.responseText);
            YAHOO.imex.pubmgr.selBtnInit( 
                { pmgr: YAHOO.imex.pubmgr,
                  filter: "status",
                  ival: PMGR.status,
                  items: messages.acom,
                  selmnu: YAHOO.imex.pubmgr.stateSel,
                  selbtn: "stateBtn",
                  selcnt: "state-button-container",
                  selnme: "state-button", 
                  seltext: YAHOO.imex.pubmgr.stateSel[0].text});

            YAHOO.util.Dom.removeClass("pubflt", "ic-hidden");

        };
        
        var stateCallback = { cache:false, timeout: 5000, 
                              success: stateSuccess,
                              failure: stateSuccess,
                              argument:{}}; // id:obj.id, btn:imexButton } };                  

        
        var stageSuccess = function( o ){
            var messages = YAHOO.lang.JSON.parse(o.responseText);
            YAHOO.imex.pubmgr.selBtnInit( 
                { pmgr: YAHOO.imex.pubmgr,
                  filter: "stage",
                  ival: PMGR.stage,
                  items: messages.acom,
                  selmnu: YAHOO.imex.pubmgr.stageSel,
                  selbtn: "stageBtn",
                  selcnt: "stage-button-container",
                  selnme: "stage-button", 
                  seltext: YAHOO.imex.pubmgr.stageSel[0].text});

            YAHOO.util.Dom.removeClass("pubflt", "ic-hidden");
        };
        
        var stageCallback = { cache:false, timeout: 5000, 
                              success: stageSuccess,
                              failure: stageSuccess,
                              argument:{}}; // id:obj.id, btn:imexButton } };                  

        if( typeof PMGR.myDataTable == "undefined" ){
            try{
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   "acom?op.psgac=ac" , 
                                   stageCallback );
        
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   "acom?op.pstac=ac" , 
                                   stateCallback );        
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   "acom?op.pagac=ac", 
                                   partnerCallback );        
            } catch (x) {
                console.log("AJAX Error:"+x);
            }
        }
         
        // create datasource
        //------------------

        PMGR.myDataSource = new YAHOO.util.DataSource("pubmgr?op.ppg=ppg&opp.jfv="+PMGR.jid+"&"); 
        
        PMGR.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        PMGR.myDataSource.responseSchema = { 
            resultsList: "records.records", 
            fields: ["id","author","title","pmid","imexId",
                     "owner","stage","state","date","time","imexDb","editor",
                     "actTStamp","actUser","modTStamp","modUser","score"], 
            metaFields: { 
                totalRecords: "records.totalRecords", 
                paginationRecordOffset : "records.startIndex", 
                paginationRowsPerPage : "records.pageSize", 
                sortKey: "records.sort", 
                sortDir: "records.dir",
                fOwr: "records.filter.owner",
                fEdr: "records.filter.editor",
                fSte: "records.filter.status",
                fPar: "records.filter.partner",
                fFlg: "records.filter.cflag" 
            }
        }; 
        
        PMGR.myDataSource.my = { myState: null };

	/*
	PMGR.myDataSource.doBeforeParseData = function( oRequest ,
							oFullResponse ,
							oCallback ){

            console.log("dbpd1: "+ YAHOO.lang.JSON.stringify(oRequest));
            console.log("dbpd2: "+ YAHOO.lang.JSON.stringify(oFullResponse));
	 
	    return oFullResponse;
	}
	*/

        
        // create paginator
        //-----------------
        
        PMGR.myPaginator = new YAHOO.widget.Paginator(
            { containers: ["dt-pag-nav"], 
              rowsPerPage: 25, 
              template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE, 
              rowsPerPageOptions: [5,10,25,50,100], 
              pageLinks: 5 
            }
        );
        
        // datatable configuration
        //------------------------
        
        var initReq = "opp.off=0&opp.max=25"
            + "&opp.wfl=" + YAHOO.imex.pubmgr.watch 
            + "&opp.ofv=" + YAHOO.imex.pubmgr.owner 
            + "&opp.efv=" + YAHOO.imex.pubmgr.admus 
            + "&opp.ffv=" + YAHOO.imex.pubmgr.cflag
            + "&opp.query=" + YAHOO.imex.pubmgr.query;

        console.log("initView: initReq=" + initReq);    

        var myConfig = {                    
            paginator : this.myPaginator,
            initialLoad: false,
            dynamicData : true,
            draggableColumns: true
        };
        
        // Instantiate DataTable
        //----------------------

	    //console.log("PMGR.myColumnDefs " + YAHOO.lang.JSON.stringify(PMGR.myColumnDefs));
  
        PMGR.myDataTable = new YAHOO.widget.DataTable(
            "pubtab", PMGR.myColumnDefs, 
            PMGR.myDataSource, myConfig
        );
        
        PMGR.myPaginator
            .unsubscribe( "changeRequest", 
                          PMGR.myDataTable.onPaginatorChangeRequest );
        PMGR.myPaginator
            .subscribe( "changeRequest", 
                        PMGR.handlePagination, PMGR.myDataTable, true );

        PMGR.myDataTable.sortColumn = PMGR.handleSorting;

        PMGR.myDataTable.my = { 
            watchFlg: PMGR.watch,
            stateFlt: PMGR.stateBtn, 
            partnerFlt: PMGR.partnerBtn,
            ownerFlt: PMGR.owner,
            admusFlt: PMGR.admus,
            cflagFlt: PMGR.cflag,
            query: PMGR.query,
            requestBuilder: PMGR.requestBuilder
        };

        PMGR.myDataTable.my.filter = {
            owner: PMGR.owner, 
            editor: PMGR.admus,
            cflag: PMGR.cflag  
        };

        PMGR.stageBtn.my.table = PMGR.myDataTable;
        PMGR.stateBtn.my.table = PMGR.myDataTable;
        PMGR.partnerBtn.my.table = PMGR.myDataTable;
        
        // Show loading message while page is being rendered
        
        PMGR.myDataTable
            .showTableMessage( PMGR.myDataTable.get("MSG_LOADING"), 
                               YAHOO.widget.DataTable.CLASS_LOADING);
        
               
        PMGR.myDataTable.doBeforeLoadData = 
            function( oRequest, oResponse, oPayload ){
                
                try{
                    var meta = oResponse.meta;
                    oPayload.totalRecords 
                        = meta.totalRecords || oPayload.totalRecords;
                    
                    var oPayloadOld = oPayload;
                    
                    //alert( "dbld: response=" + 
                    //      YAHOO.lang.JSON.stringify(oResponse));
                    oPayload.pagination = {
                        rowsPerPage: meta.paginationRowsPerPage || 10,
                        recordOffset: meta.paginationRecordOffset || 0
                    };
                    
                    if( meta.sortKey !== undefined ){
                        oPayload.sortedBy = { key: meta.sortKey,
                                              dir: "yui-dt-" + meta.sortDir };
                    }
                    
                    //alert( "dbld:\n payload(old)=" + YAHOO.lang.JSON.stringify( oPayloadOld ) +
                    //       "\n meta=" + YAHOO.lang.JSON.stringify( meta ) +
                    //       "\n payload(new)=" + YAHOO.lang.JSON.stringify( oPayload  ));
                    
                } catch (x) {
                    console.log(x);
                }
                return true;
        };
       
        PMGR.myDataTable.handleLayoutChange =
            function( ev, o ){
                //alert("reorder");                
                try{
                    PMGR.myDataTable.my.configmenu.destroy();
                    PMGR.contextMenuInit( PMGR );
                    
                    var nCookie = YAHOO.imex.pubmgr.buildCookie();
                    YAHOO.imex.pubmgr.updateUserTablePref(nCookie);
                    YAHOO.util.Cookie.set("pubmgr", nCookie );                                      
                } catch (x) { }
            };

        
        PMGR.contextMenuInit( PMGR );
        //PMGR.bodyMenuInit( PMGR );

	// record editing popups

        if(YAHOO.imex.recordedit !== undefined){
          console.log("recordedit: LOADING"); 
           YAHOO.imex.recordedit.init( PMGR );
        }

        

        PMGR.myDataTable.on( "columnReorderEvent",
                             PMGR.myDataTable.handleLayoutChange );     
                             

        PMGR.myDataTable.on( "columnHideEvent",
                             PMGR.myDataTable.handleLayoutChange );     
                             

        PMGR.myDataTable.on( "columnShowEvent",
                             PMGR.myDataTable.handleLayoutChange );     
                             
        //tossing in some css to add a black separator between the rows

        var sheet = document.createElement('style');
        sheet.innerHTML = ".yui-dt-data > tr > td {border-bottom: 1px solid black !important;}";
        document.body.appendChild(sheet); 


        // make everyting visibe
        
        YAHOO.util.Dom.removeClass("pubtab", "ic-hidden");
        YAHOO.util.Dom.removeClass("pubsrch", "ic-hidden");
        YAHOO.util.Dom.addClass("spinner", "ic-hidden");
                
        return { 
            ds: PMGR.myDataSource, 
            dt: PMGR.myDataTable 
        };
        
        //YAHOO.imex.pubmgrOld();    
           
    },

    onSelectedMenuItemChange: function (event) {
        var oMenuItem = event.newValue;
        var text = oMenuItem.cfg.getProperty("text");
        
        //alert("menu change: value=" + this.my.value);        

        for( var i = 0; i < this.my.items.length; i++ ){
            if (this.my.items[i].text === text ) {
                this.my.value = this.my.items[i].value;
            }
        }
        
        //alert("menu change: new value=" + this.my.value);

        this.set("label", ("<em class=\"yui-button-label\">" + 
                           oMenuItem.cfg.getProperty("text") + "</em>"));       
    },
    
    selBtnInit: function( o ) {
        try{
            
            // status filter 
            //-------------
        
            o.selmnu =[{ text: "---ANY---", value: "" }];
            
            for( var i = 0; i < o.items.length; i++){
                var text = o.items[i].name;
                var value = o.items[i].value;
                if( value === undefined ){
                    value = text;
                }
                
                //alert("name=" + name + " text="+ text);
                o.selmnu.push( {value: value, text: text} );        
            }
            
            //alert(YAHOO.imex.pubmgr[o.selbtn].my.foo);
            

            var seltext = o.seltext;
            if(o.ival !== undefined && o.ival !== "" ){ 
               seltext = o.ival;
            }   

            o.pmgr[o.selbtn] = new YAHOO.widget.Button(
                { id: o.selnme,  
                  name: o.selnme, 
                  label: "<em class=\"yui-button-label\">" + seltext +"</em>",                   
                  type: "menu",   
                  menu: o.selmnu,  
                  container: o.selcnt }); 
                  

            console.log("IVAL: " + o.ival);

            o.pmgr[o.selbtn].my 
                = { items: o.selmnu, value: o.ival, name: o.selnme };
            
            o.pmgr[o.selbtn].on( 
                "selectedMenuItemChange", 
                o.pmgr.onSelectedMenuItemChange );
            
            if( o.pmgr.myDataTable !== null ){  
                o.pmgr[o.selbtn]
                    .on( "selectedMenuItemChange",
                         o.pmgr.handleFilter, 
                         {filter: o.filter},
                         o.pmgr.myDataTable );
            }
            
        } catch (x) {
            console.log(x);
        }
    },

    contextMenuInit: function( o ){

	console.log("contextMenuInit:");        

        try{
           
            o.myDataTable.my.colmenu = new YAHOO.widget.Menu( "colmenu" );
            
            var defaultTableLayout = function(o){
                var pubmgr = YAHOO.imex.pubmgr;
                if( pubmgr.loginId !== undefined && pubmgr.loginId !== ""
                    && pubmgr.loginId > -1 ){
                   var Success = function( response ){ 

                   };
                   var Fail = function ( o ) {
                        console.log( "AJAX Error update failed: id=" + o.argument.id ); 
                   };
                   var callback = { cache:false, timeout: 5000, 
                                    success: Success,
                                    failure: Fail
                                     }; 
                    
                   try{
                       YAHOO.util.Connect
                        .asyncRequest( 'GET', 
                                       'userprefmgr?id=' + pubmgr.loginId +'&op.defaultTableLayout=true',
                                       callback );        
                    } catch (x) {
                        console.log("AJAX Error:"+x);
                    }
                }
                var cookie = pubmgr.getDefaultCookie();
                pubmgr.buildCDefs(cookie);
                
                YAHOO.util.Cookie.set( "pubmgr", cookie );
                var myDataTable = pubmgr.myDataTable;
                
                pubmgr.init(
                                   {admus: pubmgr.admus,
                                    owner: pubmgr.owner,
                                    cflag: pubmgr.cflag,
                                    query: pubmgr.query,
                                    watch: pubmgr.watch,
                                    stage: pubmgr.stage,
                                    status: pubmgr.status,
                                    jid: pubmgr.jid, 
                                    loginid:pubmgr.loginId });
            };
            
            var oConfMenu = [[{text:"Preferences", disabled: true }],
                             [{text: "Show Columns", 
                               submenu: o.myDataTable.my.colmenu }],
                             [{text:"Restore Default Layout",onclick: {fn: defaultTableLayout } }
                               ],
                             [{text:"Save...", disabled: true}]
                            ];        
            
            var clist=[];
            var trigger=[];
            
            for( var i = 0;  i < o.myColumnDefs.length; i++ ) {
                if( o.myColumnDefs[i].menuLabel !== undefined ) {

                    var trg = o.myDataTable.getColumn( 
                        o.myColumnDefs[i].key ); 

                    var item= {text: o.myColumnDefs[i].menuLabel,
                               checked: !trg.hidden, disabled:  !o.myColumnDefs[i].hideable,
                               onclick: { fn: o.hiddenColToggle,
                                          obj: { tbl: o.myDataTable ,col: trg } } };
                    clist.push( item );

                        
                    //alert( myColumnDefs[i].key+ "::" + trg);
                    if( trg !== null ) {
                        trigger.push( trg.getThEl() );
                        //alert(trg.getThEl());
                    }
                }
            }
            
            o.myDataTable.my.colmenu.addItems( clist );
            
	    console.log("colmenu: " + YAHOO.lang.JSON.stringify( "xxx" ));

            o.myDataTable.my.configmenu = new YAHOO.widget.ContextMenu(
                "configmenu", { trigger: trigger } );
            
            o.myDataTable.my.configmenu.addItems( oConfMenu );
            o.myDataTable.my.configmenu.render("pubtab");
            
        } catch (x) {
            console.log(x);
        }
    },
    //-----------------------------------
    // Hides deselected column attributes 
    //-----------------------------------
     

    hiddenColToggle: function( tp, ev, o ){

        //alert( "col-toggle:" + o);  
        try{
            if( o.col.hidden){
                o.tbl.showColumn( o.col );
                
            } else {
                o.tbl.hideColumn( o.col );                
            }
            o.tbl.render();
            o.tbl.my.configmenu.destroy();
            YAHOO.imex.pubmgr.contextMenuInit( YAHOO.imex.pubmgr );
        } catch (x) {
            console.log("toggle:" + x);
        }

        var nCookie = YAHOO.imex.pubmgr.buildCookie();
        YAHOO.util.Cookie.set("pubmgr", nCookie );                                      
    },
    //-----------------------------
    // Create the custom formatters 
    //-----------------------------
     
    myIcidFormatter: function( elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = "IC-" + oRecord.getData("id") + "-PUB"; 
    },

    myPubFormatter: function( elLiner, oRecord, oColumn, oData ) { 
        YAHOO.util.Dom.replaceClass(elLiner, "yui-dt-liner", "yui-dt-liner-null"); 
        elLiner.innerHTML = '<table width="100%" class="yui-table-inner">' +
            '<tr><th class="yui-table-inner-top">Author(s)</th>' +
            '<td class="yui-table-inner-top">' + 
            oRecord.getData("author") + 
            '</td></tr>' +
            '<tr><th class="yui-table-inner-bottom">Title</th>' + 
            '<td class="yui-table-inner-bottom">' + 
            oRecord.getData("title") + '</td></tr></table>';        
    }, 

    myPmidFormatter: function(elLiner, oRecord, oColumn, oData) {
        var pmid = oRecord.getData("pmid");
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");

        if( pmid.length > 0 ){
            if( typeof YAHOO.widget.DataTable.validateNumber(pmid) !== "undefined" ){
            elLiner.innerHTML = '<a href="http://www.ncbi.nlm.nih.gov/pubmed?term=' + 
                oRecord.getData( "pmid" ) + 
                '" target="icentral-tab">'+ oRecord.getData( "pmid" ) +'</a>';
            }
            else
            elLiner.innerHTML = pmid;
        }
        else
            elLiner.innerHTML = 'N/A';
    },

    myImexFormatter: function(elLiner, oRecord, oColumn, oData) {
        var imex = oRecord.getData("imexId");
        var state = oRecord.getData("state");
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        if( imex.length > 0 && imex !== "N/A" ){
                if( state == "RELEASED" ){
                    elLiner.innerHTML = '<a href="imex/rec/' + 
                        imex + '" target="icentral-tab">'+ imex +'</a>';
                } else {
                    elLiner.innerHTML = imex;
                }
        } else {
            elLiner.innerHTML = 'N/A';
        }
    },

    myElinkFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = '<a href="pubedit?id=' + 
            oRecord.getData( "id" ) + 
            '">details</a>';
    },
    
    myDateFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = oRecord.getData( "date" ) + 
            '<br/>' + oRecord.getData( "time" );
    },

    myActTStampFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = oRecord.getData( "actTStamp" ).replace(" ", "<br>" ); 
    },

    myModTStampFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = oRecord.getData( "modTStamp" ).replace(" ", "<br>" ); 
    },
    
    myCenterFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = oData; 
    },

    myPartnerListFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        if( oRecord !== undefined  &&  oRecord.getData("imexDb") !== undefined ) {            
            elLiner.innerHTML = oRecord.getData( "imexDb" );
        } else {
            elLiner.innerHTML = '<em>N/A</em>';
        } 
    },

    myEditorListFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        if( oRecord !== undefined  &&  oRecord.getData("editor") !== undefined ) {            
            elLiner.innerHTML = oRecord.getData("editor");
        } else {
            elLiner.innerHTML = '<em>N/A</em>';
        }
    },

    myValueFormatter: function( elLiner, oRecord, oColumn, oData ) {

        //if( oRecord !== undefined  &&  oRecord.getData("imexDb") !== undefined ) {                    
        //    var val = parseFloat( Math.round( oRecord.getData("value") * 100) / 100).toFixed(2);
        //    elLiner.innerHTML = "<center>" + val + "</center>";
        //}

        //console.log( "\nrecord: rec: " + YAHOO.lang.JSON.stringify( oRecord ) );
        //console.log( "record: col: " + YAHOO.lang.JSON.stringify( oColumn.key) );
        //console.log( "record: dta: " + YAHOO.lang.JSON.stringify( oData ) + "\n");


         //console.log( "record: val: " + YAHOO.lang.JSON.stringify( oRecord.getData( "score" ) ));   
        
        if( oRecord.getData( "score" ) != undefined 
            && oRecord.getData( "score" )[oColumn.key] ){
             
            elLiner.innerHTML = "<center>" + oRecord.getData( "score" )[oColumn.key] + "</center>";

        }else{
           elLiner.innerHTML = '<center><em>N/A</em></center>';
       }

    },
    
    //--------------------------
    // Add the custom formatters 
    //--------------------------
    formatterInit: function(){
        
        var YDTF = YAHOO.widget.DataTable.Formatter;
        YDTF.icid = this.myIcidFormatter;; 
        YDTF.publication = this.myPubFormatter; 
        YDTF.pmid = this.myPmidFormatter;
        YDTF.imex = this.myImexFormatter;
        YDTF.elink = this.myElinkFormatter; 
        YDTF.crt = this.myDateFormatter; 
        YDTF.ats = this.myActTStampFormatter; 
        YDTF.mts = this.myModTStampFormatter; 
        YDTF.center = this.myCenterFormatter; 
        YDTF.partnerList = this.myPartnerListFormatter; 
        YDTF.editorList = this.myEditorListFormatter; 
        YDTF.value = this.myValueFormatter; 
    },

 
   bodyMenuInit: function( parent ){

      try{
      
         console.log("PMGR.bodyMenuInit: parent.loginId=== " + parent.loginId);
         
         var RE = YAHOO.imex.recordedit;

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
            
	 console.log("  disabled:: " + uid + " :1:: " + YAHOO.lang.JSON.stringify(dFlag));
           
            var oRowMenu = [ [ { text: "Status Update", 
				 disabled: dFlag,
				 submenu: supmenu
                               }],
                             [ { text:"Curate", 
				 disabled: dFlag, 
                                 onclick: { fn: RE.gotoExtUrl, 
                                            obj: { parent: parent,
						   url: RE.parent.curateUrlx,
                                                   idp: parent.curatePat,
                                                   id: "pmid" } 
                                          }
                               }],
                             [ { text:"To PubMed", disabled: false,
                                 onclick: { fn: RE.gotoExtUrl, 
                                            obj: { parent: parent,
						   url: parent.pubmedUrl,
                                                   idp: parent.pubmedPat,
                                                   id: "pmid" } 
                                          }
                               
                               }],
                             [ { text:"To IMEx", disabled: false,
                                 onclick: { fn: RE.gotoExtUrl, 
                                            obj: { parent: parent, 
						   url: parent.imexUrl,
                                                   idp: parent.imexPat,
                                                   id: "imex" } 
                                          } 
                               }]
                           ];        
           console.log("  disabled:: " + uid + " :2:: " + YAHOO.lang.JSON.stringify(dFlag));
            
      
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

   }
    







};

//YAHOO.util.Event.addListener(
//    window, "load", YAHOO.imex.pubmgr ) ;
