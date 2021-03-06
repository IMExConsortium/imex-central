YAHOO.namespace("imex");

YAHOO.imex.stagemgr = {

    admus: "",
    owner: "",
    cflag: "",
    watch: "",
    loginId: "",
    stage: "",

    curateUrl: "",
    curatePat: "",
    pubmedUrl: "http://www.ncbi.nlm.nih.gov/pubmed/%%pmid%%",
    pubmedPat: "%%pmid%%",
    imexUrl: "imex/rec/%%imex%%",
    imexPat: "%%imex%%",
    
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

    myCL: [ "id", "pub", "pmid", "imexId", "imexDb",
            "state", "submission", "modified", "activity", "editor", "detail" ],
    myColumnDefs: [],

    requestBuilder: function( oState, oSelf ) {
        
        //alert("requestBuilder->oState=" + YAHOO.lang.JSON.stringify(oState) );

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
        
        // filters
        //--------

        //oSelf.my.stateFlt.my.value;
        var sfVal = YAHOO.imex.stagemgr.stateBtn.my.value;
        
        // oSelf.my.partnerFlt.my.value;
        var pfVal = YAHOO.imex.stagemgr.partnerBtn.my.value;
        
        if( sfVal === undefined ){
            sfVal = "";
        }
        if( pfVal === undefined ){
            pfVal = "";
        }
        
        var efVal = oSelf.my.admusFlt;
        var ofVal = oSelf.my.ownerFlt;
        var ffVal = oSelf.my.cflagFlt;

        // watch flag 
        
        var wtFlg = oSelf.my.watchFlg;
        if( wtFlg === undefined ){
            wtFlg = "";
        }

        var req = "opp.skey=" + sort +
            "&opp.wfl=" + wtFlg + 
            "&opp.sfv=" + sfVal +
            "&opp.pfv=" + pfVal +
            "&opp.efv=" + efVal +
            "&opp.ofv=" + ofVal +
            "&opp.ffv=" + ffVal +
            "&opp.sdir=" + dir +
            "&opp.off=" + startIndex +
            "&opp.max=" + results; 
        
        req = encodeURI(req);

        // build custom request
        //---------------------

        return req;
    },

    tableReload: function( o, dt ) {
        try {           
            var state = dt.get('paginator').getState();
            state.page=1;
            state.recordOffset=0;
            dt.get('paginator').setState( state );
            
            var reloadCallback = {
                success: dt.onDataReturnInitializeTable,
                failure: dt.onDataReturnInitializeTable,
                scope: dt,
                argument: dt.getState()
            };
                
            var reloadRequest = 
                dt.my.requestBuilder( dt.getState(), dt );
            
            dt.getDataSource().sendRequest( reloadRequest, 
                                            reloadCallback );
        } catch (x) {   
            console.error( x );
        }
    },
    
    getDefaultCookie: function(){
        var cookie = "";
        var stagemgr = YAHOO.imex.stagemgr;
        for(var i = 0; i < stagemgr.myCL.length; i++ ){

            var hidden= false;
            if(  stagemgr.myCD[stagemgr.myCL[i]].hidden === true ){
                hidden= true;
            }
            cookie += stagemgr.myCD[stagemgr.myCL[i]].key + ":" + hidden +"|";
        }
        return cookie;
    },

    init: function( init ){
        var SM = YAHOO.imex.stagemgr;
        SM.loginId = init.loginid;
	SM.stage = init.stage;

	if( SM.prefs == null && 
	    SM.prefStr != null && 
	    SM.prefStr != "" ){
            
            var p = SM.prefStr.replace(/&quot;/g, '"');           
            SM.prefs = YAHOO.lang.JSON.parse( p  );
            SM["curateUrl"]
                = SM.prefs["option-def"]["curation-tool"]["option-def"]["curation-url"].value;
                
            SM["curatePat"]
                = SM.prefs["option-def"]["curation-tool"]["option-def"]["curation-pmid-pattern"].value;
        }
        
        if( typeof SM.myDataTable != "undefined" ){
            SM.myDataTable.my.configmenu.destroy();
            SM.myDataTable.destroy();
            SM.myColumnDefs = [];            
        } else {
            this.userTableLayoutInit( init );
        }
        try{
            var cookie = YAHOO.util.Cookie.get("stagemgr");
	    if( cookie == null ){
                cookie = SM.getDefaultCookie();
		
                YAHOO.util.Cookie.set( "stagemgr", cookie );
            } else{
		//alert( cookie );
	    }
            
            if( cookie !== null ){
                this.buildCDefs( cookie );                
            }
        } catch (x) {
            console.error( x );
        }
        this.initView( init );
        this.historyInit( init );
    },
    //----------------------------------------------------------------
    //if a user is logged in this sets the cookie to their preferences
    //----------------------------------------------------------------

    userTableLayoutInit: function( init ){
        
	var stagemgr = YAHOO.imex.stagemgr;
	
        if( typeof stagemgr.loginId  != "undefined" 
	    && stagemgr.loginId != ""){
            
	    var Success = function( response ){                           
		
                var cookie = YAHOO.util.Cookie.get("stagemgr");
           
                if( cookie == null ){
                    cookie = stagemgr.getDefaultCookie();
                    YAHOO.util.Cookie.set( "stagemgr", cookie );
                }
                
                var responseText = YAHOO.lang.JSON.parse(response.responseText);
                var preferences = YAHOO.lang.JSON.parse(responseText.preferences);
                if( preferences.tableLayout == "null" ){
                    stagemgr.updateUserTablePref(cookie); 
		}else{
                    cookie = preferences.tableLayout;
                    YAHOO.util.Cookie.set( "stagemgr", cookie );
                    stagemgr.buildCDefs( cookie );
                }
            };
        
	    var Fail = function ( o ) {
                console.error( o.argument.id ); 
            };
            var callback = { cache:false, timeout: 5000, 
                             success: Success,
                             failure: Fail
                           }; 
            
            try{
                YAHOO.util.Connect
                .asyncRequest( 'GET', 
                               'userprefmgr?id=' + stagemgr.loginId +'&op.view=true',
                               callback );        
            } catch (x) {
                console.error( x );
            }
        }
    },
    
    updateUserTablePref: function( cookie ){
        var stagemgr = YAHOO.imex.stagemgr;
        var loginId = stagemgr.loginId;
        
        if( typeof loginId  != "undefined" && loginId != "" ){
            try{
                YAHOO.util.Connect
                .asyncRequest( 'POST', 
                               'userprefmgr?id=' + loginId + '&op.updateTable=true',
                               null, "opp.tableLayout=" + cookie );        
            } catch (x) {
                console.error( x );
            }
        }
    },
    
    buildCDefs: function( cookie ){

        var PMGR = YAHOO.imex.stagemgr;

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
                    
                }catch( x ){
                    console.error( "I:"+ i + "-> " + cs[0] + " ex=" + x );
                }
            }        
        }
    },

    buildCookie: function(){

        var PMGR = YAHOO.imex.stagemgr;
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
        return cookie;
    },

    historyInit: function( init ){

        var PMGR = YAHOO.imex.stagemgr;
        
        var defstate = {
            startIndex: 0,
            pageSize: 25,
            filter:{ status: "",
                     partner:"",
                     editor:PMGR.admus,
                     owner: PMGR.owner,
                     cflag: PMGR.cflag},
            watch: PMGR.watch,
            scol: "id",
            sdir: "asc" };
                 
        if(  init !== undefined && init.watch !== undefined ){
            defstate.watch = init.watch;
        }

        var dst = YAHOO.lang.JSON.stringify( defstate );
        
        var bState = YAHOO.util.History.getBookmarkedState( "stagemgr" );
        var iState = bState || dst;
        
        var PMGR = YAHOO.imex.stagemgr;
        
        YAHOO.util.History.register( "stagemgr", iState, 
                                     PMGR.handleHistoryNavigation ); 
        
        YAHOO.util.History.onReady( PMGR.historyReadyHandler );    
        
        try{
            YAHOO.util.History.initialize( "yui-history-field", 
                                           "yui-history-iframe" );
        } catch (x) {
            console.error( x );
        }                
    },

    handleFilter: function( ev, o ){

        var filter = o.filter;
        var newVal = ev.newValue.value;
        
        var PMGR = YAHOO.imex.stagemgr;
        
        var newState = PMGR.myDataSource.my.myState;
        
        // LS: watch flag ?
        
        newState.startIndex = 0;
        newState.filter[filter] = newVal;
        
        YAHOO.util.History
            .navigate( "stagemgr", 
                       PMGR.generateStateString( newState ) );
    },

    handlePagination: function( state, datatable ){
        
        var PMGR = YAHOO.imex.stagemgr;
        var newState = PMGR.myDataSource.my.myState;
        
        newState.startIndex = state.recordOffset;
        newState.pageSize = state.rowsPerPage;
        
        YAHOO.util.History
            .navigate( "stagemgr", 
                       PMGR.generateStateString( newState ) );
    },
    
    handleSorting: function( column ){
        
        var PMGR = YAHOO.imex.stagemgr;
        var newState = PMGR.myDataSource.my.myState;
        try{
            var sdir = this.getColumnSortDir( column );
            newState.startIndex = 0;

            if( column.key !== undefined && sdir !==undefined ){
                
                newState.scol = column.key;
                newState.sdir = sdir.substr( 7 );
            }

            YAHOO.util.History
                .navigate( "stagemgr", 
                           PMGR.generateStateString( newState ) );
        } catch (x) {
            console.error( x );
        }
        
    },
    
    handleHistoryNavigation: function( state ){
        
        var PMGR = YAHOO.imex.stagemgr;
        
        var parsed = PMGR.parseStateString( state );      
        var request = PMGR.buildRequest( parsed );
        
        PMGR.myDataSource.my.myState = parsed;
        
        // update filters
        //---------------

        var flt, 
        sflt = "";
        
        // reset filter buttons/menus
        //---------------------------
        
        var statusLabel = "---ANY---";
        //PMGR.stateSel[0].text = statusLabel;

        var partnerLabel = "---ANY---";
        //PMGR.partnerSel[0].text = partnerLabel;

        if( parsed.filter.status !== ""){
            statusLabel = parsed.filter.status;
        }

        if( parsed.filter.partner !== ""){
            partnerLabel = parsed.filter.partner;
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
    
    generateLinkState: function(status, partner){

        //LS: watch ?
        var filter = {status:status, partner:partner, editor:'', owner:'', cflag:''  };
        var state = {startIndex:0, pageSize:25,filter:filter, scol:'id', sdir:'asc' };
        return YAHOO.lang.JSON.stringify( state );
	
    },
    
    buildRequest: function ( state ){
      
        //alert("buildRequest->state" +YAHOO.lang.JSON.stringify(state));

        var req = "opp.off=" + state.startIndex + 
            "&opp.wfl=" + state.watch + 
            "&opp.max=" + state.pageSize + 
            "&opp.sfv=" + state.filter.status +
            "&opp.pfv=" + state.filter.partner +
            "&opp.efv=" + state.filter.editor +
            "&opp.ofv=" + state.filter.owner +
            "&opp.ffv=" + state.filter.cflag +
            "&opp.skey=" + state.scol +
            "&opp.sdir=" + state.sdir;
        
        return encodeURI( req );        
    },

    historyReadyHandler: function(){

        try{
            var cState = YAHOO.util.History.getCurrentState( "stagemgr" );
            YAHOO.imex.stagemgr.handleHistoryNavigation( cState );
        } catch (x) {
            console.log(x);
        }
    },

    initView: function( init, cord, chid ) { 

        var PMGR = YAHOO.imex.stagemgr;
        this.formatterInit();
        
        if( init !== undefined ){
            PMGR.admus = init.admus;
            PMGR.owner = init.owner;
            PMGR.cflag = init.cflag;
            PMGR.watch = init.watch;
        }
        
        var partnerSuccess = function( o ){
            var messages = YAHOO.lang.JSON.parse( o.responseText );
            
            YAHOO.imex.stagemgr.selBtnInit( 
                { pmgr: YAHOO.imex.stagemgr,
                  filter: "partner",
                  items: messages.acom,
                  selmnu: YAHOO.imex.stagemgr.partnerSel,
                  selbtn: "partnerBtn",
                  selcnt: "partner-button-container",
                  selnme: "partner-button" ,
                  seltext: YAHOO.imex.stagemgr.partnerSel[0].text});
        };
        
        var partnerCallback = { cache:false, timeout: 5000, 
                                success: partnerSuccess,
                                failure: partnerSuccess,
                                argument:{}}; // id:obj.id, btn:imexButton } };                  

        var stateSuccess = function( o ){
            var messages = YAHOO.lang.JSON.parse(o.responseText);
            YAHOO.imex.stagemgr.selBtnInit( 
                { pmgr: YAHOO.imex.stagemgr,
                  filter: "status",
                  items: messages.acom,
                  selmnu: YAHOO.imex.stagemgr.stateSel,
                  selbtn: "stateBtn",
                  selcnt: "state-button-container",
                  selnme: "state-button", 
                  seltext: YAHOO.imex.stagemgr.stateSel[0].text});
        };
        
        var stateCallback = { cache:false, timeout: 5000, 
                              success: stateSuccess,
                              failure: stateSuccess,
                              argument:{}}; // id:obj.id, btn:imexButton } };                  

        if( typeof PMGR.myDataTable == "undefined" ){
            try{
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   "acom?opp.stage="+PMGR.stage+"&op.pstac=ac" , 
                                   stateCallback );        
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   "acom?op.pagac=ac", 
                                   partnerCallback );        
            } catch (x) {
                console.error( x );
            }
        }
         
        // create datasource
        //------------------
	
        PMGR.myDataSource = new YAHOO.util.DataSource("stagemgr?stage="+PMGR.stage+"&op.ppg=ppg&"); 
        
        PMGR.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        PMGR.myDataSource.responseSchema = { 
            resultsList: "records.records", 
            fields: ["id","author","title","pmid","imexId",
                     "owner","state","date","time","imexDb","editor",
                     "actTStamp","actUser","modTStamp","modUser"], 
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
            + "&opp.wfl=" + YAHOO.imex.stagemgr.watch 
            + "&opp.ofv=" + YAHOO.imex.stagemgr.owner 
            + "&opp.efv=" + YAHOO.imex.stagemgr.admus 
            + "&opp.ffv=" + YAHOO.imex.stagemgr.cflag;

        var myConfig = {
            paginator : this.myPaginator,
            initialLoad: false,
            dynamicData : true,
            draggableColumns: true
        };
        
        // Instantiate DataTable
        //----------------------
    
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
            requestBuilder: PMGR.requestBuilder
        };

        PMGR.myDataTable.my.filter = {
            owner: PMGR.owner, 
            editor: PMGR.admus,
            cflag: PMGR.cflag  
        };

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

                    oPayload.pagination = {
                        rowsPerPage: meta.paginationRowsPerPage || 10,
                        recordOffset: meta.paginationRecordOffset || 0
                    };
                    
                    if( meta.sortKey !== undefined ){
                        oPayload.sortedBy = { key: meta.sortKey,
                                              dir: "yui-dt-" + meta.sortDir };
                    }
                    
                } catch (x) {
                    console.error( x );
                }
                return true;
        };
       
        PMGR.myDataTable.handleLayoutChange =
            function( ev, o ){
                try{
                    PMGR.myDataTable.my.configmenu.destroy();
                    PMGR.contextMenuInit( PMGR );
                    
                    var nCookie = YAHOO.imex.stagemgr.buildCookie();
                    YAHOO.imex.stagemgr.updateUserTablePref(nCookie);
                    YAHOO.util.Cookie.set("stagemgr", nCookie );                                      
                } catch (x) { }
            };

        // header menu
	
        PMGR.contextMenuInit( PMGR );

	// record-editing popups (if requested )
	     
	if( YAHOO.imex.recordedit !== undefined ){
	    YAHOO.imex.recordedit.init( PMGR );
        }

	// table layout changes

        PMGR.myDataTable.on( "columnReorderEvent",
                             PMGR.myDataTable.handleLayoutChange );     
                             

        PMGR.myDataTable.on( "columnHideEvent",
                             PMGR.myDataTable.handleLayoutChange );     
                             

        PMGR.myDataTable.on( "columnShowEvent",
                             PMGR.myDataTable.handleLayoutChange );     

        // CSS to add a black separator between the rows

        var sheet = document.createElement('style');
        sheet.innerHTML = ".yui-dt-data > tr > td {border-bottom: 1px solid black !important;}";
        document.body.appendChild(sheet); 
        
        return { 
            ds: PMGR.myDataSource, 
            dt: PMGR.myDataTable 
        };
        
        // initView ends here 
           
    },

    onSelectedMenuItemChange: function (event) {
        var oMenuItem = event.newValue;
        var text = oMenuItem.cfg.getProperty("text");
        
        for( var i = 0; i < this.my.items.length; i++ ){
            if (this.my.items[i].text === text ) {
                this.my.value = this.my.items[i].value;
            }
        }
        
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
                o.selmnu.push( {value: value, text: text} );        
            }
            
            o.pmgr[o.selbtn] = new YAHOO.widget.Button(
                { id: o.selnme,  
                  name: o.selnme, 
                  label: "<em class=\"yui-button-label\">" + o.seltext +"</em>", 
                  type: "menu",   
                  menu: o.selmnu,  
                  container: o.selcnt }); 
            
            o.pmgr[o.selbtn].my 
                = { items: o.selmnu, value: "", name: o.selnme };
            
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
            console.error( x );
        }
    },
    
    contextMenuInit: function( o ){
        
        try{       
            o.myDataTable.my.colmenu = new YAHOO.widget.Menu( "colmenu" );
            
            var defaultTableLayout = function( o ){
                var stagemgr = YAHOO.imex.stagemgr;
                if( typeof stagemgr.loginId  != "undefined" 
		    && stagemgr.loginId != "" ){
                    var Success = function( response ){ 
			// should do something here, shouldn't I ? 
                    };
                    var Fail = function ( o ) {
                        console.error( "failed: id=" + o.argument.id ); 
                    };
                    var callback = { cache:false, timeout: 5000, 
                                     success: Success,
                                     failure: Fail
                                     }; 
                    
                    try{
                        YAHOO.util.Connect
                            .asyncRequest( 'GET', 
					   'userprefmgr?id=' + stagemgr.loginId +'&op.defaultTableLayout=true',
					   callback );        
                    } catch (x) {
                        console.log("AJAX Error:"+x);
                    }
                }
                var cookie = stagemgr.getDefaultCookie();
                stagemgr.buildCDefs(cookie);
                
                YAHOO.util.Cookie.set( "stagemgr", cookie );
                var myDataTable = stagemgr.myDataTable;
                
                stagemgr.init( {admus: stagemgr.admus,
                                owner: stagemgr.owner,
                                cflag: stagemgr.cflag,
                                watch: stagemgr.watch,
                                loginid:stagemgr.loginId });
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
		    
                    if( trg !== null ){
                        trigger.push( trg.getThEl() );
                    }
                }
            }
            
            o.myDataTable.my.colmenu.addItems( clist );
            
            o.myDataTable.my.configmenu = new YAHOO.widget.ContextMenu(
                "configmenu", { trigger: trigger } );
            
            o.myDataTable.my.configmenu.addItems( oConfMenu );
            o.myDataTable.my.configmenu.render( "pubtab" );
            
        }catch(x){
            console.error( x );
        }
    },
    //-----------------------------------
    // Hides deselected column attributes 
    //-----------------------------------
     

    hiddenColToggle: function( tp, ev, o ){
	try{
            if( o.col.hidden){
                o.tbl.showColumn( o.col );
                
            } else {
                o.tbl.hideColumn( o.col );                
            }
            o.tbl.render();
            o.tbl.my.configmenu.destroy();
            YAHOO.imex.stagemgr.contextMenuInit( YAHOO.imex.stagemgr );
        } catch (x) {
            console.error( x );
        }

        var nCookie = YAHOO.imex.stagemgr.buildCookie();
        YAHOO.util.Cookie.set( "stagemgr", nCookie );                                      
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
                        imex + '">'+ imex +'</a>';
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
    }
};

//YAHOO.util.Event.addListener(
//    window, "load", YAHOO.imex.stagemgr ) ;
