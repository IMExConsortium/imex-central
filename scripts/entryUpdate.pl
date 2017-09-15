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

my ($pmid, $upass)=@ARGV;

$URL =~s/%UPASS%/$upass/;


my @PMID=();

if($pmid=~/[0-9]+/){
    push(@PMID, $pmid);
} else {
    
    open(F, "<".$pmid);
    while(my $ln=<F>){
        $ln=~s/\s//g;
        if($ln=~/^[0-9]+$/){
            push(@PMID,$ln);
        }
    }
    close(F);
}

my $k;
foreach $k (@PMID){
    print "processing PMID= ",$k,"\n";

    # submit to IMExCentral
    #######################

    my $rns ="http://imex.mbi.ucla.edu/icentral/ws";
                
    my $som=SOAP::Lite->uri($URL.$ver)
        ->proxy($URL.$ver)
        ->default_ns($rns)
        ->outputxml('true')
        ->createPublicationById(SOAP::Data->type( 'xml' =>
                                                  "<identifier ns='pmid' ac='$k' />" ) );
    
    #print $som;
}
