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
       <li class="selected"><a href="#tab1"><em>User Information</em></a></li> 
       <li><a href="#tab2"><em>Change Password</em></a></li> 
       <li><a href="#tab3"><em>Groups/Roles</em></a></li> 
 </ul>   
 <div class="yui-content">
 
 <s:form theme="simple" action="usermgr">
 <div id="tab1">
  <s:hidden name="id" value="%{id}"/> 
  <s:hidden name="user.id" value="%{id}"/> 
  <s:hidden name="user.login" value="%{user.login}"/> 
  <h3>Login</h3>
  <ul>
   <li>UID: <s:property value="user.id"/></li>
   <li>Login: <s:property value="user.login"/></li>
  </ul>
  <h3>User Details</h3>
  <ul>
   <li><strong>First Name: </strong>
       <s:textfield theme="simple" name="user.firstName" size="32" maxLength="64"/></li>
   <li><strong>Last Name: </strong>
       <s:textfield theme="simple" name="user.lastName" size="32" maxLength="64"/></li>
   <li><strong>Affilation: </strong>
       <s:textfield theme="simple" name="user.affiliation" size="32" maxLength="64"/></li>
   <li><strong>E-mail: </strong>
       <s:textfield theme="simple" name="user.email" size="32" maxLength="64"/></li>
  </ul>
  <h3>Account Status</h3>
  <ul> 
   <li><s:checkbox name="user.activated" value="%{user.activated}"/> activated</li>
   <li><s:checkbox name="user.enabled" value="%{user.enabled}"/> enabled</li>
   <li><s:submit theme="simple" name="op.pup" value="Save"/></li>
  </ul>
  </s:form>
  </div>
  
    <div id="tab2">
     <s:form theme="simple" action="usermgr">
    
    <h3>Change Password</h3>
    <ul>
     <li><strong>New Password: </strong>
         <s:password theme="simple" name="opp.pass1" value="" size="16" maxLength="64"/></li>
     <li><strong>Confirm Password: </strong>
            <s:password theme="simple" name="opp.pass2" value="" size="16" maxLength="64"/></li>
     <li>
      <s:submit theme="simple" name="op.prs" value="Reset"/>
     </li> 
    </ul>
   </s:form>
  </div>
  <div id="tab3">
   <s:form theme="simple" action="usermgr">
    <h3>Roles</h3>
    <ul>
     <li>
      <fieldset>
       <legend>Current Roles</legend>
       <li>
        <s:iterator value="user.roles" id="r" status="rpos"> 
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
     <h3>Groups<h3>
     <ul>
      <li>
      <fieldset>
       <legend>Current Groups</legend>
       <li>
        <s:iterator value="user.groups" id="g" status="gpos"> 
         <s:checkbox name="opp.gdel" fieldValue="%{#g.id}"/>
         <s:property value="#g.label"/>
        </s:iterator>
       </li>
       <li>
        <s:submit theme="simple" name="op.gdel" value="Drop"/>
       </li> 
      </li>
      </fieldset>
     </ul>
     <ul>
      <li>
      <fieldset>
       <legend>Add Groups</legend>
       <li>
        <s:select name="opp.gadd"  headerKey="-1" headerValue="---Select Group---"
                  list="groupAll" listKey="id" listValue="label" />
       </li>
       <li>
        <s:submit theme="simple" name="op.gadd" value="Add"/>
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
form#usermgr ul li {
  list-style: none;
}
</style>
