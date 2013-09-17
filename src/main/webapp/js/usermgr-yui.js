YAHOO.namespace("imex");

YAHOO.imex.usermgr = {
    init: function(columnDefinitions, dataSourceLink, datasourceSchema, container){
        var usermgr  = YAHOO.imex.usermgr;
        usermgr.formatterInit();
        
        usermgr.myColumnDefs = columnDefinitions
        usermgr.myDataSource = new YAHOO.util.DataSource(dataSourceLink); 
        
        usermgr.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        
        usermgr.myDataSource.responseSchema = datasourceSchema;
        //Tossing in some css to remove bottom paginator. and some other stuff
        var sheet = document.createElement('style');
        sheet.innerHTML = "#yui-dt0-paginator1 {display: none;} " +
                          "#" + container + " table {border: 1px solid #888888; width: 100%;} " + 
                          "form#mgr-form li {list-style-type: none;} "+ 
                          "#mgr-tabs{max-width: 80em;}";
        document.body.appendChild(sheet); 
        
        
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
       
        usermgr.myConfig = {
            paginator : usermgr.myPaginator,
        };
       
        
        // Instantiate DataTable
        //----------------------
    
        usermgr.myDataTable = new YAHOO.widget.DataTable(
            container, usermgr.myColumnDefs, 
            usermgr.myDataSource, usermgr.myConfig
        );
        
        usermgr.myDataTable
            .showTableMessage( usermgr.myDataTable.get("MSG_LOADING"), 
                               YAHOO.widget.DataTable.CLASS_LOADING);
        
        return { 
            ds: usermgr.myDataSource, 
            dt: usermgr.myDataTable 
        };
        
    },
   //-----------------------------
    // Create the custom formatters 
    //-----------------------------

    UserDetailsFormatter: function(elLiner, oRecord, oColumn, oData) {
        elLiner.innerHTML = '<a href="usermgr?id=' + 
            oRecord.getData( "id" ) + 
            '">Edit</a>';
    },
    groupDetailsFormatter: function(elLiner, oRecord, oColumn, oData) {
        elLiner.innerHTML = '<a href="groupmgr?id=' + 
            oRecord.getData( "id" ) + 
            '">Edit</a>';
    },
    roleDetailsFormatter: function(elLiner, oRecord, oColumn, oData) {
        elLiner.innerHTML = '<a href="rolemgr?id=' + 
            oRecord.getData( "id" ) + 
            '">Edit</a>';
    },
    
    //--------------------------
    // Add the custom formatters 
    //--------------------------
    formatterInit: function(){
        
        var YDTF = YAHOO.widget.DataTable.Formatter;

        YDTF.userDetails = this.UserDetailsFormatter; 
        YDTF.groupDetails = this.groupDetailsFormatter; 
        YDTF.roleDetails = this.roleDetailsFormatter; 
       
    }
};
