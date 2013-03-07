<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Account Settings</h1>
 <script type="text/javascript">
    var successback = function(response)
                     {
                        var process = function (key,value, options) {
                            if(typeof value.value != "undefined")
                            {
                                var checkbox = '<input type="checkbox">' + value.value + "</input>"
                                return  value.label + ": " + checkbox ;
                            }
                            else
                                return  value.label
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
                     success: successback,
                     failure: Fail
                     }; 
                     
    try{
        YAHOO.util.Connect
        .asyncRequest( 'GET', 
                       'userprefmgr?op.opcode1=update', 
                       callback );        
    } catch (x) {
        alert("AJAX Error:"+x);
    }
    
 </script>
<s:if test="id > 0">
 <div class="yui-skin-sam" width="100%">
      
 </div>

</s:if>
<s:else>
  Must be logged in to access user preferences editor.
</s:else>
 
