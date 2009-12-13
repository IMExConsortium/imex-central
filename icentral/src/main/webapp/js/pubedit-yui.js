YAHOO.namespace("imex");

YAHOO.imex.pubedit = function() {

    var onSelectedMenuItemChange = function (event) {
        var oMenuItem = event.newValue;
        var text = oMenuItem.cfg.getProperty("text");
        
        for( var i = 0; i < this.my.items.length; i++ ){
            if (this.my.items[i].text === text ) {
                this.my.value = this.my.items[i].value;
            }
        }
        
        this.set("label", ("<em class=\"yui-button-label\">" + 
                           oMenuItem.cfg.getProperty("text") + "</em>"));       
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
          disabled: true,  
          menu: stateSel,  
          container: "imex-button-container" }); 
    
    imexButton.my = { items: stateSel, value: "" };
    imexButton.on("selectedMenuItemChange", onSelectedMenuItemChange);
    
};

