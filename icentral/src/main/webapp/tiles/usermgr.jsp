<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>User Manager</h1>
<s:if test="id > 0">
 <t:insertDefinition name="useredit"/>
</s:if>
<s:else>
 <div id="mgr-tabs">
 <ul class="yui-nav"> 
       <li class="selected"><a href="#tab1"><em>Add User</em></a></li> 
       <li><a href="#tab2"><em>Users</em></a></li> 
 </ul>   
 <div class="yui-content">
  <div id="tab1">
   <h3>Add User</h3>
   <s:form theme="simple" action="usermgr" id="mgr-form"> 
    <ul>
        <s:if test="hasFieldErrors()">
         <s:if test="fieldErrors['user.login']!=null">
          <li id="errorDiv" >
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
     </ul>
     </s:form>
     </div>
     <div id="tab2">
      <div id="usermgr-table"></div>
     </div>
    <script type="text/javascript">
 
        var columnDefinitions = [
            {key:"id", label:"Id",  sortable:true, resizeable:true},
            {key:"login",label:"Login", sortable:true, resizeable:true},
            {key:"firstName",label:"First Name", sortable:true, resizeable:true},
            {key:"lastName",label:"Last Name", sortable:true, resizeable:true},
            {key:"email",label:"Email", sortable:true, resizeable:true},
            {key:"affiliation",label:"Affiliation", sortable:true, resizeable:true},
            {key:"Details", sortable:true, resizeable:true, formatter:"userDetails"}];
        
        var dataSourceLink = "usermgr?op.view=json";
  
        var datasourceSchema = { 
            resultsList: "userList", 
            fields: ["id", "login", "firstName", "lastName", "email", 
                     "affiliation"]
        }; 
        var container = "usermgr-table";
        YAHOO.imex.usermgr.init(columnDefinitions, dataSourceLink, datasourceSchema, container);
        var tabView = new YAHOO.widget.TabView("mgr-tabs");
  
    </script>
   </div>
  </div>
</s:else>
