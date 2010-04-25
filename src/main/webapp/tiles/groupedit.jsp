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
    <s:form theme="simple" action="groupedit">
     <s:hidden name="id" value="%{id}"/>
     <s:hidden name="group.id" value="%{id}"/>
     <s:hidden name="group.label" value="%{group.label}"/>
     <tr>
      <th>GID:<s:property value="group.id"/></th>
      <th>LABEL:<s:property value="group.label"/></th>
      <td width="90%">&nbsp;</td>
      <th> 
       <s:submit theme="simple" name="op.del" value="REMOVE"/>
      </th>
     </tr>
     <tr>
      <td colspan="3">
       <table width="100%" border="1">
        <tr>
         <td align="right" nowrap>
          Group Name
         </td>
         <td width="95%">
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
          <s:textfield theme="simple" name="group.name" size="30" maxLength="64"/> 
         </td>
         <th rowspan="4">
          <s:submit theme="simple" name="op.pup" value="STORE"/>
         </th>
        </tr>
        <tr>
         <td align="right" nowrap>
          Admin User
         </td>
         <td nowrap>
          <s:if test="hasFieldErrors()">
           <s:if test="fieldErrors['opp.alogin']!=null">
            <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
             <span class="error">
              <span class="errorMessage">
               <s:property value="fieldErrors['opp.alogin'][0]" />
              </span>
             </span>
            </div>
           </s:if>
          </s:if>
          <s:textfield theme="simple" name="opp.alogin" 
                       value="%{group.adminUser.login}" size="30" maxLength="64"/>
         </td>
        </tr>
        <tr>
         <td align="right" nowrap>Contact User</td>
         <td nowrap>
          <s:if test="hasFieldErrors()">
           <s:if test="fieldErrors['opp.clogin']!=null">
            <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
             <span class="error">
              <span class="errorMessage">
               <s:property value="fieldErrors['opp.clogin'][0]" />
              </span>
             </span>
            </div>
           </s:if>
          </s:if>
          <s:textfield theme="simple" name="opp.clogin" 
                       value="%{group.contactUser.login}" size="30" maxLength="64"/>
         </td>
        </tr>
        <tr>
         <td align="right" nowrap>Comments</td>
         <td>
          <s:textarea name="group.comments" value="%{group.comments}" cols="64" rows="4"/>
         </td>
        </tr>
        <tr>
         <th rowspan="2">Roles</th>
         <td>
          <s:iterator value="group.roles" id="r" status="rpos">
           <s:checkbox name="opp.rdel" fieldValue="%{#r.id}"/>
           <s:property value="#r.name"/>
          </s:iterator>
         </td>
         <th>
          <s:submit theme="simple" name="op.rdel" value="DROP"/>
         </th> 
        </tr>
        <tr>
         <td>
          <s:select  name="opp.radd"  headerKey="-1" headerValue="---Select Role---" 
                     list="roleAll" listKey="id" listValue="name" />
         </td>
         <th>
          <s:submit theme="simple" name="op.radd" value="ADD"/>
         </th>
        </tr> 
       </s:form>
      </table>
     </td>
     <td>&nbsp;</td>
    </tr>
   </table>
  </td>
 </tr>
</table>
