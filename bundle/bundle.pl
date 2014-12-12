#!/usr/bin/perl

use warnings;
use strict;

use File::Copy::Recursive qw (dircopy);
use File::Path;
use File::Spec;
use Getopt::Long;
use Term::ANSIColor;

#
# GLOBAL VARS
#
my $platform = $^O;
my $basepath = "..";
my $confirm = 0;
my $noclean = 0;

# parse command line options

GetOptions("platform=s" => \$platform,
           "baseoath=s" => \$basepath,
           "confirm" => \$confirm,
           "noclean" => \$noclean) or die(colored("error: invalid options", "bold red")."\n");

# convert basepath from relateive to absolute
$basepath = File::Spec->canonpath(File::Spec->rel2abs($basepath));

die(colored("error: unsupported platform '$platform'", "bold red")."\n") unless is_valid_platform($platform);
die(colored("error: basepath '$basepath' does not exist", "bold red")."\n") unless -d $basepath;


# display build information
print colored("Platform: ", "white").colored($platform, "bold green")."\n";
print colored("Build path: ", "white").colored($basepath, "bold green")."\n";
print "\n";

unless ($confirm == 1) {
  print colored("Continue? ([y]es/[n]o) ", "bold white");
  my $input = <STDIN>;
  chomp $input;
  exit unless ($input eq 'y' || $input eq 'yes');
}

print colored("Starting to bundle...", "bold blue").colored("\n", "reset");

# check if target platform is available
my $scriptpath = File::Spec->canonpath(File::Spec->rel2abs(File::Spec->catfile('', File::Spec->rel2abs("."), $platform)));
die(colored("no build information for this platform available", "bold yellow")."\n") unless -d $scriptpath;

# create operating file structure
mkdir 'workdir' or die(colored("error: workdir could not be created", "bold red")."\n");
mkdir 'workdir/artefacts' or 
  die(colored("error: operating structure could not be created: ", "bold red").
      colored($!, "red")."\n");
dircopy($scriptpath, 'workdir') or
  die(colored("error: could not instantiate workdir: ", "bold red").colored($!, "red")."\n");

# clean up old artefacts
if (-d 'artefacts' && $noclean == 0) {
  File::Path->remove_tree('artefacts') or
    die(colored('error: deleting old artefacts failed: ', 'bold red').
        colored($!, 'red')."\n");
}


# check if prerequisites are met
process_step('Checking prerequisites...', File::Spec->catfile('', $scriptpath, 'prereq.pl'));

# execute main script
process_step('Executing operations...', File::Spec->catfile('', $scriptpath, 'main.pl'));

# cleanups
process_step('Cleaning up...', File::Spec->catfile('', $scriptpath, 'cleanup.pl'));

# retrieve artefacts
mkdir 'artefacts' or 
  die(colored('error: allocating artefacts directory failed: ', 'bold red').
      colored($!, 'red')."\n");
my $count = dircopy('workdir/artefacts/', 'artefacts') or
  die(colored('error: copying artefacts failed: ', 'bold red').
      colored($!, 'red')."\n");
 
print colored(--$count, 'magenta').colored(' artefact(s) retrieved.', 'yellow')."\n";


print colored('done.', 'bold green')."\n";

#
# SUBS
#
sub is_valid_platform {
  my $check = shift;
  my %supported = map { $_ => 1 } ('linux', 'windows');

  return exists($supported{$check});
}

sub execute_script {
  my $script = shift;
  die(colored("error: script '$script' does not exist", "bold red")."\n") unless -f $script;
  return (system($^X, $script, $basepath)?0:1);
}

sub process_step {
  my ($description, $script) = @_;

  if (-f $script) {
    print colored($description, "bold white")."\n";
    my $rc = execute_script($script);
    if ($rc) {
      print colored("ok", "green")."\n".colored('', "reset");
    } else {
      print colored("failed", "red")."\n".colored('', 'reset');
      exit 1;
    }
    print "\n";
}


}

#
# END handling
#
END {

# delete temporary workdir if it exists
  if (-d 'workdir') {
    File::Path->remove_tree('workdir');
  }

}
