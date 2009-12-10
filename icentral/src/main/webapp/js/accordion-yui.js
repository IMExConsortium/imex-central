// Simplified version of on:
//  http://www.lutsr.nl/yui/accordion/script.js
//
// REQUIRES: yahoo-dom-event.js, animation-min.js
 
YAHOO.namespace("mbi");

YAHOO.mbi.accordion = {
    
    properties : {
        multipleOpen : false
    },

    init : function( multipleOpen ) {

        if( multipleOpen ) {
            this.multipleOpen = multipleOpen;
        }
        
        var accordionObjects = YAHOO.util.Dom.getElementsByClassName("accordion");

        if(accordionObjects.length > 0) {
            
            for(var i=0; i<accordionObjects.length; i++) {
                if(accordionObjects[i].nodeName == "DL") {
                    var headers = accordionObjects[i].getElementsByTagName("dt");
                    var bodies = headers[i].parentNode.getElementsByTagName("dd");
                }
                this.attachEvents( headers, i );
            }
        }
    },
    
    attachEvents : function( headers, nr ) {
        
        for(var i=0; i<headers.length; i++) {
            var headerProperties = {
                objRef : headers[i],
                nr : i,
                jsObj : this
            };
            
            YAHOO.util.Event.addListener( headers[i], "click", 
                                          this.clickHeader, headerProperties );
        }
    },
    
    clickHeader : function( e, headerProperties ) {
	var parentObj = headerProperties.objRef.parentNode;
	var headers = parentObj.getElementsByTagName("dd"); 
	var header = headers[headerProperties.nr];
        
	if(YAHOO.util.Dom.hasClass(header,"open")) {
	    headerProperties.jsObj.collapse(header);
	} else {
	    if(headerProperties.jsObj.properties.multipleOpen) {
		headerProperties.jsObj.expand(header);
	    } else {
		for(var i=0; i<headers.length; i++) {
		    if(YAHOO.util.Dom.hasClass(headers[i],"open")) {
			headerProperties.jsObj.collapse(headers[i]);
		    }
		}
		headerProperties.jsObj.expand(header);
	    }
	}
    },
    
    collapse : function(header) {
	YAHOO.util.Dom.removeClass( YAHOO.util.Dom.getPreviousSibling( header ),
                                    "selected" );
	YAHOO.util.Dom.removeClass(header,"open");
        YAHOO.util.Dom.addClass(header,"close");
    },
    
    expand : function(header) {
	YAHOO.util.Dom.addClass( YAHOO.util.Dom.getPreviousSibling( header ),
                                 "selected");
	YAHOO.util.Dom.removeClass(header,"close");
	YAHOO.util.Dom.addClass(header,"open");
	
    }
};
