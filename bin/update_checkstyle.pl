#!/usr/bin/perl
#

$checkstyle_file = "conf/checkstyle.xml";
open (XML,$checkstyle_file) || die "Cannot open '$checkstyle_file' for reading: $!\n";

while (<XML>)
{
    chomp;
    if (/^\s*<module name=\"([^\"]*)\"(.*$)/)
    {
        $name = $1;
        $rest = $2;

        $modules{$name} = 1;

        if ($rest =~ /<property/)
        {
            @parts = split(/<property/,$rest);
            $params = "";
            foreach (@parts)
            {
                if (/name=\"([^\"]*)\".*value=\"([^\"]*)\"/)
                {
                    $params .= $1 . "=" . $2;
                    $params .= "<";
                }
                elsif (/name=\"([^\"]*)\".*value=\'([^\']*)\'/)
                {
                    $params .= $1 . "=" . $2;
                    $params .= "<";
                }
            }
            $params{$name} = $params;
        }
    }
}
close(XML);

$wiki = "../restunit.svn/wiki/CheckstyleConventions.wiki";
open (OUT,">$wiki") || die "Cannot open $wiki for writing: $!\n";
print OUT "#summary Checkstyle Coding Conventions In Use\n";
print OUT "#labels Phase-Implementation\n";
print OUT "\n";
print OUT "= Overview =\n";
print OUT "\nThis is a list of all [http://checkstyle.sourceforge.net/ Checkstyle] coding conventions in use for the codebase.  This is generated from the {{{conf/checkstyle.xml}}} file.  Do not edit this page.\n\n";
print OUT "= Rules =\n\n";

foreach (sort(keys(%modules)) )
{
    print OUT "  * [http://checkstyle.sourceforge.net/checks.html $_]";
    $params = $params{$_};
    if ($params)
    {
        @params = split(/</,$params);
        %p = ();
        $num_params = 0;
        foreach (@params)
        {
            ($key,$value) = split(/=/,$_);
            $p{$key} = $value;
            $num_params++;
        }

        if ($p{"option"})
        {
            print OUT " *" . $p{"option"}. "*";
            undef $p{"option"};
        }
        elsif ($p{"format"})
        {
            print OUT " *{{{" . $p{"format"} . "}}}*";
            undef $p{"format"};
        }
        elsif ($num_params == 1)
        {
            foreach (sort(keys(%p)))
            {
                print OUT " *$_ - {{{" . $p{$_} . "}}}*";
            }
        }
        print OUT "\n";

        if ($num_params != 1)
        {
            foreach (sort(keys(%p)))
            {
                $key = $_;
                $value = $p{$key};
                if ($value)
                {
                    print OUT "    * *$key* - $value\n";
                }
            }
        }

    }
    else
    {
        print OUT "\n";
    }
}

close (OUT);
