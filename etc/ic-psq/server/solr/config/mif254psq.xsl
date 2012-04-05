<xsl:stylesheet version="1.0"
   xmlns:mif="http://psi.hupo.org/mi/mif"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

<!-- ======================================================================= -->
<!-- XSLT transformation  MIF 2.5.x to MITAB 2.5-like PSICQUIC index         -->
<!-- NOTE: only unary/binary interactions are processed                      -->
<!-- ======================================================================= -->

 <xsl:output method="xml" indent="yes" encoding="UTF-8" />
 <xsl:param name="file"/>

 <xsl:template match="/mif:entrySet">
  <xsl:element name="add">
   <xsl:apply-templates mode="entry" 
     select="mif:entry"/>
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

       <!-- idA/id -->
 
       <xsl:apply-templates mode="xref" 
          select="$first/mif:interactor/mif:xref/*[1]">
          <xsl:with-param name="fid">ida</xsl:with-param>
       </xsl:apply-templates>
       <xsl:apply-templates mode="xref" 
          select="$first/mif:interactor/mif:xref/*">
          <xsl:with-param name="fid">id</xsl:with-param>
       </xsl:apply-templates>
    
       <!-- idB/id -->

       <xsl:apply-templates mode="xref" 
          select="$last/mif:interactor/mif:xref/*[1]">
          <xsl:with-param name="fid">idb</xsl:with-param>
       </xsl:apply-templates>
       <xsl:apply-templates mode="xref" 
          select="$last/mif:interactor/mif:xref/*">
          <xsl:with-param name="fid">id</xsl:with-param>
       </xsl:apply-templates>
    
       <!-- alias -->

       <!-- identifiers -->

       <!-- pubauth -->

       <!-- pubid -->

       <xsl:apply-templates mode="pubid" 
          select="$evid/mif:bibref/mif:xref/*">
          <xsl:with-param name="fid">pubid</xsl:with-param>
       </xsl:apply-templates>

       <!-- taxidA -->

       <xsl:apply-templates mode="taxid" 
          select="$first/mif:interactor/mif:organism">
          <xsl:with-param name="fid">taxidA</xsl:with-param>
       </xsl:apply-templates>
     
       <!-- taxidB -->

       <xsl:apply-templates mode="taxid" 
         select="$last/mif:interactor/mif:organism">
         <xsl:with-param name="fid">taxidB</xsl:with-param>
       </xsl:apply-templates>

       <!-- species -->

       <xsl:apply-templates mode="species" 
         select="$first/mif:interactor/mif:organism">
         <xsl:with-param name="fid">species</xsl:with-param>
       </xsl:apply-templates>

       <xsl:apply-templates mode="species" 
         select="$last/mif:interactor/mif:organism">
         <xsl:with-param name="fid">species</xsl:with-param>
       </xsl:apply-templates>

       <!-- type --> 

       <xsl:apply-templates mode="cvterm" 
         select="../../mif:interactionType">
         <xsl:with-param name="fid">type</xsl:with-param>
       </xsl:apply-templates>

       <!-- detmethod --> 

       <xsl:apply-templates mode="cvterm"
         select="mif:interactionDetectionMethod">
         <xsl:with-param name="fid">detmethod</xsl:with-param>
       </xsl:apply-templates>

       <!-- interaction_id -->
       <xsl:element name="field">
         <xsl:attribute name="name">interaction_id</xsl:attribute>
         <xsl:value-of select="../../@imexId"/>
       </xsl:element>
     </xsl:element>
   </xsl:for-each>
 </xsl:template>

<!-- ======================================================================= -->
<!-- field types/modes                                                       -->
<!-- ======================================================================= -->

 <xsl:template match="*" mode="id">
   <xsl:param name="fid"/>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:value-of select=".//@id"/>
   </xsl:element>
 </xsl:template>

 <xsl:template match="*" mode="xref">
   <xsl:param name="fid"/>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:value-of select=".//@id"/>
   </xsl:element>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:choose>
       <xsl:when test=".//@dbAc = 'MI:0486'">
         <xsl:text>uniprotkb:</xsl:text><xsl:value-of select=".//@id"/>
       </xsl:when>
       <xsl:otherwise>
         <xsl:value-of select=".//@db"/>:<xsl:value-of select=".//@id"/>
      </xsl:otherwise>
    </xsl:choose>
   </xsl:element>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:choose>
       <xsl:when test=".//@dbAc = 'MI:0486'">
         <xsl:text>uniprotkb:</xsl:text>
       </xsl:when>
       <xsl:otherwise>
         <xsl:value-of select=".//@db"/><xsl:text>:</xsl:text>
       </xsl:otherwise>
     </xsl:choose>
   </xsl:element>
 </xsl:template>

 <xsl:template match="*" mode="pubid">
   <xsl:param name="fid"/>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:value-of select=".//@db"/>:<xsl:value-of select=".//@id"/>
   </xsl:element>
 </xsl:template>

 <xsl:template match="*" mode="taxid">
   <xsl:param name="fid"/>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:text>taxid:</xsl:text><xsl:value-of select="./@ncbiTaxId"/>
   </xsl:element>
 </xsl:template>

 <xsl:template match="*" mode="species">
   <xsl:param name="fid"/>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:value-of select="./mif:names/mif:fullName"/>
   </xsl:element>
 </xsl:template>

 <xsl:template match="*" mode="cvterm">
   <xsl:param name="fid"/>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:value-of select="./mif:names/mif:fullName"/>
   </xsl:element>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:value-of select="./mif:xref/mif:primaryRef/@db"/>
     <xsl:text>:</xsl:text>
     <xsl:value-of select="./mif:xref/mif:primaryRef/@id"/>
   </xsl:element>
   <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:value-of select="./mif:xref/mif:primaryRef/@id"/>
   </xsl:element>
    <xsl:element name="field">
     <xsl:attribute name="name"><xsl:value-of select="$fid"/></xsl:attribute>
     <xsl:value-of select="./mif:xref/mif:primaryRef/@db"/>
     <xsl:text>:</xsl:text>
     <xsl:value-of select="./mif:xref/mif:primaryRef/@id"/>
     <xsl:text>(</xsl:text>
     <xsl:value-of select="./mif:names/mif:fullName"/>
     <xsl:text>)</xsl:text>
   </xsl:element>
 </xsl:template>

</xsl:stylesheet>
