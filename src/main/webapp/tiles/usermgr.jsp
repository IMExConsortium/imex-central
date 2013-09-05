<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>User Manager</h1>
<s:if test="id > 0">
 <t:insertDefinition name="useredit"/>
</s:if>
<s:else>

 

     <s:form theme="simple" action="usermgr"> 
     <ul>
      <fieldset>
       <legend><h3>Add User</h3></legend>
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['user.login']!=null">
          <li> id="errorDiv" >
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['user.login'][0]" />
            </span>
           </span>
          </li>
         </s:if>
        </s:if>
        <li>
         <label for="login">Login Name: </label> 
        <s:hidden theme="simple" name="op" value="" />
        <s:textfield theme="simple" name="user.login" size="18" maxLength="32"/> 
       </li>
       
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['user.firstName']!=null">
          <li id="errorDiv" >
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['user.firstName'][0]" />
            </span>
           </span>
          </li>
         </s:if>
        </s:if>
        <li>
         <label for="firstName">First Name: </label>  
         <s:textfield theme="simple" name="user.firstName" size="19" maxLength="64"/>
        </li>
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['user.lastName']!=null">
          <li id="errorDiv" >
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['user.lastName'][0]" />
            </span>
           </span>
          </li>
         </s:if>
        </s:if>
        <li>
         <label for="lastName">Last Name: </label> 
         <s:textfield theme="simple" name="user.lastName" size="19" maxLength="64"/>
       </li>
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['user.email']!=null">
          <li id="errorDiv" >
           <span class="error">
            <span class="errorMessage">
             <s:property value="fieldErrors['user.email'][0]" />
            </span>
           </span>
          </li>
         </s:if>
        </s:if>
        <li>
         <label for="email">Email: </label>
         <s:textfield theme="simple" name="user.email" size="25" maxLength="64"/>
        </li>
       <li>
        <s:submit theme="simple" name="op.add" value="ADD" />
       </li>
      </fieldset>
      </ul>
     </s:form>
     <div id="usermgr-paginator" class="yui-pg-container"></div>
     <div id="usermgr-table"></div>
 <script type="text/javascript">
  YAHOO.imex.usermgr.init();
 </script>
</s:else>
