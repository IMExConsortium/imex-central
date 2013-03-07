<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Account Settings</h1>
 <script type="text/javascript">
    var successback = function(response)
                     {
                        var process = function (key,value) {
                            console.log(key + " : "+value);
                        }

                        var traverse = function (o,func) {
                            for (var i in o) {
                                func.apply(this,[i,o[i]]);  
                                if (typeof(o[i])=="object") {
                                    //going on step down in the object tree!!
                                    traverse(o[i],func);
                                }
                            }
                        }
                        console.log("sucess");
                         var testing;
                         testing = YAHOO.lang.JSON.parse(response.responseText);
                         testing = YAHOO.lang.JSON.parse(testing.preferences);
                         console.log(testing);     
                         
                         var content = document.getElementById('content');
                         var list = document.createElement('ul')
                         traverse(testing,process);
                         /*var i;
                         for(i = 0 ; i < testing['pref-groups'].length; i++)
                         {
                             for(var key in testing['pref-groups'][i])
                                console.log('hey');
                             
                         }*/
                         
                                         
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
 
