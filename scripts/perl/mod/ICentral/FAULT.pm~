package ICentral::RECORD;

my @ISA    = qw(Exporter);

my @EXPORT = qw(new
                toString
                getImex 
                getAuthors 
                getTitle 
                getOwner 
                getStatus 
                getPmid 
                getDoi 
                );
sub new{
 
    shift;
    my %par=@_;

    my $self={};
    bless $self;

    $self{record}=$par{record}{publication};
    
    return $self; 
}

sub toString{
    
    my $self = shift;

    #print %{$self{record}},"\n"; 

    if($self{record}->{imex} ne ""){
	print "ImexId: ",$self{record}->{imex},"\n";
    } else {
	print "ImexId: N/A\n";
    }

    my @id = @{$self{record}->{id}};
    

    for(my $i=0;$i<@id;$i++){
	print "  ",$id[$i]->{ns},": ",$id[$i]->{ac},"\n";
    }
    print "Author(s): ", $self{record}->{author},"\n";
    print "Title    : ", $self{record}->{title},"\n";
    print "Owner    : ", $self{record}->{owner},"\n";
    print "Status   : ", $self{record}->{status},"\n";
}


sub getImex(){
    return $self{record}->{imex};   
}

sub getAuthors(){
    return $self{record}->{author};   
}

sub getTitle(){
    return $self{record}->{title};   
}

sub getOwner(){
    return $self{record}->{owner};   
}
sub getStatus(){
    return $self{record}->{status};   
}

sub getPmid(){
    for(my $i=0;$i<@id;$i++){
	if($id[$i]->{ns} eq "pmid"){
	    return $id[$i]->{ac};
	}
    }
    return "";
}

sub getDoi(){
    for(my $i=0;$i<@id;$i++){
	if($id[$i]->{ns} eq "doi"){
	    return $id[$i]->{ac};
	}
    }
    return "";
}
