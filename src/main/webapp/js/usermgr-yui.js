YAHOO.namespace("imex");

YAHOO.imex.usermgr = {
    init: function(columnDefinitions, dataSourceLink, datasourceSchema, container){
        var usermgr  = YAHOO.imex.usermgr;
        usermgr.formatterInit();
        
        usermgr.myColumnDefs = columnDefinitions
        usermgr.myDataSource = new YAHOO.util.DataSource(dataSourceLink); 
        
        usermgr.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        
        usermgr.myDataSource.responseSchema = datasourceSchema;
        //Tossing in some css to remove bottom paginator.
        var sheet = document.createElement('style');
        sheet.innerHTML = "#yui-dt0-paginator1 {display: none;} " +
                          "#usermgr-table table {border: 1px solid #888888;} " + 
                          "form#usermgr li {list-style-type: none;} "+ 
                          "form#usermgr {max-width: 60em;}";
        document.body.appendChild(sheet); 
        
        

        
        //usermgr.myDataSource.my = { myState: null };
        
        // create paginator
        //-----------------
        
        usermgr.myPaginator = new YAHOO.widget.Paginator(
            { 
              rowsPerPage: 20, 
              template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE, 
              rowsPerPageOptions: [5,10,20,50,100], 
              pageLinks: 5 
            }
        );
        
        // datatable configuration
        //------------------------
        /*
        var initReq = "opp.off=0&opp.max=25"
            + "&opp.wfl=" + YAHOO.imex.pubmgr.watch 
            + "&opp.ofv=" + YAHOO.imex.pubmgr.owner 
            + "&opp.efv=" + YAHOO.imex.pubmgr.admus 
            + "&opp.ffv=" + YAHOO.imex.pubmgr.cflag;
         */
        usermgr.myConfig = {
            paginator : usermgr.myPaginator,
            //initialLoad: false,
            //dynamicData : true,
            //draggableColumns: true
        };
       
        
        // Instantiate DataTable
        //----------------------
    
        usermgr.myDataTable = new YAHOO.widget.DataTable(
            container, usermgr.myColumnDefs, 
            usermgr.myDataSource, usermgr.myConfig
        );
        /*
        usermgr.myPaginator
            .unsubscribe( "changeRequest", 
                          usermgr.myDataTable.onPaginatorChangeRequest );
        usermgr.myPaginator
            .subscribe( "changeRequest", 
                        usermgr.handlePagination, usermgr.myDataTable, true );

        usermgr.myDataTable.sortColumn = usermgr.handleSorting;
        
        
        usermgr.myDataTable.my = { 
            watchFlg: usermgr.watch,
            stateFlt: usermgr.stateBtn, 
            partnerFlt: usermgr.partnerBtn,
            ownerFlt: usermgr.owner,
            admusFlt: usermgr.admus,
            cflagFlt: usermgr.cflag,
            requestBuilder: usermgr.requestBuilder
        };
        
        usermgr.myDataTable.my.filter = {
            owner: usermgr.owner, 
            editor: usermgr.admus,
            cflag: usermgr.cflag  
        };

        usermgr.stateBtn.my.table = usermgr.myDataTable;
        usermgr.partnerBtn.my.table = usermgr.myDataTable;
        */
        // Show loading message while page is being rendered
        
        usermgr.myDataTable
            .showTableMessage( usermgr.myDataTable.get("MSG_LOADING"), 
                               YAHOO.widget.DataTable.CLASS_LOADING);
        
          /*     
        usermgr.myDataTable.doBeforeLoadData = 
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
                    console.log(x);
                }
                return true;
        };
       */
       /*
        usermgr.myDataTable.handleReorder =
            function( ev, o ){
                //alert("reorder");                
                try{
                    usermgr.myDataTable.my.configmenu.destroy();
                    usermgr.contextMenuInit( usermgr );
                    
                    var nCookie = YAHOO.imex.pubmgr.buildCookie();
                    YAHOO.imex.pubmgr.updateUserTablePref(nCookie);
                    YAHOO.util.Cookie.set("pubmgr", nCookie );                                      
                } catch (x) {}
            };
        
        usermgr.contextMenuInit( usermgr );
        
        usermgr.myDataTable.on( "columnReorderEvent",
                             usermgr.myDataTable.handleReorder );     
          */
                            
        //tossing in some css to add a black separator between the rows
        /*
        var sheet = document.createElement('style');
        sheet.innerHTML = ".yui-dt-data > tr > td {border-bottom: 1px solid black !important;}";
        document.body.appendChild(sheet); 
        */
        return { 
            ds: usermgr.myDataSource, 
            dt: usermgr.myDataTable 
        };
        
        //YAHOO.imex.pubmgrOld();   
    },
   //-----------------------------
    // Create the custom formatters 
    //-----------------------------
     
   

    myDetailsFormatter: function(elLiner, oRecord, oColumn, oData) {
        elLiner.innerHTML = '<a href="usermgr?id=' + 
            oRecord.getData( "id" ) + 
            '">Edit</a>';
    },
    
    //--------------------------
    // Add the custom formatters 
    //--------------------------
    formatterInit: function(){
        
        var YDTF = YAHOO.widget.DataTable.Formatter;

        YDTF.details = this.myDetailsFormatter; 
       
    }
};
