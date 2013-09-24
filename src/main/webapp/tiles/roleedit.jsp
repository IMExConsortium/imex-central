<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<s:if test="hasActionErrors()">
 <p>  
  <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
   <span class="error">
    <s:iterator value="actionErrors">
     <span class="errorMessage"><s:property escape="false" /></span>
    </s:iterator>
   </span>
  </div>
 </p>
</s:if>

<div id="mgr-tabs" class="main-width">
 <ul class="yui-nav"> 
  <li class="selected"><a href="#tab1"><em>Role Information</em></a></li> 
 </ul>   
<div class="yui-content">

 <s:form theme="simple" action="roleedit" cssClass="align-label">
  <div id="tab1">
   <s:hidden name="id" value="%{id}"/>
   <s:hidden name="role.id" value="%{id}"/>
   <s:hidden name="role.name" value="%{role.name}"/>
   <h3 class="header-grey-highlight">Login</h3>
   <ul>
    <li>
     <label for="roleedit_role_id"><strong>RID:</strong></label>
     <s:property value="role.id"/>
    </li>
    <li>
     <label for="roleedit_role_name"><strong>Name:</strong></label>
     <s:property value="role.name"/>
    </li>
   </ul>
   <h3 class="header-grey-highlight">Comments</h3>
   <ul>
    <li>
     <label for="roleedit_role_comments"><strong>Comments</strong></label><br />
     <s:textarea name="role.comments" value="%{role.comments}" cols="64" rows="4"/> 
    </li>
    <li><s:submit theme="simple" name="op.pup" value="Update"/></li>
   </ul>
  </s:form>
 </div>
</div>

<script>
    var tabView = new YAHOO.widget.TabView("mgr-tabs");
</script>

<style>
form#roleedit ul li {
  list-style: none;
}
</style>
