YAHOO.namespace("imex");

YAHOO.imex.pubmgr = {

    admus: "",
    owner: "",
    cflag: "",
    
    stateBtn: { my:{value:"",foo:"state"}},    
    stateSel: [ 
        { text: "---ANY---", value: "" } 
    ],
    
    partnerBtn: { my:{value:"",foo:"partner"}},    
    partnerSel: [
        { text: "---ANY---", value: "" }
        //{ text: "DIP", value: "DIP" },
        //{ text: "IntAct", value: "INTACT" },
        //{ text: "MINT", value: "MINT" },
        //{ text: "MPIDB", value: "MPIDB" }    ],
    ],

    myDataSource: null,
    myPaginator: null,
    
    myColumnDefs: [
        //{ key:"del",label:"",  sortable:false, resizeable:false,
        //  formatter:"checkbox", className:"checkbox" },
        { key:"pub", label:"Publication", sortable:true, resizeable:true, 
          formatter:"publication", maxAutoWidth:1000, menuLabel:"Publication" },
        { key:"pmid", label:"PMID", sortable:true, resizeable:true, 
          formatter:"center", className:"pmid", menuLabel:"PMID" },
        { key:"imexId", label:"<center>Imex<br/>Accession</center>", sortable:true, resizeable:true, 
          formatter:"center",menuLabel:"Imex Accession" },
        { key:"imexDb", label:"<center>Imex<br/>Partner</center>", sortable:true, resizeable:true, 
          formatter:"partnerList",menuLabel:"Imex Partner" },
        { key:"state", label:"Status", sortable:true, resizeable:false, 
          formatter:"center",menuLabel:"Status" },
        { label:"Submission", menuLabel:"Submission",key:"submission",
          children:[
             { key:"date",  label:"Date",sortable:true, resizeable:false, 
               formatter:"crt" },
             { key:"owner", label:"Submitted By",sortable:true, resizeable:false, 
               formatter:"list" }
          ]
        },
        { key:"editor", label:"Curator(s)", sortable:true, resizeable:true, 
          formatter:"editorList",menuLabel:"Curator(s)" },
        { key:"detail", label:"",sortable:false, resizeable:true, 
          formatter:"elink", className:"detail" }
    ],
    
    myRequestBuilder: function( oState, oSelf ) {

        //alert("myRequestBuilder");

        // get state (or use defaults)
        //----------------------------

        oState = oState || {pagination:null, sortedBy:null};
        var sort = (oState.sortedBy) ? oState.sortedBy.key : "id";
        var dir = (oState.sortedBy && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC) ? "false" : "true";
        var startIndex = (oState.pagination) ? oState.pagination.recordOffset : 0;
        var results = (oState.pagination) ? oState.pagination.rowsPerPage : 10;
        
        // filters
        //--------
                   
        var sfVal = YAHOO.imex.pubmgr.stateBtn.my.value;   //oSelf.my.stateFlt.my.value;
        var pfVal = YAHOO.imex.pubmgr.partnerBtn.my.value; // oSelf.my.partnerFlt.my.value;
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
    
    init: function( init ) { 

        this.formatterInit();
        
        if( init !== undefined ){
            YAHOO.imex.pubmgr.admus = init.admus;
            YAHOO.imex.pubmgr.owner = init.owner;
            YAHOO.imex.pubmgr.cflag = init.cflag;
        }
        
        //alert("init: "+ init.cflag);

        var partnerSuccess = function( o ){
            var messages = YAHOO.lang.JSON.parse( o.responseText );
            YAHOO.imex.pubmgr.selBtnInit( 
                { items: messages.acom,
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
                { items: messages.acom,
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

        YAHOO.imex.pubmgr.myDataSource = new YAHOO.util.DataSource("pubmgr?op.ppg=44&"); 
        
        YAHOO.imex.pubmgr.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        YAHOO.imex.pubmgr.myDataSource.responseSchema = { 
            resultsList: "records.records", 
            fields: ["id","author","title","pmid","imexId",
                     "owner","state","date","time","imexDb","editor"], 
            metaFields: { 
                totalRecords: "records.totalRecords", 
                paginationRecordOffset : "records.startIndex", 
                paginationRowsPerPage : "records.pageSize", 
                sortKey: "records.sort", 
                sortDir: "records.dir" 
            }
        }; 
        
        // create paginator
        //-----------------
        
        YAHOO.imex.pubmgr.myPaginator = new YAHOO.widget.Paginator(
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
            initialRequest: initReq,
            dynamicData : true,
            draggableColumns: true,
            generateRequest : this.myRequestBuilder
        };
    
        // Instantiate DataTable
        //----------------------
    
        YAHOO.imex.pubmgr.myDataTable = new YAHOO.widget.DataTable(
            "pubtab", YAHOO.imex.pubmgr.myColumnDefs, 
            YAHOO.imex.pubmgr.myDataSource, myConfig
        );
        
        YAHOO.imex.pubmgr.myDataTable.my = { 
            stateFlt: YAHOO.imex.pubmgr.stateBtn, 
            partnerFlt: YAHOO.imex.pubmgr.partnerBtn,
            ownerFlt: YAHOO.imex.pubmgr.owner,
            admusFlt: YAHOO.imex.pubmgr.admus,
            cflagFlt: YAHOO.imex.pubmgr.cflag,
            requestBuilder: YAHOO.imex.pubmgr.myRequestBuilder
        };
        
        YAHOO.imex.pubmgr.stateBtn.my.table = YAHOO.imex.pubmgr.myDataTable;
        YAHOO.imex.pubmgr.partnerBtn.my.table = YAHOO.imex.pubmgr.myDataTable;
        
        // Show loading message while page is being rendered
        
        YAHOO.imex.pubmgr.myDataTable.showTableMessage( YAHOO.imex.pubmgr.myDataTable.get("MSG_LOADING"), 
                                           YAHOO.widget.DataTable.CLASS_LOADING);
        
        YAHOO.imex.pubmgr.myDataTable.handleDataReturnPayload = 
            function(oRequest, oResponse, oPayload) { 
                oPayload.totalRecords = oResponse.meta.totalRecords;
                return oPayload; 
            }; 
        
        if( YAHOO.imex.pubmgr.stateBtn.on !== undefined ){
            
            YAHOO.imex.pubmgr.stateBtn.on( "selectedMenuItemChange",
                                           YAHOO.imex.pubmgr.tableReload, 
                                           YAHOO.imex.pubmgr.myDataTable, 
                                           YAHOO.imex.pubmgr.myDataTable );
        }
        if( YAHOO.imex.pubmgr.partnerBtn.on !== undefined ){
            
            YAHOO.imex.pubmgr.partnerBtn.on( "selectedMenuItemChange",
                                             YAHOO.imex.pubmgr.tableReload, 
                                             YAHOO.imex.pubmgr.myDataTable, 
                                             YAHOO.imex.pubmgr.myDataTable );
        }
        
        var acSelectHandler = function( sType, aArgs ) {
            var oMyAcInstance = aArgs[0];
            YAHOO.imex.pubmgr.tableReload( oMyAcInstance.my.table, 
                                           oMyAcInstance.my.table );
        };
        
        try{
            
            YAHOO.imex.pubmgr.myDataTable.my.colmenu = new YAHOO.widget.Menu( "colmenu" );
            
            var oConfMenu = [[{text:"Preferences" }],
                             [{text: "Show Columns", 
                               submenu: YAHOO.imex.pubmgr.myDataTable.my.colmenu }],
                             [{text:"Save...", disabled: true}]
                            ];        
            
            var clist=[];
            var trigger=[];
            
            for( var i = 0;  i < YAHOO.imex.pubmgr.myColumnDefs.length; i++ ) {
                if( YAHOO.imex.pubmgr.myColumnDefs[i].menuLabel !== undefined ) {
                    var item= {text: YAHOO.imex.pubmgr.myColumnDefs[i].menuLabel,
                               checked: true };
                    clist.push(item);
                    var trg = YAHOO.imex.pubmgr.myDataTable.getColumn( 
                        YAHOO.imex.pubmgr.myColumnDefs[i].key );
                    //alert( myColumnDefs[i].key+ "::"+trg);
                    if( trg !== null ) {
                        trigger.push( trg.getThEl() );
                        //alert(trg.getThEl());
                    }
                }
            }
            
            YAHOO.imex.pubmgr.myDataTable.my.colmenu.addItems( clist );
            
            YAHOO.imex.pubmgr.myDataTable.my.configmenu = new YAHOO.widget.ContextMenu(
                "configmenu", { trigger: trigger } );
            
            YAHOO.imex.pubmgr.myDataTable.my.configmenu.addItems( oConfMenu );
            
            YAHOO.imex.pubmgr.myDataTable.my.configmenu.render("pubtab");
            
        } catch (x) {
            alert(x);
        }

        
        return { 
            ds: YAHOO.imex.pubmgr.myDataSource, 
            dt: YAHOO.imex.pubmgr.myDataTable 
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
        
        // status filter 
        //-------------
        
        for( var i = 0; i < o.items.length; i++){
            var text = o.items[i].name;
            var value = o.items[i].value;
            if(value === undefined ){
                value = text;
            }
          
            //alert("name=" + name + " text="+ text);
            o.selmnu.push( {value: value, text: text} );        
        }
            
        //alert(YAHOO.imex.pubmgr[o.selbtn].my.foo);
        
        YAHOO.imex.pubmgr[o.selbtn] = new YAHOO.widget.Button(
            { id: o.selnme,  
              name: o.selnme, 
              label: "<em class=\"yui-button-label\">---ANY---</em>", 
              type: "menu",   
              menu: o.selmnu,  
              container: o.selcnt }); 
        
        YAHOO.imex.pubmgr[o.selbtn].my 
            = { items: o.selmnu, value: "", name: o.selnme };

        YAHOO.imex.pubmgr[o.selbtn].on( 
            "selectedMenuItemChange", 
            YAHOO.imex.pubmgr.onSelectedMenuItemChange );

        //alert(YAHOO.imex.pubmgr.stateBtn.my.name);
        
        if( YAHOO.imex.pubmgr.myDataTable !== null ){  
            YAHOO.imex.pubmgr[o.selbtn]
                .on( "selectedMenuItemChange",
                     YAHOO.imex.pubmgr.tableReload, 
                     YAHOO.imex.pubmgr.myDataTable, 
                     YAHOO.imex.pubmgr.myDataTable );
        }
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
 
        YAHOO.widget.DataTable.Formatter.publication = this.myPubFormatter; 
        YAHOO.widget.DataTable.Formatter.elink = this.myElinkFormatter; 
        YAHOO.widget.DataTable.Formatter.crt = this.myDateFormatter; 
        YAHOO.widget.DataTable.Formatter.center = this.myCenterFormatter; 
        YAHOO.widget.DataTable.Formatter.partnerList = this.myPartnerListFormatter; 
        YAHOO.widget.DataTable.Formatter.editorList = this.myEditorListFormatter; 
    }

};

//YAHOO.util.Event.addListener(
//    window, "load", YAHOO.imex.pubmgr ) ;

