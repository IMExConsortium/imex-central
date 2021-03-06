package ICentral::SOAP;

use SOAP::Lite;
use XML::XPath;
use XML::XPath::XMLParser;

use ICentral::RECORD;
use ICentral::FAULT;

#use LWP::Simple;
#use Carp;
#use strict;
#use DBI;
#use Exporter;


my @ISA    = qw(Exporter);

my @EXPORT = qw(new
                getPublicationById
                getPublicationImexAccession
                createPublicationById
                addAttachment
                );

my $URLDEV  = "http://%USR%:%PASS%\@10.1.200.200:8080/ws-v20";
my $URLTEST = "https://%USR%:%PASS%\@imexcentral.org/icentraltest/ws-v20";
my $URLBETA = "https://%USR%:%PASS%\@imexcentral.org/icentralbeta/ws-v20";
my $URLPROD = "https://%USR%:%PASS%\@imexcentral.org/icentral/ws-v20";

my $RNS ="http://imex.mbi.ucla.edu/icentral/ws";

sub new{
 
    shift;
    my $self={};
    bless $self;

    $self->{UID}  = ""; # default user  
    $self->{PASS} = ""; # default password 
    $self->{URL}  = ""; # default URL
    $self->{RNS}  = $RNS; # remote namespace

    my %par=@_;
    
    if( $par{UID} ne ""){
	$self->{UID}=$par{UID};	
    }
    
    if($par{PASS} ne ""){
	$self->{PASS}=$par{PASS};
    }
    
    if($par{URL} ne ""){

	$self->{URL}=$par{URL};

	if($par{URL} eq "DEV"){
	    $self->{URL}=$URLDEV;
	}
	if($par{URL} eq "BETA"){
	    $self->{URL}=$URLBETA;
	}
	if($par{URL} eq "PROD"){
	    $self->{URL}=$URLPROD;
	}
    }

    return $self; 
}


sub getPublicationById{

    my $self = shift;
    my %par=@_;

    my %RET;

    my $ns = "pubmed"; # default identifier: pubmed

    #print @_;

    if($par{NS} ne ""){
	$ns = $par{NS};
    }
    my $ac = $par{AC};

    my $url = $self->{URL};

    $url=~s/%USR%/$self->{UID}/;
    $url=~s/%PASS%/$self->{PASS}/;
    
    print "getPublicationById> NS: $ns   AC: $ac   URL: $url\n\n";
    
    my $som=SOAP::Lite->uri($url)
	->proxy($url)
	->default_ns($self->{RNS})
	->outputxml('true')
	->getPublicationById( SOAP::Data->type( 'xml' =>
						"<identifier ns='$ns' ac='$ac' />" ));
    
    print $som,"\n";
    my $xp_som = XML::XPath->new(xml=>$som);
    $xp_som->set_namespace("rns",$RNS);
    
    # find publication
    my $publist = $xp_som->find('//rns:publicationResponse/rns:publication'); 

    if( $publist ){
	return new ICentral::RECORD( record => parsePubList(PUBLIST => $publist) );
    } else {
	my $faultlist = $xp_som->find('//ImexCentralFault'); # fault
	if( $faultlist ){
	    return new ICentral::FAULT( fault =>parseFaultList(FAULTLIST => $faultlist));
	} else {
	    
	    # sholdn't get here
	}
    }
    print "**";
    return \{};

}

sub getPublicationImexAccession{
    
    my $self = shift;
    my %par=@_;
    
    my $ns="pmid";
    my $create="false";
    
    if($par{NS} ne ""){
	$ns = $par{NS};
    }
    if($par{create} ne ""){
	$create = $par{CREATE};
    }
    my $ac = $par{AC};
    
    my $url = $self->{URL};

    $url=~s/%USR%/$self->{UID}/;
    $url=~s/%PASS%/$self->{PASS}/;
    
    print "getPublicationImexAccession> NS: $ns   AC: $ac   CREATE: $create  URL: $url\n\n";
    
    my $som=SOAP::Lite->uri($url)
	->proxy($url)
	->default_ns($self->{RNS})
	->outputxml('true')
	->getPublicationImexAccession( SOAP::Data->type( 'xml' =>
							 "<identifier ns='$ns' ac='$ac' />" ),
				       SOAP::Data->name("create" => $create) ); 
    #print $som,"\n";
    
    my $xp_som = XML::XPath->new(xml=>$som);
    $xp_som->set_namespace("rns",$RNS);
    
    my $publist = $xp_som->find('//rns:publicationResponse/rns:publication'); # find publication

    if( $publist ){
	return new ICentral::RECORD( record => parsePubList(PUBLIST => $publist));
    } else {
	if( $faultlist ){
	    my $faultlist = $xp_som->find('//ImexCentralFault'); # fault
	    return parseFaultList(FAULTLIST => $faultlist);
	} else {

	    # sholdn't get here
	}
    }

    return \{};
}

sub createPublicationById{
    
    my $self = shift;
    my %par=@_;
    
    my $ns="pmid";
    
    if($par{NS} ne ""){
	$ns = $par{NS};
    }
    
    my $ac = $par{AC};
    
    my $url = $self->{URL};

    $url=~s/%USR%/$self->{UID}/;
    $url=~s/%PASS%/$self->{PASS}/;
    
    #print "createPublicationById> NS: $ns   AC: $ac   CREATE: $create  URL: $url\n\n";
    
    my $som=SOAP::Lite->uri($url)
	->proxy($url)
	->default_ns($self->{RNS})
	->outputxml('true')
	->createPublicationById( SOAP::Data->type( 'xml' =>
						   "<identifier ns='$ns' ac='$ac' />" ) ); 
    #print $som,"\n";
    
    my $xp_som = XML::XPath->new(xml=>$som);
    $xp_som->set_namespace("rns",$RNS);
    
    my $publist = $xp_som->find('//rns:publicationResponse/rns:publication'); # find publication
    
    if( $publist ){
	return new ICentral::RECORD( record => parsePubList(PUBLIST => $publist));
    } else {
	if( $faultlist ){
	    my $faultlist = $xp_som->find('//ImexCentralFault'); # fault
	    return parseFaultList(FAULTLIST => $faultlist);
	} else {

	    # sholdn't get here
	}
    }

    return \{};
}

sub addAttachment{
    
    my $self = shift;
    my %par=@_;
    
    my $ns="pmid";
    
    if($par{NS} ne ""){
	$ns = $par{NS};
    }
    
    my $ac = $par{AC};
    
    my $url = $self->{URL};

    $url=~s/%USR%/$self->{UID}/;
    $url=~s/%PASS%/$self->{PASS}/;

    my $type="";
    my $subject="";
    my $body="";

    if( $par{TYPE} eq "comment"){
	$type = "txt/comment";
	$subject = $par{SUBJECT};
	$body = $par{BODY};
           
	if($subject ne ""){

	    #print "createPublicationById> NS: $ns   AC: $ac   CREATE: $create  URL: $url\n\n";

	    my $som=SOAP::Lite->uri($url)
		->proxy($url)
		->default_ns($self->{RNS})
		->outputxml('true')
		->addAttachment( SOAP::Data->type( 'xml' =>
						   "<parent ns='$ns' ac='$ac' />" ),
				 SOAP::Data->type( 'xml' =>
						   "<attachment type='$type'>
                                                  <subject>$subject</subject>
                                                  <body>$body</body>
                                                 </attachment>")
		);
	} else {
	    die "ERROR: Cannot create a comment with no subject.";
	}
    }
}



sub updatePublicationStatus{

    my $self = shift;
    my %par=@_;

    my $ns="pmid";

    if($par{NS} ne ""){
        $ns = $par{NS};
    }

    my $ac = $par{AC};
    my $stat = $par{status};

    my $url = $self->{URL};
    $url=~s/%USR%/$self->{UID}/;
    $url=~s/%PASS%/$self->{PASS}/;

    
    if( $stat ne "") {
        $som=SOAP::Lite->uri($url)
            ->proxy($url)
            ->default_ns($self->{RNS})
            ->outputxml('true')
            ->updatePublicationStatus( SOAP::Data->type( 'xml' =>
                                                         "<identifier ns='$ns' ac='$ac' />" ),
                                       SOAP::Data->name("status" => $stat) );
 
        
        print $som,"\n";
        my $xp_som = XML::XPath->new(xml=>$som);
        $xp_som->set_namespace("rns",$RNS);

        # find publication
        my $publist = $xp_som->find('//rns:publicationResponse/rns:publication');

        if( $publist ){
            return new ICentral::RECORD( record => parsePubList(PUBLIST => $publist) );
        } else {
            my $faultlist = $xp_som->find('//ImexCentralFault'); # fault
            if( $faultlist ){
                return new ICentral::FAULT( fault =>parseFaultList(FAULTLIST => $faultlist) );
            } else {

            # sholdn't get here
            }
        }    
        print "**";


    } else {
        die "ERROR: New Status not provided.";
    }
}








#------------------------------------------------------------------------------
# private methods

sub parsePubList{

    my %par=@_;
    my $publist = $par{PUBLIST};    

    my %RET ={};

    foreach my $pub ($publist->get_nodelist) {

	$RET{publication} = {};
	
	my $authors =  $pub->find('author/text()');
	if($authors){
	    #print " AUTHORS: ",$authors,"\n";
	    $RET{publication}{author}=$authors;
	}
	
	my $title =  $pub->find('title/text()');
	if($title){
	    #print " TITLE: ",$title,"\n";
		$RET{publication}{title}=$title;
	}
	
	my $idlist =  $pub->find('identifier');
	foreach my $id ($idlist->get_nodelist) {
	    if(!$RET{publication}{id}){
		$RET{publication}{id}=[];
	    }
	    
	    push( $RET{publication}{id},
		  {ns => $id->getAttribute("ns"),
		   ac => $id->getAttribute("ac"),
		      });
	    
	    #print " NS: ",$id ->getAttribute("ns"),"  ";
	    #print " AC: ",$id ->getAttribute("ac"),"\n";
	}
	
	my $status =  $pub->find('status/text()');
	if($status){
	    #print " STATUS: ", $status, "\n";
	    $RET{publication}{status}=$status;
	}
	
	my $imex =  $pub->find('imexAccession/text()');
	if($imex){
	    #print " ImexID: ", $imex, "\n";
	    $RET{publication}{imex}=$imex;
	}
	
	my $owner =  $pub->find('owner/text()');
	if($owner){
	    #print " OWNER: ", $owner, "\n";
	    $RET{publication}{owner}=$owner;
	}    
    } 
    
    return \%RET;
}


sub parseFaultList{

  my %par=@_;
  my $faultlist = $par{FAULTLIST};
  
  my %RET ={};             

  
  foreach my $fault ($faultlist->get_nodelist) {
      my $faultcode = $fault->find(".//faultCode/text()"),"\n";
      my $message = $fault->find(".//message/text()"),"\n";
      
      #print "FAULT:\n ($faultcode) $message\n";
      
      $RET{fault} = {code => $faultcode, message =>  $message };
  }

  return \%RET;
}


