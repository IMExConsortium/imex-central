<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script src="js/pubnew-yui.js" type="text/javascript"></script>

<div width="100%">
<h1>Add a Publication</h1>
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

    <s:form theme="simple" action="pubedit" cssClass="stylized">
    
    <s:hidden name="id" value="%{id}"/>
    <s:hidden name="pub.id" value="%{id}"/>
    
    <label>Author(s):</label>
      <s:textfield theme="simple" name="pub.author" size="90" maxLength="256"/>                         
    <label>Title:</label>
      <s:textfield theme="simple" name="pub.title" size="90" maxLength="128"/>                 
      <button name="op.eadd" value="ADD">Add</button>


    </s:form>

  
 
</s:if>
<s:else>
    <s:form theme="simple" cssClass="stylized" action="pubedit"> 

     <s:hidden name="id" value="%{id}"/>
     <s:hidden name="pub.id" value="%{id}"/>
     <label>PMID:</label>
     <div><s:property  value="pub.pmid" /> </div>
     <label>Journal:</label>
     <div><s:property value="pub.source.title" /></div>
          
     <label>Author(s):</label>
     <div><s:property value="pub.author"/></div>
     
     <label>Title:</label>
     <div><s:property value="pub.title"/></div>
     
     <label>Abstract:</label>
     <div><s:property value="%{pub.abstract}"/></div>
     <button name="op.eadd" value="ADD">Add</button>
    </s:form>
</s:else>
</div>
