YAHOO.namespace("imex");

YAHOO.imex.pubmgr = function() {
    
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


    // status filter 
    //-------------

    var stateSel = [ 
        { text: "---Status---", value: "" }, 
        { text: "---ANY---", value: "" }, 
        { text: "NEW", value: "NEW" }, 
        { text: "Reserved", value: "reserved" },
        { text: "Processed", value: "processed" },
        { text: "Released", value: "released" } 
    ]; 
        
    var stateButton = new YAHOO.widget.Button(
        { id: "state-button",  
          name: "state-button", 
          label: "<em class=\"yui-button-label\">---Status---</em>", 
          type: "menu",   
          menu: stateSel,  
          container: "state-button-container" }); 
    
    stateButton.my = { items: stateSel, value: "" };
    stateButton.on("selectedMenuItemChange", onSelectedMenuItemChange);


    // partner filter
    //---------------
    
    var partnerSel = [
        { text: "---Imex Partner---", value: "" },
        { text: "---ANY---", value: "" },
        { text: "DIP", value: "DIP" },
        { text: "IntAct", value: "IntAct" },
        { text: "MINT", value: "MINT" },
        { text: "MPIDB", value: "MPIDB" }
    ];
    
    var partnerButton = new YAHOO.widget.Button(
        { id: "partner-button",
          name: "partner-button",
          label: "<em class=\"yui-button-label\">---Imex Partner---</em>",
          type: "menu",
          menu: partnerSel,
          container: "partner-button-container" });
    
    partnerButton.my = { items: partnerSel, value: "" };
    
    partnerButton.on("selectedMenuItemChange", onSelectedMenuItemChange);


    // editor filter
    //--------------
    
    var editorArray = ["lukasz", "skerrien", "hhm", "doe_99"]; 
    var oACS = new YAHOO.util.LocalDataSource( editorArray );  
    oACS.responseSchema = {fields : ["editor"]};
    
    var acEditor = new YAHOO.widget.AutoComplete(
        "myEditorInput", "myEditorContainer", oACS);
    acEditor.my = {};
    
    acEditor.prehighlightClassName = "yui-ac-prehighlight"; 
    acEditor.useShadow = true; 
    
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
    
    // Add the custom formatters 
    
    YAHOO.widget.DataTable.Formatter.publication = this.myPubFormatter; 
    YAHOO.widget.DataTable.Formatter.elink = this.myElinkFormatter; 
    YAHOO.widget.DataTable.Formatter.crt = this.myDateFormatter; 
    YAHOO.widget.DataTable.Formatter.center = this.myCenterFormatter; 
    
    // create datasource 
    //------------------

    var myDataSource = new YAHOO.util.DataSource("pubmgr?op.ppg=44&"); 
    
    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
    myDataSource.responseSchema = { 
        resultsList: "records.records", 
        fields: ["id","author","title","pmid","imexId","owner","state","date","time"], 
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
        { key:"del",label:"",  sortable:false, resizeable:false,
          formatter:"checkbox" },
        { key:"pub", label:"Publication", sortable:true, resizeable:true, 
          formatter:"publication", maxAutoWidth:1000 },
        { key:"pmid", label:"PMID", sortable:true, resizeable:true, 
          formatter:"center" },
        { key:"imexId", label:"ImexId", sortable:true, resizeable:true, 
          formatter:"center" },
        { key:"state", label:"Status", sortable:true, resizeable:false, 
          formatter:"center" },
        { label:"Submission", 
          children:[
             { key:"date",  label:"Date",sortable:true, resizeable:false, 
               formatter:"crt" },
             { key:"owner", label:"Owner",sortable:true, resizeable:false, 
               formatter:"center" }
          ]
        },
        { key:"detail", label:"",sortable:false, resizeable:true, 
          formatter:"elink" }
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
        var efVal = oSelf.my.editorFlt.getInputEl().value;

        var req = "opp.skey=" + sort +
            "&opp.sfv=" + sfVal +
            "&opp.pfv=" + pfVal +
            "&opp.efv=" + efVal +
            "&opp.sdir=" + dir +
            "&opp.off=" + startIndex +
            "&opp.max=" + results; 

        //        alert("request: " + req);
        
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
    
    var myConfig = {
        paginator : new YAHOO.widget.Paginator(
            { containers: ["dt-pag-nav"], 
              rowsPerPage: 25, 
              template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE, 
              rowsPerPageOptions: [5,10,25,50,100], 
              pageLinks: 5 
            }), 
        initialRequest: "opp.off=0&opp.max=25",
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
                       editorFlt: acEditor,
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


    acEditor.my = { table: myDataTable };

    var acSelectHandler = function( sType, aArgs ) {
        var oMyAcInstance = aArgs[0];
        tableReload( oMyAcInstance.my.table, oMyAcInstance.my.table );
    };
    
    acEditor.itemSelectEvent.subscribe( acSelectHandler );
    
    return { 
        ds: myDataSource, 
        dt: myDataTable 
    };    
};

YAHOO.util.Event.addListener(
    window, "load", YAHOO.imex.pubmgr ) ;

