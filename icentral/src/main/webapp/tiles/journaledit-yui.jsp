<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script src="js/util-yui.js" type="text/javascript"></script>
<script src="js/journaledit-yui.js" type="text/javascript"></script>
<script src="js/attach-yui.js" type="text/javascript"></script>

<h1>Journal Editor</h1>

<div class="pub-edit-head main-width">
  <h2 id="jnl_ttl"><s:property value="journal.title"/></h2>
  <p><em>IC-<s:property value="journal.id"/>-JNL</em></p>
</div>

<div class="main-width">
  <s:form id="jnl-det-edit" theme="simple" action="journaledit" cssClass="align-label">
    <fieldset>
      <legend><h3>Details</h3></legend>
      <ul>
        <li>
          <label for="pubedit_pub_pmid">Title</label>
          <s:textfield theme="simple" name="journal.title" size="32" maxLength="64"/>
          <s:submit theme="simple" name="op.epmr" value="Synchronize" disabled="true" onclick="return YAHOO.imex.jnledit.sync();"/>
        </li>
        <li>
          <label for="pubedit_pub_doi">NLMID</label>
          <s:textfield theme="simple" name="journal.nlmid" size="32" maxLength="64"/>
        </li>
        <li>
          <label for="pubedit_pub_doi">ISSN</label>
          <s:textfield theme="simple" name="journal.issn" size="32" maxLength="64"/>
        </li>
        <li>
          <label for="pubedit_pub_journalSpecific">Journal Site</label> 
          <s:textfield theme="simple" name="journal.websiteUrl" size="32" maxLength="64"/>
        </li>
        <li>
          <s:submit theme="simple" name="op.eidu" value="UPDATE" disabled="false" onclick="return YAHOO.imex.jnledit.update('detail');"/>
        </li>
      </ul>
    </fieldset>
  </s:form>

  <s:form id="jnl-acc-edit" theme="simple" action="journaledit" cssClass="align-label">
    <fieldset>
      <legend><h3>Access</h3></legend>
      <ul>
        <li>
          <label for="pubedit_pub_owner_login">Created By:</label>
          <s:hidden name="journal.owner.login"/><s:textfield theme="simple" name="journal.owner.login" size="32" maxLength="64" disabled="true"/>
        </li>
        </ul> 
        <h3 class="pub-edit-sect">Curators</h3>
        <ul>
          <fieldset>
            <legend>Drop Curators</legend>
            <li id="td-admin-user">
              <s:iterator value="journal.adminUsers" id="u" status="upos">
                <s:checkbox name="opp.eaudel" fieldValue="%{#u.id}" cssClass="admin-user-drop"/><s:property value="#u.login"/>
              </s:iterator>
            </li>
            <li><s:submit theme="simple" name="op.eaudel" value="DROP" onclick="return YAHOO.imex.jnledit.update('drop-admin');"/></li>
          </fieldset>
        </ul>
        <ul>
          <fieldset>
            <legend>Add Curators</legend>
            <li>Curator username:<s:textfield theme="simple" name="opp.eauadd" size="32" maxLength="64"/></li>
            <li><s:submit theme="simple" name="op.eauadd" value="ADD" onclick="return YAHOO.imex.jnledit.update('add-admin');"/></li>
          </fieldset>
        </ul>
        <h3 class="pub-edit-sect">Curator Groups</h3>
        <ul>
          <fieldset>
            <legend>Drop Curator Group</legend>
            <li id="td-admin-group">
              <s:iterator value="journal.adminGroups" id="g" status="gpos">
                <s:checkbox name="opp.eagdel" fieldValue="%{#g.id}" cssClass="admin-group-drop"/><s:property value="#g.label"/>
              </s:iterator>
            </li>
            <li>
              <s:submit theme="simple" name="op.eagdel" value="DROP" onclick="return YAHOO.imex.jnledit.update('drop-admin-group');"/>
            </li>
          </fieldset>
        </ul>
        <ul>
          <fieldset>
            <legend>Add Curator Group</legend>
            <li>
              <s:select name="opp.eagadd" headerKey="-1" headerValue="---Select Group---" list="groupAll" listKey="id" listValue="label"/></p>
            <li>
              <s:submit theme="simple" name="op.eagadd" value="ADD" onclick="return YAHOO.imex.jnledit.update('add-admin-group');"/></p>
          </fieldset>
        </ul>
    </fieldset>
  </s:form>
</div>
<br/>
<br/>
<br/>

<script type="text/javascript">
  YAHOO.util.Event.addListener( 
     window, "load", YAHOO.imex.jnledit.init, 
         { jid:"<s:property value="id"/>",
           login:"<s:property value="#session['LOGIN']" />" } 
     );
</script>

