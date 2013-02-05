<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script src="js/pubnew-yui.js" type="text/javascript"></script>

<div width="100%">

   <div>
    <s:if test="hasActionErrors()">    
      <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
       <span class="error">
        <s:iterator value="actionErrors">
         <span class="errorMessage"><s:property escape="false" /></span>
        </s:iterator>
       </span>
      </div>
     
    </s:if>
   </div>  
 <s:if test="pub == null or pub.pmid == null or pub.pmid.length() == 0" >  
   <div>
   <p>
    <s:form theme="simple" action="pubedit">
     <s:hidden name="id" value="%{id}"/>
     <s:hidden name="pub.id" value="%{id}"/>
     </p>     
       <div> 
       <p>Author(s)
          <s:textfield theme="simple" name="pub.author" size="90" maxLength="128"/>                         
       </p>
       <p>Title         
          <s:textfield theme="simple" name="pub.title" size="90" maxLength="128"/>                 
          <p><s:submit theme="simple" name="op.eadd" value="ADD"/></p>
       </div>
      
    </s:form>
   </div>
  
 
</s:if>
<s:else>
 <div>
   <p>
    <s:form theme="simple" action="pubedit"> 
     <s:hidden name="id" value="%{id}"/>
     <s:hidden name="pub.id" value="%{id}"/>
     </p>
     <p>   
      PMID: <s:textfield theme="simple" name="pub.pmid" size="12" maxLength="12"/>
     </p> 
     <p> 
      <s:submit theme="simple" name="op.esrc" value="SEARCH"/>
     </p>
     <p><s:submit theme="simple" name="op.eadd" value="ADD"/><p>
     <p>Journal
          <s:property value="pub.source.title" />             
     </p>     
     <p>Author(s)
        <s:property value="pub.author"/>
     </p>
     <p>Title
        <s:property value="pub.title"/>
     </p>
     <p>Abstract
        <s:property value="%{pub.abstract}"/>
     </p>
    </s:form>
   </div>
</s:else>
</div>
