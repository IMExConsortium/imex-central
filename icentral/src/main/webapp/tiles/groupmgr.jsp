<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Group Manager</h1>
<s:if test="id > 0">
 <t:insertDefinition name="groupedit"/>
 <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
 <br/><br/><br/><br/><br/><br/><br/><br/><br/>
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
     <s:form theme="simple" action="groupmgr"> 
      <s:hidden theme="simple" name="op" value="" />
      <tr>
       <th colspan="2">GID</th>
       <th>LABEL</th>
       <th>Name</th>
       <th>&nbsp</th>
      </tr>
      <s:if test="groupList!=null">
       <s:iterator value="groupList" id="grp" status="gpos">
        <tr>
         <td align="center">
          <s:checkbox name="opp.del" fieldValue="%{#grp.id}"/>
         </td>
         <td align="center">
           <s:property value="#grp.id" />
         </td>
         <td>
           <s:property value="#grp.label" />
         </td>
         <td>
           <s:property value="#grp.name" />
         </td>
         <td align="center">
           <a href='groupmgr?id=<s:property value="#grp.id"/>'>detail</a>
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
         <s:if test="fieldErrors['group.label']!=null">
          <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['group.label'][0]" />
            </span>
           </span>
          </div>
         </s:if>
        </s:if>
        <s:textfield theme="simple" name="group.label" size="16" maxLength="32"/> 
       </td>
       <td>
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['group.name']!=null">
          <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['group.name'][0]" />
            </span>
           </span>
          </div>
         </s:if>
        </s:if>
        <s:textfield theme="simple" name="group.name" size="48" maxLength="64"/>
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
