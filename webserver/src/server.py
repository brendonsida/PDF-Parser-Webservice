import subprocess
subprocess.call([
    'java', '-jar', './target/tabula-0.8.0-jar-with-dependencies.jar', 
    '-a', '257.00022,49.9996349999984,379.40022,528.889635', 
    '-f', 'CSV', 
    '-o', 'temp_out_csv',
    '../../sample_PDF_input/Border_Outer_Inner_Excel.pdf'
])

# '-a 257.00022,49.9996349999984,379.40022,528.889635 -f CSV -o temp_out_csv ../sample_PDF_input/Border_Outer_Inner_Excel.pdf'
