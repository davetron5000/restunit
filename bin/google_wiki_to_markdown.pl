#!/usr/bin/perl

system ("cd ../restunit.svn/wiki ; svn update ; cd -");
$wiki = "../restunit.svn/wiki/Features.wiki";
$output = "README.markdown";

open (WIKI,$wiki) || die "Cannot open $wiki for reading: $!\n";
open (MARKDOWN,">$output") || die "Cannot open $output for writing: $!\b";
while (<WIKI>)
{
    next if /^#summary/;
    next if /^#labels/;
    s/^====/####/;
    s/^===/###/;
    s/^==/##/g;
    s/^=/#/g;
    s/^  #/1./g;
    s/^  \*/\*/g;
    s/=*$//;
    if (/\[/)
    {
        @parts = split(/\[/);
        foreach (@parts)
        {
            ($link,$rest) = split(/\]/);
            ($url,$name) = split(/ /,$link,2);
            if ($url eq "")
            {
                print MARKDOWN $rest;
            }
            else
            {
                print MARKDOWN "[$name]($url) $rest";
            }
        }
    }
    else
    {
        print MARKDOWN;
    }
}
