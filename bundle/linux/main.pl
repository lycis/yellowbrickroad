#!/usr/bin/perl
use warnings;
use strict;

use File::Spec;
use File::Copy::Recursive qw (dircopy);
use File::Copy qw (copy);

my $path = $ARGV[0];

open my $ANTCMD, "cd $path && ant dist|" or die("\n");
while(<$ANTCMD>) {
 chomp;
 print "$_\n"; 
}

my $workdir = File::Spec->catdir('', File::Spec->rel2abs('.'), 'workdir'); 
my $artefacts = File::Spec->catdir('', $workdir, 'artefacts');
my $artdir = File::Spec->catdir('', $artefacts, 'ybr');
my $libdir = File::Spec->catdir('', $artdir, 'lib');
my $bindir = File::Spec->catdir('', $artdir, 'bin');

mkdir $artdir or 
  die("creating file structure ($artdir) failed: $!\n");
mkdir $libdir or 
  die("creating file structure ($libdir) failed: $!\n");
mkdir $bindir or 
  die("creating file structure ($bindir) failed: $!");


my $distdir = File::Spec->catdir('', $path, 'dist');
dircopy($distdir, $libdir) or die("copying binaries failed: $!\n");

my $install = File::Spec->catfile('', $workdir, 'install.sh');
my $executable = File::Spec->catfile('', $workdir, 'ybr');

copy($install, $artdir) or die("copying ($install -> $artdir) failed: $!\n");
copy($executable, $bindir) or die("copying ($executable -> $bindir) failed: $!\n");

# tar everything
my $tar = `cd $artefacts && tar -czf ybr.tar.gz ybr`;
print "tar: $tar\n"; 
