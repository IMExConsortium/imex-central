<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>Group Manager</h1>
<s:if test="id > 0">
 <t:insertDefinition name="groupedit"/>
</s:if>
<s:else>
 <s:if test="hasActionErrors()">
   <p id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
    <span class="error">
     <s:iterator value="actionErrors">
      <span class="errorMessage"><s:property escape="false" /></span>
     </s:iterator>
    </span>
   </p>
 </s:if>
 
  <div id="mgr-tabs" class="main-width">
 <ul class="yui-nav"> 
       <li class="selected"><a href="#tab1"><em>Add Group</em></a></li> 
       <li><a href="#tab2"><em>Groups</em></a></li> 
 </ul>   
 <div class="yui-content">
  <div id="tab1">
   <h3 class="header-grey-highlight">Add Group</h3>
     
 <s:form theme="simple" action="groupmgr" id="mgr-form" cssClass="align-label">
 <ul>
  <s:hidden theme="simple" name="op" value="" />
    <s:if test="hasFieldErrors()">
     <s:if test="fieldErrors['group.label']!=null">
      <li id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
       <span class="error">
        <span class="errorMessage">
         <s:property value="fieldErrors['group.label'][0]" />
        </span>
       </span>
      </li>
     </s:if>
    </s:if>
    <li>
     <label for="mgr-form_group_label" ><strong>Group Label:</strong></label>
     <s:textfield theme="simple" name="group.label" size="16" maxLength="32"/> 
   </li>
    <s:if test="hasFieldErrors()">
     <s:if test="fieldErrors['group.name']!=null">
      <li id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
       <span class="error">
        <span class="errorMessage">
         <s:property value="fieldErrors['group.name'][0]" />
        </span>
       </span>
      </li>
     </s:if>
    </s:if>
   <li>
    <label for="mgr-form_group_name" ><strong>Group Name:</strong></label>
    <s:textfield theme="simple" name="group.name" size="48" maxLength="64"/>
   </li>
   <li>
    <s:submit theme="simple" name="op.add" value="Add" />
   </li>
   </ul>
 </s:form>
 </div>
  <div id="tab2">
   <div id="groupmgr-table"></div>
  </div>
 <script type="text/javascript">

    var columnDefinitions = [
        {key:"id", label:"Id",  sortable:true, resizeable:true},
        {key:"label",label:"Label", sortable:true, resizeable:true},
        {key:"name",label:"Name", sortable:true, resizeable:true},
        {key:"Details", sortable:true, resizeable:true, formatter:"groupDetails"}
       ];
    
    var dataSourceLink = "groupmgr?op.view=json";

    var datasourceSchema = { 
        resultsList: "groupList", 
        fields: ["id", "label", "name", "details"]
    }; 
    var container = "groupmgr-table";
    
    YAHOO.imex.usermgr.init(columnDefinitions, dataSourceLink, datasourceSchema, container);
    YAHOO.imex.usermgr.tabView = new YAHOO.widget.TabView("mgr-tabs");

 </script>
</s:else>
