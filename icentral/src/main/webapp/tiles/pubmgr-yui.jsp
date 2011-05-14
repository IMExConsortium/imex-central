<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script src="js/util-yui.js" type="text/javascript"></script>
<h1>Publication Manager</h1>
<s:if test="id > 0">
 <script src="js/calendar-yui.js" type="text/javascript"></script>
 <script src="js/pubedit-yui.js" type="text/javascript"></script>
 <script src="js/attach-yui.js" type="text/javascript"></script>
 <t:insertDefinition name="pubedit"/>
  <script type="text/javascript">
    YAHOO.util.Event.addListener( 
         window, "load", YAHOO.imex.pubedit.init, 
         {id:"<s:property value="id"/>",
          imexACC:"<s:property value="pub.imexId"/>",
          login:"<s:property value="#session['LOGIN']" />"} 
      );

    YAHOO.util.Event.addListener( window, "load", YAHOO.imex.calendar.init );

    YAHOO.util.Event.addListener( 
         window, "load", YAHOO.imex.attedit.init, 
         {id:"<s:property value="id"/>",
          imexACC:"<s:property value="pub.imexId"/>",
          login:"<s:property value="#session['LOGIN']" />"}  
      );

 </script>
</s:if>
<s:else>
  <script src="js/pubmgr-yui.js" type="text/javascript"></script>
  <s:if test="opp.ou.length() > 0 ">
    <h2 class="pubmgr">Records submitted by: <i><s:property value="opp.ou"/></i></h2>
  </s:if>
  <s:else>
    <s:if test="opp.au.length() > 0 ">
    <h2 class="pubmgr">Records curated by: <i><s:property value="opp.au"/></i></h2>
    </s:if>

  </s:else>

 <div class="yui-skin-sam" width="100%">
  <center>
   <table width="99%">
    <tr>
     <td colspan="3">&nbsp;</td>
     <td class="filter-name">By Status</td>
     <td>&nbsp;</td>
     <td class="filter-name">By Partner</td>
    </tr>
    <tr>
     <td><div id="dt-pag-nav"></div></td>
     <td width="95%">&nbsp;</td>
     <th width="1%">Filter:</th>
     <td class="filter-container"><label id="state-button-container"/></td>
     <th width="1%">and</th>
     <td class="filter-container"><label id="partner-button-container"/></td>
    </tr> 
   </table>
  </center>    
  <div id="pubtab" width="100%" class="pubtab"></div>
  <table width="100%" cellpadding="5">
   <s:form theme="simple" action="pubmgr">
    <tr>
     <td align="center" width="5%">
      <%-- <s:submit theme="simple" name="op.ldel" value="DROP" /> --%>
     </td>
     <td align="right">
      <b>PMID:</b> 
      <s:textfield theme="simple" name="pub.pmid" size="24" maxLength="64"/>
     </td>
     <td colspan="1" align="center" width="5%">
      <s:submit theme="simple" name="op.esrc" value="Search" />
     </td>
    </tr>
   </s:form>
  </table>
  <br/><br/><br/><br/><br/>
 </div>
 
 <script type="text/javascript">
  YAHOO.util.Event.addListener( window, "load", 
                                YAHOO.imex.pubmgr.init(
                                   { owner:"<s:property value="opp.ou"/>", 
                                     admus:"<s:property value="opp.au"/>" }));
 </script>

</s:else>