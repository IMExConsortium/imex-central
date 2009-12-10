<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- ======================================================================
 ! $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-porta#$
 ! $Id:: layout.jsp 435 2009-08-15 01:34:09Z lukasz                       $
 ! Version: $Rev:: 435                                                    $
 !==================================================================== --%>
<html lang="en">
 <head>
 <title>
  <t:getAsString name="title"/>
 </title>
 <t:insertDefinition name="htmlhead"/>
 </head>
 <body>

  <center>
   <t:insertDefinition name="header"/>
   <table width="95%" cellspacing="0" cellpadding="0">
    <tr>
     <td class="page">
       <t:insertAttribute name="body" />
     </td>
    </tr>
   </table>
   <t:insertDefinition name="footer" />
  </center>
 </BODY>
</HTML>

