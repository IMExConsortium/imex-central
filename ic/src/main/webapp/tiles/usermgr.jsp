<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>User Manager</h1>
<s:if test="id > 0">
 <t:insertDefinition name="useredit"/>
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
     <s:form theme="simple" action="usermgr"> 
      <s:hidden theme="simple" name="op" value="" />
      <tr>
       <th colspan="2">UID</th>
       <th>Login</th>
       <th>First Name</th>
       <th>Last Name</th>
       <th>E-Mail</th>
       <th>&nbsp <s:property value="userList.size"/>  <s:property value="totalRecords"/> </th>
      </tr>
      <s:if test="userList.size < totalRecords">
       <tr>
        <td colspan="7">
         <table width="100%">
          <tr>
           <td nowrap><a href='usermgr?firstRecord=1'><i>first page</i><a></td>
           <td width="45%" align="right" nowrap>
            <s:if test="(firstRecord-1)/blockSize+1>100">
             ... [<a href='usermgr?firstRecord=<s:property value="%{firstRecord - 100*blockSize }"/>'>-100</a>]
            </s:if>
            <s:if test="(firstRecord-1)/blockSize+1>10">
             ... [<a href='usermgr?firstRecord=<s:property value="%{firstRecord - 10*blockSize }"/>'>-10</a>]
            </s:if>
           </td>
           <td align="center" width="5%" nowrap>
            <s:if test="firstRecord > 1">
             ... 
              <s:if test="(firstRecord-1)/blockSize+1 >3">
               [<a href='usermgr?firstRecord=<s:property value="firstRecord-3*blockSize"/>'><s:property value="(firstRecord-1)/blockSize-2"/></a>]
              </s:if> 
              <s:if test="(firstRecord-1)/blockSize+1 >2">
               [<a href='usermgr?firstRecord=<s:property value="firstRecord-2*blockSize"/>'><s:property value="(firstRecord-1)/blockSize-1"/></a>]
              </s:if> 
              <s:if test="(firstRecord-1)/blockSize+1 >1">
               [<a href='usermgr?firstRecord=<s:property value="firstRecord-blockSize"/>'><s:property value="(firstRecord-1)/blockSize"/></a>]
              </s:if> 
            </s:if>
            
            <b><s:property value="%{(firstRecord-1)/blockSize+1}"/></b>
            
            <s:if test="firstRecord<totalRecords-blockSize-1">
              <s:if test="firstRecord<totalRecords-blockSize-1">
               [<a href='usermgr?firstRecord=<s:property value="firstRecord+blockSize"/>'><s:property value="%{(firstRecord-1)/blockSize+2}"/></a>]
              </s:if>
              <s:if test="firstRecord<totalRecords-2*blockSize-1">
               [<a href='usermgr?firstRecord=<s:property value="firstRecord+2*blockSize"/>'><s:property value="%{(firstRecord-1)/blockSize+3}"/></a>]
              </s:if>
              <s:if test="firstRecord<totalRecords-3*blockSize-1">
               [<a href='usermgr?firstRecord=<s:property value="firstRecord+3*blockSize"/>'><s:property value="%{(firstRecord-1)/blockSize+4}"/></a>]
              </s:if>
               ...
            </s:if>
           </td>           
           <td width="45%" align="left" nowrap>
            <s:if test="firstRecord+11*blockSize < totalRecords">
             [<a href='usermgr?firstRecord=<s:property value="%{firstRecord + 10*blockSize }"/>'>+10</a>] ...
            </s:if>
            <s:if test="firstRecord+101*blockSize < totalRecords">
             [<a href='usermgr?firstRecord=<s:property value="%{firstRecord + 100*blockSize }"/>'>+100</a>] ...
            </s:if>
           </td>
           <td nowrap><a href='usermgr?firstRecord=<s:property value="%{totalRecords-totalRecords%blockSize+1}"/>'><i>last page</i></a></td>
          </tr> 
         </table>
        </td>
       </tr>
      </s:if>
      <s:if test="userList!=null">
       <s:iterator value="userList" id="usr" status="upos">
        <tr>
         <td align="center">
          <s:checkbox name="opp.del" fieldValue="%{#usr.id}"/>
         </td>
         <td align="center">
          <s:property value="#usr.id" />
         </td>
         <td>
          <s:property value="#usr.login" />
         </td>
         <td>
          <s:property value="#usr.firstName" />
         </td>
         <td>
          <s:property value="#usr.lastName" />
         </td>
         <td>
          <s:property value="#usr.email" />
         </td>
         <td align="center">
          <a href='usermgr?id=<s:property value="#usr.id"/>'>detail</a>
         </td>
        </tr>
       </s:iterator> 
      </s:if>
      <tr>
       <td colspan="2" align="center">
        <s:submit theme="simple" name="op.ldel" value="DROP" />
       </td>
       <td>
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['user.login']!=null">
          <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['user.login'][0]" />
            </span>
           </span>
          </div>
         </s:if>
        </s:if>
        <s:textfield theme="simple" name="user.login" size="12" maxLength="32"/> 
       </td>
       <td>
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['user.firstName']!=null">
          <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['user.firstName'][0]" />
            </span>
           </span>
          </div>
         </s:if>
        </s:if>
        <s:textfield theme="simple" name="user.firstName" size="12" maxLength="64"/>
       </td>
       <td> 
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['user.lastName']!=null">
          <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['user.lastName'][0]" />
            </span>
           </span>
          </div>
         </s:if>
        </s:if>
        <s:textfield theme="simple" name="user.lastName" size="24" maxLength="64"/>
       </td>
       <td>
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['user.email']!=null">
          <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['user.email'][0]" />
            </span>
           </span>
          </div>
         </s:if>
        </s:if>
        <s:textfield theme="simple" name="user.email" size="24" maxLength="64"/>
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
