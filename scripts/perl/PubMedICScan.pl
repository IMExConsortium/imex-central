#!/usr/bin/perl

$|++;

use strict;
use lib "/raid1mnt/users/lukasz/git/imex-central/scripts/perl/mod";

use Getopt::Long;
use LWP::UserAgent;
use LWP::Simple;
use Scalar::Util;

use ICentral::SOAP;
use ICentral::RECORD;
use ICentral::FAULT;


use lib "mod";
#use SQ::DBA;

use Mail::Sendmail;

use SOAP::Lite;

my $url = "";
my $jlf = "";
my $uid = "";
my $pass = "";
my $days = 10;

GetOptions ('url=s' => \$url, 
	    'user=s' => \$uid, 
	    'pass=s' => \$pass,
	    'jlist=s' => \$jlf,
	    'days=s' => \$days,

    );

if($jlf eq ""){
    die "ERROR: No journal list.";
}

if( $url eq "" || $uid eq "" || $pass eq ""){
    die "ERROR: Cannot connect to icentral.";
}

my $IC = new ICentral::SOAP( URL=>$url, UID=>$uid, PASS=>$pass );

open JLF, "<".$jlf;

while(my $ln=<JLF>){
    $ln=~ s/(\s+\#.+)?\n$//;

    print "NLMID: ",$ln,"\n";
    
    #my $q='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi';
    my $q='http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi';
    $q.='?cmd=search&db=pubmed&orig_db=pubmed'.
	'&term='.$ln.'[TA]&retmax=2500'.
	'&reldate='.$days.'&doptcmdl=brief';
    
    #print $q,"\n";
    my $request=&LWP::Simple::get($q);
    #print "XXX:",$request;
    my @rql=split(/\n/,$request);
	
    my $nbt=0;
	
    for(my $i=0;$i<@rql;$i++){
	if($rql[$i]=~/<Id>(\d+)<\/Id>/){
	    my $pmid=$1;
	    print "PMID: $pmid\t";

	    my $res = $IC->getPublicationById( NS => "pmid", AC => $pmid);
	    if(Scalar::Util::blessed($res) =~/RECORD/){
		print "record already exists\n";
	    } else {
		my $ rec = $IC->createPublicationById( NS=>"pmid",
						       AC=>$pmid );
		if(Scalar::Util::blessed($rec) =~/RECORD/){
		    print "record created\n";
		
		    # add comment
		    $IC->addAttachment( NS=>"pmid",
					AC=>$pmid,
					TYPE =>'comment',
					SUBJECT =>'Record created by PMDaemon');
		}				
	    }
	}
    }

    # if new records send notification ?

}
