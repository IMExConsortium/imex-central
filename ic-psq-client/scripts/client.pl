#!/usr/bin/perl
use SOAP::Lite;
use XML::XPath;
use XML::XPath::XMLParser;

my $URLTEST = "https://%USR%:%PASS%\@imexcentral.org/icentraltest/ws";
my $URLBETA = "https://%USR%:%PASS%\@imexcentral.org/icentralbeta/ws";
my $URL = $URLTEST;
my $PURL= "http://10.1.1.%%%:8080/ic-psq-server/ws";
#my $PURL= "http://128.97.39.203:8081/ic-psq-server/ws";

my $ip="";
my $pmid="";
my $imex="";
my $op="";
my $stat="";
my $owner="";
my $usr ="foo";
my $pass ="bar";
my $create="false";

my $firstRec = 0;
my $maxRec = 0;

my $ausr = "";
my $agrp = "";

my $ver="";

my $ac="";

my $newNS="";
my $newAC="";

my $query="";

for( my $i=0; $i < @ARGV; $i++ ) {

    if( $ARGV[$i]=~/PROD=YES/ ) {
        $URL=$URLBETA;
    }
    if( $ARGV[$i]=~/IP=(.+)/ ) {
        $ip=$1;
        $URL=$PURL;
        $URL=~s/%%%/$ip/;
    }

    if( $ARGV[$i]=~/PMID=(.+)/ ) {
        $pmid=$1;
    }

    if( $ARGV[$i]=~/AC=(.+)/ ) {
        $ac=$1;
    }

    if( $ARGV[$i]=~/Q=(.+)/ ) {
        $query=$1;
    }

    if( $ARGV[$i]=~/NEWNS=(.+)/ ) {
        $newNS=$1;
    }

    if( $ARGV[$i]=~/AC=(.+)/ ) {
        $ac=$1;
    }

    if( $ARGV[$i]=~/VER=2/ ) {
        $ver="-v20";
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

    if( $ARGV[$i]=~/OW=(.+)/ ) {
        $owner=$1;
    }

    if( $ARGV[$i]=~/FREC=(.+)/ ) {
        $firstRec=$1;
    }
    if( $ARGV[$i]=~/MREC=(.+)/ ) {
        $maxRec=$1;
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
    if( $ARGV[$i]=~/AUSR=(.+)/ ) {
        $ausr=$1;
    }
    if( $ARGV[$i]=~/AGRP=(.+)/ ) {
        $agrp=$1;
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
    
    $rns ="http://psi.hupo.org/mi/psicquic";    
   
    my $rqinfo = "<query>$query</query>". 
        "<requestInfo>".
        "<resultType/>".
        "<firstResult>0</firstResult>".
        "<blockSize>50</blockSize>".
        "</requestInfo>";


# SOAP::Data->name("query" => $query) )

    if( $op eq "getByQuery" ) {        

        print "QUERY=".$query."\n";
        $som=SOAP::Lite->uri($URL)
            ->proxy($URL)
            ->default_ns($rns)
            ->outputxml('true')
            ->getByQuery( SOAP::Data->type( 'xml' => $query), 
                          SOAP::Data->type( 'xml' => $rqinfo) );
    }


    
    if( $op eq "updatePublicationStatus" && $stat ne "") {   
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->updatePublicationStatus( SOAP::Data->type( 'xml' =>
                                                         "<identifier ns='pmid' ac='$pmid' />" ),
                                       SOAP::Data->name("status" => $stat) );
    }

    if( $op eq "updatePubId") {   

        print "URL: ",$URL.$ver,"\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->updatePublicationIdentifier( SOAP::Data->type( 'xml' =>
                                                             "<identifier ns='pmid' ac='$pmid' />" ),
                                           SOAP::Data->type( 'xml' =>
                                                             "<newIdentifier ns='$newNS' ac='$newAC' />" )
                                           );
    }
    
    if( $op eq "getPublicationById" ) {
        my $ac = $pmid;
        my $ns = "pmid";
        if( $imex ne "" ) {
            $ac = $imex;
            $ns = "imex";
        }

        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->getPublicationById( SOAP::Data->type( 'xml' =>
                                                    "<identifier ns='$ns' ac='$ac' />" ));
    }

    if( $op eq "getPublicationImexAccession" ) {
        my $ac = $pmid;
        my $ns = "pmid";
        
        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->getPublicationImexAccession( SOAP::Data->type( 'xml' =>
                                                             "<identifier ns='$ns' ac='$ac' />" ),
                                           SOAP::Data->name("create" => $create) );
    }


    if( $op eq "getPublicationByStatus" ) {
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->getPublicationByStatus( SOAP::Data->type( 'xml' =>
                                                    "<status>$stat</status>" ),
                                     SOAP::Data->type( 'xml' =>
                                                       "<firstRec>$firstRec</firstRec>" ),
                                     SOAP::Data->type( 'xml' =>
                                                       "<maxRec>$maxRec</maxRec>" ));
    }

    if( $op eq "getPublicationByOwner" ) {  
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->getPublicationByOwner( SOAP::Data->type( 'xml' =>
                                                       "<owner>$owner</owner>" ),
                                     SOAP::Data->type( 'xml' =>
                                                       "<firstRec>$firstRec</firstRec>" ),
                                     SOAP::Data->type( 'xml' =>
                                                       "<maxRec>$maxRec</maxRec>" ));
    }


    if( $op eq "addAdminUser" ) {
        my $ac = $pmid;
        my $ns = "pmid";

        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->updatePublicationAdminUser( SOAP::Data->type( 'xml' =>
                                                            "<identifier ns='$ns' ac='$ac' />" ),
                                          SOAP::Data->name("operation" => "ADD"),
                                          SOAP::Data->name("user" => $ausr) );
    }

    if( $op eq "dropAdminUser" ) {
        my $ac = $pmid;
        my $ns = "pmid";

        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->updatePublicationAdminUser( SOAP::Data->type( 'xml' =>
                                                            "<identifier ns='$ns' ac='$ac' />" ),
                                          SOAP::Data->name("operation" => "DROP"),
                                          SOAP::Data->name("user" => $ausr) );
    }
    if( $op eq "addAdminGroup" ) {
        my $ac = $pmid;
        my $ns = "pmid";

        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->updatePublicationAdminGroup( SOAP::Data->type( 'xml' =>
                                                             "<identifier ns='$ns' ac='$ac' />" ),
                                           SOAP::Data->name("operation" => "ADD"),
                                           SOAP::Data->name("group" => $agrp) );
    }

    if( $op eq "dropAdminGroup" ) {
        my $ac = $pmid;
        my $ns = "pmid";

        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->updatePublicationAdminGroup( SOAP::Data->type( 'xml' =>
                                                             "<identifier ns='$ns' ac='$ac' />" ),
                                           SOAP::Data->name("operation" => "DROP"),
                                           SOAP::Data->name("group" => $agrp) );
    }


    if( $op eq "addAtt" ) {
        my $ac = $pmid;
        my $ns = "pmid";

        print "URL: ".$URL.$ver."\n";

        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->addAttachment( SOAP::Data->type( 'xml' =>
                                               "<parent ns='$ns' ac='$ac' />" ),
                             SOAP::Data->type( 'xml' => 
                                               "<attachment type='txt/comment'> 
                                                  <label>foo</label>
                                                  <body>bar</body> 
                                                 </attachment>")
                             );
    }

    if( $op eq "getAttByParent" ) {
        my $ac = $pmid;
        my $ns = "pmid";

        print "URL: ".$URL.$ver."\n";

        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->getAttachmentByParent( SOAP::Data->type( 'xml' =>
                                               "<parent ns='$ns' ac='$ac' />" ),
                                     SOAP::Data->type( 'xml' => 
                                                       "<type>txt/comment</type>")
                                     );
    }
    
    if( $op eq "getAttById" ) {
        my $ac = $ac;
        my $ns = "ic";

        print "URL: ".$URL.$ver."\n";

        print "NS=".$ns." AC=".$ac."\n";
        $som=SOAP::Lite->uri($URL.$ver)
            ->proxy($URL.$ver)
            ->default_ns($rns)
            ->outputxml('true')
            ->getAttachmentById( SOAP::Data->type( 'xml' =>
                                                   "<identifier ns='$ns' ac='$ac' />" )
                                     );
    }
    
}

print "SOM: ",$som,"\n";

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

