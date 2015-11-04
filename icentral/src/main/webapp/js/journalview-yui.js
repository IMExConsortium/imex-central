YAHOO.namespace("imex");

YAHOO.imex.journalview = {

    admus: "",
    owner: "",
    cflag: "",
    watch: "",
    loginId: "",
    
    id: "",
    year:"",
    volume:"",
    issue:"",
    nnav:"",
    
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

/*
        "submission":{ label:"Submission", menuLabel:"Submission", key:"submission", 
                       hideable: true, hidden: true,
                       children:[
                           { key:"date",  label:"Date", 
                             sortable:true, resizeable:false, formatter:"crt" },
                           { key:"owner", label:"Submitted By",
                             sortable:false, resizeable:false, formatter:"list" }
                       ]
                     },
*/
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
        //alert( cookie );

        return cookie;
    },
    init: function( init ){
        
        var journalview = YAHOO.imex.journalview;
        journalview.loginId = init.loginid;
        if( typeof journalview.myDataTable != "undefined" ){
            journalview.myDataTable.my.configmenu.destroy();
            journalview.myDataTable.destroy();
            journalview.myColumnDefs = [];            
        } else {
            this.userTableLayoutInit( init );
        }
        try{
            var cookie = YAHOO.util.Cookie.get("journalview");
            if( cookie == null ){
                cookie = journalview.getDefaultCookie();
        
                YAHOO.util.Cookie.set( "journalview", cookie );
            }
            
            if( cookie !== null ){
                this.buildCDefs( cookie );                
            }
        } catch (x) {
            console.log("INIT: ex="+ x);
        }

        this.initView( init );
        this.historyInit( init );
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
        var journalview = YAHOO.imex.journalview;
        var loginId = journalview.loginId;
        
        if(typeof loginId  != "undefined" && loginId != "")
        {
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

        var PMGR = YAHOO.imex.journalview;


        //alert("buildCDefs:"+cookie );
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
    },

    buildCookie: function(){

        var PMGR = YAHOO.imex.journalview;
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
        console.log("cookie:"+cookie);
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
        //alert (dst );

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

            //alert(PMGR);

            var navig = o.navig;
            var newVal = ev.newValue.value;
            
            var PMGR = YAHOO.imex.journalview;
      
            //alert(navig + ":" + newVal);

  
            var newState = PMGR.myDataSource.my.myState;
      
            console.log("navig:" + navig + " newVal: " + newVal + "\noldstate: " 
                  + YAHOO.lang.JSON.stringify( newState ));
  
            // LS: watch flag ?
        
            newState.startIndex = 0;
            newState.navig[navig] = newVal;
            PMGR.nnav = navig;

            //alert(PMGR.generateStateString( newState ));
            
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
            //alert( "HS: col=" + column.key + " dir=" + sdir );

            if( column.key !== undefined && sdir !==undefined ){
                
                newState.scol = column.key;
                newState.sdir = sdir.substr( 7 );

                //alert( "new sort=(" + newState.scol 
                //       + "," + newState.sdir + ")" );
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

        //alert( "HHN:" + state );
        
        var parsed = PMGR.parseStateString( state );      
        var request = PMGR.buildRequest( parsed );
        
        PMGR.myDataSource.my.myState = parsed;
        
        // update filters
        //---------------

        var flt, 
        sflt = "";
        
        //for( flt in parsed.filter){
        //    sflt += "{" + flt + "=" + parsed.filter[flt] + "}";
        //}
        //alert( sflt );

        // reset filter buttons/menus
        //---------------------------
        
        
        var statusLabel = "---ANY---";
        //PMGR.stateSel[0].text = statusLabel;

        var stageLabel = "---ANY---";
        //PMGR.partnerSel[0].text = partnerLabel;

        if( parsed.filter.status !== ""){
            statusLabel = parsed.filter.status;
        }

        if( parsed.filter.stage !== ""){
            stageLabel = parsed.filter.stage;
        }
        
        if( PMGR.stateBtn.set !== undefined ){           
            //PMGR.stateBtn.set( "label", 
            //                   ("<em class=\"yui-button-label\">" + 
            //                    statusLabel + "</em>"));
            PMGR.stateBtn.set( "label", statusLabel );

        }else{
            PMGR.stateSel[0].text = statusLabel;
        }
        
        if( PMGR.stageBtn.set!== undefined ){
            //PMGR.stageBtn.set( "label", 
            //                     ("<em class=\"yui-button-label\">" + 
            //                      stageLabel + "</em>"));
            PMGR.stageBtn.set( "label", stageLabel);
        }else{
            PMGR.stageSel[0].text = stageLabel;
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
        var filter = {status:status, stage:stage, editor:'', owner:'', cflag:''  };
        var navig = {year:year, volume:volume, issue:issue  };
        var state = {startIndex:0, pageSize:25,filter:filter, navig:navig, scol:'id', sdir:'asc' };
        return YAHOO.lang.JSON.stringify( state );

    },

    buildRequest: function ( state ){
      
        //alert("buildRequest->state" +YAHOO.lang.JSON.stringify(state));

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
        console.log("buildRequest:"  + req);
        return encodeURI( req );
        
    },

    requestBuilder: function( oState, oSelf ) {

        var myself = YAHOO.imex.journalview;
        
        //console.log("requestBuilder->oState=" + YAHOO.lang.JSON.stringify( oState) );

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
        // <        
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
        
        // build custom request
        //---------------------

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

        var PMGR = YAHOO.imex.journalview;
        
        console.log( "initView->init= " + YAHOO.lang.JSON.stringify(init) );
        
        this.formatterInit();
        
        if( init !== undefined ){
            PMGR.admus = init.admus;
            PMGR.owner = init.owner;
            PMGR.cflag = init.cflag;
            PMGR.watch = init.watch;
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

            console.log("YIJV.year="+YIJV.year + " YIJV.volume=" + YIJV.volume + " YIJV.issue=" + YIJV.issue );

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

            //alert("IQ:" + query);

            YAHOO.util.Connect.asyncRequest( 'GET', query, yviCallback );        
        } catch (x) {
            console.log("AJAX Error:"+x);
        }
        
        if( typeof PMGR.myDataTable == "undefined" ){
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

        PMGR.myDataSource = new YAHOO.util.DataSource("journalview?id=" + init.jid 
                                                      + "&op.jppg=jppg&"); 
        
        PMGR.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        PMGR.myDataSource.responseSchema = { 
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
        
        PMGR.myDataSource.my = { myState: null };
        
        PMGR.menuRebuild = function( button, value, vList ){
            
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
        };

        PMGR.myDataSource.doBeforeParseData = function( oRequest , oFullResponse , oCallback ){
            
            try{

                //alert("XXX");

                
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
                
            } catch (x) {
                console.log("AJAX Error:"+x);
            }
        
            return oFullResponse;
            
        };
        

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
            + "&opp.yr=" + YAHOO.imex.year
            + "&opp.vo=" + YAHOO.imex.volume
            + "&opp.is=" + YAHOO.imex.issue

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

        PMGR.myDataTable.my.navig = {
            year: PMGR.year, 
            volume: PMGR.volume,
            issue: PMGR.issue  
        };

        PMGR.stateBtn.my.table = PMGR.myDataTable;
        PMGR.stageBtn.my.table = PMGR.myDataTable;
        
        PMGR.yearBtn.my.table = PMGR.myDataTable;
        PMGR.volumeBtn.my.table = PMGR.myDataTable;
        PMGR.issueBtn.my.table = PMGR.myDataTable;
        

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
                    
                    var nCookie = YAHOO.imex.journalview.buildCookie();
                    YAHOO.imex.journalview.updateUserTablePref(nCookie);
                    YAHOO.util.Cookie.set("journalview", nCookie );                                      
                } catch (x) { }
            };

        
        PMGR.contextMenuInit( PMGR );


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
        
        return { 
            ds: PMGR.myDataSource, 
            dt: PMGR.myDataTable 
        };
        
        //YAHOO.imex.journalviewOld();    
           
    },

    onSelectedMenuItemChange: function (event) {
        var oMenuItem = event.newValue;
        var text = oMenuItem.cfg.getProperty("text");
        
        //alert("menu change: value=" + this.my.value + " text=" + text);        

        for( var i = 0; i < this.my.items.length; i++ ){
            if (this.my.items[i].text === text ) {
                this.my.value = this.my.items[i].value;
            }
        }
        
        //alert("menu change: new value=" + this.my.value);

        //this.set("label", ("<em class=\"yui-button-label\">" + 
        //                   oMenuItem.cfg.getProperty("text") + "</em>"));       

        this.set("label",  oMenuItem.cfg.getProperty("text") );       
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
            
            //alert(YAHOO.imex.journalview[o.selbtn].my.foo);
            
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
            
            if(o.items != null){
                o.navmenu = [];
                for( var i = 0; i < o.items.length; i++){
                    
                    //var text = o.items[i].name;
                    //var value = o.items[i].value;
                    //if( value === undefined ){
                    //    value = text;
                    //}
                    
                    var text = o.items[i];
                    var value = o.items[i];
                    
                    //alert("name=" + name + " text="+ text);
                    o.navmenu.push( {value: value, text: text} );
                }
            }
            
            //alert(YAHOO.imex.journalview[o.selbtn].my.foo);
            
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
            
            //alert("DT: " + o.pmgr.myDataTable );

            if( o.pmgr.myDataTable !== null ){  
                o.pmgr[o.nbtn]
                    .on( "selectedMenuItemChange",
                         o.pmgr.handleNav, 
                         {navig: o.navig},
                         o.pmgr.myDataTable );
                
                //alert("o.nbtn:"+o.nbtn+ " dt:" + o.pmgr.myDataTable);
            }
        } catch (x) {
            console.log(x);
        }
    },

    contextMenuInit: function( o ){
        
        try{

            var myself = YAHOO.imex.journalview;

            // table header context
            //---------------------
            
            o.myDataTable.my.colmenu = new YAHOO.widget.Menu( "colmenu" );
            
            var defaultTableLayout = function(o){
                var journalview = YAHOO.imex.journalview;
                
                if( typeof myself.loginId  != "undefined" && myself.loginId != "" ){
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
                                       'userprefmgr?id=' + myself.loginId +'&op.defaultTableLayout=true',
                                       callback );        
                    } catch (x) {
                        console.log("AJAX Error:"+x);
                    }
                }
                var cookie = myself.getDefaultCookie();
                myself.buildCDefs(cookie);
                
                YAHOO.util.Cookie.set( "journalview", cookie );
                var myDataTable = myself.myDataTable;
                
                myself.init(
                    {admus: myself.admus,
                     owner: myself.owner,
                     cflag: myself.cflag,
                     watch: myself.watch,
                     loginid: myself.loginId });
            };
            
            var oConfMenu = [  [{text:"Preferences", disabled: true }],
                               [{text: "Show Columns", 
                                 submenu: o.myDataTable.my.colmenu }],
                               [{text:"Restore Default Layout",onclick: {fn: defaultTableLayout } } ],
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
                        //trigger.push( trg.getThEl() );
                        //alert(trg.getThEl());
                    }
                }
            }
            
            o.myDataTable.my.colmenu.addItems( clist );
            
            o.myDataTable.my.configmenu = new YAHOO.widget.ContextMenu(
                "configmenu", { trigger: o.myDataTable.getTbodyEl() } );
            
            o.myDataTable.my.configmenu.addItems( oConfMenu );
            o.myDataTable.my.configmenu.render("pubtab");

            // table row context
            //------------------

            var oSupMenu = [ [{text:"Discard", onclick: {fn: myself.statusUpdate, obj: { id: 5} }}],
                             [{text:"Queue", onclick: {fn: myself.statusUpdate, obj: { id: 12} }}],
                             [{text:"Reserve", onclick: {fn: myself.statusUpdate, obj: { id: 2} }}]
                           ];
            o.myDataTable.my.supmenu = new YAHOO.widget.Menu( "supmenu" );
            o.myDataTable.my.supmenu.addItems( oSupMenu );
            
            var oRowMenu = [ [ { text: "Status Update", submenu: o.myDataTable.my.supmenu 
                               }],
                             [ { text:"Curate", disabled: true, 
                                 onclick: { fn: myself.gotoExtUrl, 
                                            obj: { url: "http://www.ebi.ac.uk/intact/editor/publication/%id%", 
                                                   id: "%id%" } 
                                          }
                               }],
                             [ { text:"To PubMed", disabled: true, 
                                 onclick: { fn: myself.gotoExtUrl, 
                                            obj: { url: "http://www.ncbi.nlm.nih.gov/pubmed/%id%", 
                                                   id: "%id%" } 
                                          } 
                               }],
                             [ { text:"To IMEx", disabled: true,
                                 onclick: { fn: myself.gotoExtUrl, 
                                            obj: { url: "http://www.ncbi.nlm.nih.gov/pubmed/%id%", 
                                                   id: "%id%" } 
                                          } 
                               }]
                           ];        
            

            o.myDataTable.my.rowmenu = new YAHOO.widget
                .ContextMenu("rowmenu", 
                             {trigger:o.myDataTable.getTbodyEl()}); 
            o.myDataTable.my.rowmenu.addItems(oRowMenu);

            o.myDataTable.my.rowmenu.render("pubtab"); 

            // table row popup
            //----------------
            
            o.myDataTable.subscribe( "rowClickEvent", myself.rowPopup );            


        } catch (x) {
            console.log(x);
        }
    },
    

    statusUpdate: function( e, o, a ){
        try{
            var myself = YAHOO.imex.journalview;
            var elem = this.parent;
            var tgt = elem.contextEventTarget;
            var record = null;  
            for( var l=5; l>0; l--){
                //console.log( "E: " + l + " : "+ tgt +":" + record );
                if( tgt != undefined){
                    l=0;
                } else{                
                    elem = elem.parent;
                    tgt = elem.contextEventTarget; 
                } 
            }            
            if( tgt != undefined ){

                for( var p=5; p>0; p--){
                    elRow = myself.myDataTable.getTrEl( tgt ); 
                    record =  myself.myDataTable.getRecord( elRow );
                    //console.log( "R:" + p + " : "+elRow +":"+record );
                    if( record == null){
                        tgt = tgt.parentElement;
                    } else{
                        p =0;
                    }
                }
            }

            var  rid = record.getData("id");
            var url = "pubedit?id="+rid+"&op.esup=esup&opp.nsn=" + a.id;
            console.log( "URL: " + url );


            // call url, wait til done

            var urlcallback = {
                
                success: function(o) {
                    
                    var myself = YAHOO.imex.journalview;
                    console.log("urlcallback");
                    
                    if( myself.myDataTable != null ){
                        
                        // reload table
                        try{
                            console.log("reloading...");
                            myself.tableReload( {}, myself.myDataTable);
                        } catch (x) {
                            console.log(x);
                        }
                    }
                },
                failure: function(o) {
                }
            };
            
            var cObj = YAHOO.util.Connect.asyncRequest( 'GET', url,
                                                        urlcallback );
            
        } catch (x) {
            console.log(x);
        }
    },

    gotoExtUrl: function( e, o ){
        

    },

    rowPopup: function( o ){
        try{
            var t = o.target;
            var myself = YAHOO.imex.journalview;
            var trel = myself.myDataTable.getTrEl(t);
            var rec = myself.myDataTable.getRecord(trel);
            var l = 0;
            while( rec == null && l++ <10 ){
                trel = trel.parentElement;
                rec = myself.myDataTable.getRecord(trel);
            }
            var  rdat =  myself.myDataTable.getRecord(trel).getData("id");
            console.log(myself.myDataTable.getRecord(trel));
            console.log("ID=" + myself.myDataTable.getRecord(trel).getId ( ));
            console.log("KEY=" + YAHOO.lang.JSON.stringify(rdat));
            
            var url = "pubedit?id="+rdat+"&op.popup=status";
            var dUrl = "pubedit?id="+rdat+"&op.esup=esup&opp.nsn=5";
            var qUrl = "pubedit?id="+rdat+"&op.esup=esup&opp.nsn=12";
            var rUrl = "pubedit?id="+rdat+"&op.esup=esup&opp.nsn=2";

            YAHOO.mbi.modal.dialog( {mtitle:"Article", 
                                     table: myself.myDataTable,
                                     url: url,
                                     button:[{label:"Discard", id:"discard-btn", url:dUrl},
                                             {label:"ToQueue", id:"queue-btn", url:qUrl},
                                             {label:"Reserve", id:"reserve-btn", url:rUrl} ]
                                    });     
        
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
            YAHOO.imex.journalview.contextMenuInit( YAHOO.imex.journalview );
        } catch (x) {
            console.log("toggle:" + x);
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
                '">'+ oRecord.getData( "pmid" ) +'</a>';
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
        //alert(YAHOO.lang.JSON.stringify(oRecord));

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
