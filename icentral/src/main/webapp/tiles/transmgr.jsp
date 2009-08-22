<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Transition Manager</h1>
<s:if test="id > 0">
 <t:insertDefinition name="transedit"/>
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
      <s:if test="transList!=null">
       <s:iterator value="transList" id="trans" status="tpos">
        <tr>
         <td align="center">
          <s:checkbox name="opp.del" fieldValue="%{#trans.id}"/>
         </td>
         <td align="center">
           <s:property value="#trans.id" />
         </td>
         <td>
           <s:property value="#trans.name" />
         </td>
         <td align="center">
          <a href='transmgr?id=<s:property value="#trans.id"/>'>detail</a>
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
         <s:if test="fieldErrors['trans.name']!=null">
          <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['trans.name'][0]" />
            </span>
           </span>
          </div>
         </s:if>
        </s:if>
        <s:textfield theme="simple" name="trans.name" size="48" maxLength="64"/>
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