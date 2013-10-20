#!/usr/bin/perl

my ($dir) = @ARGV;

print "DEST: $dir\n";
system( "cp -Rv etc $dir" );
system( "cp -Rv pages $dir" );
system( "cp -Rv img $dir" );
system( "cp -Rv icentral $dir" );
