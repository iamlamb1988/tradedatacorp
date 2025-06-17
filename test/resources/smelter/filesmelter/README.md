# Description

The purpose of this directory is to contain known correct custom binary files explaining the type of files and contents that will be revealed upon decoding.

## OLHCV_BINARYLexical files
These specific files were written with an OLHCV_BINARYLexical instance
- [OneDatapoint.brclmb](https://github.com/iamlamb1988/tradedatacorp/blob/feature/junit-tests-for-smelters/test/resources/smelter/filesmelter/OneDatapoint.brclmb): [Content Details](#OneDatapoint.brclmb-contents)
- [TwoDatapoints.brclmb](https://github.com/iamlamb1988/tradedatacorp/blob/feature/junit-tests-for-smelters/test/resources/smelter/filesmelter/TwoDatapoints.brclmb): [Content Details](#TwoDatapoints.brclmb-contents)

### OneDatapoint.brclmb contents

OLHCV_BINARYLexical Translation Details:
Initial setup with Mini Lexical format.

Hex Content: 00 00 00 07 83 16 01 04 10 41 10 2A 22 A9 AA 1C 40 90 20 50 A5

| Header |Bit Length | Bit | Translated Value | Notes |
|-|-|-|-|-|
| H[0]=H1[0] = FreeForm Bits | 10 | 0000 0000 00 | NA | Free form transation defined by user |
| H[1]=H1[1] = Time Interval | 25 | 0000 0000 0000 0000 0000 0001 1110 0 | 60 seconds | |
| H[2]=H1[2] = Count Length | 5 | 0001 1 | 3 bits for ?? | |
| H[3]=H1[3] = Bit length of each data point | 9 | 0001 0110 0 | 44 bits | |
| H[4]=H1[4] = Gap bit length betwen Header and Data section | 3 | 000 | 0 bits | |
| H[5]=H1[5] = Data Bit Length of UTC | 6 | 0001 00 | 4 bits for D[n].UTC| |
| H[6]=H1[6] = Data Bit Length of Open, High, Low, Close whole number portions | 6 | 0001 00 | 4 bits for D[n].(OHLC)W | Whole number portion of OHLC Data portions |
| H[7]=H1[7] = Data Bit Length of Open, High, Low, Close fractional portions | 6 | 0001 00 | 4 bits for D[n].(OHLC)F| Fractional portion of OHLC Data portions |
| H[8]=H1[8] = Whole number portion of Volume Data portion | 6 | 0001 00 | 4 bits for D[n].VW| |
| H[9]=H1[9] = Fractional portion of Volume Data portion | 6 | 0001 00 | 4 bits for D[n].VF| |
| H[10]=H1[10] = Number of bits to represent Symbol value of 8 bit ASCII characters | 7 | 0100 000 | 32 bits for ??| NOTE: 32 bits is 4 bytes (1 byte for each char) = "TEST" |
| H[11]=H2[0] | 32 | 0101 0100 0100 0101 0101 0011 0101 0100 | "TEST" Each 8 bit chunk is ASCII char value | NOTE: 0101 0100 = "T", 0100 0101 = "E", 0101 0011 = "S", 0101 0100 = "T" |
| H[12]=H2[1] | 3 | 000 | 0 bits | |
| H[13]=H2[2] | 0 | NA | NA | Value not relevant, only bit length is relevant |

Translated Data point:
| UTC | Open | High | Low | Close | Volume |
|-|-|-|-|-|-|
| 12 | 4 | 9 | 2 | 5 | 10.5 |

Encoded Data point format:
| UTC | UTC bits | Open | Open Whole bits | Open Fraction bits | High | High Whole bits| High Fraction bits | Low | Low Whole bits | Low Fraction bits | Close | Close Whole bits | Close Fraction bits | Volume | Volume Whole  bits| Volume Fraction bits |
|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
| 12 | 1100 | 4 | 0100 | 0000 | 9 | 1001 | 0000 | 2 | 0010 | 0000 | 5 | 0101 | 0000 | 10.5 | 1010 | 0101 |


### TwoDatapoints.brclmb contents

OLHCV_BINARYLexical Translation Details:
- TBA
