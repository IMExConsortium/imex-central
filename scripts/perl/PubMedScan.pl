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
	    'jlist=s' => \$jlf
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

















die;


my $SRV="http://www.ebi.ac.uk:80/intact/imexcentral-webservice/imxcService";
my $URI="http://www.ebi.ac.uk:80/intact/imexcentral-webservice/imxcService";


my $days_def=60;

my ($days,$jpk)=@ARGV;

if(!($days>0)){
    $days=$days_def;
}

my $sq;  #=new SQ::DBA( UID=>1, SHSRV=>"10.1.216.101" );

my $rNLM= $sq->getNlmIdMap();

my @k=keys(%$rNLM);

print "\nPubMedScan: ";
print `date`;
print "========================================\n";

for(my $n=0;$n<@k;$n++){
    if($jpk>0 && $jpk ne $k[$n]){
	next;
    }

    my $rJou=$sq->getJournalByNlmId(NLMID=>$k[$n]);
    my $ttl=$rJou->{TITLE};
    my $imf=$rJou->{IMEX};

    print "\n\nJOURNAL: ".$k[$n]."(".$ttl."); ";
    if($rNLM->{$k[$n]}){
	print "Shadow ON\n";
    } else {
	print "Shadow OFF\n";
    }

    print "===================================\n";
    
    if($rNLM->{$k[$n]}){

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
		print "PMID: $pmid\t";
		
		my $rtr=$sq->testPubPMID(PMID=>$pmid);
		
		if($rtr->{PID}<0){
		    my $rpub=$sq->getPubNCBI($pmid);
		    my $pid =$sq->addPub(ART=>$rpub);


		    if($pid>0){
			$nbt++;
			print "NEW PID: $pid\n";
			
			# create initial message
			########################

			$sq->addMessage(PID=>$pid,
					SUBJ=>"Record Created",
					BODY=>"",
					MLVL=>"Status Change");
			

			# submit to IMExCentral
                        #######################
			
                        if($imf eq "111"){

                            my $aau =&sanitize($rpub->{AAU});
                            my $att =&sanitize($rpub->{ATT});
			    
                            my $som=SOAP::Lite->uri($URI)
                                -> proxy($SRV)
                                -> ns("http://ebi.ac.uk/imexCentral/wsclient/generated")
                                -> createPublication(SOAP::Data->name("type"       => "PubMed"),
						     SOAP::Data->name("identifier" => $pmid),
						     SOAP::Data->name("status"     => "POSTPUBLICATION"),
						     SOAP::Data->name("title"      => $att),
						     SOAP::Data->name("author"     => $aau),
						     SOAP::Data->name("loginId"    => "dip-shadow")
                                                     );

                            if(defined($som->result)){
                                print " IDENT: ",$som->result->{identifier},"\n";
                                print "  AUTH: ",$som->result->{author},"\n";

				my $som2=SOAP::Lite->uri($URI)
                                -> proxy($SRV)
                                -> ns("http://ebi.ac.uk/imexCentral/wsclient/generated")
                                -> updatePublicationStatus(SOAP::Data->name("identifier" => $pmid),
						   SOAP::Data->name("status"     => "RESERVED"),
						   SOAP::Data->name("loginId"    => "dip-shadow")
                                                  );

				if(defined($som2 -> result)){
				    print " RESPONSE: ",$som2 -> result,"\n";
				} else {
				    print "Fault: \n";
				    print " STR: ",$som2 -> faultstring(),"\n";
				    print " CDE:",$som2 -> faultcode(),"\n";
				    print " ACT:",$som2 -> faultactor(),"\n";
				    print " DET:",$som2 -> faultdetail(),"\n";
				}
                            } else {
                                print "Fault: \n";
                                print " STR: ",$som -> faultstring(),"\n";
                                print " CDE:",$som -> faultcode(),"\n";
                                print " ACT:",$som -> faultactor(),"\n";
                                print " DET:",$som -> faultdetail(),"\n";
                            }
			}
		    }
		} else {
		    print "OLD PID: $rtr->{PID}\n";
		}
	    }
	}
	    
	if($nbt>0){
	
	    # get journal title
	    
	    # my $rJou=$sq->getJournalByNlmId(NLMID=>$k[$n]);
	    # my $ttl=$rJou->{TITLE};
	    
	    # new articles found: notify curators
	    my @CurList= @{$sq->getCurator(NLMID=>$k[$n])};
	    
	    if(1==1){
		
		for(my $c=0;$c<@CurList;$c++){
		    print "\n[$c] ",$CurList[$c]->{login}," ",$CurList[$c]->{email},"\n";
		    
		    my $msg="Hello,\n  this is a kind reminder that a new batch of articles in\n".
			$ttl.", a journal you've chosen to shadow, just\n".
			"arrived.  Hope you'll find some time to contribute to DIP\n".
			"curation effort once more.\n\n".
			"Thanks a lot,\nShadowDIP script \@ imex.mbi.ucla.edu ;o)\n";
		    
		    
		    # send a notification
		    
		    if($CurList[$c]->{email} ne ""){
			my %mail = ( To      => $CurList[$c]->{email},
				     Cc      => 'lukasz@mbi.ucla.edu',
				     From    => 'dip@mbi.ucla.edu',
				     Message => $msg,
				     Subject => 'ShadowDIP: New articles arrived',
				     smtp    => 'shannon.mbi.ucla.edu');
			
			if(sendmail(%mail)){
			    print "\nOK. Log says:\n", $Mail::Sendmail::log;
			} else  {
			    print "\nERROR. Log says:\n",
			    $Mail::Sendmail::error;
			}
		    }
		}
	    }
	}
    }
}

#my $q='http://www.ncbi.nlm.nih.gov/'.
#    'entrez/query.fcgi?cmd=search&db=pubmed&orig_db=pubmed'.
#    '&term='.$jnl.'[TA]+AND+'.$yr.'[DP]+AND+'.$vol.'[VI]+AND+'.$iss.'[IP]'.
#    '&pmfilter_PDatLimit=90+Days&doptcmdl=brief';



sub sanitize{

    my ($s)=@_;

    $s=~s/\"//g;
    $s=~s/\'//g;
    $s=~s/^\s+//g;
    $s=~s/\s+$//g;
    $s=~s/\s+/ /g;

    return $s;
}
