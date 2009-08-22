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
    <s:form theme="simple" action="usermgr">
     <s:hidden name="id" value="%{id}"/> 
     <s:hidden name="user.id" value="%{id}"/> 
     <s:hidden name="user.login" value="%{user.login}"/> 
     <tr>
      <th>UID:<s:property value="user.id"/></th>
      <th>LOGIN:<s:property value="user.login"/></th>
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
          First Name 
         </td>
         <td width="90%">
          <s:textfield theme="simple" name="user.firstName" size="32" maxLength="64"/> 
         </td>
         <td rowspan="5">
          <s:submit theme="simple" name="op.pup" value="STORE"/>
         </td>
        </tr>
        <tr>
         <td align="right" nowrap>
          Last Name
         </td>
         <td>
          <s:textfield theme="simple" name="user.lastName" size="32" maxLength="64"/>
         </td>
        </tr>
        <tr>
         <td align="right" nowrap>
          Affilation
         </td>
         <td> 
          <s:textfield theme="simple" name="user.affiliation" size="32" maxLength="64"/>
         </td>
        </tr>
        <tr>
         <td align="right" nowrap>
          E-mail
         </td>
         <td>
          <s:textfield theme="simple" name="user.email" size="32" maxLength="64"/>
         </td>
        </tr>
        <tr>
         <td colspan="2">
          <table width="100%" border="1">
           <tr>
            <th colspan="2">Account Status</th>
           </tr>
           <tr> 
            <td width="50%" align="center">
             <s:checkbox name="user.activated" value="%{user.activated}"/> 
             activated
            </td>
            <td width="50%" align="center">
              <s:checkbox name="user.enabled" value="%{user.enabled}"/> 
              enabled
            </td>
           </tr>
          </table>
         </td>
        </tr>
        <tr>
         <td colspan="2">
          <table width="100%" border="1">
           <tr>
            <th width="50%">
             New Password
            </th>
            <th width="50%">
             New Password (retype)
            </th>
           </tr>
           <tr>
            <td align="center">
             <s:password theme="simple" name="opp.pass1" value="" size="16" maxLength="64"/>
            </td>
            <td align="center">
             <s:password theme="simple" name="opp.pass2" value="" size="16" maxLength="64"/>
            </td>
           </tr>
          </table>
         </td>
         <td>
          <s:submit theme="simple" name="op.prs" value="RESET"/>
         </td> 
        </tr>
        <tr>
         <th rowspan="2">Roles</th>
         <td colspan="1">
          <s:iterator value="user.roles" id="r" status="rpos"> 
           <s:checkbox name="opp.rdel" fieldValue="%{#r.id}"/>
           <s:property value="#r.name"/>
          </s:iterator>
         </td>
         <th>
          <s:submit theme="simple" name="op.rdel" value="DROP"/>
         </th> 
        </tr>
        <tr>
         <td colspan="1">
         <s:select name="opp.radd"  headerKey="-1" headerValue="---Select Role---"
                   list="roleAll" listKey="id" listValue="name" />
         </td>
         <th>
          <s:submit theme="simple" name="op.radd" value="ADD"/>
         </th>
        </tr> 
        <tr>
         <th rowspan="2">Groups</th>
         <td colspan="1">
          <s:iterator value="user.groups" id="g" status="gpos"> 
           <s:checkbox name="opp.gdel" fieldValue="%{#g.id}"/>
           <s:property value="#g.label"/>
          </s:iterator>
         </td>
         <th>
          <s:submit theme="simple" name="op.gdel" value="DROP"/>
         </th> 
        </tr>
        <tr>
         <td colspan="1">
          <s:select name="opp.gadd"  headerKey="-1" headerValue="---Select Group---"
                    list="groupAll" listKey="id" listValue="label" />
         </td>
         <th>
          <s:submit theme="simple" name="op.gadd" value="ADD"/>
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
