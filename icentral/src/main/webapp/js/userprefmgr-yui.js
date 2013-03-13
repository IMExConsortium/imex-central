
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
                var html = '';
                for (var i in o) {                                 
                    if(i =="option-def")
                    {
                        html += "\n<ul>\n"
                        for(var j = 0; j < o.options.length; j++)
                        {
                            html += "<li>" + func.apply(this,[j,o["option-def"][o.options[j]], o["options"]]); 
                            //going to step down in the object tree!!
                            html += traverse(o["option-def"][o.options[j]],func) + "</li>\n";
                        }
                        html+="</ul>\n"
                    }
                }
                return html
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
            var html = '<form id="userprefmgrForm" name="userprefmgrForm" action="/icentral/userprefmgr" method="post">';
            html += '<input type="hidden" name="op" value="update" id="userprefmgr_op" />'; 
            html +=traverse(testing,process);
            console.log (html);
            content.innerHTML = content.innerHTML + html + '<input type="submit" id="userprefmgrSave" name="userprefmgrForm" value="Save" />' +"</form>";
             
                             
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
                           //'userprefmgr?id=' + init.loginid +'&op.view=update', 
                           'userprefmgr?id=30' +'&op.view=true', 
                           callback );        
        } catch (x) {
            alert("AJAX Error:"+x);
        }
    }
}
