<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>IMEx Curation Progress</h1>

 <script src="js/stats-yui.js" type="text/javascript"></script>
 <script src="js/pubmgr-yui.js" type="text/javascript"></script> 
 
 <div class="yui-skin-sam" width="100%">   
  <br/>
  <div id="statstab"  class="statstab"></div>
  <br/><br/><br/><br/><br/>
 </div>
 
 <script type="text/javascript">
  YAHOO.util.Event.addListener( window, "load", YAHOO.imex.stats ) ;
 </script>

<br/>
<br/>
<br/>
<br/>
