YAHOO.namespace( "imex" );

YAHOO.imex.journalmgr = {
        
    loginId: "",

    partnerBtn: { my:{value:"",foo:"partner"} },
    partnerSel: [{ text: "--- ANY ---", value: "" },
                 { text: "--- IMEX ---", value: "" },
                 { text: "DIP", value: "DIP" },
                 { text: "IntAct", value: "IntAct" },
                 { text: "MINT", value: "MINT" },
                 { text: "MPIDB", value: "MPIDB" }],


    myColumnDefs: [],
    
    myDataSource: null,
    myPaginator: null,
    
    myCD:{   
        id:{ key:"id", label:"ID", menuLabel:"ICID",  sortable:true, resizeable:false, className:"id",hidden:true, hideable: true, formatter:"icid" },    
        nlmid:{ key:"nlmid", label:"NLMID", menuLabel:"NLMID", sortable:true, resizeable:false, className:"nlmid",hideable: true, formatter:"center" },
        title:{ key:"title", label:"Journal<br/>Title", menuLabel:"Journal Title", sortable:true, resizeable:false, hidden:false, hideable: false,
                formatter:"jtitle"},  

        imex:{ key:"imex", label:"IMEX<br/>Journal", menuLabel:"IMEX", sortable:false, resizeable:false, hidden:false, hideable: true, formatter: "cbox" },
        imexDB:{ key:"imexDB", label:"Tracked By", menuLabel:"Tracked By", sortable:false, resizeable:false, hidden:false, hideable: true, formatter:"imexDB" },        

        pq:{ key:"pq", label:"pre-Queue", menuLabel:"pre-Queue", xor:"pqDet", dat: "pqueue", filter:"&status=&stage=PREQUEUE",
             formatter:"dat-url-center", sortable:true, resizeable:false, hideable: true },

        pqDet:{ key:"pqDet", label:"pre-Queue", menuLabel:"pre-Queue (detailed)", xor:"pq",
                sortable:true, resizeable:false, hidden:true, hideable: true,
                 children:[
                     {key:"pqueueNew", label:"New", sortable:false, resizeable:false, formatter:"url-center", filter:"&status=NEW&stage=PREQUEUE" },
                     {key:"pqueueDiscarded", label:"Discarded", sortable:false, resizeable:false, formatter:"url-center",filter:"&status=DISCARDED&stage=PREQUEUE" }
                 ]},

        qu:{ key:"qu", label:"Queue", menuLabel:"Queue", xor:"quDet", dat: "queue", filter:"&opp.sfv=&opp.gfv=QUEUE",
             formatter:"dat-url-center", sortable:false, resizeable:false, hideable: true },
        
        quDet:{ key:"quDet", label:"Queue", menuLabel:"Queue (detailed)", xor:"qu",
                sortable:false, resizeable:false, hidden:true, hideable: true,
                children:[
                    {key:"queueNew", dat: "queueNew", label:"New", sortable:false, resizeable:false, formatter:"url-center", filter:"&status=NEW&stage==QUEUE" },
                    {key:"queueDiscarded", dat: "queueDiscarded", label:"Discarded", sortable:false, resizeable:false, formatter:"url-center", filter:"&status=DISCARDED&stage=QUEUE" }
                ]},        
        
        cu:{ key:"cu", label:"Curation", menuLabel:"Curation", xor:"cuDet", dat: "curation", filter:"&status=&stage=CURATION",
             formatter:"dat-url-center", sortable:true, resizeable:false, hideable: true },

        cuDet:{ key:"cuDet", label:"Curation", menuLabel:"Curation (detailed)", 
                sortable:true, resizeable:false, hidden:true, hideable: true, xor:"cu",
                children:[
                    {key:"curationReserved", dat: "curationReserved", label:"Reserved", 
                     sortable:false, resizeable:false, formatter:"url-center", filter:"&status=RESERVED&stage=CURATION" },
                    {key:"curationInprogress", dat: "curationInprogress", label:"In Progress", 
                     sortable:false, resizeable:false, formatter:"url-center", filter:"&status=INPROGRESS&stage=CURATION" }
                ]},
        
        rel:{ key:"rel", label:"Released", menuLabel:"Released", xor:"relDet",  dat:"release", filter:"&status=&stage=RELEASE",
              formatter:"dat-url-center", sortable:true, resizeable:false, hideable: true },

        relDet:{ key:"relDet", label:"Released", menuLabel:"Released (detailed)",  xor:"rel", 
                 sortable:true, resizeable:false, hidden:true, hideable: true,
                 children:[
                     {key:"releaseReleased", dat: "releaseReleased", label:"Released", 
                      sortable:false, resizeable:false, formatter:"url-center", filter:"&status=RELEASED&stage=RELEASE" },
                     {key:"releaseRetracted", dat: "releaseRetracted", label:"Retracted", 
                      sortable:false, resizeable:false, formatter:"url-center", filter:"&status=RETRACTED&stage=RELEASE" }
                 ]},

        detail:{ key:"detail", label:"",sortable:false, resizeable:false, hide: false, hideable: false, 
                 formatter:"elink" , className:"detail" }
    },
    
    myCP: {
        "date": "submission","modTStamp":"modified","actTStamp":"activity", "pqueueNew":"pqDet","pqueue":"pqDet", "queueNew":"quDet", "curation":"cuDet","release":"relDet"},
    
    myCL: [ "id", "nlmid", "title", "imex","imexDB", "pq","pqDet","qu","quDet","cu","cuDet","rel","relDet", "detail" ],

   
    myRequestBuilder: function( oState, oSelf ){

        try{
            
        
        // get state (or use defaults)
        //----------------------------

        oState = oState || {pagination:null, sortedBy:null};
        var sort = (oState.sortedBy) ? oState.sortedBy.key : "id";
        var dir = (oState.sortedBy && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC) ? "false" : "true";
        var startIndex = (oState.pagination) ? oState.pagination.recordOffset : 0;
        var results = (oState.pagination) ? oState.pagination.rowsPerPage : 10;
        
        // filters
        //--------
        var sfVal ="";
        var pfVal ="";
        var efVal ="";

        try {
            sfVal = oSelf.my.stateFlt.my.value;
            pfVal = oSelf.my.partnerFlt.my.value;
            efVal = oSelf.my.editorFlt.getInputEl().value;
        } catch (x) {
            // ignore
        }
        
        var req = "opp.skey=" + sort +
            "&opp.sfv=" + sfVal +
            "&opp.pfv=" + pfVal +
            "&opp.efv=" + efVal +
            "&opp.sdir=" + dir +
            "&opp.off=" + startIndex +
            "&opp.max=" + results; 

        console.log("request: " + req);
        
        // build custom request
        //---------------------
            
        } catch (x) {
            console.log(x);
        }
        return req;
    },
    

    getDefaultCookie: function(){
       try{
           
           var cookie = "";
           var myself = YAHOO.imex.journalmgr;
           for(var i = 0; i < myself.myCL.length; i++ ){
               
               var hidden = false;
               if(  myself.myCD[myself.myCL[i]].hidden === true ){
                  hidden = true;
               }
               cookie += myself.myCD[myself.myCL[i]].key + ":" + hidden +"|";
           }
           
       } catch (x) {
           console.log(x);
       }
         //console.log("default cookie:" + cookie);
        return cookie;
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
            console.log( x );
        }
    },

    // custom formatters
    //------------------

    myPubFormatter:  function( elLiner, oRecord, oColumn, oData ) { 
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
        elLiner.innerHTML = '<a href="journaledit?id=' + 
            oRecord.getData( "id" ) + 
            '">details</a>';
    },
    
    myJTitleFormatter: function(elLiner, oRecord, oColumn, oData) {
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-liner");
        elLiner.innerHTML = '<a href="journalview?id=' + 
            oRecord.getData( "id" ) + 
            '">'+ oRecord.getData( "title" )+'</a>' +
	    ' [ <a href="journalstat?id=' + oRecord.getData( "id" )+'">pipeline</a> ]';
    },
    
    myStatFormatter: function(elLiner, oRecord, oColumn, oData){
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");

        elLiner.innerHTML = oRecord.getData(oColumn.key);
        
        var skey = oColumn.key+"Sub";
        
        if( oRecord.getData(skey) !== undefined  ){
            elLiner.innerHTML += ' ('+ oRecord.getData(skey)+')';
        }
        //console.log("key:"+oColumn.key);
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


    myUrlCenterFormatter: function(elLiner, oRecord, oColumn, oData) {
        //console.log("center:");
        try{
           //console.log("    column key=" + YAHOO.lang.JSON.stringify(oColumn.key));

           var pcolkey = oColumn.getParent().key;
           var pcolchld = YAHOO.imex.journalmgr.myCD[pcolkey].children;
           var i, chld, url, dcol,dta;
           for( i in pcolchld ){
              chld = pcolchld[i]; 
              //console.log("        "+ oColumn.key+" ->  " + YAHOO.lang.JSON.stringify(chld.key));
              if( chld.key==oColumn.key){
                 //console.log("    column=" + YAHOO.lang.JSON.stringify(chld));
                 dcol= chld.dat;   // xxxxxxxxxxxxxxxxx
                 dta = oRecord.getData(dcol);
                 url = "pubmgr?jid=" + oRecord.getData("id") + chld.filter;
                 //console.log("    filter=" + YAHOO.lang.JSON.stringify(chld.filter) );
              
                 break;
              }
           } 
           
           //console.log("   url=" + url ); 
           //console.log("   data=" + YAHOO.lang.JSON.stringify(oData));
           //console.log("  *data=" + YAHOO.lang.JSON.stringify(dta));
           //console.log("   record=" + YAHOO.lang.JSON.stringify(oRecord));
           YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
           
           if( oData > 0){
              elLiner.innerHTML = "<a href='"+url+"'>" + oData + "</a>";
           } else {
              elLiner.innerHTML = oData;
           }            
        } catch(x){
            console.log(x);    
        }
        //console.log("center: DONE");
    },

    myDatUrlCenterFormatter: function(elLiner, oRecord, oColumn, oData) {
        //console.log("dcenter:");
        
        try{
           //console.log("   column key=" + YAHOO.lang.JSON.stringify(oColumn.key) );

           var col = YAHOO.imex.journalmgr.myCD[oColumn.key];
           //console.log("   col=" + YAHOO.lang.JSON.stringify(col));

           var flt = col.filter;  
           //console.log("   flt=" +  col.filter ); 
           
           //console.log("   data=" + YAHOO.lang.JSON.stringify(oRecord.getData(oColumn.dat)));
           //console.log("   record=" + YAHOO.lang.JSON.stringify(oRecord) );

           var url = "pubmgr?jid=" + oRecord.getData("id") + flt;

           YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
          
           if( oRecord.getData( oColumn.dat ) > 0 ){
             elLiner.innerHTML = "<a href='"+url+"'>" + oRecord.getData( oColumn.dat ) + "</a>"; 
           } else {
             elLiner.innerHTML = oRecord.getData( oColumn.dat ); 
           }
        } catch(x){
            console.log(x);
        }
        //console.log("dcenter: DONE");
    },

    myCboxFormatter: function(elLiner, oRecord, oColumn, oData) {
       
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        if( oData ){
            elLiner.innerHTML = "<input class='yui-dt-checkbox' type='checkbox' checked='checked' disabled='disabled'/>";
        } else {
            elLiner.innerHTML = "<input class='yui-dt-checkbox' type='checkbox' disabled='disabled'/>";       
        }
    },

    myImexDbFormatter: function(elLiner, oRecord, oColumn, oData) {
       
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        if( oData !== undefined ){
            //var label = oData;
            elLiner.innerHTML = oData.replace(" Database","");
        } else {
            elLiner.innerHTML = "<i>---</i>";
        }
    },
        
    myIcIdFormatter: function(elLiner, oRecord, oColumn, oData) {  
        YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
        elLiner.innerHTML = "IC-"+ oData+ "-PUB";
    },

   

    formatterInit: function(){

        // Add the custom formatters 
    
        YAHOO.widget.DataTable.Formatter.publication = this.myPubFormatter; 
        YAHOO.widget.DataTable.Formatter.elink = this.myElinkFormatter; 
        YAHOO.widget.DataTable.Formatter.crt = this.myDateFormatter; 
        YAHOO.widget.DataTable.Formatter.center = this.myCenterFormatter; 
        //YAHOO.widget.DataTable.Formatter["dat-center"] = this.myDatCenterFormatter; 
        YAHOO.widget.DataTable.Formatter.jtitle = this.myJTitleFormatter; 
        YAHOO.widget.DataTable.Formatter.stat = this.myStatFormatter; 
        YAHOO.widget.DataTable.Formatter.imexDB = this.myImexDbFormatter; 
        YAHOO.widget.DataTable.Formatter.cbox = this.myCboxFormatter; 
        YAHOO.widget.DataTable.Formatter.icid = this.myIcIdFormatter; 

        YAHOO.widget.DataTable.Formatter["dat-url-center"] = this.myDatUrlCenterFormatter; 
        YAHOO.widget.DataTable.Formatter["url-center"] = this.myUrlCenterFormatter; 

    },
    
    init: function( init ){

        var myself = YAHOO.imex.journalmgr;
        myself.loginId = init.loginid;
        
        if( typeof myself.myDataTable != "undefined" ){
            
            myself.myDataTable.my.configmenu.destroy();
            myself.myDataTable.destroy();
            myself.myColumnDefs = [];
            
        } else {   
            this.userTableLayoutInit( init );
        }

        try{
            var cookie = YAHOO.util.Cookie.get("jnlmgr");
            if( cookie == null ){
                  
                cookie = myself.getDefaultCookie();
                console.log(" init: COOKIE(d):" + cookie);
                YAHOO.util.Cookie.set( "jnlmgr", cookie );
            }

            if( cookie !== null ){
                console.log("init: COOKIE(c):" + cookie);
                this.buildCDefs( cookie );
            }
        } catch (x) {
            
            console.log("INIT: ex="+ x);
        }
        this.initView( init );
        this.historyInit( init );

    },
    
    //----------------------------------------------------------------
    //if a user is logged in this sets the cookie to their preferences
    //----------------------------------------------------------------

    userTableLayoutInit: function( init ){
        var myself = YAHOO.imex.journalmgr;
        if( myself.loginId  !== undefined && myself.loginId !== "" 
            && myself.loginId > -1 && 1==0 ){  // prefs turned off

            var Success = function( response ){                           
                
                var cookie = YAHOO.util.Cookie.get("jnlmgr");
           
                if( cookie == null ){
                   cookie = myself.getDefaultCookie();
                   YAHOO.util.Cookie.set( "jnlmgr", cookie );
                }
                
                var responseText = YAHOO.lang.JSON.parse(response.responseText);
                var preferences = YAHOO.lang.JSON.parse(responseText.preferences);
                if( preferences.tableLayout == "null" ){
                   myself.updateUserTablePref(cookie);
                }else{
                   cookie = preferences.tableLayout;
                   YAHOO.util.Cookie.set( "jnlmgr", cookie );
                   myself.buildCDefs( cookie );
                }
                
                myself.init( {loginid: myself.loginId } );
            };

            var Fail = function ( o ) {
                console.log( "AJAX Error update failed: id=" + o.argument.id ); 
            };
            var callback = { cache:false, timeout: 5000, 
                             success: Success,
                             failure: Fail
                             }; 
            
	    console.log("***userpref: " + journalmgr.loginId + ":"+ myself.loginId); 

            try{
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'userprefmgr?id=' + myself.loginId +'&op.view=true',
                                   callback );        
            } catch (x) {
                console.log("AJAX Error:"+x);
            }
        }
    },
    
    updateUserTablePref: function( cookie ){
        var journalmgr = YAHOO.imex.journalmgr;
        var loginId = jounrnalmgr.loginId;
        
        if( typeof loginId  !== undefined && loginId !== "" ){

            console.log("***userpref: " + loginId );
            try{
                YAHOO.util.Connect
                    .asyncRequest( 'POST', 
                                   'userprefmgr?id=' + loginId + '&op.updateTable=true',
                                   null, "opp.tableLayout=" + cookie );        
            } catch (x) {
                console.log("AJAX Error:"+x);
            }
        }
    },

    buildCDefs: function( cookie ){

        var myself = YAHOO.imex.journalmgr;
        
        console.log("buildCDefs: "+cookie );
        var col = cookie.split("|");

        
        if( col.length > 0 ){
            myself.myColumnDefs=[];
            
            for( var i =0; i < col.length-1; i++ ){
                
                try{
                
                    var cs = col[i].split(":");
                    if( myself.myCD[cs[0]] !== undefined ){
                        
                        myself.myColumnDefs.push(myself.myCD[cs[0]]);
                        if(cs[1] === 'true'){
                            myself.myColumnDefs[myself.myColumnDefs.length-1].hidden = true;
                        } else {
                            myself.myColumnDefs[myself.myColumnDefs.length-1].hidden = false;
                        }
                    }
                    
                } catch (x) {
                    console.log( "I:"+ i + "-> " + cs[0] + " ex=" + x );
                }
            }        
        }
    },

    buildCookie: function(){

        var myself = YAHOO.imex.journalmgr;
        var cookie = "";
        var ac = myself.myDataTable.getColumnSet().keys;
        for( var i=0; i< ac.length; i++ ){
            
            var key = ac[i].getKey();
            var hid = ac[i].hidden;

            //console.log("key: " + key);

            if( myself.myCD[key] !== undefined ){

                cookie += key + ":" + hid + "|";

            } else { // nested column

                key = myself.myCP[key];
                if( myself.myCD[key] !== undefined ){
                    cookie += key + ":" + hid + "|";
                }

            }
        }
        //console.log( "cookie:" + cookie );
        return cookie;
    },

    historyInit: function( init ){

        var myself = YAHOO.imex.journalmgr;
        
        var defstate = {
            startIndex: 0,
            pageSize: 20,
            filter:{ partner:""},
            scol: "id",
            sdir: "asc" };
                 
        if(  init !== undefined && init.watch !== undefined ){
            defstate.watch = init.watch;
        }

        var dst = YAHOO.lang.JSON.stringify( defstate );
        
        var bState = YAHOO.util.History.getBookmarkedState( "jnlmgr" );
        var iState = bState || dst;
        
        YAHOO.util.History.register( "jnlmgr", iState, 
                                     myself.handleHistoryNavigation ); 
        
        YAHOO.util.History.onReady( myself.historyReadyHandler );    
        
        try{
            YAHOO.util.History.initialize( "yui-history-field", 
                                           "yui-history-iframe" );
        } catch (x) {
            console.log(x);
        }                
    },

    handleFilter: function( ev, o ){

        try{
            var filter = o.filter;
            var newVal = ev.newValue.value;
            
            var myself = YAHOO.imex.journalmgr;
            
            var newState = myself.myDataSource.my.myState;
            
            // LS: watch flag ?
            
            newState.startIndex = 0;
            newState.filter[filter] = newVal;
            
            YAHOO.util.History
                .navigate( "jnlmgr", 
                           myself.generateStateString( newState ) );
        } catch (x) {
            console.log(x);
        }
    },

    handlePagination: function( state, datatable ){
        try{
    
            var myself = YAHOO.imex.journalmgr;
            var newState = myself.myDataSource.my.myState;
            
            newState.startIndex = state.recordOffset;
            newState.pageSize = state.rowsPerPage;
            
            YAHOO.util.History
                .navigate( "jnlmgr", 
                           myself.generateStateString( newState ) );
        } catch (x) {
         console.log(x);   
        }
    },
    
    handleSorting: function( column ){
        
         try{
             
             var myself = YAHOO.imex.journalmgr;
             var newState = myself.myDataSource.my.myState;
             
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
                 .navigate( "jnlmgr", 
                            myself.generateStateString( newState ) );
         } catch (x) {
             console.log(x);
         }
        
    },
    
    handleHistoryNavigation: function( state ){
      
        try{
            
            var myself = YAHOO.imex.journalmgr;

            //alert( "HHN:" + state );
        
            var parsed = myself.parseStateString( state );      
            var request = myself.buildRequest( parsed );
        
            myself.myDataSource.my.myState = parsed;
        
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
            
            var partnerLabel = "---ANY---";
            //PMGR.partnerSel[0].text = partnerLabel;
            
            if( parsed.filter.partner !== ""){
                partnerLabel = parsed.filter.partner;
            }
            
            if( myself.partnerBtn.set!== undefined ){
                myself.partnerBtn.set( "label", 
                                       ("<em class=\"yui-button-label\">" + 
                                        partnerLabel + "</em>"));
            }else{
                myself.partnerSel[0].text = partnerLabel;
            }
            
            // reload data
            //------------
            
            var mdt = myself.myDataTable;

	    request = request.replace(/=undefined/g,"=");
            console.log("request: " + request);

            myself.myDataSource
                .sendRequest( request, {
                                  success: mdt.onDataReturnSetRows,
                                  failure: mdt.onDataReturnSetRows,
                                  scope: mdt,
                                  argument: {}
                              });        
        } catch (x) {
            console.log(x);
        }
    },

    generateStateString: function( state ){            
        return YAHOO.lang.JSON.stringify( state );
    },
    
    parseStateString: function( statStr ){
        return YAHOO.lang.JSON.parse(statStr);
    },

    generateLinkState: function( status, partner ){

        //LS: watch ?
        var filter = { partner:partner  };
        var state = { startIndex:0, pageSize:20,filter:filter, scol:'id', sdir:'asc' };
        return YAHOO.lang.JSON.stringify( state );

    },
        
    buildRequest: function ( state ){
        
        console.log("buildRequest->state" +YAHOO.lang.JSON.stringify(state));
        
        var req = "opp.off=" + state.startIndex + 
            "&opp.wfl=" + state.watch + 
            "&opp.max=" + state.pageSize + 
            "&opp.pfv=" + state.filter.partner +
            "&opp.ffv=" + state.filter.cflag +
            "&opp.skey=" + state.scol +
            "&opp.sdir=" + state.sdir;
        
        return encodeURI( req );
        
    },
    
    historyReadyHandler: function(){

        try{
            var cState = YAHOO.util.History.getCurrentState( "jnlmgr" );
            YAHOO.imex.journalmgr.handleHistoryNavigation( cState );
        } catch (x) {
            console.log(x);
        }
    },


    initView: function( init, cord, chid ) { 

        var myself = YAHOO.imex.journalmgr;
        
        this.formatterInit();
        
        if( init !== undefined ){
            //myself.admus = init.admus;
            //myself.owner = init.owner;
            //myself.cflag = init.cflag;
            myself.watch = init.watch;
        }
        
        console.log( "initView->init= " + YAHOO.lang.JSON.stringify( init) );
        
        var partnerSuccess = function( o ){
            var messages = YAHOO.lang.JSON.parse( o.responseText );
            
            var items = [{ text: "---ALL---", value: "" },{ text: "<i>IMEX</i>", value: "IMEX" }];
            
            // partner filter setup
            //---------------------
                  
            if( messages.acom !==  undefined && messages.acom.length > 0 ){
                
                for( var i = 0; i < messages.acom.length; i++){
                    var text = messages.acom[i].name;
                    var value = messages.acom[i].value;
                    if( value === undefined ){
                        value = text;
                    }
                    items.push( {value: value, text: text} );        
                }
            }

            YAHOO.imex.journalmgr.selBtnInit( 
                { pmgr: YAHOO.imex.journalmgr,
                  filter: "partner",
                  items: items,
                  selmnu: YAHOO.imex.journalmgr.partnerSel,
                  selbtn: "partnerBtn",
                  selcnt: "partner-button-container",
                  selnme: "partner-button" ,
                  seltext: YAHOO.imex.journalmgr.partnerSel[0].text});
                  
        };
        
        var partnerCallback = { cache:false, timeout: 5000, 
                                success: partnerSuccess,
                                failure: partnerSuccess,
                                argument:{}}; // id:obj.id, btn:imexButton } };                  
        
        if( typeof myself.myDataTable == "undefined" ){
            try{
                   
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   "acom?op.jagac=ac", 
                                   partnerCallback );        
            } catch (x) {
                console.log("AJAX Error:"+x);
            }
        }

        // create datasource
        //------------------
      
        myself.myDataSource = new YAHOO.util.DataSource( "journalmgr?op.jpg=jpg&" ); 
        myself.myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        
        myself.myDataSource.responseSchema = {
            resultsList: "records.records",
            fields: ["id", "nlmid", "title", "imex","imexDB",
                      "pqueue", "pqueueNew","pqueueDiscarded",
                      "queue",  "queueNew","queueDiscarded",
                      "curation","curationReserved","curationInprogress","curationIncomplete","curationProcessed","curationDiscarded",
                      "release","releaseReleased", "releaseRetracted"],
            metaFields: {                
                totalRecords: "records.totalRecords",
                paginationRecordOffset : "records.startIndex",
                paginationRowsPerPage : "records.pageSize",
                sortKey: "records.sort",
                sortDir: "records.dir",
                fPar: "records.filter.partner"
            }
        };
        
        myself.myDataSource.my = { myState: null };
        
        // create paginator
        //-----------------
        
        myself.myPaginator = new YAHOO.widget.Paginator(
            { containers : ["dt-pag-nav"],
              //template : "{PreviousPageLink} {CurrentPageReport} {NextPageLink} {RowsPerPageDropdown}",
              //pageReportTemplate : "Showing items {startIndex} - {endIndex} of {totalRecords}",
              template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE,
              rowsPerPage: 20,
              rowsPerPageOptions : [5,10,20,50],
              pageLinks: 5
            });

        console.log( "initView: paginator DONE" );

        // datatable configuration
        //------------------------
        
        var initReq = "opp.off=0&opp.max=20";

        var myConfig = {
            paginator : this.myPaginator,
            initialLoad: false,
            dynamicData : true,
            draggableColumns: true
        };
        
        // Instantiate DataTable
        //----------------------
    
        myself.myDataTable = new YAHOO.widget.DataTable(
            "journaltab", myself.myColumnDefs, 
            myself.myDataSource, myConfig
        );

        
        myself.myPaginator
            .unsubscribe( "changeRequest", 
                          myself.myDataTable.onPaginatorChangeRequest );
        myself.myPaginator
            .subscribe( "changeRequest", 
                        myself.handlePagination, myself.myDataTable, true );

        myself.myDataTable.sortColumn = myself.handleSorting;

        myself.myDataTable.my = { 
            watchFlg: myself.watch,
            partnerFlt: myself.partnerBtn,
            cflagFlt: myself.cflag,
            requestBuilder: myself.requestBuilder
        };

        myself.myDataTable.my.filter = {
            owner: myself.owner, 
            editor: myself.admus,
            cflag: myself.cflag  
        };

        myself.partnerBtn.my.table = myself.myDataTable;
        
        // Show loading message while page is being rendered
        
        myself.myDataTable
            .showTableMessage( myself.myDataTable.get("MSG_LOADING"), 
                               YAHOO.widget.DataTable.CLASS_LOADING);
        
        
        myself.myDataTable.doBeforeLoadData = 
            function( oRequest, oResponse, oPayload ){
                
                try{
                    var meta = oResponse.meta;
                    oPayload.totalRecords 
                        = meta.totalRecords || oPayload.totalRecords;
                    
                    var oPayloadOld = oPayload;
                    
                    //alert( "dbld: response=" + 
                    //      YAHOO.lang.JSON.stringify(oResponse));
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
       
        myself.myDataTable.handleLayoutChange = function( ev, o ){
            //alert("reorder");                
            try{
                myself.myDataTable.my.configmenu.destroy();
                myself.contextMenuInit(  myself );
                
                var nCookie = YAHOO.imex.journalmgr.buildCookie();
                YAHOO.imex.journalmgr.updateUserTablePref( nCookie );
                YAHOO.util.Cookie.set("jnlmgr", nCookie );                                      
            } catch (x) { }
        };
        
        myself.contextMenuInit( myself );

        myself.myDataTable.on( "columnReorderEvent",
                               myself.myDataTable.handleLayoutChange );     
                             

        myself.myDataTable.on( "columnHideEvent",
                               myself.myDataTable.handleLayoutChange );     
                             

        myself.myDataTable.on( "columnShowEvent",
                             myself.myDataTable.handleLayoutChange );     
                             
        //tossing in some css to add a black separator between the rows
        var sheet = document.createElement('style');
        sheet.innerHTML = ".yui-dt-data > tr > td {border-bottom: 1px solid black !important;}";
        document.body.appendChild(sheet); 
        
        console.log( "initView: DataTable DONE" );


        return { 
            ds: myself.myDataSource, 
            dt: myself.myDataTable 
        };
        


        //YAHOO.imex.pubmgrOld();           
    },
    
    onSelectedMenuItemChange: function ( event ){
        var oMenuItem = event.newValue;
        var text = oMenuItem.cfg.getProperty("text");
        
        for( var i = 0; i < this.my.items.length; i++ ){
            if (this.my.items[i].text === text ) {
                this.my.value = this.my.items[i].value;
            }
        }
        
        this.set("label", ("<em class=\"yui-button-label\">" + 
                           oMenuItem.cfg.getProperty("text") + "</em>"));       
    },

    selBtnInit: function( o ) {
        try{
            
            o.selmnu = o.items;
 
            //alert(YAHOO.imex.pubmgr[o.selbtn].my.foo);
            
            o.pmgr[o.selbtn] = new YAHOO.widget.Button(
                { id: o.selnme,  
                  name: o.selnme, 
                  label: "<em class=\"yui-button-label\">" + o.seltext +"</em>", 
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
                         {filter: o.filter},
                         o.pmgr.myDataTable );
            }
            
        } catch (x) {
            console.log(x);
        }
    },

    contextMenuInit: function( o ){
        
        console.log("contextMenuInit");
        try{
            
            o.myDataTable.my.colmenu = new YAHOO.widget.Menu( "colmenu" );
            
            var defaultTableLayout = function( o ){
                var myself = YAHOO.imex.journalmgr;
                if( typeof myself.loginId  != "undefined" && myself.loginId != "" ){
                    
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
                        //YAHOO.util.Connect
                        //    .asyncRequest( 'GET', 
                        //               'userprefmgr?id=' + pubmgr.loginId +'&op.defaultTableLayout=true',
                        //               callback );        
                    } catch (x) {
                        console.log( "AJAX Error:" + x );
                    }
                }
                var cookie = myself.getDefaultCookie();
                myself.buildCDefs( cookie );
                
                YAHOO.util.Cookie.set( "jnlmgr", cookie );
                var myDataTable = myself.myDataTable;
                
                myself.init( {loginid:myself.loginId } );
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
            
            for( var i = 0;  i < o.myColumnDefs.length; i++ ) {
                if( o.myColumnDefs[i].menuLabel !== undefined ) {
                    var trg = o.myDataTable.getColumn( o.myColumnDefs[i].key ); 
                    
                    var item= {text: o.myColumnDefs[i].menuLabel,
                               checked: !trg.hidden, disabled:  !o.myColumnDefs[i].hideable,
                               onclick: { fn: o.hiddenColToggle,
                                          obj: { tbl: o.myDataTable ,col: trg } } };
                    clist.push( item );
                    if( trg !== null ) {
                        trigger.push( trg.getThEl() );
                    }
                }
            }
            
            o.myDataTable.my.colmenu.addItems( clist );
            
            o.myDataTable.my.configmenu = new YAHOO.widget.ContextMenu(
                "configmenu", { trigger: trigger } );
            
            o.myDataTable.my.configmenu.addItems( oConfMenu );
            o.myDataTable.my.configmenu.render("journaltab");
        } catch (x) {
            console.log(x);
        }        
    },

    //-----------------------------------
    // Hides deselected column attributes 
    //-----------------------------------
    
    hiddenColToggle: function( tp, ev, o ){

        //alert( "col-toggle:" + o);  
        try{
            if( o.col.hidden){
                o.tbl.showColumn( o.col );
                console.log( "toggle: " + o.col );
                if( o.col.xor !== undefined ){
                    o.tbl.hideColumn( o.col.xor );
                }
            } else {
                o.tbl.hideColumn( o.col );                
            }
            o.tbl.render();
            o.tbl.my.configmenu.destroy();
            YAHOO.imex.journalmgr.contextMenuInit( YAHOO.imex.journalmgr );
        } catch (x) {
            console.log("toggle:" + x);
        }

        var nCookie = YAHOO.imex.journalmgr.buildCookie();
        YAHOO.util.Cookie.set("jnlmgr", nCookie );                                      
    }
};
