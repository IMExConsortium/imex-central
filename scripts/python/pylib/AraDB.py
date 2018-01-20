import sys
sys.path.insert(0,"/home/lukasz/lib/pylib")

from pyArango.connection import *

import json

class IcConnection():
    def __init__(self):

        self.url = "http://crick.mbi.ucla.edu:8529"
        self.user = "icentral"
        self.passwd = "444ls444"
        
    def open( self ):

        print "Connecting to icentral ArangoDB"

        self.araCon = Connection( arangoURL=self.url, username=self.user, password=self.passwd )

        if self.araCon.hasDatabase( "icentral_test" ):
            self.araDb = self.araCon[ "icentral_test" ]
            if self.araDb.hasCollection( "source" ):
                self.araSrc = self.araDb[ "source" ]
            else:
                self.araSrc = self.araDb.createCollection( name="source" )                

            if self.araDb.hasCollection( "molecule" ):
                self.araMol = self.araDb[ "molecule" ]
            else:
                self.araMol = self.araDb.createCollection( name="molecule" )                

    def close( self ):
        # not sure if neeed ?
        pass

    def insertSource( self, source, replace = False):
        print source
        
        if '_key' in source:
            nskey = source["_key"]
            #del source["_key"]
        else:
            nskey = "pmid:" + source[ 'pmid' ]


        try:
            oldDoc = self.araSrc[ nskey ]
            
            if replace:
                self.updateSource( source )
            else:
                print "insertSource: skipping - record exists!!!"
        except:
            doc = self.araSrc.createDocument(source)

            # possible namespace precedence ?
            #   pmid > doi > pmc > jid > icid > cid 
            #  
            #    icid - icentral id (IC-nnnn)
            #    cid  - curator-assigned id

            nskey = "pmid:"+source['pmid']
    
            doc._key = nskey
            doc.save()
            print doc

    def updateSource( self, source ):
        
        if '_key' in source:
            nskey = source["_key"]
            #del source["_key"]
        else:
            nskey = "pmid:" + source[ 'pmid' ]
            

        try:
            oldDoc = self.araSrc[ nskey ]
            print oldDoc

            for i in source.keys():
                oldDoc[i]=source[i]
            oldDoc.save()

        except:
            print "source document not found"

    def getSource(self,  key, ns="pmid"):
        nskey = ns + ":" + str(key)
        doc = self.araSrc[ nskey ]
        print doc
        return doc
        
    def querySource( self, aql ):
        pass
    
    def insertProtein( self, protein ):
        return insertInteractor(self, protein)

    def updateProtein( self, protein ):
        return updateInteractor(self, protein)

    def insertInteractor( self, interactor,replace = False):

        # insert one or more interactors (from a list)

        if type(interactor) is list:
            for ci in interactor:
                self.__insertInteractor(ci, replace)
        else:
             self.__insertInteractor(interactor, replace)
                

    def updateInteractor( self, interactor ):

        # update one or more interactors (from a list)

        if type( interactor ) is list:
            for ci in interactor:
                self.__updateInteractor( ci )
        else:
            self.__updateInteractor( interactor )
                
    
    def __insertInteractor( self, interactor,replace = False):

        # insert exactly one interactor
             
        try:
            key = interactor["_key"]
    
        except:
            print json.dumps( interactor )
            print "interactor key missing!!!"
            return
            
        print "INT:",key," ::: ",json.dumps(interactor),"\n"
        
        try:
            oldDoc = self.araMol[ key ]

            if replace:
                self.updateInteractor( interactor )
            else:
                print "insertInteractor: skipping - record exists!!!"
            
        except:
            print "new interactor"

            doc = self.araMol.createDocument( interactor )        
            doc.save()
            print doc


    def __updateInteractor( self, interactor ):
    
        try:
            key = interactor["_key"]
            del interactor["_key"]
            
        except:
            print json.dumps( interactor )
            print "interactor key missing!!!"
            return

        try:
            oldDoc = self.araMol[ key ]

            for i in interactor.keys():
                oldDoc[i]=interactor[i]
                oldDoc.save()

        except:
            print "source document not found"
