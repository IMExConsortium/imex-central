<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Account Settings</h1>
<s:if test="#session['USER_ID'] > 0">
</s:if>
<s:if test="id == 30">
 <script type="text/javascript">
  YAHOO.util.Event.addListener( window, "load",
                                YAHOO.imex.userprefmgr.init(
                                   { owner:"<s:property value="opp.ou"/>",
                                     admus:"<s:property value="opp.au"/>",
                                     cflag:"<s:property value="opp.encf"/>", 
                                        login:"<s:property value="#session['LOGIN']" />",
                                      loginid:"<s:property value="#session['USER_ID']" />"}));

 </script>
</s:if>
 <div class="yui-skin-sam" width="100%">
      
 </div>


<s:else>
  Must be logged in to access user preferences editor.
</s:else>
 
