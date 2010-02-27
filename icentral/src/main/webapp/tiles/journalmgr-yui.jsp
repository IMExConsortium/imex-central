<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<h1>Journal Manager</h1>

<s:if test="id > 0">
 <t:insertDefinition name="journaledit"/>
 <br/><br/>
</s:if>

<s:else>
 <script src="js/journalmgr-yui.js" type="text/javascript"></script>

  <div class="yui-skin-sam" width="100%">
  <center>
   <table width="99%">
    <tr>
     <td><div id="dt-pag-nav"></div></td>
     <td width="95%">&nbsp;</td>
<%-- <th width="1%">Filter:</th>
     <td class="filter-container"><label id="state-button-container"/></td>
     <th width="1%">and</th>
     <td class="filter-container"><label id="partner-button-container"/></td>
     <th width="1%" nowrap>and Editor:</th>
     <td valign="middle">
      <div id="myAutoCompleteEditor">     
       <input id="myEditorInput" type="text"> 
       <div id="myEditorContainer"></div> 
      </div> 
     </td> --%>
    </tr> 
   </table>
  </center>    
  <div id="joutab" width="100%" class="joutab"></div>
  <table width="100%" cellpadding="5">
   <s:form theme="simple" action="journalmgr">
    <tr>
     <td align="center" width="5%">
      <%-- <s:submit theme="simple" name="op.ldel" value="DROP" /> --%>
     </td>
     <td align="right">
      <b>NLMID:</b> 
      <s:textfield theme="simple" name="journal.nlmid" size="24" maxLength="64"/>
     </td>
     <td colspan="1" align="center" width="5%">
      <s:submit theme="simple" name="op.jsrc" value="Search" />
     </td>
    </tr>
   </s:form>
  </table>
 </div>

 <br/><br/> 
 <script type="text/javascript">
  YAHOO.util.Event.addListener( window, "load", YAHOO.imex.journalmgr ) ;
 </script>
</s:else>