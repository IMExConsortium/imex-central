#!/usr/bin/perl

$|++;
use strict;

#use lib "mod";
#use SQ::DBA;

use LWP::UserAgent;
use LWP::Simple;
use Mail::Sendmail;

use SOAP::Lite;

my $URL= "http://%UPASS%\@10.1.200.206:8080/ws";
my $ver="-v20";


my $URI="";
my $SRV="";

my $days_def=60;

my ($days, $upass)=@ARGV;

if(!($days>0)){
    $days=$days_def;
}

$URL =~s/%UPASS%/$upass/;

print $URL,"\n";


my @k=("0410462");
my $imf=0;

print "\nPubMedScan: ";
print `date`;
print "========================================\n";

for(my $n=0;$n<@k;$n++){
    
    #my $q='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi';
    my $q='http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi';
    $q.='?cmd=search&db=pubmed&orig_db=pubmed'.
	'&term='.$k[$n].'[TA]&retmax=2500'.
	'&reldate='.$days.'&doptcmdl=brief';
    
    print $q,"\n";
    
    my $request=&LWP::Simple::get($q);
    #print "XXX:",$request;
    my @rql=split(/\n/,$request);
    
    my $nbt=0;
	
    for(my $i=0;$i<@rql;$i++){
	if($rql[$i]=~/<Id>(\d+)<\/Id>/){
	    my $pmid=$1;
	    print "PMID: $pmid\n";
	    
	    # submit to IMExCentral
	    #######################

            if("111" eq "111"){
                
                my $rns ="http://imex.mbi.ucla.edu/icentral/ws";
                
                my $som=SOAP::Lite->uri($URL.$ver)
                    ->proxy($URL.$ver)
                    ->default_ns($rns)
                    ->outputxml('true')
                    ->createPublicationById(SOAP::Data->type( 'xml' =>
                                                              "<identifier ns='pmid' ac='$pmid' />" ) );

                #print $som;
                
                
            }
        }        
    }
}
