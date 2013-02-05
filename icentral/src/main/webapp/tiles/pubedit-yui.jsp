<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div width="100%" class="yui-skin-sam">
   <div class="pub-edit-head">
   <h2>IC-<s:property value="pub.id"/>-PUB</h2>
   <p><em><s:if test="pub.title.length()>78"><s:property value="pub.title.substring(0,75)"/>... </s:if><s:else><s:property value="pub.title"/></s:else></em></p>
   </div>
   <div class="yui-skin-sam">
      <div id="pubTab" class="yui-navset">
         <ul class="yui-nav">
            <li class="selected"><a href="#tab1"><em>Record Status</em></a></li>
            <li><a href="#tab2"><em>Publication Details</em></a></li>
            <li><a href="#tab3"><em>Curator Access</em></a></li>
            <li><a href="#tab4"><em>Comments</em></a></li>
            <li><a href="#tab5"><em>Attachments</em></a></li>
            <li><a href="#tab6"><em>Record History</em></a></li>
         </ul>
         <div class="yui-content">
            <!--Record Status Tab--><s:form id="pub-stat-edit" theme="simple" action="pubedit">
               <div class="top-padding">
                  <fieldset>
                     <legend>
                        <h3>Availability Status</h3>
                     </legend>
                     <p>Publication Date (expected) <s:hidden name="pub.expectedPubDateStr"/><s:textfield theme="simple" name="opp.epd" size="10" maxlength="10"/><button id="epd-show" title="Show Calendar" type="button"><img src="img/calbtn.gif" alt="Calendar" height="18" width="18"/> </button></p>
                     <p>Publication Date <s:hidden name="pub.pubDateStr"/><s:textfield theme="simple" name="opp.pd" size="10" maxlength="10"/><button id="pd-show" title="Show Calendar" type="button"><img src="img/calbtn.gif" alt="Calendar" height="18" width="18"/> </button></p>
                     <p>Release Date <s:hidden name="pub.releaseDateStr"/><s:textfield theme="simple" name="opp.rd" size="10" maxlength="10"/><button id="rd-show" title="Show Calendar" type="button"><img src="img/calbtn.gif" alt="Calendar" height="18" width="18"/> </button></p>
                     <s:submit theme="simple" name="op.edup" value="UPDATE" onclick="return YAHOO.imex.pubedit.pubDate('update');"/>
                  </fieldset>
               </div>
               <div class="top-padding">
                  <fieldset>
                     <legend>
                        <h3>Contact Email</h3>
                     </legend>
                     <p><span id="td-contact-mail"><s:if test="pub.contactEmail.length()&gt; 0"><s:a id="cm-link" href="%{'mailto:'+pub.contactEmail}"><s:property value="pub.contactEmail"/></s:a><em>change to</em> </s:if><s:textfield theme="simple" name="opp.necm" size="32" maxlength="32"/></span></p>
                     <s:submit theme="simple" name="op.emup" value="UPDATE" onclick="return YAHOO.imex.pubedit.pubContactMail('update');"/>
                  </fieldset>
               </div>
               <div class="top-padding">
                  <fieldset>
                     <legend>
                        <h3>Curation Status</h3>
                     </legend>
                     <p><label id="state-label" class="pub-state-label"></label> <em>change to</em> <label id="state-button-container"></label> <s:hidden id="nsn" name="opp.nsn" value="%{pub.state.name}"/></p>
                     <p><s:submit theme="simple" name="op.esup" value="UPDATE" onclick="return YAHOO.imex.pubedit.pubState('update');"/></p>
                  </fieldset>
               </div>
               <div class="top-padding">
                  <fieldset>
                     <legend>
                        <h3>IMEx ID</h3>
                     </legend>
                     <p><label id="imex-button-container"></label> IMEx Records [---------]</p>
                     <p><s:submit theme="simple" name="op.pav" value="UPDATE" disabled="true"/></p>
                  </fieldset>
               </div>
            </s:form><!--Publication Details Tab-->
            <div class="yui-hidden">
               <s:form id="pub-det-edit" theme="simple" action="pubedit">
                  <div class="top-padding">
                     <fieldset>
                        <legend>
                           <h3>Identifiers</h3>
                        </legend>
                        <p>PubMed <s:textfield theme="simple" name="pub.pmid" size="32" maxLength="64"/><s:submit theme="simple" name="op.epmr" value="Synchronize" disabled="false" onclick="return YAHOO.imex.pubedit.pubIdent('epmr');"/></p>
                        <p>DOI <s:textfield theme="simple" name="pub.doi" size="32" maxLength="64"/></p>
                        <p>Internal <s:textfield theme="simple" name="pub.journalSpecific" size="32" maxLength="64"/></p>
                        <p><s:submit theme="simple" name="op.eidu" value="UPDATE" disabled="false" onclick="return YAHOO.imex.pubedit.pubIdent('update');"/></p>
                     </fieldset>
                  </div>
                  <div class="top-padding">
                     <fieldset>
                        <legend>
                           <h3>Journal Title</h3>
                        </legend>
                        <p><s:select name="opp.jid" headerKey="-1" headerValue="---Select Journal---" value="pub.source.id" list="journalList" listKey="id" listValue="title"/><s:submit theme="simple" name="op.jset" value="UPDATE" disabled="true"/></p>
                     </fieldset>
                  </div>
                  <div class="top-padding">
                     <fieldset>
                        <legend>
                           <h3>Authors/Title</h3>
                        </legend>
                        <p>Author(s) <s:textfield theme="simple" name="pub.author" size="90" maxLength="512"/></p>
                        <p>Title <s:textfield theme="simple" name="pub.title" size="90" maxLength="128"/></p>
                        <p><s:submit theme="simple" name="op.eatu" value="UPDATE" disabled="false" onclick="return YAHOO.imex.pubedit.pubAuthTitle('update');"/></p>
                     </fieldset>
                  </div>
                  <div class="top-padding">
                     <fieldset>
                        <legend>
                           <h3>Abstract</h3>
                        </legend>
                        <p><s:textarea cssClass="limit-width" name="pub.abstract" value="%{pub.abstract}" cols="75" rows="12"/></p>
                        <p><s:submit theme="simple" name="op.pup" value="UPDATE" disabled="true"/></p>
                     </fieldset>
                  </div>
               </s:form></div>
            <!-- access pane -->
            <div class="yui-hidden">
               <s:form id="pub-acc-edit" theme="simple" action="pubedit">
                  <div class="top-padding">
                  <p>Submitted By: <s:hidden name="pub.owner.login"/><s:textfield theme="simple" name="pub.owner.login" size="32" maxLength="64" disabled="true"/></p>
                  </div> 
                  <h3 class="pub-edit-sect">Curators</h3>
                  <fieldset>
                     <legend>Drop Curators</legend>
                     <p id="td-admin-user"><s:iterator value="pub.adminUsers" id="u" status="upos"><s:checkbox name="opp.eaudel" fieldValue="%{#u.id}" cssClass="admin-user-drop"/><s:property value="#u.login"/></s:iterator></p>
                     <p><s:submit theme="simple" name="op.eaudel" value="DROP" onclick="return YAHOO.imex.pubedit.pubAdminUser('drop');"/></p>
                  </fieldset>
                  <div class="top-padding">
                     <fieldset>
                        <legend>Add Curators</legend>
                        <p>Curators username:<s:textfield theme="simple" name="opp.eauadd" size="32" maxLength="64"/></p>
                        <p><s:submit theme="simple" name="op.eauadd" value="ADD" onclick="return YAHOO.imex.pubedit.pubAdminUser('add');"/></p>
                     </fieldset>
                  </div>
                  <h3 class="pub-edit-sect">Curator Groups</h3>
                  <fieldset>
                     <legend>Drop Curator Group</legend>
                     <p id="td-admin-group"><s:iterator value="pub.adminGroups" id="g" status="gpos"><s:checkbox name="opp.eagdel" fieldValue="%{#g.id}" cssClass="admin-group-drop"/><s:property value="#g.label"/></s:iterator></p>
                     <p><s:submit theme="simple" name="op.eagdel" value="DROP" onclick="return YAHOO.imex.pubedit.pubAdminGroup('drop');"/></p>
                  </fieldset>
                  <div class="top-padding">
                     <fieldset>
                        <legend>Add Curator Group</legend>
                        <p><s:select name="opp.eagadd" headerKey="-1" headerValue="---Select Group---" list="groupAll" listKey="id" listValue="label"/></p>
                        <p><s:submit theme="simple" name="op.eagadd" value="ADD" onclick="return YAHOO.imex.pubedit.pubAdminGroup('add');"/></p>
                     </fieldset>
                  </div>
               </s:form></div>
            <!-- comments pane -->
            <div id="cmt-pane" class="yui-hidden">
               <s:form id="cmtmgr" theme="simple" action="attachmgr"><s:hidden name="id" value="%{id}"/><s:hidden name="pub.id" value="%{id}"/>
               <s:if test="#session['USER_ID'] > 0">
                  <fieldset>
                     <legend>
                        <h3>Add a Comment</h3>
                     </legend>
                     <p><strong>Subject:</strong> <s:textfield theme="simple" name="opp.encs" size="50" value=""/></p>
                     <%--  <p><strong>Format:</strong> --</p>%><%--  <p><s:radio label="Format" name="format" list="#{'0':'PLAIN','2':'WIKI'}" value="2"/> </p>--%>
                     <p><strong>Body:</strong></p>
                     <p><s:textarea cssClass="limit-width" theme="simple" name="opp.encb" cols="50" rows="5" value=""/></p>
                     <p><strong>Flag:</strong> <%--               <label id="flag-label" class="flag-label">Flag</label> --%><s:select name="opp.encf" headerKey="-1" headerValue="----------" list="#{'1':'QControl','4':'Curation Request'}" value="-1"/></p>
                     <p><s:submit theme="simple" name="op.ecadd" value="ADD" disabled="false" onclick="return YAHOO.imex.attedit.pubAttach('comment','add');"/></p>
                     <em><a onclick="return YAHOO.imex.attedit.pubPreview('comment','preview');" href="">preview</a></em> 
                  </fieldset>
                  </s:if>
                  </s:form>
                  <s:if test="#session['USER_ID'] > 0">
                     <h3 class="pub-edit-sect">Comments</h3>
                     <div id="com-tbview"></div>
                  </s:if>
                  <s:else>
                     <h3 class="pub-edit-sect">Add a Comment</h3>               
                     <p>Please <a href="user">Log in</a> to add a comment.</p>
                  </s:else>

               </div>
            <!-- attachment pane -->
            <div id="att-pane" class="yui-hidden">
               <s:if test="#session['USER_ROLE'].curator != null || #session['USER_ID'] == pub.owner.id">
                   <s:form id="attmgr" theme="simple" action="attachmgr" method="post" enctype="multipart/form-data" onsubmit="return true;">
                     <fieldset>
                        <legend>
                           <h3>Add a File</h3>
                        </legend>
                        <s:hidden name="id" value="%{id}"/><s:hidden name="opp.pubid" value="%{id}"/>
                        
                        <p><strong>File:</strong> <s:file theme="simple" name="opp.edafile" accept="text/*" size="80"/></p>
                        <p><strong>Name:</strong> <s:textfield theme="simple" name="opp.edan" size="50" value=""/></p>
                        <p><strong>Format:</strong> <s:radio label="Format" name="opp.edat" list="#{'0':'TEXT','1':'MIF25','2':'MITAB'}" value="2"/></p>
                        <strong>Flag:</strong> <%--            <label id="flag-label" class="flag-label">Flag</label> --%><s:select name="opp.edaf" headerKey="-1" headerValue="----------" list="#{'2':'MIMIX','3':'IMEX'}" value="-1"/>
                        <s:hidden name="op.eada" value="ADD"/>
                        <s:submit theme="simple" name="op.xxx" value="ADD" disabled="false" onclick="YAHOO.imex.attedit.nameSet( {'nf':'attmgr_opp_edan', 'ff':'attmgr_opp_edafile'}); YAHOO.imex.attedit.UploadFile(); return false;"/><%--                  onclick="return YAHOO.imex.attedit.pubAttach('adata','add');"/>  --%>
                     </fieldset>
                  </s:form>
               </s:if> 
               <s:if test="#session['USER_ROLE'].curator != null || #session['USER_ID'] == pub.owner.id">
                     <h3 class="pub-edit-sect">Attachments</h3>
                     <div id="adata-tbview"></div>
               </s:if>   
               <s:else>
               <h3 class="pub-edit-sect">Add a File</h3>               
                  <p>If you are a curator or are the owner of this publication, please <a href="user">Log in</a> to add and view attachments. Attachments are not public.</p>
               </s:else>
            </div>
            <!-- log pane -->
            <div id="log-pane" class="yui-hidden">
               <s:form id="cmtmgr" theme="simple" action="attachmgr">
                  <h3 class="pub-edit-sect">Record History</h3>
                     <s:hidden name="id" value="%{id}"/><s:hidden name="pub.id" value="%{id}"/>
                     <div id="history-tbview"></div>
               </s:form></div>
         </div>
      </div>
   </div>
</div>
