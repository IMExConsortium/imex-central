YAHOO.namespace("imex");

YAHOO.imex.userprefmgr = {
    
    preferences : '',

    /*************************************************************************
     * Takes an object and recursively travels down all the nodes.
     * function is applied when it reaches an object with a option-def property
     *************************************************************************/
    traverse:  function( object, func ){

        var html = '';
           
        for (var i in object) {                   
            if(i =="option-def"){
                html += "\n<div class='cfg-block-list'>\n";
                for( var j = 0; j < object.options.length; j++ ){
                    
                    var strong = false;
                    
                    if( object["option-def"][object.options[j]]["legend"] !== undefined){
                        html += "<div class='cfg-block-legend'>";    
                        html += "\n<fieldset>\n";
                        html += "<legend>" + object["option-def"][object.options[j]]["legend"] +"</legend>";
                        strong = true;
                    }else{
                        html += "<div class='cfg-block-no-legend'>";   
                    }
                    
                    html += func
                        .apply( this, [ j, 
                                        object["option-def"][object.options[j] ], 
                                        object["options"], 
                                        object["option-def"][object.options[j]]["opp"],
                                        strong
                                      ] ); 
                    
                    //going to step down in the object tree!!
                    
                    html += this
                        .traverse( object[ "option-def"][ object.options[j] ], 
                                   func );
                    
                    if( object["option-def"][object.options[j]]["legend"] !== undefined){
                        html += "\n</fieldset>\n";
                    }
                    html += "</div>\n";
                    
                }
                
                html+="</div>\n";                    
            }
        }
        return html;
    },
    /*************************************************************************
     * Initializes the userprefmgr page with a logged in users preferences  
     * also creates the structure and displays the form element on the page
     *************************************************************************/ 
    init: function( init ){
        var userprefmgr = YAHOO.imex.userprefmgr;

        userprefmgr.viewUrl = init.viewUrl;
        userprefmgr.updateUrl = init.updateUrl;
        userprefmgr.defaultUrl = init.defaultUrl;

        var Success = function( response ){

            var process = function( key, value, options, opp, strong ){
                
                var upm = YAHOO.imex.userprefmgr;
                
                if( typeof value.value != "undefined"){
                    
                    var keyClass = "cfg-key"; 
                    
                    if( strong ){
                        keyClass = "cfg-key-strong";
                    }
                    return "<div class='cfg-key-val'>" 
                        + "<div class='" + keyClass + "'>" + value.label + "</div>" 
                        + "<div class='cfg-val' id='opp." + opp + "'>" 
                        + upm.htmlBoolRadio( options[key], opp, value.value )
                        + "</div>"
                        + "</div>";
                }
                return "";
            };
            
            console.log("sucess");
            
            userprefmgr.preferences 
                = YAHOO.lang.JSON.parse( response.responseText );
            userprefmgr.preferences 
                = YAHOO.lang.JSON.parse( userprefmgr.preferences.preferences );
            console.log( userprefmgr.preferences );     
            
            var form = document.getElementById(init["formid"]);
            var html = '';

            html += userprefmgr.traverse( userprefmgr.preferences, process );
            console.log ( html );
            form.innerHTML = html + form.innerHTML ;                             
        };

        var Fail = function ( o ) {
            console.log( "AJAX Error update failed: id=" + o.argument.id ); 
        };
        var callback = { cache:false, timeout: 5000, 
                         success: Success,
                         failure: Fail
                         }; 
        
        try{
            YAHOO.util.Connect
            .asyncRequest( 'GET', 
                           'userprefmgr?id=' + init.loginid +'&op.view=true', 
                           //'userprefmgr?id=30' +'&op.view=true', 
                           callback );        
        } catch (x) {
            console.log("AJAX Error:"+x);
        }
    },
    
    updateForm: function(){
        var upm = YAHOO.imex.userprefmgr;
        console.log("In function update");
        
        var html='';
        var process = function (key, value, options, opp, strong  ){   
            if( typeof value.value != "undefined" ){                
                var valDiv = YAHOO.util.Dom.get( "opp." + opp );
                if( valDiv !== undefined ){
                    valDiv.innerHTML 
                        = upm.htmlBoolRadio( key, opp, value.value );
                }
            }
        };
        try{
            upm.traverse( upm.preferences, process );            
        } catch (x) {
            console.log("updateForm: Traverse Error:" + x );
        }
    },
    
    htmlBoolRadio: function( optName, optOpp, optValue ){
        
        if( optValue ==='true'){
            var checkboxT = '<input type="radio" id="' 
                + optName +'True" name="opp.' + optOpp + '"' 
                +' value="true" checked="checked" ><strong>True</strong></input>';
            var checkboxF = '<input type="radio" id="' 
                + optName +'False" name="opp.' + optOpp + '"' 
                +' value="false">False</input>';
        }else{
            var checkboxT = '<input type="radio" id="' 
                + optName +'True" name="opp.' + optOpp + '"' 
                + ' value="true">True</input>';
            var checkboxF = '<input type="radio" id="' 
                + optName +'False" name="opp.' + optOpp +'"' 
                +' value="false" checked="checked" ><strong>False</strong></input>';
        }
        var html = "<div class='cfg-val'>" + checkboxT + " " +  checkboxF +"</div>";
        return html;
    },

    htmlStringText: function( optName, optOpp, optValue ){
        
        var textBox = '<input type="text" id="' + optName 
            + '" name="opp.' + optOpp + '"' 
            +' value="true" checked="checked" />';

        var html = "<div class='cfg-val'>" + textBox +"</div>";
        return html;
    },

    sendUpdatedPrefs: function( o  ){
        var Success = function ( o ) {
            console.log( 'updated' ); 
        };
        var Fail = function ( o ) {
            console.log( "AJAX Error update failed: id=" + o.argument.id ); 
        };
        var callback = { cache:false, timeout: 5000, 
                         success: Success,
                         failure: Fail
                         };    
        try{    
            YAHOO.util.Connect
                .asyncRequest( 'POST', 
                               'userprefmgr?op.update=true', 
                               callback,  
                               YAHOO.lang.JSON.stringify( userprefmgr.preferences));        
        } catch (x) {
            console.log("AJAX Error:"+x);
        }
    },

    submit: function ( formid ){

        var Success = function ( o ) {
            console.log( 'updated' );
            // update preferences fields
            
            var upm = YAHOO.imex.userprefmgr;
   
            upm.preferences 
                = YAHOO.lang.JSON.parse( o.responseText );
            upm.preferences 
                = YAHOO.lang.JSON.parse( upm.preferences.preferences );

            upm.updateForm();   
            
        };
        
        var Fail = function ( o ) {
            console.log( "AJAX Error update failed: id=" + o.argument.id ); 
        };
        var callback = { cache:false, timeout: 5000, 
                         success: Success,
                         failure: Fail
                       };             
        try{
            var formObj = document.getElementById( formid );
            YAHOO.util.Connect.setForm(formObj);
            
            var cObj = YAHOO.util.Connect
                .asyncRequest( 'POST', 
                               'userprefmgr?op.update=true', 
                               callback );            
        } catch (x) {
            console.log("AJAX Error:"+x);
        }   
    },
    
    defset: function( o ){
        var callback = { cache:false, timeout: 5000, 
                         success: Success,
                         failure: Fail
                         }; 

        var Success = function ( o ) {
            console.log( 'defset' ); 

            var upm = YAHOO.imex.userprefmgr;
   
            upm.preferences 
                = YAHOO.lang.JSON.parse( o.responseText );
            upm.preferences 
                = YAHOO.lang.JSON.parse( upm.preferences.preferences );
            
            upm.updateForm();   
        };
        
        var Fail = function ( o ) {
            console.log( "AJAX Error defset failed: id=" + o.argument.id ); 
        };
        
        try{
            YAHOO.util
                .Connect.asyncRequest( 'GET', 'userprefmgr?op.defset=true', 
                                       callback );        
        } catch (x) {
            console.log("AJAX Error:"+x);
        }
    }
};
