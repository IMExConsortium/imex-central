<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script src="js/pubnew-yui.js" type="text/javascript"></script>

<table width="100%">
 <tr>
  <td align="left">
   <br/>
   <table width="66%" cellspacing="1" cellpadding="3">
    <s:if test="hasActionErrors()">
     <tr><td>  
      <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
       <span class="error">
        <s:iterator value="actionErrors">
         <span class="errorMessage"><s:property escape="false" /></span>
        </s:iterator>
       </span>
      </div>
     </td></tr>
    </s:if>
   </table>  
  </td>
 </tr>
<s:if test="pub == null or pub.pmid == null or pub.pmid.length() == 0" >
 <tr>
  <td>
   <table width="100%" border="1">
    <s:form theme="simple" action="pubedit">
     <s:hidden name="id" value="%{id}"/>
     <s:hidden name="pub.id" value="%{id}"/>
     <tr>
      <td>&nbsp;</td>
      <th width="5%">
       <s:submit theme="simple" name="op.eadd" value="ADD"/>
      </th>
     </tr>
     <tr>
      <td colspan="1">
       <table width="100%" border="1">
        <tr>
         <th align="right" nowrap>Author(s)</th>
         <td width="90%">
           <s:textfield theme="simple" name="pub.author" size="90" maxLength="128"/>
         </td>
        </tr>
        <tr>
         <th align="right" nowrap>Title</th>
         <td>
          <s:textfield theme="simple" name="pub.title" size="90" maxLength="128"/>
         </td>
        </tr>
       </table>
      </td>
      <td>&nbsp;</td>
     </tr>
    </s:form>
   </table>
  </td>
 </tr>
</s:if>
<s:else>
 <tr>
  <td>
   <table width="100%" border="1">
    <s:form theme="simple" action="pubedit"> 
     <s:hidden name="id" value="%{id}"/>
     <s:hidden name="pub.id" value="%{id}"/>
     <tr>
      <th width="5%" nowrap>
        PMID: <s:textfield theme="simple" name="pub.pmid" size="12" maxLength="12"/>
      </th>
      <td align="left" width="85%" nowrap>
       &nbsp;             
      </td> 
      <th width="5%">
       <s:submit theme="simple" name="op.esrc" value="SEARCH"/>
      </th>
      <th width="5%">
       <s:submit theme="simple" name="op.eadd" value="ADD"/>
      </th>
     </tr>
     <tr>
      <td colspan="3">
       <table width="100%" border="1">
        <tr>
         <th align="right" width="10%" nowrap>Journal</th>
         <td width="90%" nowrap>
          <s:property value="pub.source.title" />             
         </td>
        </tr>
        <tr>
         <th align="right" nowrap>Author(s)</th>
         <td width="90%">
           <s:property value="pub.author"/>
         </td>
        </tr>
        <tr>
         <th align="right" nowrap>Title</th>
         <td>
          <s:property value="pub.title"/>
         </td>
        </tr>
        <tr>
         <th align="right" nowrap>Abstract</th>
         <td width="90%">
          <s:property value="%{pub.abstract}"/>
         </td>
        </tr> 
       </table>
      </td>
      <td>&nbsp;</td> 
     </tr>
    </s:form>
   </table>
  </td>
 </tr>
</s:else>
</table>
