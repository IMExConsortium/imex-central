YAHOO.namespace("imex");

YAHOO.imex.stats = function() {
        
    this.counts = null;

    // custom formatters
    //------------------
    
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

    this.myStatFormatter = function(elLiner, oRecord, oColumn, oData) {
        if( oRecord.getData("label") === "Total") {
            YAHOO.util.Dom.addClass( elLiner, "dt-bold");
        }
        if( oRecord.getData("label") === "Unassigned") {
            YAHOO.util.Dom.addClass( elLiner, "dt-italic");
        }
        
        var dtaString = "";

        if( oData == undefined ){
            oData = "0";
        } 
        //some of this is hard coded and I think that makes it fragile
        //but I dont know how to make it more robust. TODO
        else if(typeof oData.cnt === "number" ){
            
            dtaString = oData.cnt;

            if(typeof oData.cnt === "number"){
                dtaString = oData.cnt;
            } else {
                dtaString = "0";
            }

            //alert(JSON.stringify(oData));
            
            var currentStatus = oColumn.label.toUpperCase();
            if( currentStatus == 'PROCESSING')
                currentStatus = 'INPROGRESS';
            if( currentStatus == 'TOTAL')
                currentStatus = '';

            var currentDB = oRecord.getData("label");
            if( currentDB.toUpperCase() == 'TOTAL')
                currentDB = '';

            var url = "pubmgr#pubmgr=" + encodeURI( YAHOO.imex.pubmgr.generateLinkState( currentStatus, currentDB));

            oData = '<a href="' + url + '">' + dtaString + '</a>';             
        }     
            
        elLiner.innerHTML = oData; 
    };

    this.myAccFormatter = function(elLiner, oRecord, oColumn, oData) {
        if( oRecord.getData("label") === "Total") {
            YAHOO.util.Dom.addClass( elLiner, "dt-bold");
        }
        if( oRecord.getData("label") === "Unassigned") {
            YAHOO.util.Dom.addClass( elLiner, "dt-italic");
        }
        
        var dtaString = "";

        if( oData == undefined ){
            oData = "(0)";
        } 
        //some of this is hard coded and I think that makes it fragile
        //but I dont know how to make it more robust. TODO
        else if(typeof oData.cnt === "number" ){
            
            dtaString = oData.cnt;

            if(typeof oData.acc === "number"){
                dtaString = "(" + oData.acc + ")";
            } else {
                dtaString = "(0)";
            }

            //alert(JSON.stringify(oData));
            
            var currentStatus = oColumn.label.toUpperCase();
            if( currentStatus == 'PROCESSING')
                currentStatus = 'INPROGRESS';
            if( currentStatus == 'TOTAL')
                currentStatus = '';

            var currentDB = oRecord.getData("label");
            if( currentDB.toUpperCase() == 'TOTAL')
                currentDB = '';

            var url = "pubmgr#pubmgr=" + encodeURI( YAHOO.imex.pubmgr.generateLinkState( currentStatus, currentDB));

            oData = '<a href="' + url + '">' + dtaString + '</a>';             
        }     
            
        elLiner.innerHTML = oData; 
    };

    this.myAccStatFormatter = function(elLiner, oRecord, oColumn, oData) {
        if( oRecord.getData("label") === "Total") {
            YAHOO.util.Dom.addClass( elLiner, "dt-bold");
        }
        if( oRecord.getData("label") === "Unassigned") {
            YAHOO.util.Dom.addClass( elLiner, "dt-italic");
        }
        
        var dtaString = "";
        var accString = "";

        if( oData == undefined ){
            oData = "0";
        } 
        //some of this is hard coded and I think that makes it fragile
        //but I dont know how to make it more robust. TODO
        else if(typeof oData.cnt === "number" ){
            
            dtaString = oData.cnt;
            
            if(typeof oData.acc === "number"){
                accString = "/" + oData.acc + "<sup>*</sup>";
            } else {
                //accString += "/0<sup>*</sup>";
            }

            //alert(JSON.stringify(oData));
            
            var currentStatus = oColumn.label.toUpperCase();
            if( currentStatus == 'PROCESSING')
                currentStatus = 'INPROGRESS';
            if( currentStatus == 'TOTAL')
                currentStatus = '';

            var currentDB = oRecord.getData("label");
            if( currentDB.toUpperCase() == 'TOTAL')
                currentDB = '';

            var url = "pubmgr#pubmgr=" + encodeURI( YAHOO.imex.pubmgr.generateLinkState( currentStatus, currentDB));

            oData = '<a href="' + url + '">' + dtaString + '</a>' + accString;
            //if( dtaString  !== "0" ){
            //    oData += "<sup>*</sup>";
            //}
        }     
            
        elLiner.innerHTML = oData; 
    };

    YAHOO.widget.DataTable.Formatter.elink = this.myElinkFormatter; 
    YAHOO.widget.DataTable.Formatter.crt = this.myDateFormatter; 
    YAHOO.widget.DataTable.Formatter.center = this.myCenterFormatter; 
    YAHOO.widget.DataTable.Formatter.stat = this.myStatFormatter; 
    YAHOO.widget.DataTable.Formatter.accstat = this.myAccStatFormatter; 
    YAHOO.widget.DataTable.Formatter.acc = this.myAccFormatter; 

    
    // create datasource 
    //------------------

    var myDataSource = new YAHOO.util.DataSource("stats?format=json&query=bypartner"); 
    
    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
    myDataSource.responseSchema = { 
        resultsList: "counts.rows", 
        fields: [{key: "label"},
                 {key: "states['1']"},
                 {key: "states['2']"},
                 {key: "states['3']"},
                 {key: "states['10']"},
                 {key: "states['4']"},
                 {key: "states.total"}], 
        metaFields: { 
            //totalRecords: "records.totalRecords", 
            //paginationRecordOffset : "records.startIndex", 
            //paginationRowsPerPage : "records.pageSize", 
            //sortKey: "records.sort", 
            //sortDir: "records.dir" 
        } 
    }; 
    
    var myColumnDefs = [
        { key:"label", label:"Partners", formatter:"stat", sortable:false, resizeable:true, 
          width: 400, maxAutoWidth: 800, menuLabel:"Publication" },
        { label:"Record Status", menuLabel:"Status",key:"submission",
          children:[
             { key:"states['1']",  label:"New",sortable:false, resizeable:false, 
               formatter:"accstat", className:"dt-right" },
             { key:"states['2']", label:"Reserved",sortable:false, resizeable:false, 
               formatter:"accstat", className:"dt-right"},
             { key:"states['3']", label:"Processing",sortable:false, resizeable:false, 
               formatter:"accstat", className:"dt-right" },
             { key:"states['10']", label:"Processed",sortable:false, resizeable:false, 
               formatter:"accstat", className:"dt-right" },
             { key:"states['4']", label:"Released",sortable:false, resizeable:false, 
               formatter:"accstat", className:"dt-right" }
          ]
        },
        { key:"states.total", label:"Total", sortable:true, resizeable:true, 
          formatter:"accstat", className: "dt-right dt-bold", menuLabel:"Total" }
    ];
    
    
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
       dynamicData : true,
       draggableColumns: true
    };
    
    // Instantiate DataTable
    
    var myDataTable = new YAHOO.widget.DataTable(
        "statstab", myColumnDefs, myDataSource, myConfig
    );
    
    myDataTable.my = { 
                     };
    
    // Show loading message while page is being rendered
    
    myDataTable.showTableMessage( myDataTable.get("MSG_LOADING"), 
                                  YAHOO.widget.DataTable.CLASS_LOADING);
    
    myDataTable.handleDataReturnPayload = 
        function(oRequest, oResponse, oPayload) { 
            oPayload.totalRecords = oResponse.meta.totalRecords;
            YAHOO.imex.stats.counts = oResponse.counts;
            return oPayload; 
        }; 
  
    //acEditor.my = { table: myDataTable };

    //var acSelectHandler = function( sType, aArgs ) {
    //    var oMyAcInstance = aArgs[0];
    //    tableReload( oMyAcInstance.my.table, oMyAcInstance.my.table );
    //};
    
    try{
        
        //acEditor.itemSelectEvent.subscribe( acSelectHandler );
          
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

