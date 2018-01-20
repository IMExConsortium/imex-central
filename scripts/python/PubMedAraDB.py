#!/usr/bin/python

import sys
sys.path.insert(0,"/home/lukasz/lib/pylib")

import json

import argparse

from icentral import Mif25, PubMed, AraDB

from pyArango.connection import *

ADC = AraDB.IcConnection();

ADC.open();

parser = argparse.ArgumentParser(description='PubMedAraDB.')
parser.add_argument('--pmid', '-p',  dest="pmid", type=str, nargs='+',
                   help='one or more pmids')

parser.add_argument('--mif',  dest="mif", type=str, nargs='+',
                   help='one or more mif files')

parser.add_argument('--molecule', '-m',  dest="molecule", type=bool,
                    choices=[ True, False], help='list molecules' )

parser.add_argument('--interaction', '-i',  dest="interaction", type=bool,
                    choices=[ True, False], help='list interactions' )


parser.add_argument('--experiment', '-x',  dest="experiment", type=bool,
                    choices=[ True, False], help='include experiments' )

parser.add_argument('--source', '-s',  dest="source", type=bool,
                    choices=[ True, False], help='list sources' )


parser.add_argument('--arango', '-a',  dest="arango", type=bool, 
                   choices=[ True, False], default=False, help='ArangoDB upload')

parser.add_argument('--replace', '-r',  dest="replace", type=bool, 
                   choices=[ True, False], default=False, help='replace flag (default: False)')

args = parser.parse_args()

if args.pmid:

    PMC = PubMed.Connection()    

    for pmid in args.pmid:
        print("Requested PMID: " + pmid)

        if args.arango:
            ADC.insertSource( PMC.getByPmid( pmid ), replace=args.replace )
        else:
            print PM.getByPmid( pmid )

if args.mif:

    PMC = PubMed.Connection()

    for file in args.mif:
        MIF = Mif25.Parse(file)


        if args.source:
            print "Sources:"
            #print MIF.getSourceList()

            sl = MIF.getSourceList()
            if sl:
                for s in sl:
                    print json.dumps(PMC.getByPmid( s ))


        if args.molecule:
            interactors = MIF.getInteractors()
            print "Interactors:"

            ilist=[]

            for k in interactors.keys():
                interactors[k]["_key"] = k
                ilist.append(interactors[k])

            if args.arango:
                ADC.insertInteractor( ilist, replace=args.replace )                
            
            #print json.dumps( interactors )

         
        if args.interaction:
            ints = MIF.getInteractions(experiment=args.experiment)
            print "Interactions:"
            print json.dumps(ints)
         
