
import sys
sys.path.insert(0,"/home/lukasz/lib/pylib")
reload(sys)

sys.setdefaultencoding('utf8')
from lxml import etree as ET

class Parse():

    def __init__(self, file):
        self.file = file

        self.mifns="http://psi.hupo.org/mi/mif"
        self.dom = ET.parse(self.file)
 
        #print ET.tostring(self.dom)

    def getSourceList(self):
        
        sourcePMID = []

        edl = self.dom.xpath('//m:experimentDescription/m:bibref/m:xref/m:primaryRef[@db="pubmed"]/@id',
                             namespaces= {'m': self.mifns})

        # unique pmid

        edu = []

        if edl:
            for ed in edl:
                if ed not in sourcePMID:
                    sourcePMID.append(ed)

        return sourcePMID

    def getInteractors(self, parent=None):

        #<interactor id="609">
        # <names>
        #  <shortLabel>HSC20</shortLabel>
        #  <fullName>Iron-sulfur cluster co-chaperone protein HscB, mitochondrial</fullName>
        # </names>
        #
        # <xref>
        #  <primaryRef db="uniprot knowledge base" dbAc="MI:0486" id="Q8IWL3" refType="identity" refTypeAc="MI:0356" version="129"/>
        #  <secondaryRef db="refseq" dbAc="MI:0481" id="NP_741999" refType="identity" refTypeAc="MI:0356" version="3"/>
        #  <secondaryRef db="dip" dbAc="MI:0465" id="DIP-46570N" refType="identity" refTypeAc="MI:0356"/>
        # </xref>
        #
        # <interactorType>
        #  <names>
        #   <fullName>protein</fullName>
        #  </names>
        #
        #  <xref>
        #   <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0326" refType="identity" refTypeAc="MI:0356"/>
        #  </xref>
        # </interactorType>
        #
        # <organism ncbiTaxId="9606">
        #  <names>
        #   <fullName>Homo sapiens</fullName>
        #  </names>
        # </organism>
        #</interactor>

        interactor = {}
       
        if parent:
            inl = parent.xpath('.//m:interactor',
                                namespaces= {'m': self.mifns})
        else:
            inl = self.dom.xpath('//m:interactor',
                                 namespaces= {'m': self.mifns})
        
        for i in inl:

            # generate unique key

            keyNs = i.xpath('m:xref/m:primaryRef/@dbAc'
                             ,namespaces= {'m': self.mifns})

            keyAc = i.xpath('m:xref/m:primaryRef/@id'
                          ,namespaces= {'m': self.mifns})

            keyVer = i.xpath('m:xref/m:primaryRef/@version'
                             ,namespaces= {'m': self.mifns})

            if keyNs:
                key = keyNs[0]
            if keyAc:
                if key != "":
                    key += "_"
                key += keyAc[0]
            if keyVer:
                if key != "":
                    key += "_"
                key += keyVer[0]

            #print key
            if key not in interactor:

                interactor[key]={}

                # interactor type
                interactor[key]["type"]= [] # {"name":"","cv":"", "id":""}

                it = i.xpath('m:interactorType'
                             ,namespaces= {'m': self.mifns})
                                           
                interactor[key]["type"].append(self.cvParse( it[0] ))

                # primary identifier

                interactor[key]["accession"]={}
                
                #pxref = i.xpath('m:xref/m:primaryRef'
                #                ,namespaces= {'m': self.mifns})
                
                xref = i.xpath('m:xref'
                               ,namespaces= {'m': self.mifns})
                
                #if pxref:
                #    pdb = pxref[0].xpath('@db'
                #                         ,namespaces= {'m': self.mifns})
                #    pac =  pxref[0].xpath('@id'
                #                          ,namespaces= {'m': self.mifns})

                #    interactor[key]["accession"]["db"] = pdb[0]
                #    interactor[key]["accession"]["ac"] = pac[0]
                    
                pac = self.primaryAcParse(xref)

                if pac:
                    interactor[key]["accession"] = pac

                # secondary identifier(s)
                interactor[key]["xref"]=[]

                #sxref = i.xpath('m:xref/m:secondaryRef'
                #                ,namespaces= {'m': self.mifns})

                #for sx in sxref:
                   
                #    sdb = sx.xpath('@db'
                #                   ,namespaces= {'m': self.mifns})
                #    sac =  sx.xpath('@id'
                #                    ,namespaces= {'m': self.mifns})

                #    interactor[key]["secondaryAc"].append({"db":sdb[0], "acc":sac[0]})
                

                sac = self.secondaryAcParse(xref)

                if sac:
                    interactor[key]["xref"] = sac

    
                # name
                interactor[key]["name"]=""
                
                name  = i.xpath('m:names/m:shortLabel/text()'
                                ,namespaces= {'m': self.mifns})
                if name:
                    interactor[key]["name"] = name[0]
                    
                # description
                interactor[key]["description"]=""
                
                desc =  i.xpath('m:names/m:fullName/text()'
                                ,namespaces= {'m': self.mifns})
                if desc:
                    interactor[key]["description"] = desc[0]
                    
                # organism
                #interactor[key]["organism"]=[{"name":"","cv":"taxid", "id":""}]

                #oname = i.xpath('m:organism/m:names/m:fullName/text()'
                #                ,namespaces= {'m': self.mifns})
                #oacc = i.xpath('m:organism/@ncbiTaxId'
                #               ,namespaces= {'m': self.mifns})

                #interactor[key]["organism"][0]["name"] = oname[0]
                #interactor[key]["organism"][0]["id"] = oacc[0]                
                ol = i.xpath('m:organism'
                             ,namespaces= {'m': self.mifns})

                if ol:
                    interactor[key]["organism"] = []
                    for o in ol:
                        interactor[key]["organism"].append(self.organismParse(o))

        return  interactor



    def organismParse(self, element):
        
        org = { "cv":"taxid" }
        oname = element.xpath('m:names/m:fullName/text()'
                              ,namespaces= {'m': self.mifns})
        oacc = element.xpath('@ncbiTaxId'
                       ,namespaces= {'m': self.mifns})

        org["name"] = oname[0]
        org["id"] = oacc[0]
        
        return org



    def getInteractions(self, experiment=False):
        interactions = []

        itl = self.dom.xpath('//m:interaction',
                             namespaces= {'m': self.mifns})
        
        for i in itl:
            
            ii = {"participant":[]}


            if experiment:                
                ii["experiment"] = self.parseExperiment( i )

        
            part= i.xpath('m:participantList/m:participant',
                             namespaces= {'m': self.mifns})

            for p in part:
                
                partd = {"interactor": {} }
                #print "---"
                #print ET.tostring(p)

                # name
                
                names  = p.xpath('m:names'
                                 ,namespaces= {'m': self.mifns})
               
                if names:
                    partd["name"] = self.nameParse( names[0] )
                    partd["description"] = self.descriptionParse( names[0] )

                else:
                    partd["name"] = ""
                    partd["description"] = ""


                if partd["name"] == "":
                    names  = p.xpath('m:interactor/m:names'
                                     ,namespaces= {'m': self.mifns})
                    if names:
                        partd["name"] = self.nameParse( names[0] )
                    else:
                        partd["name"] = ""

                if partd["description"] == "":
                    names  = p.xpath('m:interactor/m:names'
                                     ,namespaces= {'m': self.mifns})
                    if names:
                        partd["description"] = self.descriptionParse( names[0] )
                    else:
                        partd["description"] = ""


                # hostOrganism

                hol = p.xpath('m:hostOrganismList/m:hostOrganism'
                                     ,namespaces= {'m': self.mifns})

                if hol:
                    partd["host"] = []  
                    for h in hol:
                        partd["host"].append(self.organismParse(h))

                # identyfication method


                #<participantIdentificationMethodList>
                #  <participantIdentificationMethod>
                #   <names>
                #    <fullName>western blot</fullName>
                #   </names>
                #
                #   <xref>
                #    <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0113" refType="identity" refTypeAc="MI:0356"/>
                #   </xref>
                #  </participantIdentificationMethod>
                # </participantIdentificationMethodList>
     

                idml = p.xpath('m:participantIdentificationMethodList/m:participantIdentificationMethod'
                               ,namespaces= {'m': self.mifns})
                if idml:
                    partd["identificationMethod"] = []
                    for idm in idml:
                        partd["identificationMethod"].append(self.cvParse(idm))


                # bioFeature
                
                bfe = p.xpath('m:featureList/m:feature'
                              ,namespaces= {'m': self.mifns})
                if bfe:
                    if "feature" not in partd:
                        partd["feature"] = []
                    #partd["feature"]["biological"] = self.cvParse( bre[0] )
                
                    #"linkedFeatures": [
                    #    {
                    #        "participantId": "P47170",
                    #        "featureType": "binding region",
                    #        "featureTypeMI": "MI:0117",
                    #        "featureTypeDefinition": null,
                    #        "ranges": [
                    #            "?-?"
                    #        ]
                    #    }
                    #],
                    #"otherFeatures": [ ]
                    
                    for bf in bfe:
                    
                        feature = {}

                        bft = bf.xpath('m:featureType'
                                       ,namespaces= {'m': self.mifns})
                        
                        if bft:
                            if "type" not in feature:
                                feature["type"]= []

                            feature["type"].append( self.cvParse( bft[0] )) 


                        # skip method for predetermined ? 

                        bfd = bf.xpath('m:featureDetectionMethod[m:xref/m:primaryRef/@id != "MI:0823"]'
                                       ,namespaces= {'m': self.mifns})

                        if bfd:
                            feature["detectionMethod"]= self.cvParse( bfd[0] )
                            
                            
                        bfr = bf.xpath('m:featureRangeList/m:featureRange'
                                       ,namespaces= {'m': self.mifns})
                        
                        if bfr:
                            feature["range"]= self.rangeParse( bfr )
                                                
                        partd["feature"].append(feature)


                    #print partd["feature"]

                    #<featureList>
                    #<feature id="5">
                    #  <names>
                    #   <fullName>29-235 binding region</fullName>
                    #  </names>
                    
                    #  <featureType>
                    #   <names>
                    #    <fullName>binding-associated region</fullName>
                    #   </names>
                    
                    #   <xref>
                    #    <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0117" refType="identity" refTypeAc="MI:0356"/>
                    #   </xref>
                    #  </featureType>
                    
                    #  <featureDetectionMethod>
                    #   <names>
                    #    <fullName>predetermined feature</fullName>
                    #   </names>
                    
                    #   <xref>
                    #    <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0823" refType="identity" refTypeAc="MI:0356"/>
                    #   </xref>
                    #  </featureDetectionMethod>
                    
                    #  <featureRangeList>
                    #   <featureRange>
                    #    <startStatus>
                    #     <names>
                    #      <fullName>certain sequence position</fullName>
                    #     </names>
                    
                    #     <xref>
                    #      <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0335" refType="identity" refTypeAc="MI:0356"/>
                    #     </xref>
                    #    </startStatus>
                    
                    #    <begin position="29"/>
                    
                    #    <endStatus>
                    #     <names>
                    #      <fullName>certain sequence position</fullName>
                    #     </names>
                    
                    #     <xref>
                    #      <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0335" refType="identity" refTypeAc="MI:0356"/>
                    #     </xref>
                    #    </endStatus>
                
                    #    <end position="235"/>
                    #   </featureRange>
                    #  </featureRangeList>
                



                # bioRole
                
                bre = p.xpath('m:biologicalRole'
                              ,namespaces= {'m': self.mifns})

                if bre:
                    if "role" not in partd:
                        partd["role"] = {}
                    partd["role"]["biological"] = self.cvParse( bre[0] )

                # expRole

                erl =  p.xpath('m:experimentalRoleList/m:experimentalRole'
                               ,namespaces= {'m': self.mifns})
  
                #print "partd", partd

                if erl:
                    if "role" not in partd:
                        partd["role"] = {}
                    partd["role"]["experimental"] = []
                    for er in erl:
                        partd["role"]["experimental"].append( self.cvParse( er ) )

                # expFeature

                # linkedFeature

                #interactor
                intd = self.getInteractors(parent=p)

                #print "\nintd:", intd, "\n\n"

                partd["interactor"]=list(intd.values())[0]


                ii["participant"].append(partd)

                #"stochiometry": "minValue: 1, maxValue: 1",
                #"bioRole": "unspecified role",
                #"bioRoleMI": "MI:0499",
                #"bioRoleDefinition": null,
                #"interactorType": "protein",
                #"interactorTypeMI": "MI:0326",
                #"interactorTypeDefinition": null,
                #"linkedFeatures": [
                #    {
                #        "participantId": "P47170",
                #        "featureType": "binding region",
                #        "featureTypeMI": "MI:0117",
                #        "featureTypeDefinition": null,
                #        "ranges": [
                #            "?-?"
                #        ]
                #    }
                #],
                #"otherFeatures": [ ]
            

            # references

            xref = i.xpath('m:xref'
                           ,namespaces= {'m': self.mifns})

            # primaryRef == accession

            ii["accession"]={}
            pac = self.primaryAcParse(xref,reftype="identity")
            if pac:                
                ii["accession"] = pac
            else:                
                sac = self.secondaryAcParse(xref, reftype="identity")
                if sac:
                    ii["accession"] = sac[0]

            # secondary identifier(s) == xrefs
            
            ii["xref"]=[]
            xref = self.secondaryAcParse(xref)
            if xref:
                ii["xref"] = xref
        
            # interaction type
            
            ii["type"]= [] # {"name":"","cv":"", "id":""}       

            if i.xpath('m:interactionType'
                       ,namespaces= {'m': self.mifns}):


                ii["type"].append( self.cvParse(i.xpath('m:interactionType'
                                                  ,namespaces= {'m': self.mifns})[0]))

                                                    
            # hostOrganism (experiment ?)
            
            
            #print ii
            interactions.append(ii)
        return interactions

        
    def cvParse(self, element):

        cvd = {"name":"","cv":"", "id":""}
            
        name = element.xpath('m:names/m:fullName/text()'
                             ,namespaces= {'m': self.mifns})

        label = element.xpath('m:names/m:shortLabel/text()'
                             ,namespaces= {'m': self.mifns})

        cv = element.xpath('m:xref/m:primaryRef/@db'
                           ,namespaces= {'m': self.mifns})

        acc = element.xpath('m:xref/m:primaryRef/@id'
                            ,namespaces= {'m': self.mifns})

        if label:
            cvd["name"] = label[0]
        else:
            cvd["name"] = name[0]

        cvd["cv"] = cv[0]
        cvd["id"] = acc[0]
        
        return cvd


    def primaryAcParse(self, xref, reftype=None):

        pad={}
        
        if reftype:
            pxref = xref[0].xpath('m:primaryRef[@refType="'+reftype+'"]'
                                  ,namespaces= {'m': self.mifns})
        else:
            pxref = xref[0].xpath('m:primaryRef'
                                  ,namespaces= {'m': self.mifns})

        if pxref:
            pdb = pxref[0].xpath('@db'
                                 ,namespaces= {'m': self.mifns})
            pac =  pxref[0].xpath('@id'
                                  ,namespaces= {'m': self.mifns})

            pad["db"] = pdb[0]
            pad["id"] = pac[0]
             
        return pad

    def secondaryAcParse(self, xref, reftype=None):

        sal=[]
        if reftype:
            sxref = xref[0].xpath('m:secondaryRef[@refType="'+reftype+'"]'
                                  ,namespaces= {'m': self.mifns})
        else:
            sxref = xref[0].xpath('m:secondaryRef'
                                  ,namespaces= {'m': self.mifns})

        for sx in sxref:
                   
            sdb = sx.xpath('@db'
                           ,namespaces= {'m': self.mifns})
            sac =  sx.xpath('@id'
                            ,namespaces= {'m': self.mifns})

            sal.append({"db":sdb[0], "id":sac[0]})
                    
        return sal


    def nameParse(self, element):                        
        nl  = element.xpath('m:shortLabel/text()'
                            ,namespaces= {'m': self.mifns})
        if nl:
            name = nl[0]
        else:
            name = ""
        return name
            
    def descriptionParse(self, element):
        
        dl =  element.xpath('m:fullName/text()'
                            ,namespaces= {'m': self.mifns})
        if dl:
            description = dl[0]
        else:
            description = ""
        
        return description
        
    def rangeParse(self, element):
        
        rngl = []

        #   <featureRange>
        #    <startStatus>
        #     <names>
        #      <fullName>certain sequence position</fullName>
        #     </names>
        
        #     <xref>
        #      <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0335" refType="identity" refTypeAc="MI:0356"/>
        #     </xref>
        #    </startStatus>
        
        #    <begin position="29"/>
        
        #    <endStatus>
        #     <names>
        #      <fullName>certain sequence position</fullName>
        #     </names>
        
        #     <xref>
        #      <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0335" refType="identity" refTypeAc="MI:0356"/>
        #     </xref>
        #    </endStatus>
        
        #    <end position="235"/>
        #   </featureRange>
        
        
        for r in element:

            rng = {}

            startId = r.xpath('m:startStatus/m:xref/m:primaryRef/@id'
                              ,namespaces= {'m': self.mifns})
            
            startId = r.xpath('m:startStatus/m:xref/m:primaryRef/@id'
                              ,namespaces= {'m': self.mifns})
            
            start = r.xpath('m:begin/@position'
                              ,namespaces= {'m': self.mifns})


            endId = r.xpath('m:endStatus/m:xref/m:primaryRef/@id'
                            ,namespaces= {'m': self.mifns})
            
            
            end = r.xpath('m:end/@position'
                          ,namespaces= {'m': self.mifns})
        

            # start position:


            if startId[0] in ["MI:0335", "MI:0336","MI:0337", "MI:0338"]: 
                # exact, less then, more than or range
                rng["start"]= start[0]

            if startId[0] =="MI:0334":     # C-terminus
                rng["start"]= "C"

            if startId[0] =="MI:0340":     # N-terminus
                rng["start"]= "N"

            if startId[0] =="MI:0339":     # unknown/unspecified
                rng["start"]= "?"


            # end position:

            
            if endId[0] in ["MI:0335", "MI:0336","MI:0337", "MI:0338"]:
                # exact, less then, more than or range
                rng["end"]= end[0]

            if endId[0] =="MI:0334":       # C-terminus
                rng["end"]= "C"

            if endId[0] =="MI:0340":       # N-terminus
                rng["end"]= "N"

            if endId[0] =="MI:0339":       # unknown/unspecified
                rng["end"]= "?"
            
            rngl.append(rng)
            
        return rngl
        


    def parseExperiment(self, interaction):
      
        ee = interaction.xpath('m:experimentList/m:experimentDescription',
                               namespaces= {'m': self.mifns})
        xl= []
        
        for e in ee:
            x = {}
            xl.append(x)

            #<experimentDescription id="144">
            # <bibref>
            #  <xref>
            #   <primaryRef db="pubmed" dbAc="MI:0446" id="24606901" refType="identity" refTypeAc="MI:0356"/>
            #  </xref>
            # </bibref>
  
            
            brl = e.xpath('m:bibref/m:xref',
                          namespaces= {'m': self.mifns})

            if brl:
                x["source"] = self.primaryAcParse(brl,reftype="identity")


            # <xref> 
            #  <primaryRef db="imex" dbAc="MI:0670" id="IM-21965" refType="imex-primary" refTypeAc="MI:0662"/>
            #  <secondaryRef db="dip" dbAc="MI:0465" id="DIP-269599X" refType="identity" refTypeAc="MI:0356"/>
            # </xref>

            xrl = e.xpath('m:xref',
                          namespaces= {'m': self.mifns})

            if xrl:
                pr = []

                pac = self.primaryAcParse(xrl,reftype="identity")
                if pac:
                    pr.append(pac)
                pr = pr + self.secondaryAcParse(xrl,reftype="identity")
        
                if pr:
                    x["xref"]=pr

            # <hostOrganismList>
            #  <hostOrganism ncbiTaxId="9606">
            #   <names>
            #    <fullName>Homo sapiens</fullName>
            #   </names>
            #  </hostOrganism>
            # </hostOrganismList>

            hl = e.xpath('m:hostOrganismList/m:hostOrganism',
                           namespaces= {'m': self.mifns})
            
            if hl:
                x["host"]=[]
                for h in hl:
                    x["host"].append(self.organismParse(h))

            # <interactionDetectionMethod>
            #  <names>
            #   <fullName>anti bait coimmunoprecipitation</fullName>
            #  </names>
            
            #  <xref>
            #   <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0006" refType="identity" refTypeAc="MI:0356"/>
            #  </xref>
            # </interactionDetectionMethod>

            #x["detectionMethod"] = []
                        
            dml = e.xpath('m:interactionDetectionMethod',
                         namespaces= {'m': self.mifns})
            if dml:
                x["detectionMethod"] = []

                for dm in dml:
                    x["detectionMethod"].append( self.cvParse(dm) )

            #</experimentDescription>
            
        #print xl
        return xl
