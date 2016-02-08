#!/bin/bash

f1() 
{
    # num=`ls -l tests | grep ^d | wc -l | xargs`
    # return $num
    ls -l tests | grep ^d | wc -l | xargs
}

echo "Calling function: f1"
output=$(f1)
# f1
echo "printing output:"
echo "Output: $output"
