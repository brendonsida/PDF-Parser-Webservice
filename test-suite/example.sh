#!/bin/bash
#
#       java -jar ./target/tabula-0.8.0-jar-with-dependencies.jar \
#             -a 257.00022,49.9996349999984,379.40022,528.889635 \
#             -f CSV \
#             -o out.csv \
#             ../pdf-tables-for-parsing/Border_Outer_Inner_Excel.pdf

tstdir=tests

#print each file and the contents of the file
runTest()
{
    echo "in runTest..."
    for f in $1/inputs/*        #for each file in inputs dir
    do
        echo "Found file: $f"
        echo "cat $f:"
        echo $(cat $f)
    done
}

#runs all the tests in tstdir
runtests()      #$1 is program being tested
{
    for d in $tstdir/*  #for all files
    do
        echo "im in!: $d"
        if [ -d $d ]    #if its a directory only
        then
            echo "found directory: $d!"
            runTest $d   #then run the test 
        fi
    done
    exit $r
}

# ----------------------
# main part starts here.

if [ ${#} = 0 ]
then
    echo "$# arguments accepted.."
    runtests
fi
