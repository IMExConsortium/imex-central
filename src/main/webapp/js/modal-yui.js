/* record rendering */

YAHOO.namespace("mbi.modal");

YAHOO.mbi.modal = {

    feedback: function( arg ) {
        var ns = arg.ns;
        var ac = arg.ac;
        var about = arg.about;

        var url = 'feedback?about=' + about +
            "&ret=modal" + "&ns=" + ns + "&ac=" + ac;
        YAHOO.mbi.modal.show({ mtitle:'Feedback', url: url });
    },

    comments: function( arg ) {
        var ns = arg.ns;
        var ac = arg.ac;

        YAHOO.mbi.modal.show({ mtitle:'Comments' });            
    },

    spcstat: function( arg ) {
        var ttl = 'Details: '+ arg;
        YAHOO.mbi.modal.show({ mtitle:ttl });            
    },

    graph: function( arg ) {
        var ns = arg.ns;
        var ac = arg.ac;

        YAHOO.mbi.modal.show({ mtitle:'DIP Graph' });            
    },

    help: function( title, id ) {
        var url = 'page?ret=body&id=' + id;
        YAHOO.mbi.modal.show({ mtitle: 'Help', title: title, url: url } );
    },
    
    show: function( arg ) {
      
        var title = arg.title;
        var mtitle = arg.mtitle === undefined ? title : arg.mtitle; 
        var id = arg.id;

        if ( YAHOO.mbi.modal.my  == null  
             || YAHOO.mbi.modal.my.panel == null ) {
                 
                 YAHOO.mbi.modal.my = {};
        
                 var hp = document.createElement('div');
                 document.body.appendChild(hp);
                 hp.id="modal-panel";
        
                 var hphd = document.createElement('div');
                 hp.appendChild(hphd);
                 YAHOO.util.Dom.addClass(hphd,'hd');
                 
                 var hpbd = document.createElement('div');
                 hp.appendChild(hpbd);
                 YAHOO.util.Dom.addClass(hpbd,'bd');
                 
                 var hpft = document.createElement('div');
                 hp.appendChild(hpft);
                 YAHOO.util.Dom.addClass(hpft,'ft');
                 
        
                 YAHOO.mbi.modal.my.panel =       
                     new YAHOO.widget.Panel("modal-panel",
                                            { width: "650px",
                                              height: "450px",
                                              fixedcenter: true,
                                              close: true,
                                              draggable: true,
                                              //zindex: 4,
                                              modal: true,
                                              constraintoviewport: false,
                                              visible: true 
                                            });

                 YAHOO.mbi.modal.my.panel.render();
                 YAHOO.mbi.modal.my.resize =
                     new YAHOO.util.Resize( "modal-panel",
                                            { handles: ["br"],
                                              autoRatio: false,
                                              minWidth: 350,
                                              minHeight: 100,
                                              status: false
                                            });
                 
                 YAHOO.mbi.modal.my.resize.on( 
                     'resize',
                     function( args ) {
                         var panelHeight = args.height; 
                         this.cfg.setProperty('height',panelHeight + "px"); 
                     }, YAHOO.mbi.modal.my.panel, true ); 
                 
                 YAHOO.mbi.modal.my.resize.on( 
                     'startResize',
                     function( args ) {
                         
                         if( this.cfg.getProperty('constraintoviewport') ) {
                             var D = YAHOO.util.Dom; 
                             var clientRegion = D.getClientRegion();
                             var elRegion = D.getRegion(this.element);
                             YAHOO.mbi.modal.my.resize.set('maxWidth', 
                                                          clientRegion.right - elRegion.left 
                                                          - YAHOO.widget.Overlay.VIEWPORT_OFFSET );
                             YAHOO.mbi.modal.my.resize.set('maxHeight', 
                                                          clientRegion.bottom - elRegion.top 
                                                          - YAHOO.widget.Overlay.VIEWPORT_OFFSET );
                         } else {
                             YAHOO.mbi.modal.my.resize.set('maxWidth',null);
                             YAHOO.mbi.modal.my.resize.set('maxHeight',null);
                         }
                     }, YAHOO.mbi.modal.my.panel, true );   
             }
        
        if ( arg.url != undefined ) {
                    
            var helpCallback = { cache:false, timeout: 5000, 
                                 success: YAHOO.mbi.modal.load,
                                 argument: {title: title} };
            YAHOO.util.Connect.asyncRequest( 'GET', arg.url, helpCallback ); 
        }

        YAHOO.mbi.modal.my.panel.setHeader( mtitle );       
        YAHOO.mbi.modal.my.panel.setBody("");
        YAHOO.mbi.modal.my.panel.show();        
    },
    
    load: function( o ) {
        var body = o.responseText;
        
        if ( o.argument.title !== undefined && o.argument.title.length > 0 ){
            body = '<h2>' + o.argument.title + 
                '</h2><hr/>' + body;               
        } 
        YAHOO.mbi.modal.my.panel.setBody( body );               
    }
};
