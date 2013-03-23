<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Account Settings</h1>
<s:if test="#session['USER_ID'] > 0">
<%--
</s:if>
<s:if test="id == 30  || id==4">
--%>
    <script type="text/javascript">
      YAHOO.util.Event.addListener( window, "load",
                                    YAHOO.imex.userprefmgr.init(
                                       { formid: "userpref-form",
                                         owner:"<s:property value="opp.ou"/>",
                                         admus:"<s:property value="opp.au"/>",
                                         cflag:"<s:property value="opp.encf"/>", 
                                         login:"<s:property value="#session['LOGIN']" />",
                                         loginid:"<s:property value="#session['USER_ID']" />"}));

    </script>
    <s:form id="userpref-form" action="userprefmgr" theme="simple" onsubmit="return false;">
        <s:submit name="op.update" value="Save" theme="simple" 
           onclick="YAHOO.imex.userprefmgr.submit('userpref-form')"/>
        <s:submit name="op.defset" value="Default" theme="simple" 
           onclick="YAHOO.imex.userprefmgr.defset()"/>
    </s:form>

 <div class="yui-skin-sam" width="100%">
 </div>
</s:if>
<s:else>
  Must be logged in to access user preferences editor.
</s:else>
 
