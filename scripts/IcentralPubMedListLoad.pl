#!/usr/bin/perl

$|++;
use strict;


use LWP::UserAgent;
use LWP::Simple;
use Mail::Sendmail;

use SOAP::Lite;

my $URL= "https://%UPASS%\@imexcentral.org/icentraltest/ws";
my $ver="-v20";

my $URI="";
my $SRV="";

my ($upass, $file)=@ARGV;

$URL =~s/%UPASS%/$upass/;
print $URL,"\n";

my @k=();  # PMID list

open(F, "<$file");
while(my $ln=<F>){
    $ln=~s/\s//g;
    push(@k,$ln);
    #print $ln,"\n";
}
close(F);

my $imf=0;

print "\nPubMed Load: ";
print "========================================\n";

my $pmid;

foreach $pmid (@k) {
    print "PMID: $pmid\n";
	    
    # submit to IMExCentral
    #######################
                
    my $rns ="http://imex.mbi.ucla.edu/icentral/ws";
    
    my $som=SOAP::Lite->uri($URL.$ver)
        ->proxy($URL.$ver)
        ->default_ns($rns)
        ->outputxml('true')
        ->createPublicationById(SOAP::Data->type( 'xml' =>
                                                  "<identifier ns='pmid' ac='$pmid' />" ) );

   
    if($som =~/S:Fault/){
        print "PMID: ",$pmid," ::: ",$som,"\n\n";
    }
    #sleep(30);
    
}
