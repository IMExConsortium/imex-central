<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Account Settings</h1>
<s:if test="id > 0">
 <div class="yui-skin-sam" width="100%">
      
 </div>

<%--
 <script type="text/javascript">
  YAHOO.util.Event.addListener( window, "load",
                                YAHOO.imex.watchmgr.init(
                                   { owner:"<s:property value="opp.ou"/>",
                                     admus:"<s:property value="opp.au"/>",
                                     cflag:"<s:property value="opp.encf"/>" }));

 </script>
--%>

 <br/><br/><br/><br/><br/><br/><br/><br/>
</s:if>
<s:else>
  Must be logged in to access user preferences editor.
 <br/><br/><br/><br/><br/><br/><br/><br/> 
</s:else>
