#!/usr/bin/perl

$|++;
use strict;
use SOAP::Lite;

my $URL= "https://%UPASS%\@imexcentral.org/icentraltest/ws";
my $ver="-v20";

my $URI="";
my $SRV="";

my ($upass)=@ARGV;

$URL =~s/%UPASS%/$upass/;
print $URL,"\n";

my @k=("0410462");  # PMID list. Fill it any way you want
my $imf=0;

print "\nPubMed Load: ";
print "========================================\n";

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
}
