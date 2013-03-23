
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
                html += "\n<dev class='cfg-block-list'>\n";
                for( var j = 0; j < object.options.length; j++ ){
                    
                    var strong = false;
                    
                    if( object["option-def"][object.options[j]]["legend"] !== undefined){
                        html += "<dev class='cfg-block-legend'>";    
                        html += "\n<fieldset>\n";
                        html += "<legend>" + object["option-def"][object.options[j]]["legend"] +"</legend>";
                        strong = true;
                    }else{
                        html += "<dev class='cfg-block-no-legend'>";   
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
                    html += "</dev>\n";
                    
                }
                
                html+="</dev>\n";                    
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
        var Success = function( response ){

            var process = function( key, value, options, opp, strong ){
                
                if( typeof value.value != "undefined"){

                    var keyClass = "cfg-key"; 
                    
                    if( strong ){
                        keyClass = "cfg-key-strong";
                    }
                    
                    if( value.value ==='true'){
                        var checkboxT = '<input type="radio" id="' 
                            + options[key] +'True" name="opp.' + opp + '"' 
                            +' value="true" checked="checked" ><strong>True</strong></input>';
                        var checkboxF = '<input type="radio" id="' 
                            + options[key] +'False" name="opp.' + opp + '"' 
                            +' value="false">False</input>';
                    }else{
                        var checkboxT = '<input type="radio" id="' 
                            + options[key] +'True" name="opp.' + opp + '"' 
                            + ' value="true">True</input>';
                        var checkboxF = '<input type="radio" id="' 
                            + options[key] +'False" name="opp.' + opp +'"' 
                            +' value="false" checked="checked" ><strong>False</strong></input>';
                    }
                    
                    return  "<div class='cfg-key-val'>" 
                        + "<div class='" + keyClass + "'>" + value.label + "</div>" 
                        + "<div class='cfg-val'>" + checkboxT + " " +  checkboxF +"</div>"
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
            //var html = '<form id="userprefmgrForm" name="userprefmgrForm" action="/icentral/userprefmgr" method="post">';
            //html += '<input type="hidden" name="op.update" value="update" id="userprefmgr_op" />'; 

            
            html += userprefmgr.traverse( userprefmgr.preferences, process );
            console.log (html);
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


    update: function(  ){
        var userprefmgr = YAHOO.imex.userprefmgr;
        console.log("In function update");
        var html='';
        var process = function (key,value, options) {
            if(typeof value.value != "undefined")
            {
                var checked = YAHOO.util.Dom.get(options[key] +'True').checked
                if(eval(value.value) !== checked)
                    value.value = !value.value;
                return  value.label ;
            }
            return "";
        };
        var checkPrefs = YAHOO.lang.JSON.parse( YAHOO.lang.JSON.stringify( userprefmgr.preferences) );
        html += userprefmgr.traverse(checkPrefs,process);
        if(!(YAHOO.lang.JSON.stringify( userprefmgr.preferences) === YAHOO.lang.JSON.stringify( checkPrefs )))
           userprefmgr.sendUpdatedPrefs();
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
            
            var cObj = YAHOO.util.Connect.asyncRequest('POST', 
                                                       'userprefmgr?op.update=true', 
                                                       callback);            
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
