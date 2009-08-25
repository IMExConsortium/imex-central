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
      <th nowrap>PID:<s:property value="pub.id"/></th>
      <th nowrap>PMID:<s:textfield theme="simple" name="pub.pmid" size="32" maxLength="64"/></th>
      <th nowrap>IMEx ID:<s:property value="pub.imexId"/></th>
      <th width="75%" nowrap>&nbsp;</th>
      <th>
       <s:submit theme="simple" name="op.add" value="ADD"/>
      </th>
     </tr>
     <tr>
      <td colspan="4">
       <table width="100%" border="1">
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
        <tr>
         <th align="right" width="15%" nowrap>
           Journal
         </th>
         <td width="75%" nowrap>
           <s:select name="opp.jid"  headerKey="-1" headerValue="---Select Journal---"
              list="#{'00':'--unpublished--','01':'Nature', '02':'Science','03':'Cell'}" />             
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
          <s:textfield theme="simple" name="pub.author" size="32" maxLength="64"/>
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
          <s:textfield theme="simple" name="pub.title" size="32" maxLength="64"/>
         </td>
        </tr>
 
        <tr>
         <td align="right" nowrap>
          Abstract
         </td>
         <td width="75%" nowrap>
          <s:textarea name="role.comments" value="%{pub.abstract}" cols="100" rows="8"/>
         </td>
         <th>
          <s:submit theme="simple" name="op.pup" value="UPDATE"/>
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
