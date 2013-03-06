<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Account Settings</h1>

<s:if test="id > 0">
 <div class="yui-skin-sam" width="100%">
      
 </div>
 <script type="text/javascript">
    var successback = function(response)
                     {
                        alert("sucess");
                         var testing;
                         testing = YAHOO.lang.JSON.parse(response.responseText);
                         testing = YAHOO.lang.JSON.parse(testing.preferences);
                         console.log(testing);                     
                     };
    var Fail = function ( o ) {
        alert( "AJAX Error update failed: id=" + o.argument.id ); 
    };
    var callback = { cache:false, timeout: 5000, 
                     success: successback,
                     failure: Fail
                     }; 
                     /*
    try{
        YAHOO.util.Connect
        .asyncRequest( 'GET', 
                       'userprefmgr?op.opcode1=update', 
                       callback );        
    } catch (x) {
        alert("AJAX Error:"+x);
    }
    * */
 </script>
</s:if>
<s:else>
  Must be logged in to access user preferences editor.
</s:else>
 
