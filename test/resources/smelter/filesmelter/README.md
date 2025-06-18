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
| H[2]=H1[2] = Count Length | 5 | 0001 1 | 3 bits for field H[12]=H2[1] | |
| H[3]=H1[3] = Bit length of each data point | 9 | 0001 0110 0 | 44 bits | |
| H[4]=H1[4] = Gap bit length betwen Header and Data section | 3 | 000 | 0 bits | |
| H[5]=H1[5] = Data Bit Length of UTC | 6 | 0001 00 | 4 bits for D[n].UTC| |
| H[6]=H1[6] = Data Bit Length of Open, High, Low, Close whole number portions | 6 | 0001 00 | 4 bits for D[n].(OHLC)W | Whole number portion of OHLC Data portions |
| H[7]=H1[7] = Data Bit Length of Open, High, Low, Close fractional portions | 6 | 0001 00 | 4 bits for D[n].(OHLC)F| Fractional portion of OHLC Data portions |
| H[8]=H1[8] = Whole number portion of Volume Data portion | 6 | 0001 00 | 4 bits for D[n].VW| |
| H[9]=H1[9] = Fractional portion of Volume Data portion | 6 | 0001 00 | 4 bits for D[n].VF| |
| H[10]=H1[10] = Number of bits to represent Symbol value of 8 bit ASCII characters | 7 | 0100 000 | 32 bits for H[11]=H2[0] | NOTE: 32 bits is 4 bytes (1 byte for each char) = "TEST" |
| H[11]=H2[0] | 32 | 0101 0100 0100 0101 0101 0011 0101 0100 | "TEST" Each 8 bit chunk is ASCII char value | NOTE: 0101 0100 = "T", 0100 0101 = "E", 0101 0011 = "S", 0101 0100 = "T" |
| H[12]=H2[1] | 3 | 001 | 1 data pont | |
| H[13]=H2[2] | 0 | NA | NA | Value not relevant, only bit length is relevant |

Translated Data point:
| UTC | Open | High | Low | Close | Volume |
|-|-|-|-|-|-|
| 12 | 4 | 9 | 2 | 5 | 10.5 |

Encoded Data point format:
| UTC | UTC bits | Open | Open Whole bits | Open Fraction bits | High | High Whole bits| High Fraction bits | Low | Low Whole bits | Low Fraction bits | Close | Close Whole bits | Close Fraction bits | Volume | Volume Whole  bits| Volume Fraction bits |
|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
| 12 | 1100 | 4 | 0100 | 0000 | 9 | 1001 | 0000 | 2 | 0010 | 0000 | 5 | 0101 | 0000 | 10.5 | 1010 | 0101 |

<table style="text-align:center;">
    <tr><td>Header</td>
        <td colspan="10" style="color:orange">H1[0]</td><td colspan="25" style="color:cyan">H1[1]</td><td colspan="5" style="color:yellow">H1[2]</td>
    </tr>
    <tr><td>Bit Index</td>
        <td style="color:orange">0:0</td>
        <td style="color:orange">1:1</td>
        <td style="color:orange">2:2</td>
        <td style="color:orange">3:3</td>
        <td style="color:orange">4:4</td>
        <td style="color:orange">5:5</td>
        <td style="color:orange">6:6</td>
        <td style="color:orange">7:7</td>
        <td style="color:orange">8:8</td>
        <td style="color:orange">9:9</td>
        <td style="color:cyan">0:11</td>
        <td style="color:cyan">1:12</td>
        <td style="color:cyan">2:13</td>
        <td style="color:cyan">3:14</td>
        <td style="color:cyan">4:15</td>
        <td style="color:cyan">5:16</td>
        <td style="color:cyan">6:17</td>
        <td style="color:cyan">7:18</td>
        <td style="color:cyan">8:19</td>
        <td style="color:cyan">9:20</td>
        <td style="color:cyan">10:21</td>
        <td style="color:cyan">11:22</td>
        <td style="color:cyan">12:23</td>
        <td style="color:cyan">13:24</td>
        <td style="color:cyan">14:25</td>
        <td style="color:cyan">15:26</td>
        <td style="color:cyan">16:27</td>
        <td style="color:cyan">17:28</td>
        <td style="color:cyan">18:29</td>
        <td style="color:cyan">19:30</td>
        <td style="color:cyan">20:31</td>
        <td style="color:cyan">21:32</td>
        <td style="color:cyan">22:33</td>
        <td style="color:cyan">23:34</td>
        <td style="color:cyan">24:35</td>
        <td style="color:yellow">0:36</td>
        <td style="color:yellow">1:37</td>
        <td style="color:yellow">2:38</td>
        <td style="color:yellow">3:39</td>
        <td style="color:yellow">4:40</td>
    </tr>
    <tr><td>Bit</td>
        <td id="h_0_0_0" style="color:orange">0</td>
        <td id="h_0_1_1" style="color:orange">0</td>
        <td id="h_0_2_2" style="color:orange">0</td>
        <td id="h_0_3_3" style="color:orange">0</td>
        <td id="h_0_4_4" style="color:orange">0</td>
        <td id="h_0_5_5" style="color:orange">0</td>
        <td id="h_0_6_6" style="color:orange">0</td>
        <td id="h_0_7_7" style="color:orange">0</td>
        <td id="h_0_8_8" style="color:orange">0</td>
        <td id="h_0_9_9" style="color:orange">0</td>
        <td id="h_1_0_10" style="color:cyan">0</td>
        <td id="h_1_1_11" style="color:cyan">0</td>
        <td id="h_1_2_12" style="color:cyan">0</td>
        <td id="h_1_3_13" style="color:cyan">0</td>
        <td id="h_1_4_14" style="color:cyan">0</td>
        <td id="h_1_5_15" style="color:cyan">0</td>
        <td id="h_1_6_16" style="color:cyan">0</td>
        <td id="h_1_7_17" style="color:cyan">0</td>
        <td id="h_1_8_18" style="color:cyan">0</td>
        <td id="h_1_9_19" style="color:cyan">0</td>
        <td id="h_1_10_20" style="color:cyan">0</td>
        <td id="h_1_11_21" style="color:cyan">0</td>
        <td id="h_1_12_22" style="color:cyan">0</td>
        <td id="h_1_13_23" style="color:cyan">0</td>
        <td id="h_1_14_24" style="color:cyan">0</td>
        <td id="h_1_15_25" style="color:cyan">0</td>
        <td id="h_1_16_26" style="color:cyan">0</td>
        <td id="h_1_17_27" style="color:cyan">0</td>
        <td id="h_1_18_28" style="color:cyan">0</td>
        <td id="h_1_19_29" style="color:cyan">1</td>
        <td id="h_1_20_30" style="color:cyan">1</td>
        <td id="h_1_21_31" style="color:cyan">1</td>
        <td id="h_1_22_32" style="color:cyan">1</td>
        <td id="h_1_23_33" style="color:cyan">0</td>
        <td id="h_1_24_34" style="color:cyan">0</td>
        <td id="h_2_0_35" style="color:yellow">0</td>
        <td id="h_2_1_36" style="color:yellow">0</td>
        <td id="h_2_2_37" style="color:yellow">0</td>
        <td id="h_2_3_38" style="color:yellow">1</td>
        <td id="h_2_4_39" style="color:yellow">1</td>
    </tr>
    <tr><td>Hex</td>
        <td colspan="4" id="hex_0">0</td>
        <td colspan="4" id="hex_1">0</td>
        <td colspan="4" id="hex_2">0</td>
        <td colspan="4" id="hex_3">0</td>
        <td colspan="4" id="hex_4">0</td>
        <td colspan="4" id="hex_5">0</td>
        <td colspan="4" id="hex_6">0</td>
        <td colspan="4" id="hex_7">7</td>
        <td colspan="4" id="hex_8">8</td>
        <td colspan="4" id="hex_9">3</td>
    </tr>
    <tr><td>Word</td>
        <td colspan="8" id="word_0">00</td>
        <td colspan="8" id="word_1">00</td>
        <td colspan="8" id="word_2">00</td>
        <td colspan="8" id="word_3">07</td>
        <td colspan="8" id="word_4">83</td>
    </tr>
</table>
### TwoDatapoints.brclmb contents

OLHCV_BINARYLexical Translation Details:
- TBA
