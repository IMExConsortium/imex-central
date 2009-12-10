<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- ===========================================================================
 ! $HeadURL::                                                                  $
 ! $Id::                                                                       $
 ! Version: $Rev::                                                             $
 !========================================================================= --%>
<html lang="en">
 <head>
  <title>
   <s:property value="page.title"/>
  </title>
  <t:insertDefinition name="htmlhead"/>
 </head>
 <body>
  <center>
  <s:if test="big">
    <t:insertDefinition name="header" />
  </s:if>
  <table width="95%" cellspacing="0" cellpadding="0">
   <s:if test="hasActionErrors()">
    <tr><td>
      <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
       <span class="error">
        <s:iterator value="actionErrors">
         <span class="errorMessage"><s:property escape="false" /></span>
        </s:iterator>
       </span>
      </div>
      <br/>
     </td></tr>
   </s:if>
   <s:else>
    <s:if test="source!=null">
     <tr>
      <td class="page"> 
       <s:property value="source" escape="false" />
      </td>
     </tr>
<%-- 
     <s:if test="#session['USER_ROLE'].administrator != null" >
      <tr>
       <td>    
        <t:insertDefinition name="editpage" /> 
       </td>
      </tr>
     </s:if>
--%>
    </s:if>
   </s:else>
  </table>
  <s:if test="big">
    <t:insertDefinition name="footer-edit"/>
  </s:if>
  <s:else>
    <t:insertDefinition name="footer.small"/>
  </s:else>
  </center>
 </body>
</html>
