#!/opt/coolstack/bin/perl
#
#  Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

use Getopt::Std;
use POSIX;
use DBI;
use File::Copy;
use MogileFS::Client;
use Getopt::Long;
use Pod::Usage qw{ pod2usage };
use Digest::MD5 qw{ md5_hex };
use Time::HiRes qw{ gettimeofday tv_interval };
use LWP::Simple;
use POSIX qw(:sys_wait_h);
use Compress::Zlib;
$| = 1;
use constant ERR_FATAL => 1;

#----validate cmd line args----#
%options=();
# d is the domain name argument, usually sfbay.sun.com
# s is the scale(active users) argument
getopts("d:s:",\%options);
print "Got -d $options{d}\n" if defined $options{d} or die "-d <domainname> is required";
print "Got -s $options{s}\n" if defined $options{s} or die "-s <scale, or number of active users> is required";

#----PARAMETERS----#
 my $user = "mogile";
 my $password = "some_pass";
 my $db_host = "";
 my $number_of_active_users = $options{s};
 my $number_of_users = $number_of_active_users * 4;
 my $e_power = $number_of_users / -10000.0;
 my $chl_prob = (1.0 - exp($e_power)) / (1.0 + exp($e_power));
 # Rounding results by adding .5 and converting to int.
 my $number_of_events = int(15000 * $chl_prob + .5);
 #     private static double cumuHalfLogistic(double x, double scale) {
 #       double power = -x / scale;
 #       return (1d - Math.exp(power)) / (1d + Math.exp(power));
 #   }
 #
 #           double prob = cumuHalfLogistic(users, 10000);
 #       // We limit to 5000 tags
 #       return (int) Math.round(5000 * prob);
 #my $number_of_events = ceil($number_of_users * 0.07);
 #$mogile_domain = "sfbay.sun.com";
 $mogile_domain = $options{d};
# $mogile_tracker_host = "10.6.141.125:6001";
 $mogile_tracker_host = "localhost:6001";
 $mogile_file_class = "Addresses";
 my $resources_dir = "/export/sw/resources"; #dir. that holds the files to be loaded.
 my $event_img = "event.jpg";
 my $event_img_thmb = "event_thumb.jpg";
 my $event_literature = "event.pdf";
 my $person_img = "person.jpg";
 my $person_img_thmb = "person_thumb.jpg";

#----CONSTANTS----#
 my $sleep_time_after_load = 10;
 my $person_prefix = "p";
 my $event_prefix = "e";
 my $person_thumbnail_suffix = "t";
 my $event_thumbnail_suffix = "t";
 my $event_literature_suffix = "l";
 my $image_extn = "jpg";
 my $literature_extn = "pdf";
#----dont change this section----#
$opts{help} = 0;
$opts{trackers}=$mogile_tracker_host;
$opts{domain}=$mogile_domain;
$opts{class}=$mogile_file_class;
#$opts{big}="vvv";

#----Connect to db, obtain initial list of----# 
#----rows in the file_to_replicate table  ----#
 my $datasource = 'DBI:mysql:mogilefs:$db_host';
 my $dbh = DBI->connect($datasource, $user, $password)
                or die "Couldn't connect to database: " . DBI->errstr;
 print "Connected to Mogile database as $user \n"; 
 my $sth = $dbh->prepare('SELECT * FROM file_to_replicate')
                or die "Couldn't prepare statement: " . $dbh->errstr;
 $sth->execute()
 		or die "Couldn't execute statement: " . $sth->errstr;
 print "Number of pre existing rows = " . $sth->rows . " \n";
 my $initial_row_count = $sth->rows;

#----Begin loading files----#
 my $total = 0;

 print "\n\nInserting person data: Main images\n";
 print "----------------------------------\n";
for (my $count = 0; $count < $number_of_users; $count ++) 
  {
 $key = gen_key($person_prefix, $count, "", $image_extn);
 print "Inserting $key into Mogile.\n";
 load($resources_dir . "/" . $person_img, $key);

 print "Finished inserting $key\n";
 $total ++;
 }

 print "\n\nInserting person data: Thumbnail images\n";
 print "---------------------------------------\n";
for (my $count = 0; $count < $number_of_users; $count ++) 
  {
 $key = gen_key($person_prefix, $count, $person_thumbnail_suffix, $image_extn);
 print "Inserting $key into Mogile.\n";
 load($resources_dir . "/" . $person_img_thmb, $key);

 print "Finished inserting $key\n";
 $total ++;
 }

 print "\n\nInserting event data: Images\n";
 print "----------------------------\n";
for (my $count = 0; $count < $number_of_events; $count ++) 
  {
 $key = gen_key($event_prefix, $count, "", $image_extn);
 print "Inserting $key into Mogile.\n";
 load($resources_dir . "/" . $event_img, $key);

 print "Finished inserting $key\n";
 $total ++;
 }


 print "\n\nInserting event data: Thumbnail Images\n";
 print "----------------------------\n";
for (my $count = 0; $count < $number_of_events; $count ++)
  {
 $key = gen_key($event_prefix, $count, $event_thumbnail_suffix, $image_extn);
 print "Inserting $key into Mogile.\n";
 load($resources_dir . "/" . $event_img_thmb, $key);

 print "Finished inserting $key\n";
 $total ++;
 }



 print "\n\nInserting event data: Literature\n";
 print "--------------------------------\n";
for (my $count = 0; $count < $number_of_events; $count ++) 
  {
 $key = gen_key($event_prefix, $count, $event_literature_suffix, $literature_extn);
 print "Inserting $key into Mogile.\n";
 load($resources_dir . "/" . $event_literature, $key);

 print "Finished inserting $key\n";
 $total ++;
 }

#----Connect to db, obtain final list of----# 
#----rows in the file_to_replicate table  ----#
 my $dbh = DBI->connect('DBI:mysql:mogilefs', $user, $password)
                or die "Couldn't connect to database: " . DBI->errstr;
 print "Connected to Mogile database as $user \n"; 
 my $sth = $dbh->prepare('SELECT * FROM file_to_replicate')
                or die "Couldn't prepare statement: " . $dbh->errstr;
 $sth->execute()
 		or die "Couldn't execute statement: " . $sth->errstr;
 print "Number of pre existing rows = " . $sth->rows . " \n";
 my $final_row_count = $sth->rows;
 print "waiting for replication to complete\n";
 while ($final_row_count > $initial_row_count)
  {
   sleep 2;
   print ". ";
 $sth->execute();
 $final_row_count = $sth->rows;
  }

  print "\n\nREPLICATION SUCCESSFUL! MOGILE DATA LOADING COMPLETE!\n\n";




#**************HELPER FUNCTIONS****************#
#**************FROM HERE***********************#




#----To load a file into MogileFS----#
#----Call method like:
#    load_file($data_string, $key);
#--------#
 sub load_file
  {
   if ($#_ < 1) {print "load_file: wrong # of arguments"; return;}
   my $data_string = $_[0];
   my $key = $_[1];
   $mogc = MogileFS::Client->new(domain => $mogile_domain,
                               hosts  => [$mogile_tracker_host]);
   $fh = $mogc->new_file($key, $class);
   print $fh $data_string;
   unless ($fh->close) {
    die "Error writing file: " . $mogc->errcode . ": " . $mogc->errstr;
   }
   @urls = $mogc->get_paths($key);
   print "\n urls = " . @urls[0] . "\n";

  }




#----generate the storage key----#
  sub gen_key()
  {
   if ($#_ < 3) {print "gen_key: wrong # of arguments"; return;}
   my $prefix = $_[0];
   my $body = $_[1];
   my $suffix = $_[2];
   my $extn = $_[3];
   my $key = $prefix . $body . $suffix . "." . $extn;
   return $key;
 }




#----read an event file into a string----#
  sub read_file()
  {
  if ($#_ < 0) {print "read_file: wrong # of arguments"; return;}
  my $file_name = $_[0];
  open(IN, "< $file_name");
  binmode(IN);
  while (read(IN, $b,1)) {
   $data = $data . $b;
   }
  close(IN);
  return $data;
  }




#----test write the data string----#
  sub test_write()
  {
  }



#----mogtool chunked load----#
sub load()
  {

   if ($#_ < 1) {print "load_file: wrong # of arguments"; return;}
   my $src = $_[0];
   my $key = $_[1];


abortWithUsage() unless
    GetOptions(
               # general purpose options
               'trackers=s'    => \$opts{trackers},
               'domain=s'      => \$opts{domain},
               'class=s'       => \$opts{class},
               'config=s'      => \$opts{conf},
               'help'          => \$opts{help},
               'debug'         => \$MogileFS::DEBUG,
               'lib'           => \$opts{lib},

               # extract+inject options
               'gzip|z'        => \$opts{gzip},
               'bigfile|b'     => \$opts{big},

               # inject options
               'overwrite'     => \$opts{overwrite},
               'chunksize=s'   => \$opts{chunksize},
               'receipt=s'     => \$opts{receipt},
               'reciept=s'     => \$opts{receipt}, # requested :)
               'verify'        => \$opts{verify},
               'description=s' => \$opts{des},
               'concurrent=i'  => \$opts{concurrent},

               # extract options
               'asfile'        => \$opts{asfile},
               );

# now load the config file?
my @confs = ( $opts{conf}, "$ENV{HOME}/.mogtool", "/etc/mogilefs/mogtool.conf" );
foreach my $conf (@confs) {
    next unless $conf && -e $conf;
    open FILE, "<$conf";
    foreach (<FILE>) {
        s!#.*!!;
        next unless m!(\w+)\s*=\s*(.+)!;
        $opts{$1} = $2;
    }
    close FILE;
}

# now bring in MogileFS, because hopefully we have a lib by now
if ($opts{lib}) {
    eval "use lib '$opts{lib}';";
}

# no trackers and domain..?
unless ($opts{trackers} && $opts{domain}) {
    abortWithUsage();
}

eval qq{
    use MogileFS::Client; 1
} or die "Failed to load MogileFS::Client module: $@\n";

# init connection to mogile
my $mogfs = get_mogfs();

# get our command and pass off to our functions
#my $cmd = shift;
my $cmd = "inject";
inject($src, $key) if $cmd eq 'i' || $cmd eq "inject";
#extract() if $cmd eq 'x' || $cmd eq "extract";
#list() if $cmd eq 'ls' || $cmd eq "list";
#listkey() if $cmd eq 'lsk' || $cmd eq "listkey";
#mdelete() if $cmd eq 'rm' || $cmd eq "delete";
return;
  }


sub get_mogfs {
    my @trackerinput = split(/\s*,\s*/, $opts{trackers});
    my @trackers;
    my %pref_ip;
    foreach my $tracker (@trackerinput) {
        if ($tracker =~ m!(.+)/(.+):(\d+)!) {
            $pref_ip{$2} = $1;
            push @trackers, "$2:$3";
        } else {
            push @trackers, $tracker;
        }
    }

    my $mogfs = MogileFS::Client->new(
                              domain => $opts{domain},
                              hosts  => \@trackers,
                              )
            or error("Could not initialize MogileFS", ERR_FATAL);
    $mogfs->set_pref_ip(\%pref_ip);
    return $mogfs;
}

sub error {
    my $err = shift() || "ERROR: no error message provided!";
    print STDERR "$err\n";

    if (my $errstr = $mogfs->errstr) {
        $errstr =~ s/^\s+//;
        $errstr =~ s/\s+$//;
        if ($errstr) {
            print STDERR "MogileFS backend error message: $errstr\n";
        }
    }

    if ($@) {
        my $err = $@;
        $err =~ s/[\r\n]+$//;
        print STDERR "System error message: $@\n";
    }

    # if a second argument, exit
    if (defined (my $exitcode = shift())) {
        exit $exitcode+0;
    }
}

sub inject {
   if ($#_ < 1) {print "load_file: wrong # of arguments"; return;}
   my $src = $_[0];
   my $key = $_[1];


    abortWithUsage() unless $src && $key;

    # make sure the source exists and the key is valid
    die "Error: source $src doesn't exist.\n"
        unless -e $src;
    die "Error: key $key isn't valid; must not contain spaces or commas.\n"
        unless $key =~ /^[^\s\,]+$/;

    # before we get too far, find sendmail?
    my $sendmail;
    if ($opts{receipt}) {
        $sendmail = `which sendmail` || '/usr/sbin/sendmail';
        $sendmail =~ s/[\r\n]+$//;
        unless (-e $sendmail) {
            die "Error: attempted to find sendmail binary in /usr/sbin but couldn't.\n";
        }
    }

    # open up O as the handle to use for reading data
    my $type = 'unknown';
    if (-d $src) {
        my $taropts = ($opts{gzip} ? 'z' : '') . "cf";
        $type = 'tarball';
        open (O, '-|', 'tar', $taropts, '-', $src)
            or die "Couldn't open tar for reading: $!\n";
    } elsif (-f $src) {
        $type = 'file';
        open (O, "<$src")
            or die "Couldn't open file for reading: $!\n";
    } elsif (-b $src) {
        $type = 'partition';
        open (O, "<$src")
            or die "Couldn't open block device for reading: $!\n";
    } else {
        die "Error: not file, directory, or partition.\n";
    }

    # now do some pre-file checking...
    my $size = -s $src;
    if ($type ne 'file') {
        die "Error: you specified to store a file of type $type but didn't specify --bigfile.  Please see documentation.\n"
            unless $opts{big};
    } elsif ($size > 64 * 1024 * 1024) {
        die "Error: the file is more than 64MB and you didn't specify --bigfile.  Please see documentation.\n"
            unless $opts{big};
    }

    # see if there's already a pre file?
    if ($opts{big}) {
        my $data = $mogfs->get_file_data("_big_pre:$key");
        if (defined $data) {
            unless ($opts{overwrite}) {
                error(<<MSG, ERR_FATAL);
ERROR: The pre-insert file for $key exists.  This indicates that a previous
attempt to inject a file failed--or is still running elsewhere!  Please
verify that a previous injection of this file is finished, or run mogtool
again with the --overwrite inject option.

$$data
MSG
            }

            # delete the pre notice since we didn't die (overwrite must be on)
            $mogfs->delete("_big_pre:$key")
                or error("ERROR: Unable to delete _big_pre:$key.", ERR_FATAL);
        }

        # now create our pre notice
        my $prefh = $mogfs->new_file("_big_pre:$key", $opts{class})
            or error("ERROR: Unable to create _big_pre:$key.", ERR_FATAL);
        $prefh->print("starttime:" . time());
        $prefh->close()
            or error("ERROR: Unable to save to _big_pre:$key.", ERR_FATAL);
    }

    # setup config and temporary variables we're going to be using
    my $chunk_size = 64 * 1024 * 1024;  # 64 MB
    if ($opts{big}) {
        if ($opts{chunksize} && ($opts{chunksize} =~ m!^(\d+)(G|M|K|B)?!i)) {
            $chunk_size = $1;
            unless (lc $2 eq 'b') {
                $chunk_size *= (1024 ** ( { g => 3, m => 2, k => 1 }->{lc $2} || 2 ));
            }
            print "NOTE: Using chunksize of $chunk_size bytes.\n";
        }
    }
    my $read_size = ($chunk_size > 1024*1024 ? 1024*1024 : $chunk_size);

    # temporary variables
    my $buf;
    my $bufsize = 0;
    my $chunknum = 0;
    my %chunkinfo; # { id => [ md5, length ] }
    my %chunkbuf; # { id => data }
    my %children; # { pid => chunknum }
    my %chunksout; # { chunknum => pid }

    # this function writes out a chunk
    my $emit = sub {
        my $cn = shift() + 0;
        return unless $cn;

        # get the length of the chunk we're going to send
        my $bufsize = length $chunkbuf{$cn};
        return unless $bufsize;

        # now spawn off a child to do the real work
        if (my $pid = fork()) {
            print "Spawned child $pid to deal with chunk number $cn.\n";
            $chunksout{$cn} = $pid;
            $children{$pid} = $cn;
            return;
        }

        # drop other memory references we're not using anymore
        foreach my $chunknum (keys %chunkbuf) {
            next if $chunknum == $cn;
            delete $chunkbuf{$chunknum};
        }

        # as a child, get a new mogile connection
        my $mogfs = get_mogfs();
        my $dkey = $opts{big} ? "$key,$chunknum" : "$key";

        # TODO: be resilient to transient errors, retry, etc.
        my $start_time = [ gettimeofday() ];
        my $try = 0;
        while (1) {
            $try++;
            my $fh = $mogfs->new_file($dkey, $opts{class}, $bufsize);
            unless (defined $fh) {
                error("WARNING: Unable to create new file '$dkey'.");
                printf "This was try #$try and it's been %.2f seconds since we first tried.  Retrying...\n", tv_interval($start_time);
                sleep 1;
                next;
            }
            $fh->print($chunkbuf{$cn});
            unless ($fh->close) {
                error("WARNING: Unable to save file '$dkey'.");
                printf "This was try #$try and it's been %.2f seconds since we first tried.  Retrying...\n", tv_interval($start_time);
                sleep 1;
                next;
            }
            last;
        }
        my $diff = tv_interval($start_time);
        printf "        chunk $cn saved in %.2f seconds.\n", $diff;

        # make sure we never return, always exit
        exit 0;
    };

    # just used to reap our children in a loop until they're done.  also
    # handles respawning a child that failed.
    my $reap_children = sub {
        # find out if we have any kids dead
        while ((my $pid = waitpid -1, WNOHANG) > 0) {
            my $cnum = delete $children{$pid};
            unless ($cnum) {
                print "Error: reaped child $pid, but no idea what they were doing...\n";
                next;
            }
            if (my $status = $?) {
                print "Error: reaped child $pid for chunk $cnum returned non-zero status... Retrying...\n";
                $emit->($cnum);
                next;
            }
#            my @paths = grep { defined $_ } $mogfs->get_paths($opts{big} ? "$key,$cnum" : "$key", 1);
#            unless (@paths) {
#                print "Error: reaped child $pid for chunk $cnum but no paths exist... Retrying...\n";
#                $emit->($cnum);
#                next;
#            }
            delete $chunkbuf{$cnum};
            delete $chunksout{$cnum};
            print "Child $pid successfully finished with chunk $cnum.\n";
        }
    };

    # this function handles parallel threads
    $opts{concurrent} ||= 1;
    $opts{concurrent} = 1 if $opts{concurrent} < 1;
    my $handle_children = sub {
        # here we pause while our children are working
        my $first = 1;
        while ($first || scalar(keys %children) >= $opts{concurrent}) {
            $first = 0;
            $reap_children->();
            select undef, undef, undef, 0.1;
        }

        # now spawn until we hit the limit
        foreach my $cnum (keys %chunkbuf) {
            next if $chunksout{$cnum};
            $emit->($cnum);
            last if scalar(keys %children) >= $opts{concurrent};
        }
    };

    # setup compression stuff
    my $dogzip = 0;
    my $zlib;
    if ($opts{gzip}) {
        # if they turned gzip on we may or may not need this stream, so make it
        $zlib = deflateInit()
            or error("Error: unable to create gzip deflation stream", ERR_FATAL);
    }

    # read one meg chunks while we have data
    my $sum = 0;
    my $readbuf = '';
    while (my $rv = read(O, $readbuf, $read_size)) {
        # if this is a file, and this is our first read, see if it's gzipped
        if (!$sum && $rv >= 2) {
            if (substr($readbuf, 0, 2) eq "\x1f\x8b") {
                # this is already gzipped, so just mark it as such and insert it
                $opts{gzip} = 1;
            } else {
                # now turn on our gzipping if the user wants the output gzipped
                $dogzip = 1 if $opts{gzip};
            }
        }

        # now run it through the deflation stream before we process it here
        if ($dogzip) {
            my ($out, $status) = $zlib->deflate($readbuf);
            error("Error: Deflation failure processing stream", ERR_FATAL)
                unless $status == Z_OK;
            $readbuf = $out;
            $rv = length $readbuf;

            # we don't always get a chunk from deflate
            next unless $rv;
        }

        # now stick our data into our real buffer
        $buf .= $readbuf;
        $bufsize += $rv;
        $sum += $rv;
        $readbuf = '';

        # generate output
        if ($type ne 'tarball' && $size && $size > $read_size) {
            printf "Buffer so far: $bufsize bytes [%.2f%% complete]\r", ($sum / $size * 100);
        } else {
            print "Buffer so far: $bufsize bytes\r";
        }

        # if we have one chunk, handle it
        if ($bufsize >= $chunk_size) {
            $chunkbuf{++$chunknum} = substr($buf, 0, $chunk_size);

            # calculate the md5, print out status, and save this chunk
            my $md5 = md5_hex($buf);
            if ($opts{big}) {
                print "chunk $key,$chunknum: $md5, len = $chunk_size\n";
            } else {
                print "file $key: $md5, len = $chunk_size\n";
            }
            $chunkinfo{$chunknum} = [ $md5, $chunk_size ];

            # reset for the next read loop
            $buf = substr($buf, $chunk_size);
            $bufsize = length $buf;

            # now spawn children to save chunks
            $handle_children->();
        }
    }
    close O;

    # now we need to flush the gzip engine
    if ($dogzip) {
        my ($out, $status) = $zlib->flush;
        error("Error: Deflation failure processing stream", ERR_FATAL)
            unless $status == Z_OK;
        $buf .= $out;
        $bufsize += length $out;
        $sum += length $out;
    }

    # final piece
    if ($buf) {
        $chunkbuf{++$chunknum} = $buf;
        my $md5 = md5_hex($buf);
        if ($opts{big}) {
            print "chunk $key,$chunknum: $md5, len = $bufsize\n";
        } else {
            print "file $key: $md5, len = $bufsize\n";
        }
        $chunkinfo{$chunknum} = [ $md5, $bufsize ];
    }

    # now, while we still have chunks to process...
    while (%chunkbuf) {
        $handle_children->();
        sleep 1;
    }

    # verify replication and chunks
    # not any more.
    my %paths; # { chunknum => [ path, path, path ... ] }
    my %still_need = ( %chunkinfo );


    # prepare the info file
    my $des = $opts{des} || 'no description';
    my $compressed = $opts{gzip} ? '1' : '0';
    #FIXME: add 'partblocks' to info file

    # create the info file
    my $info = <<INFO;
des $des
type $type
compressed $compressed
filename $src
chunks $chunknum
size $sum

INFO
    foreach (sort { $a <=> $b } keys %chunkinfo) {
        $info .= "part $_ bytes=$chunkinfo{$_}->[1] md5=$chunkinfo{$_}->[0] paths: ";
        $info .= join(', ', @{$paths{$_} || []});
        $info .= "\n";
    }

    # now write out the info file
    if ($opts{big}) {
        my $fhinfo = $mogfs->new_file("_big_info:$key", $opts{class})
            or error("ERROR: Unable to create _big_info:$key.", ERR_FATAL);
        $fhinfo->print($info);
        $fhinfo->close()
            or error("ERROR: Unable to save _big_info:$key.", ERR_FATAL);

        # verify info file
        print "Waiting for info file replication...\n";
        while (1) {
            my @paths = $mogfs->get_paths("_big_info:$key", 1);
            next unless scalar(@paths) >= 2;
            foreach my $path (@paths) {
                my $data = get($path);
                error("       FATAL: content mismatch on $path", ERR_FATAL)
                    unless $data eq $info;
            }
            last;
        }

        # now delete our pre file
        print "Deleting pre-insert file...\n";
        $mogfs->delete("_big_pre:$key")
            or error("ERROR: Unable to delete _big_pre:$key", ERR_FATAL);
    }

    # now email and save a receipt
    if ($opts{receipt}) {
        open MAIL, "| $sendmail -t"
            or error("ERROR: Unable to open sendmail binary: $sendmail", ERR_FATAL);
        print MAIL <<MAIL;
To: $opts{receipt}
From: mogtool\@dev.null
Subject: mogtool.$key.receipt

$info
.
MAIL
        close MAIL;
        print "Receipt emailed.\n";

        # now dump to a file
        open FILE, ">mogtool.$key.receipt"
            or error("ERROR: Unable to create file mogtool.$key.receipt in current directory.", ERR_FATAL);
        print FILE $info;
        close FILE;
        print "Receipt stored in mogtool.$key.receipt.\n";
    }
}

sub _parse_info {
    my $info = shift;
    my $res = {};

    # parse out the header data
    $res->{des} = ($info =~ /^des\s+(.+)$/m) ? $1 : undef;
    $res->{type} = ($info =~ /^type\s+(.+)$/m) ? $1 : undef;
    $res->{compressed} = ($info =~ /^compressed\s+(.+)$/m) ? $1 : undef;
    $res->{filename} = ($info =~ /^filename\s+(.+)$/m) ? $1 : undef;
    $res->{chunks} = ($info =~ /^chunks\s+(\d+)$/m) ? $1 : undef;
    $res->{size} = ($info =~ /^size\s+(\d+)$/m) ? $1 : undef;

    # now get the pieces
    $res->{maxnum} = undef;
    while ($info =~ /^part\s+(\d+)\s+bytes=(\d+)\s+md5=(.+)\s+paths:\s+(.+)$/mg) {
        $res->{maxnum} = $1 if !defined $res->{maxnum} || $1 > $res->{maxnum};
        $res->{parts}->{$1} = {
            bytes => $2,
            md5 => $3,
            paths => [ split(/\s*,\s*/, $4) ],
        };
    }

    return $res;
}

sub extract {
    my $key = shift @ARGV;
    my $dest = shift @ARGV;
    abortWithUsage() unless $key && $dest;

    error("Error: key $key isn't valid; must not contain spaces or commas.", ERR_FATAL)
        unless $key =~ /^[^\s\,]+$/;
    unless ($dest eq '-' || $dest eq '.') {
        error("Error: destination exists: $dest (specify --overwrite if you want to kill it)", ERR_FATAL)
            if -e $dest && !$opts{overwrite} && !-b $dest;
    }

    # see if this is really a big file
    my $file;
    if ($opts{big}) {
        my $info = $mogfs->get_file_data("_big_info:$key");
        die "$key doesn't seem to be a valid big file.\n"
            unless $info && $$info;

        # verify validity
        $file = _parse_info($$info);

        # make sure we have enough info
        error("Error: info file doesn't contain the number of chunks", ERR_FATAL)
            unless $file->{chunks};
        error("Error: info file doesn't contain the total size", ERR_FATAL)
            unless $file->{size};

    } else {
        # not a big file, so it has to be of a certain type
        $file->{type} = 'file';
        $file->{maxnum} = 1;
        $file->{parts}->{1} = {
            paths => [ grep { defined $_ } $mogfs->get_paths($key) ],
        };

        # now, if it doesn't exist..
        unless (scalar(@{$file->{parts}->{1}->{paths}})) {
            error("Error: file doesn't exist (or did you forget --bigfile?)", ERR_FATAL);
        }
    }

    # several cases.. going to stdout?
    if ($dest eq '-') {
        *O = *STDOUT;
    } else {
        # open up O as the handle to use for reading data
        if ($file->{type} eq 'file' || $file->{type} eq 'partition' ||
            ($file->{type} eq 'tarball' && $opts{asfile})) {
            # just write it to the file with this name, but don't overwrite?
            if ($dest eq '.') {
                $dest = $file->{filename};
                $dest =~ s!^(.+)/!!;
            }
            if (-b $dest) {
                # if we're targetting a block device...
                warn "FIXME: add in block checking\n";
                open O, ">$dest"
                    or die "Couldn't open $dest: $!\n";
            } elsif (-e $dest) {
                if ($opts{overwrite}) {
                    open O, ">$dest"
                        or die "Couldn't open $dest: $!\n";
                } else {
                    die "File already exists: $dest ... won't overwrite without --overwrite.\n";
                }
            } else {
                open O, ">$dest"
                    or die "Couldn't open $dest: $!\n";
            }

        } elsif ($file->{type} eq 'tarball') {
            my $taropts = ($file->{compressed} ? 'z' : '') . "xf";
            open O, '|-', 'tar', $taropts, '-'
                or die "Couldn't open tar for writing: $!\n";

        } else {
            die "Error: unable to handle type '$file->{type}'\n";
        }
    }

    # start fetching pieces
    foreach my $i (1..$file->{maxnum}) {
        print "Fetching piece $i...\n";

        foreach my $path (@{$file->{parts}->{$i}->{paths} || []}) {
            print "        Trying $path...\n";
            my $data = get($path);
            next unless $data;

            # now verify MD5, etc
            if ($opts{big}) {
                my $len = length $data;
                my $md5 = md5_hex($data);
                print "                ($len bytes, $md5)\n";
                next unless $len == $file->{parts}->{$i}->{bytes} &&
                            $md5 eq $file->{parts}->{$i}->{md5};
            }

            # this chunk verified, write it out
            print O $data;
            last;
        }
    }

    # at this point the file should be complete!
    close O;
    print "Done.\n";

    # now make sure we have enough data
#$ mogtool [opts] extract <key> {<file>,<dir>,<device>}
                                 #=>  -  (for stdout)    (if compressed, add "z" flag)
                                 #=>  .   (to untar)     (if compressed, do nothing???, make .tar.gz file -- unless they use -z again?)
                                 #=> /dev/sda4  (but check /proc/partitions that it's big enough)  (if compress, Compress::Zlib to ungzip
#                                 => foo.jpg  (write it to a file)


    # now check
    exit 0;
}

sub list {
    # list all big files in mogile
    my ($ct, $after, $list);
    while (($after, $list) = $mogfs->list_keys("_big_info:", $after)) {
        last unless $list && @$list;

        # now extract the key and dump it
        foreach my $key (@$list) {
            next unless $key =~ /^_big_info:(.+)$/;

            $key = $1;
            $ct++;

            print "$key\n";
        }
    }
    print "#$ct files found\n";
    exit 0;
}

sub listkey {

    my $key_pattern = shift(@ARGV);
    abortWithUsage() unless $key_pattern;

    # list all files matchine a key
    my ($ct, $after, $list);
    while (($after, $list) = $mogfs->list_keys("$key_pattern", $after)) {
        last unless $list && @$list;

        # now extract the key and dump it
        foreach my $key (@$list) {

            $ct++;

            print "$key\n";
        }
    }
    print "#$ct files found\n";
    exit 0;
}

sub mdelete {
    my $key = shift(@ARGV);
    abortWithUsage() unless $key;

    # delete simple file
    unless ($opts{big}) {
        my $rv = $mogfs->delete($key);
        error("Failed to delete: $key.", ERR_FATAL)
            unless $rv;
        print "Deleted.\n";
        exit 0;
    }

    # delete big file
    my $info = $mogfs->get_file_data("_big_info:$key");
    error("$key doesn't seem to be a valid big file.", ERR_FATAL)
        unless $info && $$info;

    # verify validity
    my $file = _parse_info($$info);

    # make sure we have enough info to delete
    error("Error: info file doesn't contain required information?", ERR_FATAL)
        unless $file->{chunks} && $file->{maxnum};

    # now delete each chunk, best attempt
    foreach my $i (1..$file->{maxnum}) {
        $mogfs->delete("$key,$i");
    }

    # delete the main pieces
    my $rv = $mogfs->delete("_big_info:$key");
    error("Unable to delete _big_info:$key.", ERR_FATAL)
        unless $rv;
    print "Deleted.\n";
    exit 0;
}

abortWithUsage() if $opts{help};


sub abortWithUsage {
    my $msg = join '', @_;

    if ( $msg ) {
        pod2usage( -verbose => 1, -exitval => 1, -message => "$msg" );
    } else {
        pod2usage( -verbose => 1, -exitval => 1 );
    }
}





