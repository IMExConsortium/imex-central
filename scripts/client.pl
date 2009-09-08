#!/usr/bin/perl
use SOAP::Lite;
use XML::XPath;
use XML::XPath::XMLParser;

my $URL= "http://dip.doe-mbi.ucla.edu/icentral/ws";

my $PURL= "http://foo:bar\@10.1.1.%%%:8080/icentral/ws";

my $ip="";
my $pmid="";
my $op="";

for( my $i=0; $i < @ARGV; $i++ ) {
    if( $ARGV[$i]=~/IP=(.+)/ ) {
        $ip=$1;
        $URL=$PURL;
        $PURL=~s/%%%/$ip/;
    }

    if( $ARGV[$i]=~/PMID=(.+)/ ) {
        $pmid=$1;
    }

    if( $ARGV[$i]=~/OP=(.+)/ ) {
        $op=$1;
    }
}

print "URL: $PURL\n";
print "OP: $op\n";
print "PMID: $pmid\n";

my $som="";
my $rns ="";    
if($op ne "" ) {
    
    $rns ="http://imex.mbi.ucla.edu/icentral/ws";    

    if($op eq "createPublicationById"){

        $som=SOAP::Lite->uri($PURL)
            ->proxy($PURL)
            ->default_ns($rns)
            ->outputxml('true')
            ->createPublicationById(SOAP::Data->name("provi" => $prv),
                            SOAP::Data->name("service" => $format),
                            SOAP::Data->name("ns" => $ns),
                            SOAP::Data->name("ac" => $ac) );
	print $som,"\n";
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

