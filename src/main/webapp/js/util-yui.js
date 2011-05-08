YAHOO.namespace("imex");

YAHOO.imex.util = {
  copyField : function( inField, outField ) {
        
        var h = YAHOO.util.Dom.get( inField );
        if(h) {
            var opp = YAHOO.util.Dom.get( outField );
            if( opp ) {
                opp.value = h.value; 
            }
        }
  }
};
