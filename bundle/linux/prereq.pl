#!/usr/bin/perl
use strict;
use warnings;

use Term::ANSIColor;

# ant
print 'ant (>= 1.9.3) ... ';
open my $ANT, "ant -version|" or die(colored('not available', 'red')."\n");
my $antversion = <$ANT>;
chomp $antversion;
close $ANT;
if ($antversion =~ /^Apache Ant\(TM\) version (\d+)\.(\d+)\.(\d+) compiled on .*$/) {
  die(colored("failed (version: $1.$2.$3)", 'red')."\n") unless ($1 >= 1 && $2 >= 9 && $3 >= 3);
} else {
  die(colored('failed', 'red')."\n");
}
print colored('ok', 'green')."\n";

# java
print 'javac (>= 1.8.0_25) ... ';
open my $JAVAC, "javac -version 2>&1|" or die(colored('not available', 'red')."\n");
my $jv = <$JAVAC>;
chomp $jv;
close $JAVAC;
if ($jv =~ /^javac (\d+)\.(\d+)\.(\d+)\_(\d+)$/) {
  die(colored("failed (version: $1.$2.$3_$4)", 'red')."\n") 
    unless ($1 >= 1 && $2 >= 8 && $3 >= 0 && $4 >= 25);
} else {
  die(colored('failed', 'red')."\n");
}
print colored('ok', 'green')."\n";
