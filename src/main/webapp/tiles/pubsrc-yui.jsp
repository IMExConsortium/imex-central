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
        <th align="right" width="50%" nowrap>
         PMID: <s:textfield name="pub.pmid"  size="16" maxlength="128" />
        </th>
        <td align="left">
         <input type="submit" id="pubedit_op_esrc" name="op.esrc" value="SEARCH" tabindex="3"/>
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
        <th align="right" width="50%" nowrap>
         IMEX ID: <s:textfield name="opp.imex"  size="16" maxlength="128" />
        </th>
        <td align="left">
         <input type="submit" id="pubedit_op_esrc" name="op.esrc" value="SEARCH" tabindex="3"/>
        </td>
       </tr>
      </table>
     </fieldset>
    </s:form>
   </center>
  </td>
 </tr>
</table>
