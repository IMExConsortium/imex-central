<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
  YAHOO.util.Event.addListener(window, "load", function() {
    var myTabs = new YAHOO.widget.TabView("pubTab"); 
  });
</script>

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
       &nbsp;<b><i>
        <s:if test="pub.title.length()>75">
         <s:property value="pub.title.substring(0,75)"/>...
        </s:if>
        <s:else>
         <s:property value="pub.title"/>
        </s:else>
       </i</b>             
      </td> 
      <th width="5%">
       <s:submit theme="simple" name="op.del" value="DELETE"/>
      </th>
     </tr>
     <tr>
      <td colspan="3">
       <div class="yui-skin-sam">
         <div id="pubTab" class="yui-navset"> 
           <ul class="yui-nav"> 
             <li class="selected"><a href="#tab1"><em>Status</em></a></li> 
               <li><a href="#tab2"><em>Details</em></a></li> 
              <li><a href="#tab3"><em>Comments</em></a></li> 
              <li><a href="#tab4"><em>Access</em></a></li> 
          </ul>         
          <div class="yui-content"> 
<div>
 <table width="100%" border="1">
  <tr>
   <th align="right" width="15%" nowrap>Availability<br/>Status</th>
   <td width="75%">
    <table width="100%" border="1">
     <tr>
      <th width="33%" nowrap>Publication Date (expected)</th>
      <th width="33%" nowrap>Publication Date</th>
      <th width="33%" nowrap>Release Date</th>
     </tr>
     <tr> 
      <td align="center">
       <s:textfield theme="simple" name="pub.expectedPubDateStr" size="10" maxLength="10"/>
      </td>
      <td align="center">
       <s:textfield theme="simple" name="pub.pubDateStr" size="10" maxLength="10"/>
      </td>
      <td align="center">
       <s:textfield theme="simple" name="pub.releaseDateStr" size="10" maxLength="10"/>
      </td>
     </tr>
    </table>
   </td>
   <th  width="10%">
    <s:submit theme="simple" name="op.pav" value="UPDATE"/>
   </th>
  </tr>
  <tr>
   <th align="right" nowrap>Contact<br/>Email</th>
   <td>
    <s:textfield theme="simple" name="pub.contactEmail" size="32" maxLength="32"/>
   </td>
   <th>
    <s:submit theme="simple" name="op.pcm" value="UPDATE"/>
   </th>
  </tr>
  <tr>
   <th align="right" nowrap>Curation<br/>Status</th>
   <td>
    <table width="100%" border="0">
     <tr>
      <td align="right" valign="middle" width="5%">
        <b><s:property  value="pub.state.name"/></b>
      </td>
      <td align="center" valign="middle" width="5%" nowrap>  
       <i>change to</i>
      </td>
       <td align="left" valign="middle" width="90%">  
       <label id="state-button-container"/>
      </td>
     </tr>
    </table>
   </td>
   <th>
    <s:submit theme="simple" name="op.pav" value="UPDATE"/>
   </th>
  </tr>
  <tr>
   <th align="right" nowrap>IMEx ID</th>
   <td>
    <label id="imex-button-container"/>
   </td>
   <td>&nbsp;</td>
  </tr>  
  <tr>
   <th align="right" nowrap>IMEx Records</th>
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
 </table>
</div> 

<div>
 <table width="100%" border="1">
  <tr>
   <th align="right"  width="15%" nowrap>DOI</th>
   <td width="75%" nowrap>
    <s:textfield theme="simple" name="pub.doi" size="32" maxLength="64"/>
   </td>
   <th rowspan="2" width="10%">
    <s:submit theme="simple" name="op.pup" value="UPDATE"/>
   </th>
  </tr>
  <tr>
   <th align="right" nowrap>Internal</th>
   <td>
    <s:textfield theme="simple" name="pub.journalSpecific" size="32" maxLength="64"/>
   </td>
  </tr>
  <tr>
   <th align="right" width="15%" nowrap>Journal</th>
   <td width="75%" nowrap>
    <s:select name="opp.jid" headerKey="-1" headerValue="---Select Journal---" 
              value="pub.source.id" list="journalList" 
              listKey="id" listValue="title" />                  
   </td>
   <th width="10%">
    <s:submit theme="simple" name="op.jset" value="UPDATE"/>
   </th>
  </tr>
  <tr>
   <th align="right" nowrap>Author(s)</th>
   <td width="75%" nowrap>
          <s:textfield theme="simple" name="pub.author" size="90" maxLength="128"/>
         </td>
         <th rowspan="2">
          <s:submit theme="simple" name="op.pup" value="UPDATE"/>
         </th>
  </tr>
  <tr>
   <th align="right" nowrap>Title</th>
   <td>
    <s:textfield theme="simple" name="pub.title" size="90" maxLength="128"/>
   </td>
  </tr>
  <tr>
   <th align="right" nowrap>Abstract</th>
   <td width="75%" nowrap>
          <s:textarea name="role.comments" value="%{pub.abstract}" cols="100" rows="12"/>
   </td>
   <th>
    <s:submit theme="simple" name="op.pup" value="UPDATE"/>
   </th>
  </tr> 
 </table>
</div> 

<div>
 <table width="100%" border="1">
  <tr>
   <th align="right" width="15%" nowrap>Comments</th>
   <td>
    <table width="100%" border="1">
     <tr>
      <td align="left">
        [---------]
       </td>
     </tr>
    </table>
   </td>
   <th>&nbsp;</th>
  </tr>
 </table>
</div> 
            
<div>
 <table width="100%" border="1">
  <tr>
   <th width="15%" align="right" nowrap>Owner</th>
   <td align="left" width="75%">
    <s:textfield theme="simple" name="pub.owner.login" size="32" maxLength="64"/>
   </td>
   <th rowspan="1" width="10%">
    <s:submit theme="simple" name="op.oup" value="UPDATE"/>
   </th>
  </tr>
  <tr>
         <th rowspan="2" align="right" nowrap>Admin Users</th>
         <td colspan="1">
          <s:iterator value="pub.adminUsers" id="u" status="upos">
           <s:checkbox name="opp.eaudel" fieldValue="%{#u.id}"/>
           <s:property value="#u.login"/>
          </s:iterator>
         </td>
        <th>
          <s:submit theme="simple" name="op.eaudel" value="DROP"/>
         </th>
        </tr>
        <tr>
         <td colspan="1">
          <s:textfield theme="simple" name="opp.eauadd" size="32" maxLength="64"/>
         </td>
         <th>
          <s:submit theme="simple" name="op.eauadd" value="ADD"/>
         </th>
        </tr>

        <tr>
         <th rowspan="2" align="right" nowrap>Admin Groups</th>
         <td colspan="1">
          <s:iterator value="pub.adminGroups" id="g" status="gpos">
           <s:checkbox name="opp.eagdel" fieldValue="%{#g.id}"/>
           <s:property value="#g.label"/>
          </s:iterator>
         </td>
        <th>
          <s:submit theme="simple" name="op.eagdel" value="DROP"/>
         </th>
        </tr>
        <tr>
         <td colspan="1">
           <s:select name="opp.eagadd"  headerKey="-1" headerValue="---Select Group---"
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
</div> 
          </div> 
         </div> 
       </div>      
     
  </td>
 </tr>
</table>
