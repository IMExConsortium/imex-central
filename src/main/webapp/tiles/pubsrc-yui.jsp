<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<h1>Publication Search</h1>

<table width="100%" class="yui-skin-sam">
 <s:if test="hasActionErrors()">
  <tr>
   <td align="left">
    <br/>
     <table width="66%" cellspacing="1" cellpadding="3">
     <tr><td>
      <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
       <span class="error">
        <s:iterator value="actionErrors">
         <span class="errorMessage"><s:property escape="false" /></span>
        </s:iterator>
       </span>
      </div>
     </td></tr>
    </table>
   </td>
  </tr>
 </s:if>
 <tr>
  <td>
   <center>
    <s:form theme="simple" action="pubsrc">
     <fieldset class="qfield">
      <legend class="qlegend">By PubMed Identifier</legend>
      <table width="100%" class="qtable">
       <tr>
        <td class="pubsrc-td" align="left" valign="top" nowrap>
         <b>PMID:</b><br> 
         <s:textfield name="pub.pmid"  size="16" maxlength="128" />
         <s:submit theme="simple" name="op.esrc" value="SEARCH"/>
        </td>
       </tr>
      </table>
     </fieldset>
    </s:form>
   </center>
  </td>
 </tr>
 <tr>
  <td>
   <center>
    <s:form theme="simple" action="pubsrc">
     <fieldset class="qfield">
      <legend class="qlegend">By IMEX Accession</legend>
      <table width="100%" class="qtable">
       <tr>
        <td class="pubsrc-td" align="left" valign="top" nowrap>
         <b>IMEX ID:</b><br>
          <s:textfield name="opp.imex"  size="16" maxlength="128" />
          <s:submit theme="simple" name="op.esrc" value="SEARCH"/>
        </td>
       </tr>
      </table>
     </fieldset>
    </s:form>
   </center>
  </td>
 </tr>

 <tr>
  <td>
   <center>
    <s:form theme="simple" action="pubsrc">
     <fieldset class="qfield">
      <legend class="qlegend">By Record Owner/Creator</legend>
      <table width="100%" class="qtable">
       <tr>
        <td class="pubsrc-td" align="left" valign="top" nowrap>
         <b>Owner:</b>
          <div class="acom"> 
           <s:textfield name="opp.owner"/>
           <s:submit theme="simple" name="op.esrc" value="SEARCH" cssClass="pubsrc-sub"/>
           <div id="poo_cnt">
         </div>
        </td>
       </tr>
      </table>
     </fieldset>
    </s:form>
   </center>
  </td>
 </tr>

 <tr>
  <td>
   <center>
    <s:form theme="simple" action="pubsrc">
     <fieldset class="qfield">
      <legend class="qlegend">By Record Curator</legend>
      <table width="100%" class="qtable">
       <tr>
        <td class="pubsrc-td" align="left" valign="top" nowrap>
         <b>Curator:</b>
         <div class="acom"> 
          <s:textfield name="opp.curator" />          
          <s:submit theme="simple" name="op.esrc" value="SEARCH" cssClass="pubsrc-sub"/>
          <div id="poc_cnt">
         </div>
        </td>
       </tr>
      </table>
     </fieldset>
    </s:form>
   </center>
  </td>
 </tr>

</table>

<script type="text/javascript">
   YAHOO.util.Event.addListener(
         window, "load", YAHOO.imex.autocom.init,
         { inp:"pubsrc_opp_curator", cnt:"poc_cnt",opt:"curac" }
      );

   YAHOO.util.Event.addListener(
         window, "load", YAHOO.imex.autocom.init,
         { inp:"pubsrc_opp_owner", cnt:"poo_cnt",opt:"ownac" }
      );


</script>

