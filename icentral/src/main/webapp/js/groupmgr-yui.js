YAHOO.namespace("imex");

YAHOO.imex.groupmgr = {

    myCD: {},
    myCL: [],

    myContainer: null,

    myDataSource: null,
    myPaginator: null,

    defPageSize: 50,

    myLogLevel: "none",

    myColumnDefs: [],

    init: function( columnDefinitions, dataSourceLink, datasourceSchema, container ){
      var GMGR  = YAHOO.imex.groupmgr;
        
	  GMGR.formatterInit();        
      GMGR.myColumnDefs = columnDefinitions;
      GMGR.myContainer = container;
      
	  for( var i =0; i< columnDefinitions.length-1; i++ ){
	    
	    var key = columnDefinitions[i].key;
	    GMGR.myCD[key]=columnDefinitions[i];
	    GMGR.myCL[GMGR.myCL.length] = key;
	  }
	
	  // set/restore groupmgr cookie
	  //----------------------------
	  /*

	  try{
        var GMGR  = YAHOO.imex.groupmgr;
        var cookie = YAHOO.util.Cookie.get("groupmgr");
        if( cookie == null ){
          cookie = GMGR.getDefaultCookie();
		  YAHOO.util.Cookie.set( "groupmgr", cookie );
        }
            
        if( cookie !== null ){
          GMGR.buildCDefs( cookie );                
        }
      }catch( x ){
        console.log("INIT: ex="+ x);
      }
	  */

	  // create data source
	  //-------------------

      GMGR.myDataSource = new YAHOO.util.DataSource( dataSourceLink ); 
      GMGR.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        
      GMGR.myDataSource.responseSchema = datasourceSchema;
 
	  //Tossing in some css to remove bottom paginator. and some other stuff
	  //-------------------------------------------------------------------
	
	  var sheet = document.createElement('style');
      sheet.innerHTML = "#yui-dt0-paginator1 {display: none;} " +
            "#" + container + " table {border: 1px solid #888888; width: 100%;} " + 
            document.body.appendChild(sheet); 
        
      GMGR.myDataSource.my = { myState: null };
        
      // create paginator
      //-----------------
        
      GMGR.myPaginator = new YAHOO.widget.Paginator(
        {rowsPerPage: 10, 
	     template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE, 
	     rowsPerPageOptions: [5,10,20,50,100], 
	     pageLinks: 5,
	     containers: ["dt-pag-nav"]  
        });
        
      // datatable configuration
      //------------------------

	  var initReq = "opp.off=0&opp.max=50";
        // + "&opp.ffv=" + YAHOO.imex.pubmgr.cflag;   ???
	
      var myConfig = {
          paginator : GMGR.myPaginator,
	      initialLoad: false,
          dynamicData : true,
          draggableColumns: true
      };
       
        
      // Instantiate DataTable
      //----------------------
    
      GMGR.myDataTable = new YAHOO.widget.DataTable(
            container, GMGR.myColumnDefs, 
            GMGR.myDataSource, myConfig );
                
	  GMGR.myPaginator
            .unsubscribe( "changeRequest", 
                          GMGR.myDataTable.onPaginatorChangeRequest );
      GMGR.myPaginator
           .subscribe( "changeRequest", 
                        GMGR.handlePagination, GMGR.myDataTable, true );
	
	  GMGR.myDataTable.sortColumn = GMGR.handleSorting;

      GMGR.myDataTable.my = { 
            requestBuilder: GMGR.requestBuilder };

	  // Show loading message while page is being rendered
	  //--------------------------------------------------

      GMGR.myDataTable.showTableMessage( GMGR.myDataTable.get("MSG_LOADING"), 
                                         YAHOO.widget.DataTable.CLASS_LOADING);
	
      GMGR.myDataTable.doBeforeLoadData = 
        function( oRequest, oResponse, oPayload ){
		
		var GMGR  = YAHOO.imex.groupmgr;
                
        try{
          var meta = oResponse.meta;
          oPayload.totalRecords = meta.totalRecords || oPayload.totalRecords;

          var oPayloadOld = oPayload;
                		    
		  if( GMGR.myLogLevel == 'debug' ){
			console.log( YAHOO.lang.JSON.stringify( meta ) );
		  }
		    
          oPayload.pagination = {
             rowsPerPage: meta.paginationRowsPerPage || 10,
             recordOffset: meta.paginationRecordOffset || 0 };
                    
             if( meta.sortKey !== undefined ){
               oPayload.sortedBy = { key: meta.sortKey,
                                              dir: "yui-dt-" + meta.sortDir };
             }

		    if( GMGR.myLogLevel == 'debug' ){                    
			  alert( "dbld:\n payload(old)=" + YAHOO.lang.JSON.stringify( oPayloadOld ) +
                     "\n meta=" + YAHOO.lang.JSON.stringify( meta ) +
                     "\n payload(new)=" + YAHOO.lang.JSON.stringify( oPayload  ));
            }
		    
        }catch( x ){
          console.log(x);
        }
		
        return true;
      }; //  , ?
	
      GMGR.myDataTable.handleLayoutChange =
        function( ev, o ){
		
		  var GMGR = YAHOO.imex.groupmgr;
		
          try{
            GMGR.myDataTable.my.configmenu.destroy();
            GMGR.contextMenuInit( GMGR );
                    
            //var nCookie = GMGR.buildCookie();
            //GMGR.updateUserTablePref(nCookie);
            //YAHOO.util.Cookie.set("groupmgr", nCookie );                                      
          } catch (x) { }
        };

      GMGR.contextMenuInit( GMGR );
	
      GMGR.myDataTable.on( "columnReorderEvent",
                         GMGR.myDataTable.handleLayoutChange );     
        
      GMGR.myDataTable.on( "columnHideEvent",
                         GMGR.myDataTable.handleLayoutChange );     
        
      GMGR.myDataTable.on( "columnShowEvent",
                         GMGR.myDataTable.handleLayoutChange );     
	
	// initialize tab view 
	//--------------------

	GMGR.tabView = new YAHOO.widget.TabView("mgr-tabs");
	
	GMGR.tabView.subscribe( "activeIndexChange",
			                GMGR.handleActiveTabChange, GMGR.tabView, true );

	// initialize history
	//-------------------
	
    var defstate = {
            startIndex: 0,
            pageSize: GMGR.defPageSize,
            //filter:{ stage: "",
            //         status: "",
            //         partner:"",
            //         editor:PMGR.admus,
            //         owner: PMGR.owner,
            //         cflag: PMGR.cflag},
            //watch: PMGR.watch,
            scol: "id",
            sdir: "asc",
	    stab: 0
	};
                 
    //if(  init !== undefined && init.watch !== undefined ){
    //    defstate.watch = init.watch;
    //}

    var dst = YAHOO.lang.JSON.stringify( defstate );
        
    var bState = YAHOO.util.History.getBookmarkedState( "groupmgr" );
    var iState = bState || dst;
        
    YAHOO.util.History.register( "groupmgr", iState, 
                                 GMGR.handleHistoryNavigation ); 
        
    YAHOO.util.History.onReady( GMGR.historyReadyHandler );    
        
    try{
      YAHOO.util.History.initialize( "yui-history-field", 
                                     "yui-history-iframe" );
    }catch( x ){
      console.log(x);
    }                

	if( GMGR.myLogLevel == 'debug'){
	    console.log('groupmgr initialized');
	}
	
    return { 
            ds: GMGR.myDataSource, 
            dt: GMGR.myDataTable 
    };
        
  },

  handleActiveTabChange: function( e, oTabView ){
	try{

	  var GMGR = YAHOO.imex.groupmgr;
      var newState = GMGR.myDataSource.my.myState;
	    
	  newState.stab = e.newValue;

      YAHOO.util.History.navigate( "groupmgr",
                                   GMGR.generateStateString( newState ) );

	  console.log("atcstate: " + GMGR.generateStateString( newState ));

	}catch(x){
	    console.log(x);
	}	
  },
  
  contextMenuInit: function( o ){

    var GMGR = YAHOO.imex.groupmgr;

    try{         
            o.myDataTable.my.colmenu = new YAHOO.widget.Menu( "colmenu" );
            
            var defaultTableLayout = function( o ){
                var GMGR = YAHOO.imex.groupmgr;
                /*
		        if( typeof GMGR.loginId  != "undefined" && GMGR.loginId != "" ){
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
                                       'userprefmgr?id=' + pubmgr.loginId +'&op.defaultTableLayout=true',
                                       callback );        
                    } catch (x) {
                        console.log("AJAX Error:"+x);
                    }
                }
		        */

		        /*
                var cookie = GMGR.getDefaultCookie();
                GMGR.buildCDefs(cookie);
                
                YAHOO.util.Cookie.set( "groupmgr", cookie );
                var myDataTable = GMGR.myDataTable;
                
                GMGR.init(
                    {admus: pubmgr.admus,
                     owner: pubmgr.owner,
                     cflag: pubmgr.cflag,
                     watch: pubmgr.watch,
                     loginid:pubmgr.loginId });
		        */
            };
            
            var oConfMenu = [[{text:"Preferences", disabled: true }],
                             [{text: "Show Columns", 
                               submenu: o.myDataTable.my.colmenu }],
                             [{text:"Restore Default Layout",onclick: {fn: defaultTableLayout } }
                               ],
                             [{text:"Save...", disabled: true}]
                            ];        
            
            var clist=[];
            var trigger=[];

            var cdefs = GMGR.myColumnDefs; 

            for( var i = 0;  i < cdefs.length; i++ ) {
                 
              var trg = null;

              if( cdefs[i].menuLabel !== undefined || cdefs[i].trigger) {
                
                trg = o.myDataTable.getColumn( 
                        cdefs[i].key ); 
		    
                if( cdefs[i].menuLabel !== undefined ){
                  var item = { text: cdefs[i].menuLabel,
                               checked: !trg.hidden, disabled:  !cdefs[i].hideable,
                               onclick: { fn: o.hiddenColToggle,
                                          obj: { tbl: o.myDataTable ,col: trg } } };
                
                  clist.push( item );
                }        
                
                if( trg !== null ) {
                        trigger.push( trg.getThEl() );                        
                }
              }
            }
            
            o.myDataTable.my.colmenu.addItems( clist );
            
            o.myDataTable.my.configmenu = new YAHOO.widget.ContextMenu(
                "configmenu", { trigger: trigger } );
            
            o.myDataTable.my.configmenu.addItems( oConfMenu );            
	        o.myDataTable.my.configmenu.render( GMGR.myContainer );
            
      }catch( x ){
        console.log(x);
      }
    },

    /*
    getDefaultCookie: function(){
        var cookie = "";
        var GMGR = YAHOO.imex.groupmgr;

	  try{
        for( var i = 0; i < groupmgr.myCL.length; i++ ){

		var hidden = false;
		if(  GMGR.myCD[GMGR.myCL[i]].hidden === true ){
                    hidden = true;
		}
		cookie += GMGR.myCD[GMGR.myCL[i]].key + ":" + hidden +"|";
            }
      }catch( x ){
        console.log(x);
      }
	    
      //alert( cookie );
      return cookie;
    },
    
    */

    /*

    buildCDefs: function( cookie ){

      var GMGR = YAHOO.imex.groupmgr;
	
      //alert("buildCDefs:"+cookie );
      var col = cookie.split("|");
        
      if(col.length > 0){
            GMGR.myColumnDefs=[];
            
            for( var i =0; i < col.length-1; i++ ){
                try{
                    
                    var cs = col[i].split(":");
                    if( GMGR.myCD[cs[0]] !== undefined ){
                        
                        GMGR.myColumnDefs.push(GMGR.myCD[cs[0]]);
                        if(cs[1] === 'true'){
                            GMGR.myColumnDefs[GMGR.myColumnDefs.length-1].hidden = true;
                        } else {
                            GMGR.myColumnDefs[GMGR.myColumnDefs.length-1].hidden = false;
                        }
                    }
                    
                } catch (x) {
                    console.log( "I:"+ i + "-> " + cs[0] + " ex=" + x );
                }
            }        
      }
    },

    */

    buildRequest: function ( state ){
      
       //alert("buildRequest->state" +YAHOO.lang.JSON.stringify(state));

       var req = "opp.off=" + state.startIndex + 
            "&opp.max=" + state.pageSize + 
            "&opp.skey=" + state.scol +
            "&opp.sdir=" + state.sdir;
        
       return encodeURI( req );      
    },
    
    requestBuilder: function( oState, oSelf ) {

      var GMGR = YAHOO.imex.groupmgr;
      
      //alert("requestBuilder->oState=" + YAHOO.lang.JSON.stringify(oState) );

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

      var req = 
	    "opp.skey=" + sort +
            "&opp.sdir=" + dir +
            "&opp.off=" + startIndex +
            "&opp.max=" + results; 
        
      req = encodeURI(req);

	  if( GMGR.myLogLevel == 'debug'){
            console.log("request: " + req);
	  }

      return req;
    },

    tableReload: function( o, dt ) {
      var GMGR = YAHOO.imex.groupmgr;
      try{ 
        var state = dt.get('paginator').getState();
	    
	    if( GMGR.myLogLevel == 'debug'){
		  console.log( "tableReload: dt.state=" + YAHOO.lang.JSON.stringify( state ) );
	    }
	    
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
      }catch( x ){   
        console.log(x);
      }
    },

    /*
    getDefaultCookie: function(){
      var cookie = "";
      var GMGR = YAHOO.imex.groupmgr;
	  //var myCD = GMGR.myColumnDefs;

      for(var i = 0; i < GMGR.myCL.length; i++ ){

        var hidden= false;
        if( GMGR.myCD[GMGR.myCL[i]].hidden === true ){
              hidden= true;
        }
        cookie += GMGR.myCD[GMGR.myCL[i]].key + ":" + hidden +"|";
      }
      //alert( cookie );

      return cookie;
    },
    */

    handlePagination: function( oState, oDatatable ){
        
	  try{
	    oDatatable.get('paginator').setState( oState );
	    
	    var GMGR = YAHOO.imex.groupmgr;
        var newState = GMGR.myDataSource.my.myState;

        newState.startIndex = oState.recordOffset;
        newState.pageSize = oState.rowsPerPage;
            
        YAHOO.util.History.navigate( "groupmgr", 
			                          GMGR.generateStateString( newState ) );	    
	  }catch( x ){
	    console.log(x);
	  }
    },
    
    handleSorting: function( column ){
	
      var GMGR = YAHOO.imex.groupmgr;
      var newState = GMGR.myDataSource.my.myState;
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

        YAHOO.util.History.navigate( "groupmgr", 
                                      GMGR.generateStateString( newState ) );
      }catch( x ){
        console.log(x);
      }  
    },

    generateStateString: function( state ){            
      return YAHOO.lang.JSON.stringify( state );
    },
    
    parseStateString: function( statStr ){
      return YAHOO.lang.JSON.parse( statStr );
    },

    historyInit: function( init ){

        var GMGR = YAHOO.imex.groupmgr;
	
        var defstate = {
            startIndex: 0,
            pageSize: GMGR.defPageSize,
            scol: "id",
            sdir: "asc" };
                 
        if(  init !== undefined && init.watch !== undefined ){
            defstate.watch = init.watch;
        }

        var dst = YAHOO.lang.JSON.stringify( defstate );
        
        var bState = YAHOO.util.History.getBookmarkedState( "groupmgr" );
        var iState = bState || dst;
        
        YAHOO.util.History.register( "groupmgr", iState, 
                                     GMGR.handleHistoryNavigation ); 
        
        YAHOO.util.History.onReady( GMGR.historyReadyHandler );    
        
        try{
            YAHOO.util.History.initialize( "yui-history-field", 
                                           "yui-history-iframe" );
        } catch (x) {
            console.log(x);
        }                
    },
    
    handleHistoryNavigation: function( state ){
        
      var GMGR = YAHOO.imex.groupmgr;      
      var parsed = GMGR.parseStateString( state );      
      var request = GMGR.buildRequest( parsed );
        
      GMGR.myDataSource.my.myState = parsed;
         
      // reload data
      //------------
        
      var mdt = GMGR.myDataTable;
        
      GMGR.myDataSource
            .sendRequest( request, {
                success: mdt.onDataReturnSetRows,
                failure: mdt.onDataReturnSetRows,
                scope: mdt,
                argument: {}
            });

	  // select tab
	  //-----------

	  if( GMGR.myLogLevel == 'debug' ){
	    console.log("handle history: stab=" + parsed.stab);
	    console.log("handle history: GMGR.tabView=" + GMGR.tabView);
	  }

	  if( GMGR.tabView !== undefined ){
	    GMGR.tabView.set('activeIndex', parsed.stab );
	  }	
    },

    historyReadyHandler: function(){
        try{
            var cState = YAHOO.util.History.getCurrentState( "groupmgr" );
            YAHOO.imex.groupmgr.handleHistoryNavigation( cState );
        } catch (x) {
            console.log(x);
        }
    },

    //----------------------------
    // Shows/Hides selected column
    //----------------------------
     
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
            YAHOO.imex.groupmgr.contextMenuInit( YAHOO.imex.groupmgr );
        } catch (x) {
            console.log("toggle:" + x);
        }

        //var nCookie = YAHOO.imex.groupmgr.buildCookie();
        //YAHOO.util.Cookie.set("groupmgr", nCookie );                                      
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
        elLiner.innerHTML = '<center><a href="groupmgr?id=' + 
            oRecord.getData( "id" ) + 
            '">Edit</a></center>';
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
