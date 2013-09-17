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
       <li class="selected"><a href="#tab1"><em>Group Information</em></a></li> 
       <li><a href="#tab2"><em>Group's Roles</em></a></li> 
 </ul>   
 <div class="yui-content">
 
 <s:form theme="simple" action="groupedit">
 <div id="tab1">
 <s:hidden name="id" value="%{id}"/>
 <s:hidden name="group.id" value="%{id}"/>
 <s:hidden name="group.label" value="%{group.label}"/>
  <h3>Login</h3>
  <ul>
      <li><strong>GID:</strong> <s:property value="group.id"/></li>
      <li><strong>Label:</strong> <s:property value="group.label"/></li>
  </ul>
  <h3>User Details</h3>
  <ul>
  
   <s:if test="hasFieldErrors()">
    <s:if test="fieldErrors['group.name']!=null">
    <li>
     <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
      <span class="error">
       <span class="errorMessage">
        <s:property value="fieldErrors['group.name'][0]" />
       </span>
      </span>
     </div>
     </li>
    </s:if>
   </s:if>
  
   <li><strong>Group Name: </strong>
       <s:textfield theme="simple" name="group.name" size="30" maxLength="64"/> </li>
  
   <s:if test="hasFieldErrors()">
    <s:if test="fieldErrors['opp.alogin']!=null">
     <li>
     <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
      <span class="error">
       <span class="errorMessage">
        <s:property value="fieldErrors['opp.alogin'][0]" />
       </span>
      </span>
     </div>
     </li>
    </s:if>
   </s:if>
   
   <li><strong>Admin User: </strong>
       <s:textfield theme="simple" name="opp.alogin" 
                       value="%{group.adminUser.login}" size="30" maxLength="64"/>
    </li>
      <s:if test="hasFieldErrors()">
       <s:if test="fieldErrors['opp.clogin']!=null">
        <li>
        <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
         <span class="error">
          <span class="errorMessage">
           <s:property value="fieldErrors['opp.clogin'][0]" />
          </span>
         </span>
        </div>
        </li>
       </s:if>
      </s:if>
      
   <li><strong>Contact User: </strong>
       <s:textfield theme="simple" name="opp.clogin" 
                       value="%{group.contactUser.login}" size="30" maxLength="64"/></li>
   <li><strong>Comments: </strong><br />
       <s:textarea name="group.comments" value="%{group.comments}" cols="64" rows="4"/></li>
       
   <li><s:submit theme="simple" name="op.pup" value="Save"/></li>
  </ul>
  
    </s:form>
    </div>
    <div id="tab2">
     <s:form theme="simple" action="groupedit">
    <ul>
     <li><h3>Roles</h3></li>
     <li>
     <fieldset>
      <legend>Current Roles</legend>
      <li>
      <s:iterator value="group.roles" id="r" status="rpos">
       <s:checkbox name="opp.rdel" fieldValue="%{#r.id}"/>
       <s:property value="#r.name"/>
      </s:iterator>
      </li>
     <li>
      <s:submit theme="simple" name="op.rdel" value="Drop"/>
     </li> 
     </fieldset>
     </li>
    </ul>
    <ul>
     <li>
     <fieldset>
      <legend>Add Roles</legend>
      <li>
      <s:select name="opp.radd"  headerKey="-1" headerValue="---Select Role---"
                list="roleAll" listKey="id" listValue="name" />
      </li>
      <li>
       <s:submit theme="simple" name="op.radd" value="Add"/>
      </li>
      </li> 
     </fieldset>
    </ul> 
   </s:form>
    </div>
 </div>
</div>
<script>
    var tabView = new YAHOO.widget.TabView("mgr-tabs");
</script>

<style>
form#groupedit ul li {
  list-style: none;
}
</style>
