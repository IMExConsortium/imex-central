<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<center>
<br/>
 <s:form theme="simple" action="edit">
 <s:hidden name="mst" value="%{mst}"/>
 <s:hidden name="pageid" value="%{id}"/>
 <div id="edit_acc">

  <h2>Page Attributes</h2>
  <div class="pane">
   <br/>
   <table cellpadding="3">
    <tr>
     <td>Page Id</td>
     <td>
      <s:textfield size="86" value="%{id}" name="newid"/>
     </td>
    </tr>
    <tr>
     <td>Page Source</td>
     <td>
      <s:textfield size="86" value="%{page.viewpath}" name="page.viewpath"/>
     </td>
    </tr>
    <tr>
     <td>Page Format</td>
     <td>
      <s:textfield size="86" value="%{page.viewtype}" name="page.viewtype" disabled="true"/>
     </td>
    </tr>
    <tr>
     <td>Page Title</td>
     <td>
      <s:textfield size="86" value="%{page.title}" name="page.title" />
     </td>
    </tr>
    <tr>
     <td>Menu Definition</td>
     <td>
      <s:textfield size="86" value="%{mdf}" name="page.menudef"/>
     </td>
    </tr>
    <tr>
     <td>Menu Selection</td>
     <td>
      <s:textfield size="86"  value="%{mst}" name="page.menusel"/>
     </td>
    </tr>
    <tr>
     <td colspan="2">
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
  </div>
  <h2>Page Source</h2>
  <div class="pane">
   <br/>
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
   </div>
   <h2>Hide</h2>
   <div class="pane"></div>    
  </div>
 </s:form>
 <br/>
</center>