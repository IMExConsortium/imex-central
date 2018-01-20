package ICentral::FAULT;

my @ISA    = qw(Exporter);

my @EXPORT = qw(new
                toString
                );
sub new{
 
    shift;
    my %par=@_;

    my $self={};
    bless $self;

    $self{fault}=$par{fault};
    
    return $self; 
}

sub toString{
    my $self = shift;
    return "fault";
}

