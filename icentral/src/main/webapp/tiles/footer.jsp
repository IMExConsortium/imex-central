<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div id="footer">
 <table width="100%" cellpadding="0" cellspacing="0">
  <tr>
   <td>
    <table width="100%" class="footer" cellpadding="0" cellspacing="0">
     <s:if test="#session['USER_ROLE'].administrator != null or
                 #session['USER_ROLE'].editor != null" >
      <t:insertAttribute name="edit" ignore="true"/>
     </s:if>
     <tr>
      <td class="copyright2" nowrap>
       Copyright 2008-2014<br/>IMEx Consortium
      </td>
      <td class="cite">
        <b>Cite IMEx:</b> Orchard S, Kerrien S, Abbani S, <it>et al</i>. 
        Protein interaction data curation: the International Molecular Exchange (IMEx) consortium.;
        <i>Nat Methods.</i> <b>9</b>:626 (2012). 
        [<a href="http://www.ncbi.nlm.nih.gov/pubmed/22453911">Pubmed</a>]
        [<a href="http://www.ncbi.nlm.nih.gov/pmc/articles/PMC3703241/">Article</a>] 
      </td> 
      <td width="5%" class="copyright3" align="center" nowrap>
       <A HREF="http://www.imexconsortium.org">Imex Consortium</A>.
      </td>
     </tr>
    </table>
   </td>
  </tr>
  <tr>
   <td align="center" width="100%">
    <table width="98%" cellspacing="0">
     <tr>
      <td align="left" nowrap>
       <font size="-5">Ver: ${icentral.version} (${icentral.bld})</font>
      </td>
      <td align="right" nowrap>
       <font size="-5">
         <i>Code by:<A HREF="mailto:lukasz@mbi.ucla.edu">LS</A>,
                    <A HREF="mailto:zplat@mbi.ucla.edu">ZP</A>.</i>
         <i>Design by:<A HREF="mailto:rebecca@mbi.ucla.edu">RN</A>.</i>
       </font>
      </td>
     </tr>
    </table>
   </td>
  </tr>
 </table>
</div>
