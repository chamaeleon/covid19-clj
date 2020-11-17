# covid19

A simple table generator of Covid-19 statistics based on the NY Times Covid-19 github data repository, implemented in Clojure.

## Usage

    $ java -jar covid19.jar [options] <data directory> <location>

Location is of the form (without brackets) <state>, <county, state>, or <fips number>.

## Options

Option | Description
-------|------------
-d, --days <n> | Number of days to output, defaults to 28
-s, --search <string> | Simplistic search for fips numbers
-h, --help | Display help

## Examples

Assuming the NY Times Covid-19 data in github has been cloned into C:\Temp\covid-19-data

PS C:\temp\covid-19-data> covid19 . -d 3 ny
Date           Cases     Daily    7d-avg   28d-avg    Deaths     Daily    7d-avg   28d-avg
2020-08-26    436063       568     591.3     634.3     32499         4       6.9       5.6
2020-08-27    436852       789     618.4     634.7     32507         8       8.0       5.2
2020-08-28    437487       635     608.1     634.4     32515         8       8.4       5.1

## License

Copyright 2020, Lars Nilsson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.