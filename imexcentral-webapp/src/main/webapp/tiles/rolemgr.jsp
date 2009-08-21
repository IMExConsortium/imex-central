<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Role Manager</h1>
<s:if test="id > 0">
 <t:insertDefinition name="roleedit"/>
 <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
 <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
 <br/><br/><br/><br/><br/><br/><br/><br/>
</s:if>
<s:else>
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
     <s:form theme="simple" action="rolemgr"> 
      <tr>
       <th colspan="2">RID</th>
       <th>Name</th>
       <th>&nbsp</th>
      </tr>
      <s:if test="roleList!=null">
       <s:iterator value="roleList" id="role" status="rpos">
        <tr>
         <td align="center">
          <s:checkbox name="opp.del" fieldValue="%{#role.id}"/>
         </td>
         <td align="center">
           <s:property value="#role.id" />
         </td>
         <td>
           <s:property value="#role.name" />
         </td>
         <td align="center">
          <a href='rolemgr?id=<s:property value="#role.id"/>'>detail</a>
         </td>
        </s:iterator>
       </tr>
      </s:if>
      <tr>
       <td colspan="2" align="center">
        <s:submit theme="simple" name="op.ldel" value="DROP" />
       </td>
       <td>
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['role.name']!=null">
          <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['role.name'][0]" />
            </span>
           </span>
          </div>
         </s:if>
        </s:if>
        <s:textfield theme="simple" name="role.name" size="48" maxLength="64"/>
       </td>
       <td align="center">
        <s:submit theme="simple" name="op.add" value="ADD" />
       </td>
      </tr>
     </s:form>
    </table>
   </td>
  </tr>
 </table>
 <br/>
 <br/>
</s:else>