YAHOO.namespace("imex");

YAHOO.imex.pubmgr = function( init ) {
    
    var onSelectedMenuItemChange = function (event) {
        var oMenuItem = event.newValue;
        var text = oMenuItem.cfg.getProperty("text");
        
        for( var i = 0; i < this.my.items.length; i++ ){
            if (this.my.items[i].text === text ) {
                this.my.value = this.my.items[i].value;
            }
        }
        
        this.set("label", ("<em class=\"yui-button-label\">" + 
                           oMenuItem.cfg.getProperty("text") + "</em>"));       
    };

    var admus="";
    var owner="";

    if( init !== undefined ){
        admus = init.admus;
        owner = init.owner;
    }

    // status filter 
    //-------------

    var stateSel = [ 
        { text: "---ANY---", value: "" }, 
        { text: "NEW", value: "NEW" }, 
        { text: "Reserved", value: "RESERVED" },
        { text: "Incomplete", value: "INCOMPLETE" },        
        { text: "Processing", value: "INPROGRESS" },
        { text: "Discarded", value: "DISCARDED" },
        { text: "Released", value: "RELEASED" } 
    ]; 
        
    var stateButton = new YAHOO.widget.Button(
        { id: "state-button",  
          name: "state-button", 
          label: "<em class=\"yui-button-label\">---ANY---</em>", 
          type: "menu",   
          menu: stateSel,  
          container: "state-button-container" }); 
    
    stateButton.my = { items: stateSel, value: "" };
    stateButton.on("selectedMenuItemChange", onSelectedMenuItemChange);


    // partner filter
    //---------------
    
    var partnerSel = [
        { text: "---ANY---", value: "" },
        { text: "DIP", value: "DIP" },
        { text: "IntAct", value: "INTACT" },
        { text: "MINT", value: "MINT" },
        { text: "MPIDB", value: "MPIDB" }
    ];
    
    var partnerButton = new YAHOO.widget.Button(
        { id: "partner-button",
          name: "partner-button",
          label: "<em class=\"yui-button-label\">---ANY---</em>",
          type: "menu",
          menu: partnerSel,
          container: "partner-button-container" });
    
    partnerButton.my = { items: partnerSel, value: "" };
    
    partnerButton.on("selectedMenuItemChange", onSelectedMenuItemChange);

    
    // custom formatters
    //------------------

    this.myPubFormatter = function( elLiner, oRecord, oColumn, oData ) { 
        YAHOO.util.Dom.replaceClass(elLiner, "yui-dt-liner", "yui-dt-liner-null"); 
        elLiner.innerHTML = '<table width="100%" class="yui-table-inner">' +
            '<tr><th class="yui-table-inner-top">Author(s)</th>' +
            '<td class="yui-table-inner-top">' + 
            oRecord.getData("author") + 
            '</td></tr>' +
            '<tr><th class="yui-table-inner-bottom">Title</th>' + 
            '<td class="yui-table-inner-bottom">' + 
            oRecord.getData("title") + '</td></tr></table>';        
    }; 
    
    this.myElinkFormatter = function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = '<a href="pubmgr?id=' + 
            oRecord.getData( "id" ) + 
            '">details</a>';
    };
    
    this.myDateFormatter = function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = oRecord.getData( "date" ) + 
            '<br/>' + oRecord.getData( "time" );
    };
    
    this.myCenterFormatter = function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = oData; 
    };

    this.myPartnerListFormatter = function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        if( oRecord !== undefined  &&  oRecord.getData("imexDb") !== undefined ) {            
            elLiner.innerHTML = oRecord.getData( "imexDb" );
        } else {
            elLiner.innerHTML = '<i>N/A</i>';
        } 
    };

    this.myEditorListFormatter = function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        if( oRecord !== undefined  &&  oRecord.getData("editor") !== undefined ) {            
            elLiner.innerHTML = oRecord.getData("editor");
        } else {
            elLiner.innerHTML = '<i>N/A</i>';
        } 
    };
    
    // Add the custom formatters 
    
    YAHOO.widget.DataTable.Formatter.publication = this.myPubFormatter; 
    YAHOO.widget.DataTable.Formatter.elink = this.myElinkFormatter; 
    YAHOO.widget.DataTable.Formatter.crt = this.myDateFormatter; 
    YAHOO.widget.DataTable.Formatter.center = this.myCenterFormatter; 
    YAHOO.widget.DataTable.Formatter.partnerList = this.myPartnerListFormatter; 
    YAHOO.widget.DataTable.Formatter.editorList = this.myEditorListFormatter; 
    
    // create datasource 
    //------------------

    var myDataSource = new YAHOO.util.DataSource("pubmgr?op.ppg=44&"); 
    
    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
    myDataSource.responseSchema = { 
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

    var myPaginator = new YAHOO.widget.Paginator(
        { containers : ["dt-pag-nav"],
          template : "{PreviousPageLink} {CurrentPageReport} {NextPageLink} {RowsPerPageDropdown}",
          pageReportTemplate : "Showing items {startIndex} - {endIndex} of {totalRecords}",
          rowsPerPageOptions : [5,10,20,50]
        });
    
    var myColumnDefs = [
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
    ];
    
    var myRequestBuilder = function( oState, oSelf ) {

        // get state (or use defaults)
        //----------------------------

        oState = oState || {pagination:null, sortedBy:null};
        var sort = (oState.sortedBy) ? oState.sortedBy.key : "id";
        var dir = (oState.sortedBy && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC) ? "false" : "true";
        var startIndex = (oState.pagination) ? oState.pagination.recordOffset : 0;
        var results = (oState.pagination) ? oState.pagination.rowsPerPage : 10;
        
        // filters
        //--------

        var sfVal = oSelf.my.stateFlt.my.value;
        var pfVal = oSelf.my.partnerFlt.my.value;


        var efVal = oSelf.my.admusFlt;
        var ofVal = oSelf.my.ownerFlt;
        


        var req = "opp.skey=" + sort +
            "&opp.sfv=" + sfVal +
            "&opp.pfv=" + pfVal +
            "&opp.efv=" + efVal +
            "&opp.ofv=" + ofVal +
            "&opp.sdir=" + dir +
            "&opp.off=" + startIndex +
            "&opp.max=" + results; 

        req = encodeURI(req);
        //alert("request: " + req);
        
        // build custom request
        //---------------------

        return req;
    };

    var tableReload = function( o, dt ) {
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
    };
    
    // datatable configuration
    //------------------------
    
    var initReq = "opp.off=0&opp.max=25"
        + "&opp.ofv=" + owner+ "&opp.efv=" + admus;


    var myConfig = {
        paginator : new YAHOO.widget.Paginator(
            { containers: ["dt-pag-nav"], 
              rowsPerPage: 25, 
              template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE, 
              rowsPerPageOptions: [5,10,25,50,100], 
              pageLinks: 5 
            }), 
        initialRequest: initReq,
        dynamicData : true,
        draggableColumns: true,
        generateRequest : myRequestBuilder
    };
    
    // Instantiate DataTable
    
    var myDataTable = new YAHOO.widget.DataTable(
        "pubtab", myColumnDefs, myDataSource, myConfig
    );
    
    myDataTable.my = { stateFlt: stateButton, 
                       partnerFlt: partnerButton,
                       ownerFlt: owner,
                       admusFlt: admus,
                       requestBuilder: myRequestBuilder
                     };
    
    stateButton.my.table = myDataTable;
    partnerButton.my.table = myDataTable;

    // Show loading message while page is being rendered
    
    myDataTable.showTableMessage( myDataTable.get("MSG_LOADING"), 
                                  YAHOO.widget.DataTable.CLASS_LOADING);
    
    myDataTable.handleDataReturnPayload = 
        function(oRequest, oResponse, oPayload) { 
            oPayload.totalRecords = oResponse.meta.totalRecords;
            return oPayload; 
        }; 
    
    stateButton.on("selectedMenuItemChange",
                   tableReload , myDataTable, myDataTable );

    partnerButton.on("selectedMenuItemChange",
                     tableReload , myDataTable, myDataTable );
    
    var acSelectHandler = function( sType, aArgs ) {
        var oMyAcInstance = aArgs[0];
        tableReload( oMyAcInstance.my.table, oMyAcInstance.my.table );
    };
    
    try{
               
        myDataTable.my.colmenu = new YAHOO.widget.Menu( "colmenu" );
        
        var oConfMenu = [[{text:"Preferences" }],
                         [{text: "Show Columns", 
                           submenu: myDataTable.my.colmenu }],
                         [{text:"Save...", disabled: true}]
                        ];        
        
        var clist=[];
        var trigger=[];

        for( var i = 0;  i < myColumnDefs.length; i++ ) {
            if( myColumnDefs[i].menuLabel !== undefined ) {
                var item= {text: myColumnDefs[i].menuLabel,
                           checked: true };
                clist.push(item);
                var trg = myDataTable.getColumn( myColumnDefs[i].key );
                //alert( myColumnDefs[i].key+ "::"+trg);
                if( trg !== null ) {
                    trigger.push( trg.getThEl() );
                    //alert(trg.getThEl());
                }
            }
        }

        myDataTable.my.colmenu.addItems( clist );

        myDataTable.my.configmenu = new YAHOO.widget.ContextMenu(
            "configmenu", { trigger: trigger } );


        myDataTable.my.configmenu.addItems( oConfMenu );
    
        myDataTable.my.configmenu.render("pubtab");

    } catch (x) {
        alert(x);
    }


    return { 
        ds: myDataSource, 
        dt: myDataTable 
    };    
};

//YAHOO.util.Event.addListener(
//    window, "load", YAHOO.imex.pubmgr ) ;

