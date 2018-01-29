
import sys
sys.path.insert(0,"/home/lukasz/lib/pylib")

reload(sys)
sys.setdefaultencoding('utf8')


import http.client
from lxml import etree as ET

class Connection():

    def __init__(self):

        self.server="eutils.ncbi.nlm.nih.gov"
        self.path  = "/entrez/eutils/efetch.fcgi?db=pubmed&retmode=xml&id="
        self.xsl   = "pubmed2json.xsl"
        #self.xdir = ncbi.__path__[0]
        self.cache = {}

    def getByPmid( self, pmid ):
        
        if pmid in self.cache:
            print " Retrieving from cache"
            
        else:
            print " Connecting to PubMed"

            es = http.client.HTTPSConnection(self.server)
            rq = es.request( "GET",self.path+str(pmid))
            response = es.getresponse()
            recXml = response.read().decode()
            #print(recXml)                                                                                                                                                 
            dom = ET.fromstring(recXml)

            record = {}
            record['title'] =""
            record['abstract'] = ""
            if dom.xpath('//ArticleTitle/text()'):
                record['title'] = dom.xpath('//ArticleTitle/text()')[0]
            if dom.xpath('//AbstractText/text()'):
                record['abstract'] = dom.xpath('//AbstractText/text()')[0]

            #       <ArticleIdList>
            #            <ArticleId IdType="pubmed">28931606</ArticleId>
            #            <ArticleId IdType="pii">M117.794107</ArticleId>
            #            <ArticleId IdType="doi">10.1074/jbc.M117.794107</ArticleId>
            #            <ArticleId IdType="pmc">PMC5704488</ArticleId>
            #        </ArticleIdList>

            record['pmid'] = ""
            record['doi'] = ""
            record['pmc'] = ""
            if dom.xpath( '//ArticleIdList/ArticleId[@IdType="pubmed"]/text()' ):
                record['pmid'] = dom.xpath( '//ArticleIdList/ArticleId[@IdType="pubmed"]/text()' )[0]
            if dom.xpath( '//ArticleIdList/ArticleId[@IdType="doi"]/text()' ):
                record['doi'] = dom.xpath( '//ArticleIdList/ArticleId[@IdType="doi"]/text()' )[0]
            if dom.xpath( '//ArticleIdList/ArticleId[@IdType="pmc"]/text()' ):
                record['pmc'] = dom.xpath( '//ArticleIdList/ArticleId[@IdType="pmc"]/text()' )[0]

            record['author-list'] = ""
            authLstDom = dom.xpath( '//AuthorList/Author' )

            for author in authLstDom:

                #<Author ValidYN="Y>
                # <LastName>Sigurdson</LastName>
                # <ForeName>Christina J</ForeName>
                # <Initials>CJ</Initials>
                # <AffiliationInfo>
                #  <Affiliation>UC San Diego, United States; csigurdson@ucsd.edu.</Affiliation>
                # </AffiliationInfo>
                #</Author>
                 
                if record['author-list'] != "":
                    record['author-list'] += ", "
                record['author-list'] += author.xpath( 'LastName/text()')[0] + " "+ author.xpath('Initials/text()')[0]
                                        
            #<MedlineJournalInfo>
            # <Country>United States</Country>
            # <MedlineTA>J Biol Chem</MedlineTA>
            # <NlmUniqueID>2985121R</NlmUniqueID>
            # <ISSNLinking>0021-9258</ISSNLinking>
            #</MedlineJournalInfo>

            record['journal-title'] = ""
            record['nlmid'] = ""
            record['issn'] = ""

            if dom.xpath( '//MedlineJournalInfo/MedlineTA/text()' ):
                record['journal-title'] = dom.xpath( '//MedlineJournalInfo/MedlineTA/text()' )[0]
            if dom.xpath( '//MedlineJournalInfo/NlmUniqueID/text()' ):
                record['nlmid'] = dom.xpath( '//MedlineJournalInfo/NlmUniqueID/text()' )[0]
            if dom.xpath( '//MedlineJournalInfo/ISSNLinking/text()' ):
                record['issn'] = dom.xpath( '//MedlineJournalInfo/ISSNLinking/text()' )[0]

            #<Article PubModel="Print">
            # <Journal>
            #  <ISSN IssnType="Electronic">1545-4126</ISSN>
            #  <JournalIssue CitedMedium="Internet">
            #   <Volume>40</Volume>
            #   <PubDate>
            #    <Year>2017</Year>
            #    <Month>Jul</Month>
            #    <Day>25</Day>
            #   </PubDate>
            #  </JournalIssue>
            #  <Title>Annual review of neuroscience</Title>
            #  <ISOAbbreviation>Annu. Rev. Neurosci.</ISOAbbreviation>
            # </Journal>
            # <ArticleTitle>Propagation of Tau Aggregates and Neurodegeneration.</ArticleTitle>
            # <Pagination>
            #  <MedlinePgn>189-210</MedlinePgn>
            # </Pagination>
            # ....

            record['year'] = ""
            record['volume'] = ""
            record['issue'] = ""
            record['pages'] = ""
            
            if dom.xpath( '//Journal/JournalIssue/PubDate/Year/text()' ):
               record['year']  = dom.xpath( '//Journal/JournalIssue/PubDate/Year/text()' )[0]
            if dom.xpath( '//Journal/JournalIssue/Volume/text()' ):
               record['volume']  = dom.xpath( '//Journal/JournalIssue/Volume/text()' )[0]
            if dom.xpath( '//Journal/JournalIssue/Issue/text()' ):
                record['issue'] = dom.xpath( '//Journal/JournalIssue/Issue/text()' )[0]
            if dom.xpath( '//Article/Pagination/MedlinePgn/text()' ):
                record['pages'] = dom.xpath( '//Article/Pagination/MedlinePgn/text()' )[0]

            self.cache[pmid] = record
            
        return self.cache[pmid]
