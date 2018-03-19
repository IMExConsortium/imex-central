YAHOO.namespace("imex");

YAHOO.imex.journalstat = {

    stringify: YAHOO.lang.JSON.stringify,

    admus: "",
    owner: "",
    cflag: "",
    watch: "",
    loginId: null,
    prefs: null, 
   
    id: "",
    year:"",
    volume:"",
    issue:"",
    nnav:"",
    
    curateUrl: "",
    curatePat: "",
    pubmedUrl: "http://www.ncbi.nlm.nih.gov/pubmed/%%pmid%%",
    pubmedPat: "%%pmid%%",
    imexUrl: "imex/rec/%%imex%%",
    imexPat: "%%imex%%",
    
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

    myDataSource: null,
    myPaginator: null,
    
    myCP: { "date": "submission"},
    
    myCL: [ "id", "pub", "pages", "pmid", "imexId", "submission",
            "stage", "state",  "detail" ],
    myColumnDefs: [],

    jstab: [],
    jsyear: "",
    jsyearlst: [],
    jsvolume: "",
    jsvolumelst: [],
    
    jstable: [],

    jsctab: 0,
    
    init: function( init ){
      try{
	  var JS = YAHOO.imex.journalstat;
          JS.jid = init.jid;
	  JS.jsyear=init.jsyear;
	  JS.jsvolume=init.jsvolume;
	  
 	  //console.log("JS.init: init=" + JS.stringify(init));

          //JS.loginId = init.loginid;
          //JS.prefStr = init.prefStr;

          if( typeof JS.jstable != "undefined" ){
	      for( i =0; i < JS.jstable.length;i++){		  
		  JS.jstable[i].destroy();
	      }              
          }
	  
	  /*
	  var cookie = YAHOO.util.Cookie.get("journalstat");
          if( cookie == null ){
              cookie = JS.getDefaultCookie();                
              YAHOO.util.Cookie.set( "journalstat", cookie );
          }            
          if( cookie !== null ){
              this.buildCDefs( cookie );                
          }
          */

	  //console.log("JS.init calling jsTableLayoutInit");
	  JS.jsTableLayoutInit( init );
	  
      }catch( err ){
          console.error("JS.init: "+ err);
      }    
    },

    jsTableLayoutInit: function( init ){
        var JS = YAHOO.imex.journalstat;
	var jid = JS.jid;

	var Success = function( o ){
	    var response = YAHOO.lang.JSON.parse( o.responseText );
	    var table = response.init.tables;
	    	 
	    JS.jstab = table["stage-list"];
	
	    JS.jsyear = response.init.year;
	    JS.jsyearlst = response.init["year-list"];
	    
	    JS.jsvolume = response.init.volume;
	    JS.jsvolumelst = response.init["volume-list"];

	    JS.jtitle = response.init.title;

	    //console.log( "table-def:" + JS.stringify( JS.jstab) );
	    //console.log( "jsTableLayoutInit: year=" + response.init.year 
	    // + " volume=" + response.init.volume );
	    
	    JS.jsfield = [];

	    for( var s1 = 0; s1 < JS.jstab.length; s1++){
		var stg = JS.jstab[s1]["name"];
		for( var s2 = 0; s2 < JS.jstab[s1]["status-list"].length; s2++){
		    
		    var stat = JS.jstab[s1]["status-list"][s2];		   
		    JS.jsfield.push( {key: "['" + stg + "-" + stat +"']"} );
		}
	    }

	    //console.log( "jsfield:" + JS.stringify( JS.jsfield) );
	    
	    JS.jsInitView( init );	    
	}
	
	var Fail = function( o ){
	    console.error( "jsTableLayoutInit:" +  o );
	}
	
        var callback = { cache:false, timeout: 5000, 
                         success: Success,
                         failure: Fail
                       }; 
        try{
	    var irequest = "&opp.year="+JS.jsyear+"&opp.volume="+JS.jsvolume;
	    //console.log("jsTableLayoutInit ireq: id=" + jid + irequest);
            YAHOO.util.Connect
                .asyncRequest( 'GET', 
                               'journalstat?op.init=tables&id=' + jid + irequest,
                               callback );
        } catch (x) {
            console.error( x );
        }	
    },
    
    jsInitView: function( init, cord, chid ) { 

        try{	    
            var JS = YAHOO.imex.journalstat;
            
            JS.formatterInit();
        
            if( init !== undefined ){
                //JS.admus = init.admus;
                //JS.owner = init.owner;
                //JS.cflag = init.cflag;
                //JS.watch = init.watch;
            }
                                        
            // journal title
            //--------------
            
            var jname = YAHOO.util.Dom.get('journal-name');
            jname.textContent = JS.jtitle;
                
            // year/volume buttons
	    //--------------------
	    
            JS.navBtnInit(
                { pmgr: JS,
                  nbtn: "yearCurrent",
                  navig: "year",
                  ntxt: JS.jsyear,
                  items: JS.jsyearlst,
                  navmnu: JS.yearSel,
                  ncnt: "year-curr-container",
                  nnme: "year-curr-button"
                }
            );

            JS.navBtnInit(
                { pmgr: JS,
                  nbtn: "volumeCurrent",
                  navig: "volume",
                  ntxt: JS.jsvolume,
                  items: JS.jsvolumelst,
                  menu: JS.volumeSel,
                  ncnt: "volume-curr-container",
                  nnme: "volume-curr-button"
                }
            );
	    
	    // build response schema

	    var fields = [{key:"['name']"}];
	    fields = fields.concat(JS.jsfield);
	    
	    
	    //console.log( "FIELDS: " + JS.stringify(fields));
	    
            // create datasource
            //------------------

            JS.tableDS = new YAHOO.util.DataSource("journalstat?id=" + init.jid 
                                                   + "&op.ystat=ystat&"); 
            
            JS.tableDS.responseType = YAHOO.util.DataSource.TYPE_JSON; 
            JS.tableDS.responseSchema = { 
                resultsList: "count", 
                fields: fields  //, 
                //metaFields: { 
                //    totalRecords: "records.totalRecords", 
                //    sortKey: "records.sort", 
                //    sortDir: "records.dir"                    
                //}
            }; 
            
            JS.tableDS.my = { myState: null };
            
            JS.tableDS.doBeforeParseData = function( oRequest , 
						     oFullResponse , 
						     oCallback ){
		var JS = YAHOO.imex.journalstat;

		//console.log("dbpd:   req= "+ JS.stringify(oRequest));
		//console.log("dbpd: oresp= "+ JS.stringify(oFullResponse));
		
		var response = { count:[] };

		try{                    
		    var ikList = [];
		    var ikMap = {};
		    var counts = oFullResponse.records.counts;

		    for( var i = 0; i< counts.length; i++ ){
			var issue = counts[i][0];
			if( ! ikList.includes( issue )){
			    ikList.push( issue );
			    ikMap[ issue ] = {};
			}
			var cname = counts[i][1]+"-"+counts[i][2];
			ikMap[issue][cname]=counts[i][3];		    	
		    }
		    
                    for( var i = 0; i< ikList.length; i++ ){
			var issue = ikList[i];

			var row = { name: issue };

			// pre-set blank fields
			for( var j = 0; j<JS.jsfield.length; j++ ){
			    var key = JS.jsfield[j]["key"];
			    
			    row[key.substring(2,key.length-2)] = 0;
			}
			
			var keys = Object.keys( ikMap[ issue ] );
			for( var j = 0; j<keys.length; j++ ){
			    row[keys[j]]=ikMap[ issue ][keys[j]];
			}
			response.count.push(row);
		    }
		           
		    // set year/volume buttons & menus
		    //--------------------------------
		    
		    JS.updateYV( oFullResponse.init );	    
		    
		} catch (x) {
                    console.error( x );
		}            
		//console.log("newresponse: " + JS.stringify( response ) );
                return response;            
            };
	    
	    // stage tabs
	    //-----------

            JS.stageTabs = new YAHOO.widget.TabView("stageTab");
	    
	    var atab = 0;
	    
	    for( var i = 0; i < JS.jstab.length; i++ ){
	        	
		var active = false;
		if( i == atab ){
		    active = true;
		}

		var label = JS.jstab[i].name;
		var content = '<div id="tabTable-' + i 
		    + '" class="jstat-table"/>'; 
		
		// table within tab
		
		var ntab =  new YAHOO.widget.Tab({label: label,
						  content: content,
						  active: active
						});
		
	    	JS.stageTabs.addTab( ntab );
		JS.jstable[i] = JS.jsInitTable( "tabTable-" + i, 
						JS.jstab[i], JS.tableDS ) ;		
	    }

	    JS.stageTabs.addListener( "activeIndexChange", 
				      JS.handleTabClick ); 
	    JS.historyInit( init );
	    
        } catch (x) {
            console.error( x );
        }
        
        return { 
            ds: JS.myDataSource, 
            dt: JS.myDataTable 
        };        
	
        // initView ends here
    },
    
    jsInitTable: function( elem, jstab, datasource ){ 
        try{
	    var JS = YAHOO.imex.journalstat;
	    
	    //console.log("initTable: "+ JS.stringify(jstab));
	    //console.log( "initTable:  name "+ jstab.name);

            // datatable configuration
            //------------------------
            
            var initReq = "opp.off=0&opp.max=25"
                + "&opp.year=" + JS.jsyear
                + "&opp.volume=" + JS.jsvolume;
            
            var config = {
                initialLoad: false,
                dynamicData : true,
                draggableColumns: false
            };
            
	    //"stage":{ key:"stage", label:"Stage",
            //      sortable:false, resizeable:false, hideable: true,
            //      formatter:"center", menuLabel:"Stage" },
	    
	    var cdef = [{key:"['name']", label:"Issue",
			 sortable:false, resizeable:false, hideable: false, 
			 className: "jstat-column-fixed", formatter:"center",
			 menuLabel:"Issue"}];

	    var stg = jstab["name"];
	    for( var s = 0; s < jstab["status-list"].length; s++){
		    
		var stat = jstab["status-list"][s];
		cdef.push( {key: "['" + stg + "-" + stat + "']", label: stat, 
			    className: "jstat-column-fixed",
			    sortable:false, resizeable:false, hideable: false,
                            menuLabel: stat, formatter:"vlink"});
	    }

	    cdef.push( {key:"['spacer']", label:"",
			sortable:false, resizeable:false, hideable: false,
			className: "jstat-column-spacer",
			menuLabel:".", formatter:"blank", maxAutoWidth: "1920px"} );
	    
	    cdef.push( {key:"['stage-total']", label:"Total (" + jstab.name + ")",
			className: "jstat-column-fixed",formatter:"stage", stage: "'"+ jstab.name,
			sortable:false, resizeable:false, hideable: false,
			menuLabel:"Stage total"} );
	    
	    cdef.push( {key:"['issue-total']", label:"Total (Issue)",
			className: "jstat-column-fixed", formatter:"total",
			sortable:false, resizeable:false, hideable: false,
			menuLabel:"Isue Total"} );

	    //console.log("cdef: "+ JS.stringify(cdef));
	    
            // Instantiate DataTable
            //----------------------
           
            var table = new YAHOO.widget.DataTable(
                elem, cdef, datasource, config
            );
                         
            // table.sortColumn = JV.handleSorting;
            
            table.my = { 
                requestBuilder: JS.jstableRB,
		stage: jstab.name 
            };
            
            JS.yearBtn.my.table = table;
            JS.volumeBtn.my.table = table;
            
            // Show loading message while page is being rendered
            
            //table.showTableMessage( table.get("MSG_LOADING"), 
            //                         YAHOO.widget.DataTable.CLASS_LOADING );
            
            table.doBeforeLoadData =
                function( oRequest, oResponse, oPayload ){
                    //console.log("table.doBeforeLoadData ");
		    try{
                        
                    } catch (x) {
                        console.error( x );
                    }
                    return true;
                };
            
            table.handleLayoutChange = function( ev, o ){
                try{
		    var JS = YAHOO.imex.journalstat;
                    var nCookie = JS.buildCookie();
                    JS.updateUserTablePref( nCookie );
                    YAHOO.util.Cookie.set( "journalstat", nCookie );
                } catch (x) { 
                    console.error( x );
                }
            };
	    

	    // load data
            //----------
	    
            datasource.sendRequest( JS.jstableRB(null,null), 
				    {success: table.onDataReturnSetRows,
				     failure: table.onDataReturnSetRows,
				     scope: table,
				     argument: {foo:"bar"}
				    });	    
	    return table;
	} catch (x) {
            console.error( x );
        }
    },
    
    jstableRB: function( oState, oSelf ) {
	
	// request builder

        var JS = YAHOO.imex.journalstat;
	var req = "opp.year=" + JS.jsyear + "&opp.volume=" + JS.jsvolume;

        //console.log( "jstableRB: oSelf:" + oSelf + " oState:" + oState);
   	//console.log("jstableRB: request:" + req); 
        
	return req;
    },

    updateYV: function( resp ){
	
	var JS = YAHOO.imex.journalstat;
        
        // update buttons/menus
        
	// -----------------------------------------------------------------------	
        // YEAR
	
	JS.jsyear = resp["year"];

        JS.menuRebuild( JS.yearCurrent, JS.jsyear, 
			resp["year-list"] );
                    
        if( resp["year-list"].length <=-1){
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

        // -----------------------------------------------------------------------	
	// VOLUME

	JS.jsvolume = resp["volume"];
	
        JS.menuRebuild( JS.volumeCurrent, JS.jsvolume, 
			resp["volume-list"] );
        
	if(resp["volume-list"].length <=-1){ 
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
        
        //myself.myDataSource.my.myState.navig.year = filter.year;
        //myself.myDataSource.my.myState.navig.volume = filter.volume;
        
    },
    
    menuRebuild: function( button, value, vList ){
	
	//console.log("menurebuild: button=" + button + "  value=" + value);

        if( button !== undefined ){
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
		console.error( x );
            }
	}
    },

    handleTabClick: function(e){

	var JS = YAHOO.imex.journalstat;

	try{	    
	    //console.log(JS.stageTabs.get("activeIndex"));
	    //console.log("new tab: " + e.newValue );
	    
	    var ntable = JS.jstable[e.newValue];
	    var request = JS.buildRequest( JS.tableDS.my.myState );

            //console.log("handleTabClick:-> request:" + request );

            // reload data
	    //------------

            JS.tableDS.sendRequest( request, {
                success: ntable.onDataReturnSetRows,		
                failure: ntable.onDataReturnSetRows,
                scope: ntable,
                argument: {}
            });
	    
	} catch(x){
            console.error( x );
        }	
    },
    
    getDefaultCookie: function(){
        var cookie = "";
        var JS = YAHOO.imex.journalstat;

	/*
        for(var i = 0; i < JS.myCL.length; i++ ){
            var hidden= false;
            if(  journalview.myCD[journalview.myCL[i]].hidden === true ){
                hidden= true;
            }
            cookie += journalview.myCD[journalview.myCL[i]].key + ":" + hidden +"|";
        }
	*/
        return cookie;
    },

    tableReload: function( o, dt ) {
        try {            
            var state = dt.get('paginator').getState();
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
            console.error( "JS.tableReload: " + x);
        }
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
            };
            
            var Fail = function ( o ) {
                console.error( "update failed: id=" + o.argument.id ); 
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
                console.error( x );
            }
        }
    },
    
    updateUserTablePref: function( cookie ){
        var my = YAHOO.imex.journalview;
        var loginId = my.loginId;
        
        if( typeof loginId  != "undefined" && loginId != "" ){
            try{
                YAHOO.util.Connect
                .asyncRequest( 'POST', 
                               'userprefmgr?id=' + loginId + '&op.updateTable=true',
                               null, "opp.tableLayout=" + cookie );        
            } catch (x) {
                console.error( x );
            }
        }
    },
    
    buildCDefs: function( cookie ){

        var my = YAHOO.imex.journalview;
        var col = cookie.split("|");
        
        if(col.length > 0){
            my.myColumnDefs=[];
            
            for( var i =0; i < col.length-1; i++ ){
                try{
                
                    var cs = col[i].split(":");
                    if( my.myCD[cs[0]] !== undefined ){
                        
                        my.myColumnDefs.push(my.myCD[cs[0]]);
                        if(cs[1] === 'true'){
                            my.myColumnDefs[my.myColumnDefs.length-1].hidden = true;
                        } else {
                            my.myColumnDefs[my.myColumnDefs.length-1].hidden = false;
                        }
                    }
                    
                } catch (x) {
                    console.error( "I:"+ i + "-> " + cs[0] + " ex=" + x );
                }
            }        
        }
    },

    buildCookie: function(){

        var JS = YAHOO.imex.journalstat;
        var cookie = "";
	/*
        var ac = my.myDataTable.getColumnSet().keys;
        for(var i=0; i< ac.length; i++ ){

            var key = ac[i].getKey();
            var hid = ac[i].hidden;
            if( my.myCD[key] !== undefined ){
                cookie += key + ":" + hid + "|";
            } else {              // nested column
                key = my.myCP[key];
                if( my.myCD[key] !== undefined ){
                    cookie += key + ":" + hid + "|";
                }
            }
        }
	*/
        return cookie;
    },    

    historyInit: function( init ){

        var JS = YAHOO.imex.journalstat;
        
        var defstate = {
            navig:{ year: JS.jsyear,
                    volume: JS.jsvolume,
		    tab: JS.jsctab
		  }
	};
            	
        var dst = YAHOO.lang.JSON.stringify( defstate );        
        var bState = YAHOO.util.History.getBookmarkedState( "journalstat" );
        var iState = bState || dst;
        
        YAHOO.util.History.register( "journalstat", iState, 
                                     JS.handleHistoryNavigation ); 
        
        YAHOO.util.History.onReady( JS.historyReadyHandler );    
        
        try{
            YAHOO.util.History.initialize( "yui-history-field", 
                                           "yui-history-iframe" );
        } catch (x) {
            console.log(x);
        }                
    },
 
    historyInitOld: function( init ){

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
        
        YAHOO.util.History
            .navigate( "journalview", 
                       PMGR.generateStateString( newState ) );
    },

    handleNav: function( ev, o ){

        try{

	    var JS = YAHOO.imex.journalstat;

            var navig = o.navig;
            var newVal = ev.newValue.value;
            
            var newState = JS.tableDS.my.myState;
            
	    console.log("handleNav: navig=" + navig + " val=" +newVal );

            // LS: watch flag ?
            
            newState.startIndex = 0;
            newState.navig[navig] = newVal;

	    if( navig == "year"){
		newState.navig["volume"] = "";
	    }

            JS.nnav = navig;
            
            console.log("handle nav: newstate: "
                        + YAHOO.lang.JSON.stringify( newState ));            
            
            YAHOO.util.History
                .navigate( "journalstat", 
                           JS.generateStateString( newState ) );
            
        } catch (x) {
            console.error(x);
        }
    },

    handlePagination: function( state, datatable ){
        
        var JV = YAHOO.imex.journalview;
        var newState = JV.myDataSource.my.myState;
        
        newState.startIndex = state.recordOffset;
        newState.pageSize = state.rowsPerPage;
        
        YAHOO.util.History
            .navigate( "journalview", 
                       JV.generateStateString( newState ) );
    },
    
    handleSorting: function( column ){
        
        var JV = YAHOO.imex.journalview;
        var newState = JV.myDataSource.my.myState;
        try{
            var sdir = this.getColumnSortDir( column );
            newState.startIndex = 0;
            
            if( column.key !== undefined && sdir !==undefined ){
                newState.scol = column.key;
                newState.sdir = sdir.substr( 7 );
            }
            
            YAHOO.util.History
                .navigate( "journalview", 
                           JV.generateStateString( newState ) );
        } catch (x) {
            console.error( x );
        }
        
    },
    
    handleHistoryNavigation: function( state ){
	
        var JS = YAHOO.imex.journalstat;

	//console.log("HandleHistoryNavigation:" + state );
	//console.log("HandleHistoryNavigation:-> state:" + JS.stringify(state) );

	try{

	    if( typeof state == "string" ){
		
		var parsed = JS.parseStateString( state ); 
		var request = JS.buildRequest( parsed );
        
		JS.tableDS.my.myState = parsed;

		//console.log("HandleHistoryNavigation:-> request:" + request );
        
		// reload data
		//------------
        
		var atab = JS.stageTabs.get( "activeIndex" );
		//console.log( "active tab: " + atab );
		
		var mdt = JS.jstable[ atab ];
        
		JS.tableDS.sendRequest( request, {
		    success: mdt.onDataReturnSetRows,
		    failure: mdt.onDataReturnSetRows,
		    scope: mdt,
		    argument: {}
		});        
	    }
	} catch (x) {
            console.error( x );
        }
    },
    
    generateStateString: function( state ){            
	//console.log("state: " + YAHOO.lang.JSON.stringify(state));
        return YAHOO.lang.JSON.stringify( state );
    },
    
    parseStateString: function( statStr ){
        return YAHOO.lang.JSON.parse(statStr);
    },
    
    generateLinkState: function( status, stage, year, volume, issue ){

        //LS: watch ?
        var filter = { status:status, stage:stage, editor:'', owner:'', cflag:''  };
        var navig = { year:year, volume:volume, issue:issue  };
        var state = { startIndex:0, pageSize:25, filter:filter, 
		      navig:navig, scol:'id', sdir:'asc' };
        return YAHOO.lang.JSON.stringify( state );
    },
    
    buildRequest: function ( state ){
        
        var req = "&opp.year=" + state.navig.year +
            "&opp.volume=" + state.navig.volume;
        
        YAHOO.imex.journalstat.nnav = "";
        return encodeURI( req );
        
    },

    requestBuilder: function( oState, oSelf ) {

        var myself = YAHOO.imex.journalview;
        
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
        //console.log("request: " + req);
        
        return req;
    },

    historyReadyHandler: function(){

        try{
            var cState = YAHOO.util.History.getCurrentState( "journalstat" );
            YAHOO.imex.journalstat.handleHistoryNavigation( cState );
        } catch (x) {
            console.error( x );
        }
    },

    initView: function( init, cord, chid ) { 
        try{
    
            var PMGR = YAHOO.imex.journalview;
            var JV = YAHOO.imex.journalview;
            
            this.formatterInit();
        
            if( init !== undefined ){
                JV.admus = init.admus;
                JV.owner = init.owner;
                JV.cflag = init.cflag;
                JV.watch = init.watch;
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
                
                // journal title
                //--------------
                
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
                
                YAHOO.util.Connect.asyncRequest( 'GET', query, yviCallback );        
            } catch (x) {
                console.error( x );
            }
        
            if( typeof JV.myDataTable == "undefined" ){
                try{
                    YAHOO.util.Connect.asyncRequest( 'GET', 
                                                     "acom?op.pstac=ac" , 
                                                     stateCallback );        
                    YAHOO.util.Connect.asyncRequest( 'GET', 
                                                     "acom?op.psgac=ac", 
                                                     stageCallback );        
                } catch (x) {
                    console.error( x );
                }
            }
            
            // create datasource
            //------------------

            JV.myDataSource = new YAHOO.util.DataSource("journalstat?id=" + init.jid 
                                                          + "&op.ystat=ystat&"); 
            
            JV.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
            JV.myDataSource.responseSchema = { 
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
            
            JV.myDataSource.my = { myState: null };
            
            JV.menuRebuild = function( button, value, vList ){

                if( button !== undefined){

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
			console.error( x );
                    }
		}
            };
            
            JV.myDataSource.doBeforeParseData = function( oRequest , 
							  oFullResponse , 
							  oCallback ){
            
                try{
                    
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
                    
                    var JV = YAHOO.imex.journalview;
                    if( filter.year != null){
                        //console.log("beforeparse:  year=" + filter.year);
                        JV.year = filter.year;
                    }
                    if( filter.volume != null){
                        //console.log("beforeparse:  volume=" + filter.volume);
                        JV.volume = filter.volume;
                    }
                    if( filter.issue != null){
                        //console.log("beforeparse:  issue=" + filter.issue);
                        JV.issue = filter.issue;
                    }
                } catch (x) {
                    console.error( x );
                }
                
                return oFullResponse;            
            };
            
            // create paginator
            //-----------------
            
            JV.myPaginator = new YAHOO.widget.Paginator(
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
                + "&opp.yr=" + YAHOO.imex.journalview.year
                + "&opp.vo=" + YAHOO.imex.journalview.volume
                + "&opp.is=" + YAHOO.imex.journalview.issue
            
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
            
            JV.myDataTable = new YAHOO.widget.DataTable(
                "pubtab", PMGR.myColumnDefs, 
                JV.myDataSource, myConfig
            );
            
            JV.myPaginator
                .unsubscribe( "changeRequest", 
                              JV.myDataTable.onPaginatorChangeRequest );
            JV.myPaginator
                .subscribe( "changeRequest", 
                            JV.handlePagination, JV.myDataTable, true );
            
            JV.myDataTable.sortColumn = JV.handleSorting;
            
            JV.myDataTable.my = { 
                watchFlg: JV.watch,
                stateFlt: JV.stateBtn, 
                partnerFlt: JV.partnerBtn,
                ownerFlt: JV.owner,
                admusFlt: JV.admus,
                cflagFlt: JV.cflag,
                requestBuilder: JV.requestBuilder
            };
            
            JV.myDataTable.my.filter = {
                owner: JV.owner, 
                editor: JV.admus,
                cflag: JV.cflag  
            };
            
            JV.myDataTable.my.navig = {
                year: JV.year, 
                volume: JV.volume,
                issue: JV.issue  
            };
            
            JV.stateBtn.my.table = JV.myDataTable;
            JV.stageBtn.my.table = JV.myDataTable;
            
            JV.yearBtn.my.table = JV.myDataTable;
            JV.volumeBtn.my.table = JV.myDataTable;
            JV.issueBtn.my.table = JV.myDataTable;
            
            // Show loading message while page is being rendered
            
            JV.myDataTable
                .showTableMessage( JV.myDataTable.get("MSG_LOADING"), 
                                   YAHOO.widget.DataTable.CLASS_LOADING);
            
            JV.myDataTable.doBeforeLoadData = 
                function( oRequest, oResponse, oPayload ){
                    
                    try{
                        var meta = oResponse.meta;
                        oPayload.totalRecords 
                            = meta.totalRecords || oPayload.totalRecords;
                        
                        var oPayloadOld = oPayload;
                        oPayload.pagination = {
                            rowsPerPage: meta.paginationRowsPerPage || 10,
                            recordOffset: meta.paginationRecordOffset || 0
                        };
                        
                        if( meta.sortKey !== undefined ){
                            oPayload.sortedBy = { key: meta.sortKey,
                                                  dir: "yui-dt-" + meta.sortDir };
                        }
                        
                    } catch (x) {
                        console.error( x );
                    }
                    return true;
                };
            
            JV.myDataTable.handleLayoutChange = function( ev, o ){
                try{
                    var nCookie = YAHOO.imex.journalview.buildCookie();
                    YAHOO.imex.journalview.updateUserTablePref( nCookie );
                    YAHOO.util.Cookie.set( "journalview", nCookie );
                } catch (x) { 
                    console.error( x );
                }
            };
            
	    // header menu
            
	    PMGR.headMenuInit( PMGR );
            
	    // record editing popups
	    
	    if(YAHOO.imex.recordedit !== undefined){
		YAHOO.imex.recordedit.init(JV);
            }

            // table row popup
            //----------------
            /*
            console.log("myself.loginId:"+ PMGR.loginId );
            
            if( JV.loginId != null && PMGR.loginId> 0 ){
                JV.myDataTable.subscribe( "rowClickEvent", JV.rowPopup );
            }
            */
            
	    // table layout changes

            PMGR.myDataTable.on( "columnReorderEvent",
                                 PMGR.myDataTable.handleLayoutChange );     
            
            PMGR.myDataTable.on( "columnHideEvent",
                                 PMGR.myDataTable.handleLayoutChange );     
            
            PMGR.myDataTable.on( "columnShowEvent",
                                 PMGR.myDataTable.handleLayoutChange );     
                        
            // CSS to add a black separator between the rows
            var sheet = document.createElement('style');
            sheet.innerHTML = ".yui-dt-data > tr > td {border-bottom: 1px solid black !important;}";
            document.body.appendChild(sheet); 
            
        } catch (x) {
            console.error( x );
        }
        
        return { 
            ds: PMGR.myDataSource, 
            dt: PMGR.myDataTable 
        };        
	
        // initView ends here
    },

    onSelectedMenuItemChange: function (event) {
        var oMenuItem = event.newValue;
        var text = oMenuItem.cfg.getProperty("text");
        
        for( var i = 0; i < this.my.items.length; i++ ){
            if (this.my.items[i].text === text ) {
                this.my.value = this.my.items[i].value;
            }
        }
        
        this.set( "label",  oMenuItem.cfg.getProperty("text") );       
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
                o.selmnu.push( {value: value, text: text} );        
            }
            
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
            
            //if( o.pmgr.myDataTable !== null ){  
	    if( o.pmgr.jstable[0] !== null ){  
                o.pmgr[o.selbtn]
                    .on( "selectedMenuItemChange",
                         o.pmgr.handleFilter, 
                         {filter: o.filter},
            //             o.pmgr.myDataTable );
			 o.pmgr.jstable[0] );
            }
            
        } catch (x) {
            console.error(x);
        }
    },
    
    navBtnInit: function( o ) {
        try{
            
            // navigate button 
            //----------------
            
            o.navmenu = [{ text: o.ntxt, value: o.ntxt }];
            
            if( o.items != null ){
                o.navmenu = [];
                for( var i = 0; i < o.items.length; i++){
                    
                    //var text = o.items[i].name;
                    //var value = o.items[i].value;
                    //if( value === undefined ){
                    //    value = text;
                    //}
                    
                    var text = o.items[i];
                    var value = o.items[i];
                    
                    o.navmenu.push( {value: value, text: text} );
                }
            }
            
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
            

            //if( o.pmgr.myDataTable !== null ){  
	    if( o.pmgr.jstable[0] !== null ){  
                o.pmgr[o.nbtn]
                    .on( "selectedMenuItemChange",
                         o.pmgr.handleNav, 
                         {navig: o.navig},
                         //o.pmgr.myDataTable );
			 o.pmgr.jstable[0] );
            }

        } catch (x) {
            console.error( "navBtnInit:" + x );
        }
    },

    headMenuInit:  function( o ){
        try{
            var my = YAHOO.imex.journalview;
            var colmenu = new YAHOO.widget.Menu( "colmenu" );
            
            var defaultTableLayout = function( o ){
                var my = YAHOO.imex.journalview;    
                
                if( typeof my.loginId  != "undefined" && my.loginId != "" ){
                    
                    var Success = function( response ){
                        // should do something here, shouldn't I ? 
                    };
		    
                    var Fail = function ( o ) {
                        console.error( "update failed: id=" + o.argument.id ); 
                    };
                    var callback = { cache:false, timeout: 5000, 
                                     success: Success,
                                     failure: Fail
                                   };                     
                    try{
                        
                        YAHOO.util.Connect
                            .asyncRequest( 'GET', 
                                           'userprefmgr?id=' + my.loginId +
					   '&op.defaultTableLayout=true',
                                           callback );        
                    } catch (x) {
                        console.log("AJAX Error:"+x);
                    }
                }
                var cookie = my.getDefaultCookie();
                my.buildCDefs(cookie);
                
                YAHOO.util.Cookie.set( "pubmgr", cookie );
                var myDataTable = my.myDataTable;
                
                my.init( {admus: my.admus,
                          owner: my.owner,
                          cflag: my.cflag,
                          watch: my.watch,
                          loginid: my.loginId });
            };
            
            var clist = [];
            
            for( var i = 0;  i < my.myColumnDefs.length; i++ ){
                if( my.myColumnDefs[i].menuLabel !== undefined ){

		    
		    console.log("HMB: col=" + my.myColumnDefs[i].menuLabel);
                    var trg = my.myDataTable.getColumn( 
                        my.myColumnDefs[i].key ); 
                    
                    var item = { text: my.myColumnDefs[i].menuLabel, 
                                 id: "headmenu-col-" + my.myColumnDefs[i].key,
                                 checked: !trg.hidden, disabled:  !my.myColumnDefs[i].hideable,
                                 onclick: { fn: my.hiddenColToggle,
                                            obj: { tbl: my.myDataTable ,col: trg } } };
                    clist.push( item );
                }
            }
            
            colmenu.addItems( clist );

            var oConfMenu = [[{text:"Preferences", disabled: true }],
                             [{text: "Show Columns", 
                               submenu: colmenu }],
                             [{text:"Restore Default Layout",onclick: {fn: defaultTableLayout } }
                             ],
                             [{text:"Save...", disabled: true}]
                            ];        
            
            my.myDataTable.my.colmenu = colmenu;
            my.myDataTable.my.headmenu = new YAHOO.widget.ContextMenu(
                "headmenu", { trigger: my.myDataTable.getTheadEl() } );
            
            my.myDataTable.my.headmenu.addItems( oConfMenu );
            my.myDataTable.my.headmenu.render("pubtab");
            my.myDataTable.my.headmenu.subscribe( "triggerContextMenu", my.onHeadMenuTrigger);
            
        }  catch( x ){
            console.error( x );
        } 
    },
    
    onHeadMenuTrigger: function(p_sType, p_aArgs, p_myDataTable) {
        try{
            var my = YAHOO.imex.journalview;
            var table = my.myDataTable;
            var items = my.myDataTable.my.colmenu.getItems();
            
            for( var i =0; i< items.length; i++ ){
                var item = items[ i ];
                var id = item.id;
                var col = table.getColumn(id.replace( "headmenu-col-", "" ) );
                item.cfg.setProperty("checked", !col.hidden);
            }
        } catch (x) {
            console.error( x );
        }
    },

    //-----------------------------------
    // Hides deselected column attributes 
    //-----------------------------------
    
    hiddenColToggle: function( tp, ev, o ){
        
        try{
            if( o.col.hidden ){
                o.tbl.showColumn( o.col );
            } else {
                o.tbl.hideColumn( o.col );                
            }
            o.tbl.render();
        } catch (x) {
            console.error( x );
        }
        
        var nCookie = YAHOO.imex.journalview.buildCookie();
        YAHOO.util.Cookie.set( "journalview", nCookie );
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
                '" target="icentral-tab">'+ oRecord.getData( "pmid" ) +'</a>';
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
                        imex + '" target="icentral-tab">'+ imex +'</a>';
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

    myVlinkFormatter: function(elLiner, oRecord, oColumn, oData) {

	var JS = YAHOO.imex.journalstat;
	var data = oRecord.getData();
	var index = JS.stageTabs.get("activeIndex");
	
	var cyr = JS.jsyear;
	var cvo = JS.jsvolume;
	var cis = data["['name']"];
	var cstg = JS.stageTabs.getTab(index).get("label");
	var csts = oColumn["label"];
	
	var params = "&IYear=" + cyr 
	    + "&IVolume=" + cvo 
	    + "&IIssue=" + cis
	    + "&IStage=" + cstg
	    + "&IStatus=" + csts;
	
	var url = "journalview?id="+ JS.jid + params;
	
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
   
	if( oData > 0 ){
	    elLiner.innerHTML = '<a href="' + encodeURI(url) 
		+ '">'+oData+'</a>';
	} else {
	    elLiner.innerHTML = "0";
	}
    },

    blankFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = '   ';	
    },
    
    totalFormatter: function(elLiner, oRecord, oColumn, oData) {
        
	var JS = YAHOO.imex.journalstat;
	
	YAHOO.util.Dom.addClass( elLiner, "yui-dt-center" );
	
	var total = 0;
	var data = oRecord.getData();
	
	for(var key in  data){
	    if( key !== "['name']"){		
		if( data[key] != NaN){
		    total += data[key];
		}
	    }
	}
        
        var cyr = JS.jsyear;
        var cvo = JS.jsvolume;
        var cis = data["['name']"];
        
	var params = "&IYear=" + cyr
            + "&IVolume=" + cvo
            + "&IIssue=" + cis

        var url = "journalview?id=" + JS.jid + params;
	
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
	
        if( total > 0 ){
	    elLiner.innerHTML = '<a href="' + encodeURI(url)
                + '">' + total + '</a>';
        } else {
            elLiner.innerHTML = "0";
        }	
    },

    stageFormatter: function(elLiner, oRecord, oColumn, oData) {
        
	var JS = YAHOO.imex.journalstat;

	YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
	
	var total = 0;
	var stage = oColumn["stage"];
	var data = oRecord.getData();
	for(var key in  data){
	    if( key.indexOf(stage) !== -1){
                total += data[key];
            }
	}
	var data = oRecord.getData();
	var index = JS.stageTabs.get("activeIndex");
	
	var cyr = JS.jsyear;
	var cvo = JS.jsvolume;
	var cis = data["['name']"];
	var cstg = JS.stageTabs.getTab(index).get("label");
	
	var params = "&IYear=" + cyr 
	    + "&IVolume=" + cvo 
	    + "&IIssue=" + cis
	    + "&IStage=" + cstg;
	
	var url = "journalview?id=" + JS.jid + params;
	
        YAHOO.util.Dom.addClass( elLiner, "yui-dt-center" );
   
	if( total > 0 ){
	    elLiner.innerHTML = '<a href="' + encodeURI(url)
                + '">' + total + '</a>';
	} else {
	    elLiner.innerHTML = "0";
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
        YDTF.vlink = this.myVlinkFormatter; 
        YDTF.blank = this.blankFormatter; 
        YDTF.total = this.totalFormatter; 
        YDTF.stage = this.stageFormatter; 
    }
};

//YAHOO.util.Event.addListener(
//    window, "load", YAHOO.imex.journalview ) ;
