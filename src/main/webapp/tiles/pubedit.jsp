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
    <s:form theme="simple" action="pubedit"> 
     <s:hidden name="id" value="%{id}"/>
     <s:hidden name="pub.id" value="%{id}"/>
     <tr>
      <th width="5%" nowrap>PID:<s:property value="pub.id"/></th>
      <th width="5%" nowrap>PMID:<s:property value="pub.pmid"/></th>
      <td align="left" width="85%" nowrap>
       <s:if test="pub.imexid.length > 0">
        <b>IMEx ID:</b><s:property value="pub.imexid"/></th>
       </s:if>
       <s:else>
        <b>IMEx ID:</b> <s:submit theme="simple" name="op.pasim" value="ASSIGN"/>
       </s:else> 
      <th width="5%">
       <s:submit theme="simple" name="op.del" value="DELETE"/>
      </th>
     </tr>
     <tr>
      <td colspan="3">
       <table width="100%" border="1">

        <tr>
         <th align="right" nowrap>
          Publication Status
         </th>
         <td>
          <table width="100%" border="1">
           <tr>
            <td align="left">
             <b><s:property value="pub.state.name"/></b>
            </td>
           </tr>
          </table>
         </td>
         <th>
          <s:submit theme="simple" name="op.pav" value="UPDATE"/>
         </th>
        </tr>

        <tr>
         <th align="right" nowrap>
          Availability
         </th>
         <td>
          <table width="100%" border="1">
           <tr>
            <th width="33%" nowrap>
             Publication Date (expected)
            </th>
            <th width="33%" nowrap>
             Publication Date
            </th>
            <th width="33%" nowrap>
             Release Date
            </th>
           </tr>
           <tr> 
            <td align="center">
             <s:textfield theme="simple" name="pub.expectedPubDate" size="10" maxLength="10"/>
            </td>
            <td align="center">
             <s:textfield theme="simple" name="pub.pubDate" size="10" maxLength="10"/>
            </td>
            <td align="center">
             <s:textfield theme="simple" name="pub.releaseDate" size="10" maxLength="10"/>
            </td>
           </tr>
          </table>
         </td>
         <th>
          <s:submit theme="simple" name="op.pav" value="UPDATE"/>
         </th>
        </tr>

        <tr>
         <th align="right" nowrap>
          Contact Email
         </th>
         <td>
          <s:textfield theme="simple" name="pub.contactEmail" size="32" maxLength="32"/>
         </td>
         <th>
          <s:submit theme="simple" name="op.pcm" value="UPDATE"/>
         </th>

        </tr>  
        <tr>
         <th align="right" nowrap>
          IMEx Records
         </th>
         <td>
          <table width="100%" border="1">
           <tr>
            <td align="left">
             [---------]
            </td>
           </tr>
          </table>
         </td>
         <th>
          <s:submit theme="simple" name="op.pav" value="UPDATE"/>
         </th>
        </tr>

        <tr>
         <th align="right" nowrap>
          Comments
         </th>
         <td>
          <table width="100%" border="1">
           <tr>
            <td align="left">
             [---------]
            </td>
           </tr>
          </table>
         </td>
         <th>
          &nbsp;
         </th>
        </tr>

        <tr>
         <th colspan="3" nowrap>
          Publication Details
         </th>
        </tr>

        <tr>
         <td align="right" nowrap>
          DOI
         </td>
         <td width="75%" nowrap>
          <s:textfield theme="simple" name="pub.doi" size="32" maxLength="64"/>
         </td>
         <th rowspan="2">
          <s:submit theme="simple" name="op.pup" value="UPDATE"/>
         </th>
        </tr>

        <tr>
         <td align="right" nowrap>
          Internal
         </td>
         <td>
          <s:textfield theme="simple" name="pub.journalSpecific" size="32" maxLength="64"/>
         </td>
        </tr>
        <tr>
         <td align="right" width="15%" nowrap>
           Journal
         </td>
         <td width="75%" nowrap>
           <s:select name="opp.jid"  headerKey="-1" headerValue="---Select Journal---" value="pub.source.id"
              list="#{'00':'--unpublished--','1':'Nature', '2':'Science','3':'Cell'}" />             
<%--                    list="groupAll" listKey="id" listValue="label" /> --%>           
         </td>
         <th>
          <s:submit theme="simple" name="op.jset" value="UPDATE"/>
         </th>
        </tr>

        <tr>
         <td align="right" nowrap>
          Author(s)
         </td>
         <td width="75%" nowrap>
          <s:textfield theme="simple" name="pub.author" size="90" maxLength="128"/>
         </td>
         <th rowspan="2">
          <s:submit theme="simple" name="op.pup" value="UPDATE"/>
         </th>
        </tr>
        <tr>
         <td align="right" nowrap>
          Title
         </td>
         <td>
          <s:textfield theme="simple" name="pub.title" size="90" maxLength="128"/>
         </td>
        </tr>
 
        <tr>
         <td align="right" nowrap>
          Abstract
         </td>
         <td width="75%" nowrap>
          <s:textarea name="role.comments" value="%{pub.abstract}" cols="100" rows="12"/>
         </td>
         <th>
          <s:submit theme="simple" name="op.pup" value="UPDATE"/>
         </th>
        </tr> 

       <tr>
         <th colspan="3" nowrap>Access Management</td>
        </tr>
        <tr>
         <th width="15%" align="right" nowrap>Owner</th>
         <td align="left" width="95%">
          <s:textfield theme="simple" name="pub.owner.login" size="32" maxLength="64"/>
         </td>
         <th rowspan="1">
          <s:submit theme="simple" name="op.oup" value="UPDATE"/>
         </th>
        </tr>
        <tr>
         <th rowspan="2" align="right" nowrap>Admin Users</th>
         <td colspan="1">
          <s:iterator value="pub.adminUsers" id="u" status="upos">
           <s:checkbox name="opp.udel" fieldValue="%{#u.id}"/>
           <s:property value="#u.login"/>
          </s:iterator>
         </td>
        <th>
          <s:submit theme="simple" name="op.udel" value="DROP"/>
         </th>
        </tr>
        <tr>
         <td colspan="1">
          <s:textfield theme="simple" name="opp.uadd" size="32" maxLength="64"/>
         </td>
         <th>
          <s:submit theme="simple" name="op.gadd" value="ADD"/>
         </th>
        </tr>

        <tr>
         <th rowspan="2" align="right" nowrap>Admin Groups</th>
         <td colspan="1">
          <s:iterator value="pub.adminGroups" id="g" status="gpos">
           <s:checkbox name="opp.gdel" fieldValue="%{#g.id}"/>
           <s:property value="#g.label"/>
          </s:iterator>
         </td>
        <th>
          <s:submit theme="simple" name="op.eagdel" value="DROP"/>
         </th>
        </tr>
        <tr>
         <td colspan="1">
           <s:select name="opp.gadd"  headerKey="-1" headerValue="---Select Group---"
                     list="groupAll" listKey="id" listValue="label" />  
         </td>
         <th>
          <s:submit theme="simple" name="op.eagadd" value="ADD"/>
         </th>
        </tr>
        
       </table>
      </td>
      <td nowrap>&nbsp;</td>
     </tr> 

    </s:form>
   </table>
  </td>
 </tr>
</table>
