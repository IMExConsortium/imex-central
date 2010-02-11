#!/usr/bin/perl
use SOAP::Lite;
use XML::XPath;
use XML::XPath::XMLParser;

my $URL= "https://%USR%:%PASS%\@imexcentral.org/icentraltest/ws";
my $PURL= "http://%USR%:%PASS%\@10.1.1.%%%:8080/icentral/ws";

my $ip="";
my $pmid="";
my $imex="";
my $op="";
my $stat="";
my $usr ="foo";
my $pass ="bar";
my $create="false";

for( my $i=0; $i < @ARGV; $i++ ) {
    if( $ARGV[$i]=~/IP=(.+)/ ) {
        $ip=$1;
        $URL=$PURL;
        $URL=~s/%%%/$ip/;
    }

    if( $ARGV[$i]=~/PMID=(.+)/ ) {
        $pmid=$1;
    }
    if( $ARGV[$i]=~/IMEX=(.+)/ ) {
        $imex=$1;
    }

    if( $ARGV[$i]=~/OP=(.+)/ ) {
        $op=$1;
    }

    if( $ARGV[$i]=~/ST=(.+)/ ) {
        $stat=$1;
    }

    if( $ARGV[$i]=~/USR=(.+)/ ) {
        $usr=$1;
    }

    if( $ARGV[$i]=~/PASS=(.+)/ ) {
        $pass=$1;
    }
    if( $ARGV[$i]=~/CREATE=(.+)/ ) {
        $create=$1;
    }

}


$URL=~s/%USR%/$usr/;
$URL=~s/%PASS%/$pass/;

print "URL: $URL\n";
print "OP: $op\n";
print "PMID: $pmid\n";


my $som="";
my $rns ="";    
if($op ne "" ) {
    
    $rns ="http://imex.mbi.ucla.edu/icentral/ws";    

    if( $op eq "createPublicationById" ) {        
        $som=SOAP::Lite->uri($URL)
            ->proxy($URL)
            ->default_ns($rns)
            ->outputxml('true')
            ->createPublicationById(SOAP::Data->type( 'xml' =>
                                                      "<identifier ns='pmid' ac='$pmid' />" ) );
    }
    
    if( $op eq "updatePublicationStatus" && $stat ne "") {   
        $som=SOAP::Lite->uri($URL)
            ->proxy($URL)
            ->default_ns($rns)
            ->outputxml('true')
            ->updatePublicationStatus( SOAP::Data->type( 'xml' =>
                                                         "<identifier ns='pmid' ac='$pmid' />" ),
                                       SOAP::Data->name("status" => $stat) );
    }
    
    if( $op eq "getPublicationById" ) {
        my $ac = $pmid;
        my $ns = "pmid";
        if( $imex ne "" ) {
            $ac = $imex;
            $ns = "imex";
        }

        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL)
            ->proxy($URL)
            ->default_ns($rns)
            ->outputxml('true')
            ->getPublicationById( SOAP::Data->type( 'xml' =>
                                                    "<identifier ns='$ns' ac='$ac' />" ));
    }

    if( $op eq "getPublicationImexAccession" ) {
        my $ac = $pmid;
        my $ns = "pmid";
        
        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL)
            ->proxy($URL)
            ->default_ns($rns)
            ->outputxml('true')
            ->getPublicationImexAccession( SOAP::Data->type( 'xml' =>
                                                             "<identifier ns='$ns' ac='$ac' />" ),
                                           SOAP::Data->name("create" => $create) );
    }
    
}

print $som,"\n";

my $xp_som = XML::XPath->new(xml=>$som);
$xp_som->set_namespace("rns",$rns);

my $nodeset;

if( $format eq "native" ){
    $nodeset = $xp_som->find('//rns:nativerecord/text()'); # find all paragraphs
    foreach my $node ($nodeset->get_nodelist) {
        print "FOUND NATIVE\n\n",
        $node->string_value(),
        "\n\n";
    }
} else {
    $xp_som->set_namespace("rns","http://dip.doe-mbi.ucla.edu/services/dxf14");

    $nodeset = $xp_som->find('//rns:dataset'); # find all paragraphs
    foreach my $node ($nodeset->get_nodelist) {
        print "FOUND DXF\n\n",
        XML::XPath::XMLParser::as_string($node),"\n";
    }
}

