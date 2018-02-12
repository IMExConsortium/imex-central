/* icentral: modal windows */

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
        var title = 'Details: '+ arg.title;
        YAHOO.mbi.modal.show({ mtitle:title, body:arg.body });            
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

    iview: function( image ) {
        var url = 'image?op.view=view&opp.name=' + image ;
        YAHOO.mbi.modal.show({ mtitle: 'Image Preview', mid: 'modal-panel', url: url } );
    },


    exception: function( code, message ) {
        
        var title = code + ": "+message;
        var url = "page?ret=body&id=exception"+ code;
        YAHOO.mbi.modal.show({ mtitle: 'Exception', title: title, url: url } );
    },


    adataView:function( arg ){
        var rid = arg.rid;
        var aid = arg.aid;

        var header = '<tr>'
            + '<td class="att-field-head" width="10%" nowrap>Name:</td>'
            + '<td>%SUB% [<a href="%DWN%">download</a>]</td></tr>';
        
        var attSuccess = function( o ){
            
            var messages = {attach:[]};
            var header = "";
            
            if( o.responseText !== undefined ){
                header = o.argument.header;
                messages = YAHOO.lang.JSON.parse( o.responseText);
            } else{                           
                if( o.prev !== undefined ){
                    messages.attach[0] = prev;
                    header = o.header;
                }
            }
           
            if( messages.attach[0] !== undefined ){                
                var subject = messages.attach[0].subject;
                var body = messages.attach[0].body;
                var bodyTp = messages.attach[0]["bodyType"];
                
                var dwnUrl = "attachmgr?op.aidg=aidg&id=" + rid
                    + "&opp.aid=" + aid + "&opp.rt=data";

                header = header.replace( "%SUB%", subject );
                header = header.replace( "%DWN%", dwnUrl );
                
                YAHOO.mbi.modal.showAData( { header:header, 
                                             body:bodyTp, body:body } );
            }
        };

        var attFail = function( o ){
            consol.log(o);
        };
        
        var attCallback = { cache:false, timeout: 5000, 
                            success: attSuccess,
                            failure: attFail,
                            argument:{ header:header }
                          };   

        if( aid > 0 && rid > 0 ){
            var url = 'attachmgr?op.aidg=aidg'
                + '&opp.aid=' + aid 
                + '&id=' + rid;        
            
            try{
                YAHOO.util.Connect.asyncRequest( 'GET', url, attCallback );        
            } catch (x) {
                alert("AJAX Error:"+x);
            }
        } else {
            if( prev !== undefined ){
                attSuccess({ header: header, prev: prev } );
            }
        }
        
    },


    attachment:function( arg ){
    
        var rid = arg.rid;
        var aid = arg.aid;

        var atp = arg.atp;
        var prev = arg.prev;
        
        if( atp !== null && atp == "adata" ){
            this.adataView( { "rid":rid, "aid":aid } );
            return;
        }

        var header = '<tr>'
            + '<td class="att-field-head" width="10%" nowrap>Subject:</td>'
            + '<td>%SUB%</td></tr>';

        if( rid > 0 && aid > 0){
            
            header += '<tr><td class="att-field-head" nowrap>Author:</td>'
                + '<td>%AUTH%</td></tr>'
                + '<tr><td class="att-field-head" nowrap>Date:</td>'
                + '<td>%DATE%</td></tr>';
        }
        
        var attSuccess = function( o ){
            
            var messages = {attach:[]};
            var header = "";
            
            if( o.responseText !== undefined ){
                header = o.argument.header;
                messages = YAHOO.lang.JSON.parse( o.responseText);
            } else{                           
                if( o.prev !== undefined ){
                    messages.attach[0] = prev;
                    header = o.header;
                }
            }
           
            if( messages.attach[0] !== undefined ){                
                var subject = messages.attach[0].subject;
                var body = messages.attach[0].body;
                var bodyTp = messages.attach[0]["bodyType"];
                var author = messages.attach[0].author;
                var date =  messages.attach[0].date;
                
                header = header.replace( "%SUB%", subject );
                header = header.replace( "%AUTH%", author );
                header = header.replace( "%DATE%", date );
                
                if( bodyTp === "WIKI" ){

                    var wikiUrl = "wikiparse";
                    
                    var postData = "txt=" + encodeURIComponent(body);
                    
                    var wikiSuccess = function( o ){
                        
                        var messages = YAHOO.lang.JSON.parse( o.responseText);        
                        var header = o.argument.header;
                        YAHOO.mbi.modal.showComment( { header:header,
                                                       bodyTp:"HTML", 
                                                       body: messages["html"] } );
                    };

                    var wikiFail = function( o ){
                        
                    };

                    var wikiCallback = { cache:false, timeout: 5000, 
                                         success: wikiSuccess,
                                         failure: wikiFail,
                                         argument:{header:header}
                                       };      
                    
                    try{
                        YAHOO.util.Connect.asyncRequest( 'POST', 
                                                         wikiUrl,
                                                         wikiCallback, 
                                                         postData  );        
                    } catch (x) {
                        alert("AJAX Error:"+x);
                    }
                    
                } else {
                    
                    YAHOO.mbi.modal.showComment( { header:header, 
                                                   bodyTp:bodyTp, body:body } );
                } 
            }
        };

        var attFail = function( o ){
            
        };
        
        var attCallback = { cache:false, timeout: 5000, 
                            success: attSuccess,
                            failure: attFail,
                            argument:{ header:header }
                          };   

        if( aid > 0 && rid > 0 ){
            var url = 'attachmgr?op.cidg=cidg'
                + '&opp.cid=' + aid 
                + '&id=' + rid;        
            
            try{
                YAHOO.util.Connect.asyncRequest( 'GET', url, attCallback );        
            } catch (x) {
                alert("AJAX Error:"+x);
            }
        } else {
            if( prev !== undefined ){
                attSuccess({ header: header, prev: prev } );
            }
        }
    },

    showAData: function( arg ){
        
        var bodyHTML = '<table width="99%">'
            + arg.header
            + '<tr><td colspan="2"><hr/></td></tr>'
            + '<tr><td class="att-body" colspan="2">';
                            
        if( arg.bodyTp !== "HTML" ){
            bodyHTML += '<pre>' + arg.body + '</pre>';
        } else {
            bodyHTML += arg.body;
        }
        
        bodyHTML += "</td></tr></table>";

        YAHOO.mbi.modal.show( { mtitle: 'Attachment:', 
                                title: "", 
                                body: bodyHTML } );         
    },

    showComment: function( arg ){
        
        var bodyHTML = '<table width="99%">'
            + arg.header
            + '<tr><td colspan="2"><hr/></td></tr>'
            + '<tr><td class="att-body" colspan="2">';
                            
        if( arg.bodyTp === "TEXT" ){
            bodyHTML += '<pre>' + arg.body + '</pre>';
        }
                
        if( arg.bodyTp === "HTML" ){
            bodyHTML += arg.body;
        }
        bodyHTML += "</td></tr></table>";

        YAHOO.mbi.modal.show( { mtitle: 'Comment', 
                                title: "", 
                                body: bodyHTML } );         
    },


    dialog: function( arg ) {
        
        var title = arg.title;
        var mtitle = arg.mtitle === undefined ? title : arg.mtitle; 
        var id = arg.id;
	
        var myself = YAHOO.mbi.modal;

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
                 
                 var bgr = document.createElement('div');
                 hpft.appendChild(bgr);
                 bgr.id="dqr-group";
                 YAHOO.util.Dom.addClass(bgr,'yui-buttongroup');
                 if( arg.table != null ){
                     YAHOO.mbi.modal.my.table = arg.table;
                 }
                 if( arg.parent != null ){
                     YAHOO.mbi.modal.my.parent = arg.parent;
                 }
                 if( arg.button != null ){
                     
                     YAHOO.mbi.modal.my.buttons = {};
                     
                     myself.onButtonClick = function(p_oEvent) {
                         
                         
                         var mybuttons = YAHOO.mbi.modal.my.buttons;
                         
                         var id = this.get("id");
                         console.log("button="+ id );
                         console.log("url="+ mybuttons[ id ].url );
                         
                         // call url, wait til done
                         
                         var urlcallback = {
                             success: function(o) {
                                 var myself = YAHOO.mbi.modal.my;
                                 
                                 console.log("dialog: myself.table=" + myself.table);
                                 console.log("dialog: myself.parent=" + myself.parent);
                                 
				 

                                 if( myself.table != null && myself.parent != null){
                                     // reload table
                                     
                                     try{
                                         console.log("reloading...");
                                         
					 myself.parent 
					     .tableReload( {}, myself.table );
                                         //YAHOO.imex.journalview
                                         //    .tableReload( {}, myself.table );


                                         console.log("reloading... DONE");
                                     } catch (x) {
                                         console.log(x);
                                     }
                                 }
                                 
                                 // close modal
                                 YAHOO.mbi.modal.my.panel.destroy();
                                 YAHOO.mbi.modal.my.panel = null;
                             },
                             failure: function(o) {
                                 // close modal
                                 YAHOO.mbi.modal.my.panel.destroy();
                                 YAHOO.mbi.modal.my.panel = null;
                             }
                         };
                         
                         var cObj = YAHOO.util.Connect
                             .asyncRequest( 'GET', mybuttons[ id ].url, 
                                            urlcallback );
                     };
                     
                     var i;
                     for( i=0; i<arg.button.length; i++ ){
                         var label = arg.button[i].label;
                         var id = arg.button[i].id;
                         var url = arg.button[i].url;
                         var cButton = new YAHOO.widget
                             .Button( {label:label,
                                       container:"dqr-group",
                                       id:id,
                                       onclick: { fn: myself.onButtonClick }
                                      });

                         YAHOO.mbi.modal.my.buttons[ id ] ={url:url};       
                     }
                 }    
                 
/*
                 var discardButton = new YAHOO.widget.Button({label:"Discard",  
                                                              id:"discard-button",  
                                                              container:"dqr-group" }); 

                 var toqueueButton = new YAHOO.widget.Button({label:"ToQueue",  
                                                              id:"toqueue-button",  
                                                              container:"dqr-group" }); 
                 var reserveButton = new YAHOO.widget.Button({label:"Reserve",  
                                                              id:"reserve-button",  
                                                              container:"dqr-group" }); 
  */            
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

        if ( arg.body !== undefined ) {
            var body =  arg.body;
            if( arg.title !== undefined && arg.title.length > 0 ){
                body = '<h2>' + arg.title + '</h2><hr/>' + arg.body;
            }
            YAHOO.mbi.modal.my.panel.setBody( body );
            
        } else {
            YAHOO.mbi.modal.my.panel.setBody("");
        }

        YAHOO.mbi.modal.my.panel.setHeader( mtitle );       
        YAHOO.mbi.modal.my.panel.show();
        document.body.scrollTop = document.documentElement.scrollTop = 0;        
        
        
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

        if ( arg.body !== undefined ) {
            var body =  arg.body;
            if( arg.title !== undefined && arg.title.length > 0 ){
                body = '<h2>' + arg.title + '</h2><hr/>' + arg.body;
            }
            YAHOO.mbi.modal.my.panel.setBody( body );
            
        } else {
            YAHOO.mbi.modal.my.panel.setBody("");
        }

        YAHOO.mbi.modal.my.panel.setHeader( mtitle );       
        YAHOO.mbi.modal.my.panel.show();
        document.body.scrollTop = document.documentElement.scrollTop = 0;        
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
