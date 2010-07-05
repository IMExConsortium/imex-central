YAHOO.namespace("imex");

YAHOO.imex.stats = function() {
        
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
        if( oData == undefined ){
            oData = "0";
        }     
        if( oRecord.getData("label") === "Total") {
            YAHOO.util.Dom.addClass( elLiner, "dt-bold");
        }
   
        elLiner.innerHTML = oData; 
    };
    
    YAHOO.widget.DataTable.Formatter.elink = this.myElinkFormatter; 
    YAHOO.widget.DataTable.Formatter.crt = this.myDateFormatter; 
    YAHOO.widget.DataTable.Formatter.center = this.myCenterFormatter; 
    YAHOO.widget.DataTable.Formatter.stat = this.myStatFormatter; 

    
    // create datasource 
    //------------------

    var myDataSource = new YAHOO.util.DataSource("stats?format=json&query=bypartner"); 
    
    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
    myDataSource.responseSchema = { 
        resultsList: "counts.rows", 
        fields: [{key: "label"},
                 {key: "states['1'].cnt"},
                 {key: "states['2'].cnt"},
                 {key: "states['3'].cnt"},
                 {key: "states['10'].cnt"},
                 {key: "states['4'].cnt"},
                 {key: "states.total.cnt"}], 
        metaFields: { 
            //totalRecords: "records.totalRecords", 
            //paginationRecordOffset : "records.startIndex", 
            //paginationRowsPerPage : "records.pageSize", 
            //sortKey: "records.sort", 
            //sortDir: "records.dir" 
        } 
    }; 
    
    var myColumnDefs = [
        { key:"label", label:"Partners", formatter:"stat", sortable:true, resizeable:false, 
          width: 600, maxAutoWidth: 1000, menuLabel:"Publication" },
        { label:"Status", menuLabel:"Status",key:"submission",
          children:[
             { key:"states['1'].cnt",  label:"New",sortable:true, resizeable:false, 
               formatter:"stat", className:"dt-right" },
             { key:"states['2'].cnt", label:"Reserved",sortable:true, resizeable:false, 
               formatter:"stat", className:"dt-right"},
             { key:"states['3'].cnt", label:"Processing",sortable:true, resizeable:false, 
               formatter:"stat", className:"dt-right" },
             { key:"states['10'].cnt", label:"Processed",sortable:true, resizeable:false, 
               formatter:"stat", className:"dt-right" },
             { key:"states['4'].cnt", label:"Released",sortable:true, resizeable:false, 
               formatter:"stat", className:"dt-right" }
          ]
        },
        { key:"states.total.cnt", label:"Total", sortable:true, resizeable:true, 
          formatter:"stat", className: "dt-right dt-bold", menuLabel:"Total" }
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

