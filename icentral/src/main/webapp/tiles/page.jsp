<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html lang="en">
 <head>
  <title>
   <s:property value="page.title"/>
  </title>
  <link rel="stylesheet" href="css/icentral.css" type="text/css"/>
  <link rel="stylesheet" href="css/icentraltab.css" type="text/css"/>
  <link rel="stylesheet" href="css/icentralstruts.css" type="text/css"/>
 </head>
 <body>
  <s:if test="#session['USER_ROLE'].administrator != null" >
   <script type="text/javascript">
    $(document).ready( function(){
      var edtpage = jqpage( {edt:"<s:text name='#session.EDT'/>"} );
      edtpage.start();
    });
   </script>
  </s:if>
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
     <s:if test="#session['USER_ROLE'].administrator != null" >
      <tr>
       <td>    
        <t:insertDefinition name="editpage" /> 
       </td>
      </tr>
     </s:if>
    </s:if>
   </s:else>
  </table>
  <s:if test="big">
    <t:insertDefinition name="footer"/>
  </s:if>
  <s:else>
    <t:insertDefinition name="footer.small"/>
  </s:else>
  </center>
 </body>
</html>



