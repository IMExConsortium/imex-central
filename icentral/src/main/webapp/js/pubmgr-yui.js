YAHOO.namespace("imex");

YAHOO.imex.pubmgr = {

    admus: "",
    owner: "",
    cflag: "",
    
    stateBtn: { my:{value:"",foo:"state"} },    
    stateSel: [ { text: "---ANY---", value: "" } ],
    
    partnerBtn: { my:{value:"",foo:"partner"} },    
    partnerSel: [ { text: "---ANY---", value: "" } ],

    myDataSource: null,
    myPaginator: null,
    
    myColumnDefs: [
        //{ key:"del",label:"",  sortable:false, resizeable:false,
        //  formatter:"checkbox", className:"checkbox" },
        { key:"id", label:"ICID", menuLabel:"ICID",
          sortable:true, resizeable:true, hidden: true,
          formatter:"icid" },
        { key:"pub", label:"Publication", menuLabel:"Publication",
          sortable:true, resizeable:true, 
          formatter:"publication", maxAutoWidth:1000  },
        { key:"pmid", label:"PMID", menuLabel:"PMID", 
          sortable:true, resizeable:true, 
          formatter:"center", className:"pmid" },
        { key:"imexId", label:"<center>Imex<br/>Accession</center>", 
          sortable:true, resizeable:true, 
          formatter:"center",menuLabel:"Imex Accession" },
        { key:"imexDb", label:"<center>Imex<br/>Partner</center>", 
          sortable:true, resizeable:true, 
          formatter:"partnerList",menuLabel:"Imex Partner" },
        { key:"state", label:"Status", 
          sortable:true, resizeable:false, 
          formatter:"center", menuLabel:"Status" },
        { label:"Submission", menuLabel:"Submission", key:"submission",
          children:[
             { key:"date",  label:"Date", 
               sortable:true, resizeable:false, formatter:"crt" },
             { key:"owner", label:"Submitted By",
               sortable:true, resizeable:false, formatter:"list" }
          ]
        },
        { key:"editor", label:"Curator(s)", menuLabel:"Curator(s)",
          sortable:true, resizeable:true, 
          formatter:"editorList" },
        { key:"detail", label:"",
          sortable:false, resizeable:true, 
          formatter:"elink", className:"detail" }
    ],
    
    requestBuilder: function( oState, oSelf ) {

        alert("myRequestBuilder");

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
        
        // filters
        //--------

        //oSelf.my.stateFlt.my.value;
        var sfVal = YAHOO.imex.pubmgr.stateBtn.my.value;
        
        // oSelf.my.partnerFlt.my.value;
        var pfVal = YAHOO.imex.pubmgr.partnerBtn.my.value;
        
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
        
        var req = "opp.skey=" + sort +
            "&opp.sfv=" + sfVal +
            "&opp.pfv=" + pfVal +
            "&opp.efv=" + efVal +
            "&opp.ofv=" + ofVal +
            "&opp.ffv=" + ffVal +
            "&opp.sdir=" + dir +
            "&opp.off=" + startIndex +
            "&opp.max=" + results; 

        req = encodeURI(req);
        //alert("request: " + req);
        
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
            alert(x);
        }
    },


    init: function( init ){
        this.initView(init);
        this.historyInit();
    },

    historyInit: function(){
        
        var defstate = {
            startIndex: 0,
            pageSize: 25,
            filter:{ status: "",
                     partner:"",
                     editor:"",
                     owner:"",
                     cflag:""},
            scol: "id",
            sdir: "asc" };
                 
        var dst = YAHOO.lang.JSON.stringify( defstate );
        
        var bState = YAHOO.util.History.getBookmarkedState( "pubmgr" );
        var iState = bState || dst;
        
        var PMGR = YAHOO.imex.pubmgr;
        
        YAHOO.util.History.register( "pubmgr", iState, 
                                     PMGR.handleHistoryNavigation ); 
        
        YAHOO.util.History.onReady( PMGR.historyReadyHandler );    
        
        try{
            YAHOO.util.History.initialize( "yui-history-field", 
                                           "yui-history-iframe" );
        } catch (x) {
            alert(x);
        }                
    },

    handleFilter: function( ev, o ){

        var filter = o.filter;
        var newVal = ev.newValue.value;
        
        var PMGR = YAHOO.imex.pubmgr;
        
        var newState = PMGR.myDataSource.my.myState;
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
            alert(x);
        }
        
    },
    
    handleHistoryNavigation: function( state ){

        var PMGR = YAHOO.imex.pubmgr;

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
        var partnerLabel = "---ANY---";

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
        }
        
        if( PMGR.partnerBtn.set!== undefined ){
            PMGR.partnerBtn.set( "label", 
                                 ("<em class=\"yui-button-label\">" + 
                                  partnerLabel + "</em>"));
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

    buildRequest: function ( state ){
      
        var req = "opp.off=" + state.startIndex + 
            "&opp.max=" + state.pageSize + 
            "&opp.sfv=" + state.filter.status +
            "&opp.pfv=" + state.filter.partner +
            "&opp.efv=" + state.filter.editor +
            "&opp.ofv=" + state.filter.owner +
            "&opp.ffv=" + state.filter.cflag +
            "&opp.skey=" + state.scol +
            "&opp.sdir=" + state.sdir;

        return encodeURI(req);
        
    },

    historyReadyHandler: function(){

        try{
            var cState = YAHOO.util.History.getCurrentState( "pubmgr" );
            YAHOO.imex.pubmgr.handleHistoryNavigation( cState );
        } catch (x) {
            alert(x);
        }
    },

    initView: function( init ) { 

        var PMGR = YAHOO.imex.pubmgr;
        
        this.formatterInit();
        
        if( init !== undefined ){
            PMGR.admus = init.admus;
            PMGR.owner = init.owner;
            PMGR.cflag = init.cflag;
        }
        
        //alert("init: "+ init.cflag);

        var partnerSuccess = function( o ){
            var messages = YAHOO.lang.JSON.parse( o.responseText );
            YAHOO.imex.pubmgr.selBtnInit( 
                { pmgr: YAHOO.imex.pubmgr,
                  filter: "partner",
                  items: messages.acom,
                  selmnu: YAHOO.imex.pubmgr.partnerSel,
                  selbtn: "partnerBtn",
                  selcnt: "partner-button-container",
                  selnme: "partner-button" });
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
                  items: messages.acom,
                  selmnu: YAHOO.imex.pubmgr.stateSel,
                  selbtn: "stateBtn",
                  selcnt: "state-button-container",
                  selnme: "state-button" });
        };
        
        var stateCallback = { cache:false, timeout: 5000, 
                              success: stateSuccess,
                              failure: stateSuccess,
                              argument:{}}; // id:obj.id, btn:imexButton } };                  
        try{
            YAHOO.util.Connect
                .asyncRequest( 'GET', 
                               "acom?op.pstac=ac" , 
                               stateCallback );        
            YAHOO.util.Connect
                .asyncRequest( 'GET', 
                               "acom?op.pagac=ac", 
                               partnerCallback );        
        } catch (x) {
            alert("AJAX Error:"+x);
        }

        // create datasource
        //------------------

        PMGR.myDataSource = new YAHOO.util.DataSource("pubmgr?op.ppg=44&"); 
        
        PMGR.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        PMGR.myDataSource.responseSchema = { 
            resultsList: "records.records", 
            fields: ["id","author","title","pmid","imexId",
                     "owner","state","date","time","imexDb","editor"], 
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
            + "&opp.ofv=" + YAHOO.imex.pubmgr.owner 
            + "&opp.efv=" + YAHOO.imex.pubmgr.admus 
            + "&opp.ffv=" + YAHOO.imex.pubmgr.cflag;

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
            stateFlt: PMGR.stateBtn, 
            partnerFlt: PMGR.partnerBtn,
            ownerFlt: PMGR.owner,
            admusFlt: PMGR.admus,
            cflagFlt: PMGR.cflag,
            requestBuilder: PMGR.requestBuilder
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
                    
                    //alert( "dbld: response=" + 
                    //       YAHOO.lang.JSON.stringify(oResponse));
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
                    alert(x);
                }
                return true;
        };
        
        try{
            
            PMGR.myDataTable.my.colmenu = new YAHOO.widget.Menu( "colmenu" );
            
            var oConfMenu = [[{text:"Preferences" }],
                             [{text: "Show Columns", 
                               submenu: PMGR.myDataTable.my.colmenu }],
                             [{text:"Save...", disabled: true}]
                            ];        
            
            var clist=[];
            var trigger=[];
            
            for( var i = 0;  i < PMGR.myColumnDefs.length; i++ ) {
                if( PMGR.myColumnDefs[i].menuLabel !== undefined ) {
                    var item= {text: PMGR.myColumnDefs[i].menuLabel,
                               checked: true };
                    clist.push(item);
                    var trg = PMGR.myDataTable.getColumn( 
                        PMGR.myColumnDefs[i].key );
                    //alert( myColumnDefs[i].key+ "::"+trg);
                    if( trg !== null ) {
                        trigger.push( trg.getThEl() );
                        //alert(trg.getThEl());
                    }
                }
            }
            
            PMGR.myDataTable.my.colmenu.addItems( clist );
            
            PMGR.myDataTable.my.configmenu = new YAHOO.widget.ContextMenu(
                "configmenu", { trigger: trigger } );
            
            PMGR.myDataTable.my.configmenu.addItems( oConfMenu );
            PMGR.myDataTable.my.configmenu.render("pubtab");
            
        } catch (x) {
            alert(x);
        }
        
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
            
            o.pmgr[o.selbtn] = new YAHOO.widget.Button(
                { id: o.selnme,  
                  name: o.selnme, 
                  label: "<em class=\"yui-button-label\">---ANY---</em>", 
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
                         {filter: o.filter}, // o.pmgr.myDataTable, 
                         o.pmgr.myDataTable );
            }
            
        } catch (x) {
            alert(x);
        }
    },

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
    
    myElinkFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = '<a href="pubmgr?id=' + 
            oRecord.getData( "id" ) + 
            '">details</a>';
    },
    
    myDateFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = oRecord.getData( "date" ) + 
            '<br/>' + oRecord.getData( "time" );
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
            elLiner.innerHTML = '<i>N/A</i>';
        } 
    },

    myEditorListFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        if( oRecord !== undefined  &&  oRecord.getData("editor") !== undefined ) {            
            elLiner.innerHTML = oRecord.getData("editor");
        } else {
            elLiner.innerHTML = '<i>N/A</i>';
        } 
    },

    formatterInit: function(){
        
        // Add the custom formatters 
        //--------------------------
 
        var YDTF = YAHOO.widget.DataTable.Formatter;
        YDTF.icid = this.myPubIcidFormatter;; 
        YDTF.publication = this.myPubFormatter; 
        YDTF.elink = this.myElinkFormatter; 
        YDTF.crt = this.myDateFormatter; 
        YDTF.center = this.myCenterFormatter; 
        YDTF.partnerList = this.myPartnerListFormatter; 
        YDTF.editorList = this.myEditorListFormatter; 
    }

};

//YAHOO.util.Event.addListener(
//    window, "load", YAHOO.imex.pubmgr ) ;
