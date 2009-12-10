<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- ===========================================================================
 ! $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/central/trunk/icentral/src/#$
 ! $Id:: page.jsp 95 2009-12-10 01:28:22Z lukasz                               $
 ! Version: $Rev:: 95                                                          $
 !========================================================================= --%>
<html lang="en">
 <head>
  <title>
   <s:property value="title"/>
  </title>
  <t:insertDefinition name="htmlhead"/>

<%--
  <link rel="stylesheet" href="css/dip2.css" type="text/css" title="dip2">
  <link rel="stylesheet" href="css/dip2tab.css" type="text/css" title="dip2">

  <link rel="stylesheet" type="text/css" media="screen" href="jquery/jqgrid/themes/basic/grid.css" /> 
  <link rel="stylesheet" type="text/css" media="screen" href="jquery/jqgrid/themes/jqModal.css" />
  <link rel="stylesheet" type="text/css" media="screen" href="jquery/css/tabs-accordion.css"/> 

  <link rel="stylesheet" type="text/css" media="screen" href="css/edit.css"/>

  <script src="jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
  <script src="jquery/jquery.tools.min.js" type="text/javascript"></script>
--%>
<%--  <script src="js/jqpage.js" type="text/javascript"></script> --%>
 </head>
 <body>
<%--  <s:if test="#session['USER_ROLE'].administrator != null" >
   <script type="text/javascript">
    $(document).ready( function(){
      var edtpage = jqpage( {edt:"<s:text name='#session.EDT'/>"} );
      edtpage.start();
    });
   </script>
  </s:if> --%>
  <center>
   <t:insertDefinition name="header" />
   <table width="95%" cellspacing="0" cellpadding="0">
    <s:if test="#session['USER_ROLE'].administrator != null" >
     <tr>
      <td>    
       <t:insertDefinition name="menuedit-yui" />
      </td>
     </tr>
    </s:if>
   </table>
   <t:insertDefinition name="footer" />
  </center>
 </body>
</html>




