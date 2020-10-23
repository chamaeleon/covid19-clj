# covid19

A simple table generator of Covid-19 statistics based on the NY Times Covid-19 github data repository. 

## Installation

Download from http://example.com/FIXME.

## Usage

    $ java -jar covid19.jar [options] <data directory> <state or county, state>

## Options



## Examples

Assuming the NY Times Covid-19 data in github has been cloned into C:\Temp\covid-19-data

```
PS C:\temp\covid-19-data> covid19 . -d 3 ny
Date           Cases     Daily    7d-avg   28d-avg    Deaths     Daily    7d-avg   28d-avg
2020-08-26    436063       568     591.3     634.3     32499         4       6.9       5.6
2020-08-27    436852       789     618.4     634.7     32507         8       8.0       5.2
2020-08-28    437487       635     608.1     634.4     32515         8       8.4       5.1
```

## License

