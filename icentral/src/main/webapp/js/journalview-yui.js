YAHOO.namespace("imex");

YAHOO.imex.journalview = {

    admus: "",
    owner: "",
    cflag: "",
    watch: "",
    loginId: null,
    prefs: null, 
   
    id: "",
    year:"",
    volume:"",
    issue:"",
    nnav:"",
    
    curateUrl: "",
    curatePat: "",
    pubmedUrl: "http://www.ncbi.nlm.nih.gov/pubmed/%%pmid%%",
    pubmedPat: "%%pmid%%",
    imexUrl: "imex/rec/%%imex%%",
    imexPat: "%%imex%%",
    
    stateBtn: { my:{value:"",foo:"state"} },    
    stateSel: [ { text: "---ANY---", value: "" } ],
    
    stageBtn: { my:{value:"",foo:"stage"} },    
    stageSel: [ { text: "---ANY---", value: "" } ],

    yearBtn: { my:{value:"",foo:"year"} },    
    yearSel: [ { text: "---ANY---", value: "" } ],

    yearFBtn: { my:{value:"--",foo:"year"} },    
    yearPtn: { my:{value:"-",foo:"year"} },    
    yearNBtn: { my:{value:"+",foo:"year"} },    
    yearLBtn: { my:{value:"++",foo:"year"} },    

    volumeBtn: { my:{value:"",foo:"voulme"} },    
    volumeSel: [ { text: "---ANY---", value: "" } ],

    volumeFBtn: { my:{value:"--",foo:"year"} },    
    volumePtn: { my:{value:"-",foo:"year"} },    
    volumeNBtn: { my:{value:"+",foo:"year"} },    
    volumeLBtn: { my:{value:"++",foo:"year"} },    


    issueBtn: { my:{value:"",foo:"issue"} },    
    issueSel: [ { text: "---ANY---", value: "" } ],

    issueFBtn: { my:{value:"--",foo:"year"} },    
    issuePtn: { my:{value:"-",foo:"year"} },    
    issueNBtn: { my:{value:"+",foo:"year"} },    
    issueLBtn: { my:{value:"++",foo:"year"} },    

    myDataSource: null,
    myPaginator: null,
    
    myCD: { 
        "id":{ key:"id", label:"ICID", menuLabel:"ICID",
               sortable:true, resizeable:true, hidden: true, hideable: true,
               formatter:"icid" },
        
        "pub":{ key:"pub", label:"Publication", menuLabel:"Publication",
                sortable:true, resizeable:true, hideable: false,
                formatter:"publication", maxAutoWidth:1000  },

        "pages":{ key:"pages", label:"Pages", menuLabel:"Pages",
                  sortable:true, resizeable:true, hideable: false,
                  formatter:"center", maxAutoWidth:200  },

        "pmid":{ key:"pmid", label:"PMID", menuLabel:"PMID", 
                 sortable:true, resizeable:true,  hideable: true,
                 formatter:"pmid", className:"pmid"},

        "imexId":{ key:"imexId", label:"<center>Imex<br/>Accession</center>", 
                   sortable:true, resizeable:true, hideable: true,
                   formatter:"imex",menuLabel:"Imex Accession" },

        
        //"submission":{ label:"Submission", menuLabel:"Submission", key:"submission", 
        //               hideable: true, hidden: true,
        //               children:[
        //                   { key:"date",  label:"Date", 
        //                     sortable:true, resizeable:false, formatter:"crt" },
        //                   { key:"owner", label:"Submitted By",
        //                     sortable:false, resizeable:false, formatter:"list" }
        //               ]
        //             },

        "state":{ key:"state", label:"Status", 
                  sortable:false, resizeable:false, hideable: true, 
                  formatter:"center", menuLabel:"Status" },

        "stage":{ key:"stage", label:"Stage", 
                  sortable:false, resizeable:false, hideable: true, 
                  formatter:"center", menuLabel:"Stage" },


        "detail":{ key:"detail", label:"",
                   sortable:false, resizeable:true, hideable: true,
                   formatter:"elink", className:"detail" }        
    },

    
    myCP: { "date": "submission"},

    myCL: [ "id", "pub", "pages", "pmid", "imexId", // "submission",
            "stage", "state",  "detail" ],
    myColumnDefs: [],
    
    tableReload: function( o, dt ) {
        try {            
            var state = dt.get('paginator').getState();
            //state.page=1;
            //state.recordOffset=0;
	    console.log("state: page=" + state.page + " off="  + state.recordOffset );
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
            console.log(x);
        }
    },

    getDefaultCookie: function(){
        var cookie = "";
        var journalview = YAHOO.imex.journalview;
        for(var i = 0; i < journalview.myCL.length; i++ ){

            var hidden= false;
            if(  journalview.myCD[journalview.myCL[i]].hidden === true ){
                hidden= true;
            }
            cookie += journalview.myCD[journalview.myCL[i]].key + ":" + hidden +"|";
        }
        return cookie;
    },

    init: function( init ){
        try{    
            var JV = YAHOO.imex.journalview;
            JV.loginId = init.loginid;
            JV.prefStr = init.prefStr;
        
            if( JV.prefs == null && 
		JV.prefStr != null && 
		JV.prefStr != "" ){
            
                var p = JV.prefStr.replace(/&quot;/g, '"');           
                JV.prefs = YAHOO.lang.JSON.parse( p  );
                JV["curateUrl"]
                    = JV.prefs["option-def"]["curation-tool"]["option-def"]["curation-url"].value;
                
                JV["curatePat"]
                    = JV.prefs["option-def"]["curation-tool"]["option-def"]["curation-pmid-pattern"].value;
            }
            
            if( typeof JV.myDataTable != "undefined" ){
                JV.myDataTable.my.contextmenu.destroy();
                JV.myDataTable.destroy();
                JV.myColumnDefs = [];            
            } else {
                this.userTableLayoutInit( init );
            }
            
            var cookie = YAHOO.util.Cookie.get("journalview");
            if( cookie == null ){
                cookie = JV.getDefaultCookie();
                
                YAHOO.util.Cookie.set( "journalview", cookie );
            }
            
            if( cookie !== null ){
                this.buildCDefs( cookie );                
            }
            
            this.initView( init );
            this.historyInit( init );

            console.log("INIT: DONE");
            
        } catch (x) {
             console.log("INIT ERROR: "+ x);
        }
        
    },

    //----------------------------------------------------------------
    //if a user is logged in this sets the cookie to their preferences
    //----------------------------------------------------------------

    userTableLayoutInit: function( init ){
        var journalview = YAHOO.imex.journalview;
        if( typeof journalview.loginId  != "undefined" && journalview.loginId != "" ){
            var Success = function( response ){                           
            
                var cookie = YAHOO.util.Cookie.get("journalview");
           
                if( cookie == null ){
                    cookie = journalview.getDefaultCookie();
                    YAHOO.util.Cookie.set( "journalview", cookie );
                }
                
                var responseText = YAHOO.lang.JSON.parse(response.responseText);
                var preferences = YAHOO.lang.JSON.parse(responseText.preferences);
                if(preferences.tableLayout == "null")
                {
                    journalview.updateUserTablePref(cookie);
                }
                else
                {
                    cookie = preferences.tableLayout;
                    YAHOO.util.Cookie.set( "journalview", cookie );
                    journalview.buildCDefs( cookie );
                }
                
                init.admus = journalview.admus;
                init.owner = journalview.owner;
                init.cflag = journalview.cflag;
                init.watch = journalview.watch;
                init.loginid = journalview.loginId;

                //journalview.init( init );
                //                    {admus: journalview.admus,
                //                     owner: journalview.owner,
                //                     cflag: journalview.cflag,
                //                     watch: journalview.watch,
                //                     loginid:journalview.loginId });
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
                               'userprefmgr?id=' + journalview.loginId +'&op.view=true',
                               callback );        
            } catch (x) {
                console.log("AJAX Error:"+x);
            }
        }
    },
    
    updateUserTablePref: function( cookie ){
        var my = YAHOO.imex.journalview;
        var loginId = my.loginId;
        
        if( typeof loginId  != "undefined" && loginId != "" ){
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
    
    buildCDefs: function( cookie ){

        var my = YAHOO.imex.journalview;
        var col = cookie.split("|");
        
        if(col.length > 0){
            my.myColumnDefs=[];
            
            for( var i =0; i < col.length-1; i++ ){
                try{
                
                    var cs = col[i].split(":");
                    if( my.myCD[cs[0]] !== undefined ){
                        
                        my.myColumnDefs.push(my.myCD[cs[0]]);
                        if(cs[1] === 'true'){
                            my.myColumnDefs[my.myColumnDefs.length-1].hidden = true;
                        } else {
                            my.myColumnDefs[my.myColumnDefs.length-1].hidden = false;
                        }
                    }
                    
                } catch (x) {
                    console.log( "I:"+ i + "-> " + cs[0] + " ex=" + x );
                }
            }        
        }
    },

    buildCookie: function(){

        var my = YAHOO.imex.journalview;
        var cookie = "";
        var ac = my.myDataTable.getColumnSet().keys;
        for(var i=0; i< ac.length; i++ ){

            var key = ac[i].getKey();
            var hid = ac[i].hidden;
            if( my.myCD[key] !== undefined ){
                cookie += key + ":" + hid + "|";
            } else {              // nested column
                key = my.myCP[key];
                if( my.myCD[key] !== undefined ){
                    cookie += key + ":" + hid + "|";
                }
            }
        }
        console.log( "cookie:" + cookie );
        return cookie;
    },

    historyInit: function( init ){

        var PMGR = YAHOO.imex.journalview;
        
        var defstate = {
            startIndex: 0,
            pageSize: 25,
            filter:{ status: "",
                     stage: "",
                     editor: PMGR.admus,
                     owner: PMGR.owner,
                     cflag: PMGR.cflag},
            navig:{ year: PMGR.year,
                    volume: PMGR.volume,
                    issue: PMGR.issue},
            watch: PMGR.watch,
            scol: "id",
            sdir: "asc" };
        
        if(  init !== undefined && init.watch !== undefined ){
            defstate.watch = init.watch;
        }

        var dst = YAHOO.lang.JSON.stringify( defstate );        
        var bState = YAHOO.util.History.getBookmarkedState( "journalview" );
        var iState = bState || dst;
        
        var PMGR = YAHOO.imex.journalview;
        
        YAHOO.util.History.register( "journalview", iState, 
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
        
        var PMGR = YAHOO.imex.journalview;
        
        var newState = PMGR.myDataSource.my.myState;
        
        // LS: watch flag ?
        
        newState.startIndex = 0;
        newState.filter[filter] = newVal;
        
        //alert(PMGR.generateStateString( newState ));

        YAHOO.util.History
            .navigate( "journalview", 
                       PMGR.generateStateString( newState ) );
    },

    handleNav: function( ev, o ){

        try{
            var navig = o.navig;
            var newVal = ev.newValue.value;
            
            var PMGR = YAHOO.imex.journalview;
            var newState = PMGR.myDataSource.my.myState;
      
            console.log("navig:" + navig + " newVal: " + newVal + "\noldstate: " 
                  + YAHOO.lang.JSON.stringify( newState ));
            
  
            // LS: watch flag ?
        
            newState.startIndex = 0;
            newState.navig[navig] = newVal;
            PMGR.nnav = navig;

            //alert(PMGR.generateStateString( newState ));
            console.log("newstate: "
                        + YAHOO.lang.JSON.stringify( newState ));            
            
            console.log("year: "+ YAHOO.imex.journalview.year 
                        + " volume: " + YAHOO.imex.journalview.volume
                        + " issue: " + YAHOO.imex.journalview.issue + "\n" );
            
            YAHOO.util.History
                .navigate( "journalview", 
                           PMGR.generateStateString( newState ) );
            
        } catch (x) {
            console.log(x);
        }
    },

    handlePagination: function( state, datatable ){
        
        var PMGR = YAHOO.imex.journalview;
        var newState = PMGR.myDataSource.my.myState;
        
        newState.startIndex = state.recordOffset;
        newState.pageSize = state.rowsPerPage;
        
        YAHOO.util.History
            .navigate( "journalview", 
                       PMGR.generateStateString( newState ) );
    },
    
    handleSorting: function( column ){
        
        var PMGR = YAHOO.imex.journalview;
        var newState = PMGR.myDataSource.my.myState;
        try{
            var sdir = this.getColumnSortDir( column );
            newState.startIndex = 0;
            
            if( column.key !== undefined && sdir !==undefined ){
                newState.scol = column.key;
                newState.sdir = sdir.substr( 7 );
            }
            
            YAHOO.util.History
                .navigate( "journalview", 
                           PMGR.generateStateString( newState ) );
        } catch (x) {
            console.log(x);
        }
        
    },
    
    handleHistoryNavigation: function( state ){
        
        var PMGR = YAHOO.imex.journalview;
        
        var parsed = PMGR.parseStateString( state );      
        var request = PMGR.buildRequest( parsed );
        
        PMGR.myDataSource.my.myState = parsed;
        console.log("HandleHistoryNavigation:-> request:" + request );
        
        // update filters
        //---------------

        var flt, 
        sflt = "";
        
        // reset filter buttons/menus
        //---------------------------
        
        var statusLabel = "---ANY---";
        var stageLabel = "---ANY---";

        if( parsed.filter.status !== "" && 
	    parsed.filter.status !== null){
            statusLabel = parsed.filter.status;
        } else {
	    parsed.filter.status = "";
	}
        
        if( parsed.filter.stage !== "" && 
	    parsed.filter.stage !== null){
            stageLabel = parsed.filter.stage;
        } else {
	    parsed.filter.stage = "";
	}
        
        if( PMGR.stateBtn.set !== undefined ){           
            PMGR.stateBtn.set( "label", statusLabel );

        }else{
            PMGR.stateSel[0].text = statusLabel;
        }
        
        if( PMGR.stageBtn.set!== undefined ){
            PMGR.stageBtn.set( "label", stageLabel);
        }else{
            PMGR.stageSel[0].text = stageLabel;
        }

	// reload stage/state filter button menus
	//---------------------------------------

	console.log("HHN: status="+parsed.filter.status +
		    " stage="+ parsed.filter.stage );

        var buttonUpdateSuccess = function( o ){	    
            var messages = YAHOO.lang.JSON.parse( o.responseText );
	    var button=o.argument.btn;
	    //console.log("HHN: buttonUpdateSuccess: button=" + button.getMenu + " type=" + typeof button.getMenu);

	    if( button.getMenu !== undefined){

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
              argument: { btn: YAHOO.imex.journalview.stageBtn } }	

        var statusUpdateCallback = 
	    { cache:false, timeout: 5000, 
              success: buttonUpdateSuccess,
              failure: buttonUpdateFailure,
              argument: { btn:YAHOO.imex.journalview.stateBtn } };	

        try{
	    var oppstg ="";
	    if( parsed.filter.stage !== "" && parsed.filter.stage !== null){
		oppstg = "&opp.stage=" + parsed.filter.stage;		
	    }
	    console.log("HHN: oppstg="+oppstg);

            YAHOO.util.Connect.asyncRequest( 'GET', 
                                             "acom?op.pstac=ac" + oppstg, 
                                             statusUpdateCallback );	    
	    var oppsts ="";
	    if( parsed.filter.status !== "" && parsed.filter.status !== null){
		oppsts = "&opp.status=" + parsed.filter.status;		
	    }
	    console.log("HHN: oppsts="+oppsts);

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
    
    generateLinkState: function( status, stage, year, volume, issue ){

        //LS: watch ?
        var filter = { status:status, stage:stage, editor:'', owner:'', cflag:''  };
        var navig = { year:year, volume:volume, issue:issue  };
        var state = { startIndex:0, pageSize:25, filter:filter, 
		      navig:navig, scol:'id', sdir:'asc' };
        return YAHOO.lang.JSON.stringify( state );
    },

    buildRequest: function ( state ){
        
        var req = "opp.off=" + state.startIndex + 
            "&opp.wfl=" + state.watch + 
            "&opp.max=" + state.pageSize +
            "&opp.yr=" + state.navig.year +
            "&opp.vo=" + state.navig.volume +
            "&opp.is=" + state.navig.issue + 
            "&opp.sfv=" + state.filter.status +
            "&opp.gfv=" + state.filter.stage +
            "&opp.efv=" + state.filter.editor +
            "&opp.ofv=" + state.filter.owner +
            "&opp.ffv=" + state.filter.cflag +
            "&opp.skey=" + state.scol +
            "&opp.sdir=" + state.sdir +
            "&opp.nnav=" + YAHOO.imex.journalview.nnav;
        
        YAHOO.imex.journalview.nnav = "";
        return encodeURI( req );
        
    },

    requestBuilder: function( oState, oSelf ) {

        var myself = YAHOO.imex.journalview;
        
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
        var sfVal = myself.stateBtn.my.value;
        
        // oSelf.my.partnerFlt.my.value;
        var gfVal = myself.stageBtn.my.value;
        
        if( sfVal === undefined ){
            sfVal = "";
        }
        if( gfVal === undefined ){
            gfVal = "";
        }

        // navigation
        //-----------

        //var yrVal = myself.yearBtn.my.value;
        //var voVal = myself.volumeBtn.my.value;
        //var isVal = myself.issueBtn.my.value;

        var yrVal = myself.yearCurrent.get( "value");
        var voVal = myself.volumeCurrent.get( "value");
        var isVal = myself.issueCurrent.get( "value"); 
        
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
            "&opp.yr=" + yrVal + 
            "&opp.vo=" + voVal + 
            "&opp.is=" + isVal + 
            "&opp.sfv=" + sfVal +
            "&opp.gfv=" + gfVal +
            "&opp.efv=" + efVal +
            "&opp.ofv=" + ofVal +
            "&opp.ffv=" + ffVal +
            "&opp.sdir=" + dir +
            "&opp.off=" + startIndex +
            "&opp.max=" + results; 
        
        req = encodeURI(req);
        console.log("request: " + req);
        
        return req;
    },

    historyReadyHandler: function(){

        try{
            var cState = YAHOO.util.History.getCurrentState( "journalview" );
            YAHOO.imex.journalview.handleHistoryNavigation( cState );
        } catch (x) {
            console.log(x);
        }
    },

    initView: function( init, cord, chid ) { 
        try{
    
            var PMGR = YAHOO.imex.journalview;
            var JV = YAHOO.imex.journalview;
            console.log( "initView->init= " + YAHOO.lang.JSON.stringify(init) );
        
            this.formatterInit();
        
            if( init !== undefined ){
                JV.admus = init.admus;
                JV.owner = init.owner;
                JV.cflag = init.cflag;
                JV.watch = init.watch;
            }
            
            var stageSuccess = function( o ){
                var messages = YAHOO.lang.JSON.parse( o.responseText );
                
                YAHOO.imex.journalview.selBtnInit( 
                    { pmgr: YAHOO.imex.journalview,
                      filter: "stage",
                      items: messages.acom,
                      selmnu: YAHOO.imex.journalview.stageSel,
                      selbtn: "stageBtn",
                      selcnt: "stage-button-container",
                      selnme: "stage-button",
                      seltext: YAHOO.imex.journalview.stageSel[0].text});     
            };
        
            var stageCallback = { cache:false, timeout: 5000, 
                                  success: stageSuccess,
                                  failure: stageSuccess,
                                  argument:{}}; // id:obj.id, btn:imexButton } };                  
            
            var stateSuccess = function( o ){
                var messages = YAHOO.lang.JSON.parse(o.responseText);
                YAHOO.imex.journalview.selBtnInit( 
                    { pmgr: YAHOO.imex.journalview,
                      filter: "status",
                      items: messages.acom,
                      selmnu: YAHOO.imex.journalview.stateSel,
                      selbtn: "stateBtn",
                      selcnt: "state-button-container",
                      selnme: "state-button", 
                      seltext: YAHOO.imex.journalview.stateSel[0].text});
            };
            
            var stateCallback = { cache:false, timeout: 5000, 
                                  success: stateSuccess,
                                  failure: stateSuccess,
                                  argument:{}}; // id:obj.id, btn:imexButton } };                  
            
        
            var yviSuccess = function( o ){
                var messages = YAHOO.lang.JSON.parse(o.responseText);
            
                var YIJV = YAHOO.imex.journalview;
                YIJV.year = messages.init.year;
                YIJV.volume = messages.init.volume;
                YIJV.issue = messages.init.issue;
                
                console.log( "YIJV.year="+YIJV.year + 
			     " YIJV.volume=" + YIJV.volume + 
			     " YIJV.issue=" + YIJV.issue );

                // journal title
                //--------------

                //alert("RT:" + o.responseText);
                          
                var jname = YAHOO.util.Dom.get('journal-name');
                jname.textContent=messages.init.title;
                
                // year/volume/issue buttons
                YAHOO.imex.journalview.navBtnInit(
                    { pmgr: YAHOO.imex.journalview,
                      nbtn: "yearCurrent",
                      navig: "year",
                      ntxt: messages.init.year,
                      items: messages.init['year-list'],
                      navmnu: YAHOO.imex.journalview.yearSel,
                      ncnt: "year-curr-container",
                      nnme: "year-curr-button"
                    }
                );
            
                YAHOO.imex.journalview.navBtnInit(
                    { pmgr: YAHOO.imex.journalview,
                      nbtn: "volumeCurrent",
                      navig: "volume",
                      ntxt: messages.init.volume,
                      items: messages.init['volume-list'],
                      menu: YAHOO.imex.journalview.volumeSel,
                      ncnt: "volume-curr-container",
                      nnme: "volume-curr-button"
                    }
                );
                YAHOO.imex.journalview.navBtnInit(
                    { pmgr: YAHOO.imex.journalview,
                      nbtn: "issueCurrent",
                      navig: "issue",
                      ntxt: messages.init.issue, 
                      items: messages.init['issue-list'],
                      menu: YAHOO.imex.journalview.issueSel,
                      ncnt: "issue-curr-container",
                      nnme: "issue-curr-button"
                    }
                );
            };
        
            var yviCallback = { cache:false, timeout: 5000, 
                                success: yviSuccess,
                                failure: yviSuccess,
                                argument:{}}; // id:obj.id, btn:imexButton } };                  
            
            try{

                var query =  "journalview?id="+init.jid
                    + "&op.init="+init.jid + "&opp.year=" + init.year
                    + "&opp.volume=" + init.volume + "&opp.issue=" + init.issue;
                
                YAHOO.util.Connect.asyncRequest( 'GET', query, yviCallback );        
            } catch (x) {
                console.log("AJAX Error:"+x);
            }
        
            if( typeof JV.myDataTable == "undefined" ){
                try{
                    YAHOO.util.Connect.asyncRequest( 'GET', 
                                                     "acom?op.pstac=ac" , 
                                                     stateCallback );        
                    YAHOO.util.Connect.asyncRequest( 'GET', 
                                                     "acom?op.psgac=ac", 
                                                     stageCallback );        
                } catch (x) {
                    console.log("AJAX Error:"+x);
                }
            }
            
            // create datasource
            //------------------

            JV.myDataSource = new YAHOO.util.DataSource("journalview?id=" + init.jid 
                                                          + "&op.jppg=jppg&"); 
            
            JV.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
            JV.myDataSource.responseSchema = { 
                resultsList: "records.records", 
                fields: ["id","author","title","pmid","imexId",
                         "pages","issue","volume","year","stage",
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
                    fPar: "records.filter.stage",
                    fFlg: "records.filter.cflag" 
                }
            }; 
            
            JV.myDataSource.my = { myState: null };
            
            JV.menuRebuild = function( button, value, vList ){

                if( button !== undefined){

                    try{                
			button.set( "label", value );
			button.set( "value", value );
                    
			var menu = button.getMenu();
			var menuItems=[];
			for( var i=0; i<vList.length; i++ ){
                            menuItems[i]= { text: vList[i], value: vList[i] };
			}
			
			if( YAHOO.util.Dom.inDocument( menu.element ) ){                    
                            menu.clearContent();                 
                            menu.addItems( menuItems );
                            menu.render();
			} else {
                            menu.itemData = menuItems;
			}
		    
                    } catch(x){
			console.log( "AJAX Error:" + x );
                    }
		}
            };
            
            JV.myDataSource.doBeforeParseData = function( oRequest , oFullResponse , oCallback ){
            
                try{
                    
                    var myself = YAHOO.imex.journalview;
                    var filter = oFullResponse.records.filter;
                    
                    // update buttons/menus
                    
                    myself.menuRebuild( myself.yearCurrent, filter.year,  
                                        oFullResponse.init["year-list"] );
                    
                    if(oFullResponse.init["year-list"].length <=1){
                        document.getElementById("year-first-container").style.display ="none"; 
                        document.getElementById("year-prev-container").style.display="none";  
                        document.getElementById("year-next-container").style.display="none"; 
                        document.getElementById("year-last-container").style.display="none"; 
                    } else{
                        document.getElementById("year-first-container").style.display="inline"; 
                        document.getElementById("year-prev-container").style.display="inline";  
                        document.getElementById("year-next-container").style.display="inline"; 
                        document.getElementById("year-last-container").style.display="inline";                     
                    }
                    
                    myself.menuRebuild( myself.volumeCurrent, filter.volume,
                                        oFullResponse.init["volume-list"] );
                    
                    if(oFullResponse.init["volume-list"].length <=1){ 
                        document.getElementById("volume-first-container").style.display="none"; 
                        document.getElementById("volume-prev-container").style.display="none";
                        document.getElementById("volume-next-container").style.display="none";
                        document.getElementById("volume-last-container").style.display="none";
                    } else{
                        document.getElementById("volume-first-container").style.display="inline"; 
                        document.getElementById("volume-prev-container").style.display="inline";
                        document.getElementById("volume-next-container").style.display="inline";
                        document.getElementById("volume-last-container").style.display="inline";                    
                    }
                    
                    myself.menuRebuild( myself.issueCurrent, filter.issue,
                                        oFullResponse.init["issue-list"] );
                    
                    if(oFullResponse.init["year-list"].length <=1){
                        document.getElementById("issue-first-container").style.display="none"; 
                        document.getElementById("issue-prev-container").style.display="none";
                        document.getElementById("issue-next-container").style.display="none";
                        document.getElementById("issue-last-container").style.display="none";
                    } else{
                        document.getElementById("issue-first-container").style.display="inline"; 
                        document.getElementById("issue-prev-container").style.display="inline";
                        document.getElementById("issue-next-container").style.display="inline";
                        document.getElementById("issue-last-container").style.display="inline";
                    }                       
                    
                    myself.myDataSource.my.myState.navig.year = filter.year;
                    myself.myDataSource.my.myState.navig.volume = filter.volume;
                    myself.myDataSource.my.myState.navig.issue = filter.issue;
                    
                    var JV = YAHOO.imex.journalview;
                    if( filter.year != null){
                        //console.log("beforeparse:  year=" + filter.year);
                        JV.year = filter.year;
                    }
                    if( filter.volume != null){
                        //console.log("beforeparse:  volume=" + filter.volume);
                        JV.volume = filter.volume;
                    }
                    if( filter.issue != null){
                        //console.log("beforeparse:  issue=" + filter.issue);
                        JV.issue = filter.issue;
                    }
                } catch (x) {
                    console.log("AJAX Error:"+x);
                }
                
                return oFullResponse;            
            };
            
            // create paginator
            //-----------------
        
            JV.myPaginator = new YAHOO.widget.Paginator(
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
                + "&opp.yr=" + YAHOO.imex.journalview.year
                + "&opp.vo=" + YAHOO.imex.journalview.volume
                + "&opp.is=" + YAHOO.imex.journalview.issue
            
                + "&opp.wfl=" + YAHOO.imex.journalview.watch 
                + "&opp.ofv=" + YAHOO.imex.journalview.owner 
                + "&opp.efv=" + YAHOO.imex.journalview.admus 
                + "&opp.ffv=" + YAHOO.imex.journalview.cflag;
            
            var myConfig = {
                paginator : this.myPaginator,
                initialLoad: false,
                dynamicData : true,
                draggableColumns: true
            };
            
            // Instantiate DataTable
            //----------------------
            
            JV.myDataTable = new YAHOO.widget.DataTable(
                "pubtab", PMGR.myColumnDefs, 
                JV.myDataSource, myConfig
            );
            
            JV.myPaginator
                .unsubscribe( "changeRequest", 
                              JV.myDataTable.onPaginatorChangeRequest );
            JV.myPaginator
                .subscribe( "changeRequest", 
                            JV.handlePagination, JV.myDataTable, true );
            
            JV.myDataTable.sortColumn = JV.handleSorting;
            
            JV.myDataTable.my = { 
                watchFlg: JV.watch,
                stateFlt: JV.stateBtn, 
                partnerFlt: JV.partnerBtn,
                ownerFlt: JV.owner,
                admusFlt: JV.admus,
                cflagFlt: JV.cflag,
                requestBuilder: JV.requestBuilder
            };
            
            JV.myDataTable.my.filter = {
                owner: JV.owner, 
                editor: JV.admus,
                cflag: JV.cflag  
            };
            
            JV.myDataTable.my.navig = {
                year: JV.year, 
                volume: JV.volume,
                issue: JV.issue  
            };
            
            JV.stateBtn.my.table = JV.myDataTable;
            JV.stageBtn.my.table = JV.myDataTable;
            
            JV.yearBtn.my.table = JV.myDataTable;
            JV.volumeBtn.my.table = JV.myDataTable;
            JV.issueBtn.my.table = JV.myDataTable;
            
            // Show loading message while page is being rendered
            
            JV.myDataTable
                .showTableMessage( JV.myDataTable.get("MSG_LOADING"), 
                                   YAHOO.widget.DataTable.CLASS_LOADING);
            
            JV.myDataTable.doBeforeLoadData = 
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
                        console.log(x);
                    }
                    return true;
                };
            
            JV.myDataTable.handleLayoutChange = function( ev, o ){
                try{
                    var nCookie = YAHOO.imex.journalview.buildCookie();
                    YAHOO.imex.journalview.updateUserTablePref( nCookie );
                    YAHOO.util.Cookie.set( "journalview", nCookie );
                } catch (x) { 
                    console.log(x);
                }
            };
            
	    // header menu
            
	    PMGR.headMenuInit( PMGR );
            
	    // record editing popups
	    
	    if(YAHOO.imex.recordedit !== undefined){
		YAHOO.imex.recordedit.init(JV);
            }

            // table row popup
            //----------------
            /*
            console.log("myself.loginId:"+ PMGR.loginId );
            
            if( JV.loginId != null && PMGR.loginId> 0 ){
                JV.myDataTable.subscribe( "rowClickEvent", JV.rowPopup );
            }
            */
            
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
            
        } catch (x) {
            console.log("ERR:" + x);
        }
        
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
        
        this.set( "label",  oMenuItem.cfg.getProperty("text") );       
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
                  //label: "<em class=\"yui-button-label\">" + o.seltext +"</em>", 
                  label: o.seltext, 
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
            console.log(x);
        }
    },
    
    navBtnInit: function( o ) {

        try{
            
            // navigate button 
            //----------------
            
            o.navmenu = [{ text: o.ntxt, value: o.ntxt }];
            
            if( o.items != null ){
                o.navmenu = [];
                for( var i = 0; i < o.items.length; i++){
                    
                    //var text = o.items[i].name;
                    //var value = o.items[i].value;
                    //if( value === undefined ){
                    //    value = text;
                    //}
                    
                    var text = o.items[i];
                    var value = o.items[i];
                    
                    o.navmenu.push( {value: value, text: text} );
                }
            }
            
            o.pmgr[o.nbtn] = new YAHOO.widget.Button(
                { id: o.nbtn,  
                  name: o.nbtn, 
                  //label: "<em class=\"yui-button-label\">" + o.ntxt +"</em>", 
                  label: o.ntxt, 
                  type: "menu",
                  menu: o.navmenu,
                  container: o.ncnt }); 
            
            o.pmgr[o.nbtn].my 
                = { items: o.navmenu, value: "", name: o.bname };
            
            o.pmgr[o.nbtn].on( 
                "selectedMenuItemChange", 
                o.pmgr.onSelectedMenuItemChange );
            
            if( o.pmgr.myDataTable !== null ){  
                o.pmgr[o.nbtn]
                    .on( "selectedMenuItemChange",
                         o.pmgr.handleNav, 
                         {navig: o.navig},
                         o.pmgr.myDataTable );
            }
        } catch (x) {
            console.log(x);
        }
    },

    headMenuInit:  function( o ){
        try{
            var my = YAHOO.imex.journalview;
            var colmenu = new YAHOO.widget.Menu( "colmenu" );
            
            var defaultTableLayout = function( o ){
                var my = YAHOO.imex.journalview;    
                
                if( typeof my.loginId  != "undefined" && my.loginId != "" ){
                    
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
                                           'userprefmgr?id=' + my.loginId +
					   '&op.defaultTableLayout=true',
                                           callback );        
                    } catch (x) {
                        console.log("AJAX Error:"+x);
                    }
                }
                var cookie = my.getDefaultCookie();
                my.buildCDefs(cookie);
                
                YAHOO.util.Cookie.set( "pubmgr", cookie );
                var myDataTable = my.myDataTable;
                
                my.init( {admus: my.admus,
                          owner: my.owner,
                          cflag: my.cflag,
                          watch: my.watch,
                          loginid: my.loginId });
            };
            
            var clist = [];
            
            for( var i = 0;  i < my.myColumnDefs.length; i++ ){
                if( my.myColumnDefs[i].menuLabel !== undefined ){

		    
		    console.log("HMB: col=" + my.myColumnDefs[i].menuLabel);
                    var trg = my.myDataTable.getColumn( 
                        my.myColumnDefs[i].key ); 
                    
                    var item = { text: my.myColumnDefs[i].menuLabel, 
                                 id: "headmenu-col-" + my.myColumnDefs[i].key,
                                 checked: !trg.hidden, disabled:  !my.myColumnDefs[i].hideable,
                                 onclick: { fn: my.hiddenColToggle,
                                            obj: { tbl: my.myDataTable ,col: trg } } };
                    clist.push( item );
                }
            }
            
            colmenu.addItems( clist );

            var oConfMenu = [[{text:"Preferences", disabled: true }],
                             [{text: "Show Columns", 
                               submenu: colmenu }],
                             [{text:"Restore Default Layout",onclick: {fn: defaultTableLayout } }
                             ],
                             [{text:"Save...", disabled: true}]
                            ];        
            
            my.myDataTable.my.colmenu = colmenu;
            my.myDataTable.my.headmenu = new YAHOO.widget.ContextMenu(
                "headmenu", { trigger: my.myDataTable.getTheadEl() } );
            
            my.myDataTable.my.headmenu.addItems( oConfMenu );
            my.myDataTable.my.headmenu.render("pubtab");
            my.myDataTable.my.headmenu.subscribe( "triggerContextMenu", my.onHeadMenuTrigger);
            
        }  catch( x ){
            console.log( "HeadMenu: INIT ERROR: " + x );
        } 
    },
    
    onHeadMenuTrigger: function(p_sType, p_aArgs, p_myDataTable) {
        try{
            var my = YAHOO.imex.journalview;
            var table = my.myDataTable;
            var items = my.myDataTable.my.colmenu.getItems();
            
            for( var i =0; i< items.length; i++ ){
                var item = items[ i ];
                var id = item.id;
                var col = table.getColumn(id.replace( "headmenu-col-", "" ) );
                item.cfg.setProperty("checked", !col.hidden);
            }
        } catch (x) {
            console.log( "HeadMenu: TRIGGER ERROR: " + x );
        }
    },

    //-----------------------------------
    // Hides deselected column attributes 
    //-----------------------------------
    
    hiddenColToggle: function( tp, ev, o ){
        
        console.log( "hiddenColToggle: o=" + o );
        console.log( "hiddenColToggle: hidden(m): " + o.col.hidden );
        console.log( "hiddenColToggle: hidden(c): " + o.tbl.getColumn( o.col ).hidden );

        try{
            if( o.col.hidden ){
                o.tbl.showColumn( o.col );
            } else {
                o.tbl.hideColumn( o.col );                
            }
            o.tbl.render();
        } catch (x) {
            console.log("ERROR: toggle:" + x);
        }
        
        var nCookie = YAHOO.imex.journalview.buildCookie();
        YAHOO.util.Cookie.set("journalview", nCookie );                                      
         
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
//    window, "load", YAHOO.imex.journalview ) ;
