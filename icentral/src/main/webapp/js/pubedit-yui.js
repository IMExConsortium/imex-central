YAHOO.namespace("imex");

YAHOO.imex.pubedit = function( e, obj ) {
        
    var onSelectedMenuItemChange = function ( event ) {
        var oMenuItem = event.newValue;
        var text = oMenuItem.cfg.getProperty( "text" );
        
        for( var i = 0; i < this.my.items.length; i++ ) {
            if ( this.my.items[i].text === text ) {
                this.my.value = this.my.items[i].value;
            }
        }
        
        this.set("label", ("<em class=\"yui-button-label\">" + 
                           oMenuItem.cfg.getProperty("text") + "</em>"));       
    };

    var onImexClick = function ( event ) {
        
        var oMenuItem = event.newValue;
        var imexCallback = { cache:false, timeout: 5000, 
                             success: imexUpdate,
                             failure: imexUpdateFail,
                             argument:{ id:obj.id, btn:imexButton } };                  
        
        var r = confirm("Confirm IMEx Accession request ?");
        if ( r == true ) {
            try{
                YAHOO.util.Connect
                    .asyncRequest( 'GET', 
                                   'pubedit?op.epix=update&id=' + obj.id, 
                                   imexCallback );        
            } catch (x) {
                alert("AJAX Error:"+x);
            }
        } else {
            // do nothing
        }    
    };

    var imexUpdate = function ( o ) {
        var messages = YAHOO.lang.JSON.parse( o.responseText );
        var pid = messages.id;
        var imexACC = messages.pub.imexId;
        o.argument.btn.set("label",imexACC);
        o.argument.btn.set("disabled",true);        
    };

    var imexUpdateFail = function ( o ) {
        alert( "AJAX Error update failed: id=" + o.argument.id ); 
    };
           
    // new status 
    //-----------

    var stateSel = [ 
        { text: "NEW", value: "NEW" }, 
        { text: "Reserved", value: "reserved" },
        { text: "Processed", value: "processed" },
        { text: "Released", value: "released" } 
    ]; 
        
    var stateButton = new YAHOO.widget.Button(
        { id: "state-button",  
          name: "state-button", 
          label: "<em class=\"yui-button-label\">"+stateSel[0].text+"</em>", 
          type: "menu",   
          menu: stateSel,  
          container: "state-button-container" }); 
    
    stateButton.my = { items: stateSel, value: "" };
    stateButton.on("selectedMenuItemChange", onSelectedMenuItemChange);
    
    
    // current status
    //---------------

    //var curStateButton = new YAHOO.widget.Button(
    //    { id: "state-label",  
    //      name: "state-label", 
    //      label: "<em class=\"yui-button-label\">"+stateSel[0].text+"</em>", 
    //      type: "push",   
    //      disabled: true,
    //      container: "state-label-container" }); 


    // imex id
    //---------
        
    var imexButton = new YAHOO.widget.Button(
        { id: "imex-button",  
          name: "imex-button", 
          label: "<em class=\"yui-button-label\">"+"ASSIGN"+"</em>", 
          type: "push", 
          disabled: false,  
          menu: stateSel,  
          container: "imex-button-container" }); 
    
    if( obj.imexACC  !== undefined &&  obj.imexACC.length > 0 
        && obj.imexACC !== "N/A" ) {
            imexButton.set( "label", obj.imexACC );
            imexButton.set( "disabled", true );
        }
     
    imexButton.my = { items: stateSel, value: "" };
    imexButton.on("click", onImexClick );
};

