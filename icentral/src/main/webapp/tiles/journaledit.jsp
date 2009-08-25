<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
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
 <tr>
  <td>
   <table width="100%" border="1">
    <s:form theme="simple" action="roleedit"> 
     <s:hidden name="id" value="%{id}"/>
     <s:hidden name="journal.id" value="%{id}"/>
     <tr>
      <th nowrap>JID:<s:property value="journal.id"/></th>
      <th nowrap>ISSN:<s:textfield theme="simple" name="journal.issn" size="32" maxLength="64"/></th>
      <td width="95%">&nbsp;</td> 
      <th>
        <s:submit theme="simple" name="op.del" value="ADD"/>
      </th>
     </tr>
     <tr>
      <td colspan="3">
       <table width="100%" border="1">
        <tr>
         <td width="15%" align="right" nowrap>Title</td>
         <td align="left" width="95%"> 
          <s:textfield theme="simple" name="journal.title" size="32" maxLength="64"/>
         </td>
         <th rowspan="4">
          <s:submit theme="simple" name="op.pup" value="UPDATE"/>
         </th>
        </tr>
        <tr>
         <td width="15%" align="right" nowrap>NLMID</td>
         <td align="left" width="95%">
          <s:textfield theme="simple" name="journal.nlmid" size="32" maxLength="64"/>
         </td>
        </tr>
        <tr>
         <td width="15%" align="right" nowrap>Journal Site</td>
         <td align="left" width="95%">
          <s:textfield theme="simple" name="journal.websiteUrl" size="32" maxLength="64"/>
         </td>
        </tr>
        <tr>
         <td width="15%" align="right" nowrap>Comments</td>
         <td align="left" width="95%"> 
          <s:textarea name="journal.comments" value="%{journal.comments}" cols="64" rows="4"/>
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
</table>
