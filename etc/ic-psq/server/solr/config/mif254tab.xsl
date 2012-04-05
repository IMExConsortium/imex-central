<xsl:stylesheet version="1.0"
   xmlns:mif="http://psi.hupo.org/mi/mif"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

<!-- ======================================================================= -->
<!-- XSLT transformation  MIF 2.5.x to MITAB 2.5 record                      -->
<!-- NOTE: only unary/binary interactions are processed                      -->
<!-- ======================================================================= -->

 <xsl:output method="xml" indent="yes" encoding="UTF-8" />
 <xsl:param name="file"/>

 <xsl:template match="/mif:entrySet">
   <xsl:element name="add">
     <xsl:apply-templates mode="entry" select="mif:entry"/>
   </xsl:element>
 </xsl:template>

 <xsl:template match="*" mode="entry">
   <xsl:apply-templates mode="interaction"
        select="mif:interactionList/mif:interaction">
      <xsl:with-param name="ep" select="position()"/>
   </xsl:apply-templates>
 </xsl:template>

 <xsl:template match="*" mode="interaction">
   <xsl:param name="ep"/>
   <xsl:variable name="fep" select="concat($file,'::',$ep)"/>
   <xsl:if test="count(mif:participantList/mif:participant) &lt;= 2">
     <xsl:call-template name="evidence">
       <xsl:with-param name="fep" select='$fep'/>
       <xsl:with-param name="evl" select='mif:experimentList'/>
     </xsl:call-template>
   </xsl:if>
 </xsl:template>

 <xsl:template name="evidence" > 
   <xsl:param name="fep" />
   <xsl:param name="evl" />
   <xsl:for-each select='$evl/mif:experimentDescription'>

   <xsl:variable name="first"
       select="../../mif:participantList/mif:participant[1]"/>
     <xsl:variable name="last"
       select="../../mif:participantList/mif:participant[last()]"/>
     <xsl:variable name="evid"
       select="../../mif:experimentList/mif:experimentDescription"/>

     <xsl:element name="doc">
       <xsl:element name="field">
        <xsl:attribute name="name">pid</xsl:attribute>
         <xsl:value-of select="concat($fep,':',../../@id)"/>
       </xsl:element>
       <xsl:element name="field">
        <xsl:attribute name="name">mitab</xsl:attribute>
          <!-- IdA -->
          <xsl:apply-templates mode="xref"
            select="$first/mif:interactor/mif:xref/*[1]"/> 
          <xsl:text>&#x9;</xsl:text>

          <!-- IdB: NOTE: set to '-' if intramolecular -->
          <xsl:apply-templates mode="xref"
            select="$last/mif:interactor/mif:xref/*[1]"/>   
          <xsl:text>&#x9;</xsl:text>
   
          <!-- Alt IdA -->
          <xsl:apply-templates mode="xref-list"
             select="$first/mif:interactor"/>
          <xsl:text>&#x9;</xsl:text>

          <!-- Alt IdB:  NOTE: set to '-' if intramolecular -->
          <xsl:apply-templates mode="xref-list"
             select="$last/mif:interactor"/>
          <xsl:text>&#x9;</xsl:text>

          <!-- Alias A -->
          <xsl:text>-</xsl:text>
          <xsl:text>&#x9;</xsl:text>

          <!-- Alias B: NOTE: set to '-' if intramolecular -->
          <xsl:text>-</xsl:text>
          <xsl:text>&#x9;</xsl:text>

          <!-- Interaction detection method(s) -->
          <xsl:apply-templates mode="cvterm"
             select="mif:interactionDetectionMethod"/>       
          <xsl:text>&#x9;</xsl:text>

          <!-- First author: Doe-2005 -->
          <xsl:text>-</xsl:text>
          <xsl:text>&#x9;</xsl:text>

          <!-- Publication identifier -->
          <xsl:apply-templates mode="xref"
             select="$evid/mif:bibref/mif:xref/*"/>
          <xsl:text>&#x9;</xsl:text>

          <!-- TaxId A -->
          <xsl:apply-templates mode="taxid"
            select="$first/mif:interactor/mif:organism"/>
          <xsl:text>&#x9;</xsl:text>

          <!-- TaxId B: NOTE: set to '-' if intramolecular -->
          <xsl:apply-templates mode="taxid"
             select="$last/mif:interactor/mif:organism"/>
          <xsl:text>&#x9;</xsl:text>

          <!-- Interaction types -->
          <xsl:apply-templates mode="cvterm"
             select="../../mif:interactionType"/>
          <xsl:text>&#x9;</xsl:text>

          <!-- Source databases -->
          <xsl:text>&#x9;</xsl:text>

          <!-- Interaction identifiers -->
          <xsl:apply-templates mode="imexid"
             select="../.." />
          <xsl:text>&#x9;</xsl:text>
         
          <!-- Confidence score -->
          <xsl:text>-</xsl:text>
        </xsl:element>
      </xsl:element>
    </xsl:for-each>
  </xsl:template>


 <!-- ====================================================================== -->
 <!-- column types                                                           -->
 <!-- ====================================================================== -->

 <xsl:template match="*" mode="xref">
   <xsl:choose>
     <xsl:when test=".//@dbAc = 'MI:0486'">
       <xsl:text>uniprotkb:</xsl:text><xsl:value-of select=".//@id"/>
     </xsl:when>
     <xsl:otherwise>
       <xsl:value-of select=".//@db"/>:<xsl:value-of select=".//@id"/>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

 <xsl:template match="*" mode="xref-list">
   <xsl:for-each select="mif:xref/*">
     <xsl:variable name="a" select="current()"/>

     <xsl:variable name="ns" select="$a//@db"/>
     <xsl:variable name="db" select="$a//@dbAc"/>
     <xsl:variable name="ac" select="$a//@id"/>
     <xsl:choose> 
       <xsl:when test="position()=1">
         <xsl:choose>
           <xsl:when test="$db = 'MI:0486'">
             <xsl:value-of select="concat('uniprotkb:',$ac)"/>
           </xsl:when>
           <xsl:otherwise>
             <xsl:value-of select="concat($ns,':',$ac)"/>
           </xsl:otherwise>
          </xsl:choose> 
       </xsl:when>
       <xsl:otherwise>
         <xsl:choose>
           <xsl:when test="$db = 'MI:0486'">
             <xsl:value-of select="concat('|uniprotkb:',$ac)"/>
           </xsl:when>
           <xsl:otherwise>
             <xsl:value-of select="concat('|',$ns,':',$ac)"/>
           </xsl:otherwise>
         </xsl:choose>
       </xsl:otherwise>
     </xsl:choose>
   </xsl:for-each>
 </xsl:template>

 <xsl:template match="*" mode="cvterm">
   <xsl:value-of select="./mif:xref/mif:primaryRef/@db"/>
   <xsl:text>:"</xsl:text>
   <xsl:value-of select="./mif:xref/mif:primaryRef/@id"/>
   <xsl:text>"(</xsl:text>
   <xsl:value-of select="./mif:names/mif:fullName"/>
   <xsl:text>)</xsl:text>
 </xsl:template>

 <xsl:template match="*" mode="cvterm-list">
 </xsl:template>

 <xsl:template match="*" mode="imexid">
    <xsl:text>imex:</xsl:text><xsl:value-of select="./@imexId" />
 </xsl:template>

 <xsl:template match="*" mode="taxid">
  <xsl:text>taxid:</xsl:text>
  <xsl:value-of select="./@ncbiTaxId"/>
  <xsl:text>(</xsl:text>
    <xsl:value-of select="./mif:names/mif:fullName"/>
  <xsl:text>)</xsl:text>
 </xsl:template>

</xsl:stylesheet>
