/* foldable side panel */

YAHOO.namespace("mbi.view");

YAHOO.mbi.view.panel = {

    index: function( url, ptd ){

        var ctab = { my:{} } ;        
        YAHOO.mbi.view.panel.build( ctab, ptd );
        var setSidebarContent = function(o) {
            ctab.my.sidediv.innerHTML = o.responseText;
        };
        var setSidebarError = function(o) {
            ctab.my.sidediv.innerHTML = "";
        };

        var sidebarCallback = {success:setSidebarContent,
                               failure:setSidebarError};

        YAHOO.util.Connect.asyncRequest('GET', url, sidebarCallback);
       
    },

    build : function ( ctab, ptd ) {
                      
        /* side panel: td  */

        /*YAHOO.mbi.view.panel.build( tab,
                                     document.getElementById('summary-panel');
         */
        ctab.my.panel = ptd; // document.createElement("td");
        
        YAHOO.util.Dom.setAttribute( ctab.my.panel,'valign','top');
        YAHOO.util.Dom.addClass( ctab.my.panel, 'rec-tab-filter');

        //side panel header/control
        
        var fhd =  document.createElement("div");
        YAHOO.util.Dom.addClass( fhd, 'rec-tab-filter-hd');  
        YAHOO.util.Dom.addClass( fhd, 'yui-layout-hd');
        ctab.my.panel.appendChild( fhd );
        
        var fhdc = document.createElement("div");
        ctab.my.collapse = YAHOO.util.Dom.generateId( fhdc );
        ctab.my.fop = true; 
        YAHOO.util.Dom.setAttribute( fhdc, 'title', 
                                     'Click to collapse this pane.' );
        YAHOO.util.Dom.addClass( fhdc, 'collapse' );  
        fhd.appendChild(fhdc);

        YAHOO.util.Event.addListener ( 
            ctab.my.collapse, "click", 
            function() {
                this.my.fop = ! this.my.fop;            
                if ( this.my.fop ){
                    YAHOO.util.Dom.replaceClass( this.my.panel, 
                                                 'rec-tab-filter-col',
                                                 'rec-tab-filter' );
                    YAHOO.util.Dom.setAttribute( 
                        this.my.collapse, 'title',
                        'Click to collapse this pane.');
                } else {
                    YAHOO.util.Dom.replaceClass( this.my.panel, 
                                                 'rec-tab-filter',
                                                 'rec-tab-filter-col');
                    YAHOO.util.Dom.setAttribute( 
                        this.my.collapse, 'title',
                        'Click to expand this pane.');
                }
                
            }, ctab, true ); 
        
        // panel content
        //--------------
        
        var lpanel = document.createElement( 'table' );
        ctab.my.lpanel = YAHOO.util.Dom.generateId( lpanel );
        YAHOO.util.Dom.setAttribute( lpanel, 'cellpadding', '0');
        YAHOO.util.Dom.addClass( lpanel, 'rec-tab-left-panel');
        ctab.my.panel.appendChild( lpanel );
        
        var lpr0 = document.createElement('tr');
        lpanel.appendChild( lpr0 );

        ctab.my.ptop = document.createElement('td');
        YAHOO.util.Dom.setAttribute( ctab.my.ptop, 'valign', 'top');
        YAHOO.util.Dom.setAttribute( ctab.my.ptop, 'align', 'left');

        lpr0.appendChild( ctab.my.ptop );
  
        var lpr1 = document.createElement('tr');
        lpanel.appendChild( lpr1 );  

        ctab.my.pbot = document.createElement('td');
        YAHOO.util.Dom.setAttribute( ctab.my.pbot, 'valign', 'bottom');
        lpr1.appendChild( ctab.my.pbot );  
        
        return self;
    }    
};

