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
     <s:hidden name="role.id" value="%{id}"/>
     <s:hidden name="role.name" value="%{role.name}"/>
     <tr>
      <th nowrap>RID:<s:property value="role.id"/></th>
      
      <th nowrap>Name:<s:property value="role.name"/></th>
      <td width="95%">&nbsp;</td> 
      <th>
        <s:submit theme="simple" name="op.del" value="REMOVE"/>
      </th>
     </tr>
     <tr>
      <td colspan="4">
       <table width="100%" border="1">
        <td nowrap>Comments</td>
        <td align="left" width="95%"> 
         <s:textarea name="role.comments" value="%{role.comments}" cols="32" rows="4"/>
        </td>
         <th rowspan="1">
          <s:submit theme="simple" name="op.pup" value="STORE"/>
         </th>
       </table>
      </td>
     </tr> 
    </s:form>
   </table>
  </td>
 </tr>
</table>
