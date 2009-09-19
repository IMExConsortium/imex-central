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

<!-- YUI CSS files: --> 

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/container/assets/skins/sam/container.css"> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/menu/assets/skins/sam/menu.css"> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/autocomplete/assets/skins/sam/autocomplete.css"> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/button/assets/skins/sam/button.css"> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/calendar/assets/skins/sam/calendar.css"> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/datatable/assets/skins/sam/datatable.css"> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/paginator/assets/skins/sam/paginator.css"> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.0r4/build/tabview/assets/skins/sam/tabview.css"> 

<!-- Combo-handled YUI JS files: --> 

<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/yahoo/yahoo-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/dom/dom-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/event/event-min.js"></script> 


<!--Utilities (also partialy aggregated utilities.js; see readmes in the 
YUI download for details on each of the aggregate files and their contents):--> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/element/element-min.js"></script> 
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/animation/animation-min.js"></script>  -->
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/connection/connection-min.js"></script> 
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/cookie/cookie-min.js"></script>  -->
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/datasource/datasource-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/dragdrop/dragdrop-min.js"></script> 
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/get/get-min.js"></script>  -->
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/history/history-min.js"></script>  -->
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/imageloader/imageloader-min.js"></script>  -->
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/json/json-min.js"></script> 
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/resize/resize-min.js"></script>  -->
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/selector/selector-min.js"></script>  -->
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/yuiloader/yuiloader-min.js"></script>  -->
 
<!--YUI's UI Controls:--> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/container/container-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/menu/menu-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/autocomplete/autocomplete-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/button/button-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/calendar/calendar-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/charts/charts-min.js"></script> 
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/colorpicker/colorpicker-min.js"></script>  -->
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/datatable/datatable-min.js"></script> 
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/editor/editor-min.js"></script>  -->
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/imagecropper/imagecropper-min.js"></script>  -->
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/layout/layout-min.js"></script>  -->
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/paginator/paginator-min.js"></script> 
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/slider/slider-min.js"></script>  -->
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/tabview/tabview-min.js"></script> 
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/treeview/treeview-min.js"></script>  -->
<!-- <script type="text/javascript" src="http://yui.yahooapis.com/2.8.0r4/build/uploader/uploader-min.js"></script>  -->

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

