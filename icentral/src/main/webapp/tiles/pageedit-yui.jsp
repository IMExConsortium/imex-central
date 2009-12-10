<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script src="js/accordion-yui.js" type="text/javascript"></script>
<tr>
 <td colspan="2">
  <s:form theme="simple" action="edit">
  <s:hidden name="mst" value="%{mst}"/>
  <s:hidden name="pageid" value="%{id}"/>
  <dl id="acc-top" class="accordion">
   <dt id="acc-top-tab">Edit</dt>
   <dd class="close">
    <div class="bd">
     <dl class="accordion">
      <dt>Page Attributes</dt>
      <dd class="open">
       <div class="bd">
        <br/>
        <center> 
        <table cellpadding="3" width="50%">
         <tr>
          <td nowrap>Page Id</td>
          <td colspan="3">
           <s:textfield size="86" value="%{id}" name="newid"/>
          </td>
         </tr>
<%--
         <tr>
          <td>&nbsp;</td>
          <td width="35%" align="right" valign="middle">Show Title
           <s:checkbox value="%{page.showtitle}" name="page.showtitle"/>
          </td>
          <td width="10%">&nbsp;</td> 
          <td width="45%" align="left" valign="middle">Show Comments
           <s:checkbox value="%{page.showcomment}" name="page.showcomment"/>
          </td>
         </tr>
--%>
         <tr>
          <td nowrap>Page Source</td>
          <td colspan="3">
           <s:textfield size="86" value="%{page.viewpath}" name="page.viewpath"/>
          </td>
         </tr>
         <tr>
          <td nowrap>Page Format</td>
          <td colspan="3">
           <s:textfield size="86" value="%{page.viewtype}" name="page.viewtype" disabled="true"/>
          </td>
         </tr>
         <tr>
          <td nowrap>Page Title</td>
          <td colspan="3">
           <s:textfield size="86" value="%{page.title}" name="page.title" />
          </td>
         </tr>
         <tr>
          <td nowrap>Menu Selection</td>
          <td colspan="3">
           <s:textfield size="86"  value="%{mst}" name="page.menusel"/>
          </td>
         </tr>
         <tr>
          <td colspan="4">
           <table width="100%" cellpadding="3">
            <tr>     
             <td align="left">
              <s:submit theme="simple" name="opr.pageAttStore" value="STORE" />
             </td> 
             <td align="right">
              <s:submit theme="simple" name="opr.pageAttReset" value="RESET" />
             </td>
            </tr>
           </table>
          </td>
         </tr>
        </table>
       </center>  
       </div>
      </dd>
      <dt>Page Source</dt>
      <dd class="open"> 
       <div class="bd">
        <br/>
        <center> 
        <table cellpadding="3"> 
         <tr>
          <td align="center">
           <s:textarea theme="simple" name="source" value="%{source}" cols="110" rows="12"/>
          </td>
         </tr> 
         <tr>
          <td>
           <table width="100%" cellpadding="3">
            <tr>     
             <td align="left">
              <s:submit theme="simple" name="opr.pageSrcStore" value="STORE" />
             </td> 
             <td align="center">
              <s:submit theme="simple" name="opr.pageSrcPrev" value="PREVIEW" disabled="true"/>
             </td> 
             <td align="right">
              <s:submit theme="simple" name="opr.pageSrcReset" value="RESET" />
             </td>
            </tr>
           </table>
          </td>
         </tr>
        </table>
        </center> 
       </div>
      </dd>
     </dl>
    </div>
   </dd>    
  </dl>
  </s:form>
 </td>
</tr> 
<script type="text/javascript">
  var ip = function() {
    YAHOO.mbi.accordion.init();
  }
  YAHOO.util.Event.on(window,"load", ip);
</script>
