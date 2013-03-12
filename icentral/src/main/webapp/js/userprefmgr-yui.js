
YAHOO.namespace("imex");

YAHOO.imex.userprefmgr = {
    init: function( init ){
        var Success = function(response)
         {
            var process = function (key,value, options) {
                if(typeof value.value != "undefined")
                {
                    if(value.value )
                    {
                        var checkboxT = '<input type="checkbox" checked><strong>True</strong></input>';
                        var checkboxF = '<input type="checkbox">False</input>';
                    }
                    else
                    {
                        var checkboxT = '<input type="checkbox">True</input>';
                        var checkboxF = '<input type="checkbox" checked><strong>False</strong></input>';
                    }
                    return  value.label + ": " + checkboxT + " " +  checkboxF;
                }
            }
            var traverse = function (o,func) {
                var string = "<form>";
                for (var i in o) {                                 
                    if(i =="option-def")
                    {
                        string += "\n<ul>\n"
                        for(var j = 0; j < o.options.length; j++)
                        {
                            string += "<li>" + func.apply(this,[j,o["option-def"][o.options[j]], o["options"]]); 
                            //going to step down in the object tree!!
                            string += traverse(o["option-def"][o.options[j]],func) + "</li>\n";
                        }
                        string+="</ul>\n"
                    }
                }
                return string
            }
            /*var traverse = function (o,func) {
                for (var i in o) { 
                    if (typeof(o[i])=="object") {
                        if(i =="option-def")
                        {
                            console.log("<ul>");
                            func.apply(this,[i,o[i], o["options"]]); 
                            console.log("</ul>");
                        }
                        //going to step down in the object tree!!
                        traverse(o[i],func);
                    }
                }
            }*/
            console.log("sucess");
             var testing;
             testing = YAHOO.lang.JSON.parse(response.responseText);
             testing = YAHOO.lang.JSON.parse(testing.preferences);
             console.log(testing);     
             
             var content = document.getElementById('content');
             var string = traverse(testing,process);
             console.log (string);
             content.innerHTML = content.innerHTML + string + "</form>";
             
                             
         };
        var Fail = function ( o ) {
            alert( "AJAX Error update failed: id=" + o.argument.id ); 
        };
        var callback = { cache:false, timeout: 5000, 
                         success: Success,
                         failure: Fail
                         }; 
                         
        try{
            YAHOO.util.Connect
            .asyncRequest( 'GET', 
                           //'userprefmgr?id=' + init.loginid +'&op.opcode1=update', 
                           'userprefmgr?id=30' +'&op.opcode1=update', 
                           callback );        
        } catch (x) {
            alert("AJAX Error:"+x);
        }
    }
}
