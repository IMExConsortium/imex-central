YAHOO.namespace("mbi.news");

YAHOO.mbi.news.tabs = {

  tabInit: function( o ){ 

    var messages = YAHOO.lang.JSON.parse( o.responseText );
    var id = o.argument.id;  
    var feed = o.argument.feed;  
    var tabView = new YAHOO.widget.TabView();

    for(var i = 0 ; i < messages.years.length; i++ ) {
      var label = messages.years[i];
      var src = feed+'?ret=list&year=' + label; 

      var tab =  new YAHOO.widget.Tab({
        label: label,
        dataSrc: src ,
        active: true
        });
       YAHOO.util.Dom.addClass( tab.get('contentEl'), "news-tab");
       tabView.addTab( tab );
    }
      
    tabView.selectTab( 0 ); 
    tabView.appendTo( id ); 
  },

  build : function( feed ){
      var tabCallback = { cache:false, timeout: 5000, 
                          success:YAHOO.mbi.news.tabs.tabInit,
                          argument:{ id:"newscontainer", feed:feed } };                  
      YAHOO.util.Connect.asyncRequest( 'GET', feed+'?ret=ylist', tabCallback );
  }
};

