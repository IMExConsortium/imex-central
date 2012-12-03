<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div width="100%" class="yui-skin-sam">
    <h2>IC-<s:property value="pub.id"/>-PUB</h2>
    <p><em>
       <s:if test="pub.title.length()>78">
        <s:property value="pub.title.substring(0,75)"/>...
       </s:if>
       <s:else>
        <s:property value="pub.title"/>
       </s:else>
     </em><p>
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
          <s:form id="pub-stat-edit" theme="simple" action="pubedit">  
           <table width="100%" border="1">
            <tr>
             <th align="right" width="15%" nowrap>Availability<br/>Status</th>
             <td width="75%">
              <table width="100%" border="1">
               <tr>
                <th width="33%" nowrap>Publication Date (expected)</th>
                <th width="33%" nowrap>Publication Date</th>
                <th width="33%" nowrap>Release Date</th>
               </tr>
               <tr> 
                <td align="center">
                  <table border="0" cellspacing="0">
                   <tr>
                    <td>
                     <s:hidden name="pub.expectedPubDateStr"/>
                     <s:textfield theme="simple" name="opp.epd" size="10" maxLength="10"/>
                    </td>
                    <td>
                     <button type="button" id="epd-show" title="Show Calendar">
                      <img src="img/calbtn.gif" width="18" height="18" alt="Calendar">
                     </button>
                    </td>
                   </tr>
                  </table>
                </td>
                <td align="center">
                  <table border="0" cellspacing="0">
                   <tr>
                    <td>
                     <s:hidden name="pub.pubDateStr"/>
                     <s:textfield theme="simple" name="opp.pd" size="10" maxLength="10"/>
                    </td>
                    <td>
                     <button type="button" id="pd-show" title="Show Calendar">
                      <img src="img/calbtn.gif" width="18" height="18" alt="Calendar">
                     </button>
                    </td>
                   </tr>
                  </table>
                </td>
                <td align="center">
                  <table border="0" cellspacing="0">
                   <tr>
                    <td>
                     <s:hidden name="pub.releaseDateStr"/>
                     <s:textfield theme="simple" name="opp.rd" size="10" maxLength="10"/>
                    </td>
                    <td>
                     <button type="button" id="rd-show" title="Show Calendar">
                      <img src="img/calbtn.gif" width="18" height="18" alt="Calendar" >
                     </button>
                    </td>
                   </tr>
                  </table>
                </td>
               </tr>
              </table>
             </td>
             <th width="10%">
               <s:submit theme="simple" name="op.edup" value="UPDATE"
                onclick="return YAHOO.imex.pubedit.pubDate('update');" />
             </th>
            </tr>
            <tr>
              <th align="right" nowrap>Contact<br/>Email</th>
              <td id="td-contact-mail">
               <s:if test="pub.contactEmail.length() > 0">
                <s:a id="cm-link" href="%{'mailto:'+pub.contactEmail}">
                 <s:property value="pub.contactEmail"/>
                </s:a>
                <i>change to</i>
               </s:if>
               <s:textfield theme="simple" name="opp.necm" size="32" maxLength="32"/>
              </td>
              <th>
               <s:submit theme="simple" name="op.emup" value="UPDATE"
                  onclick="return YAHOO.imex.pubedit.pubContactMail('update');" />
              </th>
            </tr>
            <tr>
              <th align="right" nowrap>Curation<br/>Status</th>
              <td>
               <table width="100%" border="0">
                <tr>
                 <td align="right" valign="middle" width="5%">        
                  <label id="state-label" class="pub-state-label"></label>
                 </td>
                 <td align="center" valign="middle" width="5%" nowrap>  
                  <i>change to</i>
                 </td>
                 <td align="left" valign="middle" width="90%">  
                  <label id="state-button-container"></label>
                 </td>
                </tr>
               </table>
              </td>
              <th>
               <s:hidden id="nsn" name="opp.nsn" value="%{pub.state.name}"/>
               <s:submit theme="simple" name="op.esup" value="UPDATE" 
                  onclick="return YAHOO.imex.pubedit.pubState('update');"/>
              </th>
            </tr>
            <tr>
              <th align="right" nowrap>IMEx ID</th>
              <td>
               <label id="imex-button-container"></label>
              </td>
              <td>&nbsp;</td>
            </tr>  
            <tr>
             <th align="right" nowrap>IMEx Records</th>
             <td>
               <table width="100%" border="1">
                <tr>
                 <td align="left">
                  [---------]
                 </td>
                </tr>
               </table>
             </td>
             <th>
               <s:submit theme="simple" name="op.pav" value="UPDATE" disabled="true"/>
             </th>
            </tr>
           </table>
          </s:form>


           <!-- detail pane -->

         <div class="yui-hidden">
          <s:form id="pub-det-edit" theme="simple" action="pubedit">  
           <table width="100%" border="1">
             <tr>
              <th align="right"  width="15%" nowrap>PubMed</th>
              <td width="75%" nowrap>
               <s:textfield theme="simple" name="pub.pmid" size="32" maxLength="64"/>
                <s:submit theme="simple" name="op.epmr" value="Synchronize" disabled="false"
                  onclick="return YAHOO.imex.pubedit.pubIdent('epmr');"/> 
              </td>
              <th rowspan="3" width="10%">
               <s:submit theme="simple" name="op.eidu" value="UPDATE" disabled="false"
                  onclick="return YAHOO.imex.pubedit.pubIdent('update');"/>
              </th>
             </tr>
             <tr>
              <th align="right" nowrap>DOI</th>     
              <td>
               <s:textfield theme="simple" name="pub.doi" size="32" maxLength="64"/>
              </td>
             </tr>
             <tr>
              <th align="right" nowrap>Internal</th>
              <td>
               <s:textfield theme="simple" name="pub.journalSpecific" size="32" maxLength="64"/>
              </td>
             </tr>
             <tr>
              <th align="right" width="15%" nowrap>Journal</th>
              <td width="75%" nowrap>
               <s:select name="opp.jid" headerKey="-1" headerValue="---Select Journal---" 
                     value="pub.source.id" list="journalList" 
                     listKey="id" listValue="title" />                  
              </td>
              <th width="10%">
               <s:submit theme="simple" name="op.jset" value="UPDATE" disabled="true"/>
              </th>
             </tr>
             <tr>
              <th align="right" nowrap>Author(s)</th>
              <td width="75%" nowrap>
               <s:textfield theme="simple" name="pub.author" size="90" maxLength="128"/>
              </td>
              <th rowspan="2">
               <s:submit theme="simple" name="op.eatu" value="UPDATE" disabled="false"
                  onclick="return YAHOO.imex.pubedit.pubAuthTitle('update');"/>
              </th>
             </tr>
             <tr>
              <th align="right" nowrap>Title</th>
              <td>
               <s:textfield theme="simple" name="pub.title" size="90" maxLength="128"/>
              </td>
             </tr>
             <tr>
              <th align="right" nowrap>Abstract</th>
              <td width="75%" nowrap>
               <s:textarea name="pub.abstract" value="%{pub.abstract}" cols="100" rows="12"/>
              </td>
              <th>
               <s:submit theme="simple" name="op.pup" value="UPDATE" disabled="true"/>
              </th>
             </tr> 
           </table>
          </s:form>
         </div> 
         
          <!-- access pane -->
    
         <div class="yui-hidden">
          <s:form id="pub-acc-edit" theme="simple" action="pubedit">
           <table width="100%" border="1">
            <tr>
              <th width="15%" align="right" nowrap>Submitted By</th>
              <td align="left" width="80%">
               <s:hidden name="pub.owner.login"/>
               <s:textfield theme="simple" name="pub.owner.login" size="32" maxLength="64" disabled="true"/>
              </td>
              <th rowspan="1" width="5%">
               &nbsp;
              </th>
            </tr>
            <tr>
              <th rowspan="2" align="right" nowrap>Admin Users<br>(<i>Curators</i>)</th>
              <td colspan="1" id="td-admin-user" >
                <s:iterator value="pub.adminUsers" id="u" status="upos">
                  <s:checkbox name="opp.eaudel" fieldValue="%{#u.id}" cssClass="admin-user-drop" />
                 <s:property value="#u.login"/>
                </s:iterator>
              </td>
              <th>
                <s:submit theme="simple" name="op.eaudel" value="DROP" 
                   onclick="return YAHOO.imex.pubedit.pubAdminUser('drop');" />
              </th>
            </tr>
            <tr>
              <td colspan="1">
                 <s:textfield theme="simple" name="opp.eauadd" size="32" maxLength="64"/>
              </td>
              <th>
                 <s:submit theme="simple" name="op.eauadd" value="ADD"
                    onclick="return YAHOO.imex.pubedit.pubAdminUser('add');" />
              </th>
            </tr>
            <tr>
                <th rowspan="2" align="right" nowrap>Admin Groups<br>(<i>IMEx Partners</i>)</b></th>
                <td colspan="1" id="td-admin-group" >
                 <s:iterator value="pub.adminGroups" id="g" status="gpos">
                  <s:checkbox name="opp.eagdel" fieldValue="%{#g.id}" cssClass="admin-group-drop" />
                  <s:property value="#g.label"/>
                 </s:iterator>
                </td>
                <th>
                 <s:submit theme="simple" name="op.eagdel" value="DROP"
                    onclick="return YAHOO.imex.pubedit.pubAdminGroup('drop');" />
                </th>
            </tr>
            <tr>
                <td colspan="1">
                 <s:select name="opp.eagadd"  headerKey="-1" headerValue="---Select Group---"
                           list="groupAll" listKey="id" listValue="label" />  
                </td>
                <th>
                 <s:submit theme="simple" name="op.eagadd" value="ADD"
                    onclick="return YAHOO.imex.pubedit.pubAdminGroup('add');" />
                </th>
            </tr>
           </table>
          </s:form>
         </div>
         
       <!-- comments pane -->

         <div id="cmt-pane" class="yui-hidden">
          <s:form id="cmtmgr" theme="simple" action="attachmgr">
           <table width="100%" border="0" cellspacing="2px">
            <s:hidden name="id" value="%{id}"/>
            <s:hidden name="pub.id" value="%{id}"/>
            <tr cellpadding="1">
             <td width="5%">
              <b>Subject:</b> 
             </td>
             <td align="left">
              <s:textfield theme="simple" name="opp.encs" size="50" value=""/>  
             <td align="right">
<%--              <b>Format:</b> --%>
             </td>

             <td align="left"> 
<%--           <s:radio label="Format" name="format" list="#{'0':'PLAIN','2':'WIKI'}" value="2"/> --%>
               &nbsp;
             </td>
             <td align="right" nowrap>
               <b>Flag:</b>
<%--               <label id="flag-label" class="flag-label">Flag</label> --%>
                 <s:select name="opp.encf" headerKey="-1" headerValue="----------" 
                          list="#{'1':'QControl'}" value="-1"/>

             </td>
             <td>&nbsp;</td>
             </tr>
            <tr>
             <td colspan="5" align="center">
              <s:textarea theme="simple" name="opp.encb" cols="128"rows="5" value=""/>
             </td>
              <td rowspan="1" width="10%" align ="center">
               <s:submit theme="simple" name="op.ecadd" value="ADD" disabled="false"
                  onclick="return YAHOO.imex.attedit.pubAttach('comment','add');"/>
               <br/>
               <i><a href="" onclick="return YAHOO.imex.attedit.pubPreview('comment','preview');">preview</a></i>
             </td>
            </tr>
            <tr>
              <td colspan="6"><hr/></td>  
            </tr>
            <tr>
             <td colspan="6">
              <div id="com-tbview"></div>
             </td>
            </tr>       
           </table>
          </s:form>
         </div>

       <!-- attachment pane -->
      
         <div id="att-pane" class="yui-hidden">

          <s:form id="attmgr" theme="simple" action="attachmgr" method="post" enctype="multipart/form-data" onsubmit="return true;">
           <table width="100%" border="0" cellspacing="2px">
            <s:hidden name="id" value="%{id}"/>
            <s:hidden name="opp.pubid" value="%{id}"/>
            <tr cellpadding="1">
             <td width="5%" nowrap>
              <b>Name</b> (<i>optional</i>)<b>:</b> 
             </td>
             <td align="left" width="25%">
              <s:textfield theme="simple" name="opp.edan" size="50" value=""/>  
             <td align="right">
              <b>Format:</b>
             </td>
             <td align="left" nowrap width="10%"> 
               <s:radio label="Format" name="opp.edat" list="#{'0':'TEXT','1':'MIF25','2':'MITAB'}" value="2"/>
               &nbsp;
             </td>
             <td align="left" width="30%" nowrap>
               <b>Flag:</b>  
<%--               <label id="flag-label" class="flag-label">Flag</label> --%>
                 <s:select name="opp.edaf" headerKey="-1" headerValue="----------" 
                          list="#{'2':'MIMIX','3':'IMEX'}" value="-1"/>

             </td>
             <td>&nbsp;</td>

             <td colspan="1" rowspan="2" width="10%" align ="center" valign="middle">
               <s:submit theme="simple" name="op.eada" value="ADD" disabled="false"
                  onclick="YAHOO.imex.attedit.nameSet( {'nf':'attmgr_opp_edan',
                                                       'ff':'attmgr_opp_edafile'}); 
                           YAHOO.imex.attedit.testing();
                           return false;"/> 
<%--                  onclick="return YAHOO.imex.attedit.pubAttach('adata','add');"/>  --%>
               </td>
             </tr>
            <tr>
             <td colspan="5" align="left">
              <s:file theme="simple" name="opp.edafile"  accept="text/*" size="80" />
             </td>
              
            </tr>
            <tr>
              <td colspan="8"><hr/></td>  
            </tr>
            <tr>
             <td colspan="8">
              <div id="adata-tbview"></div>
             </td>
            </tr>       
           </table>
          </s:form>

         </div>

         <!-- log pane -->

         <div id="log-pane" class="yui-hidden">
          <s:form id="cmtmgr" theme="simple" action="attachmgr">
           <table width="100%" border="0">
            <s:hidden name="id" value="%{id}"/>
            <s:hidden name="pub.id" value="%{id}"/>            
            <tr>
             <td colspan="4">
              <div id="history-tbview"></div>
             </td>
            </tr>       
           </table>
          </s:form>
         </div>

        </div> 
       </div> 
</div>
         </div> 
      </div>
