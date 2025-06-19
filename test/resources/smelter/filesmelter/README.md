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
        <td colspan="10" style="color:orange">H[0]=H1[0]</td>
        <td colspan="25" style="color:cyan">H[1]=H1[1]</td>
        <td colspan="5" style="color:yellow">H[2]=H1[2]</td>
        <td colspan="9" style="color:green">H[3]=H1[3]</td>
        <td colspan="3" style="color:orange">H[4]=H1[4]</td>
        <td colspan="6" style="color:cyan">H[5]=H1[5]</td>
        <td colspan="6" style="color:yellow">H[6]=H1[6]</td>
        <td colspan="6" style="color:green">H[7]=H1[7]</td>
        <td colspan="6" style="color:orange">H[8]=H1[8]</td>
        <td colspan="6" style="color:cyan">H[9]=H1[9]</td>
        <td colspan="7" style="color:yellow">H[10]=H1[10]</td>
        <td colspan="32" style="color:green">H[11]=H2[0]</td>
        <td colspan="3" style="color:orange">H[12]=H2[1]</td>
        <td colspan="1" style="color:cyan">H[13]=H2[2]</td>
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
        <td style="color:cyan">0:10</td>
        <td style="color:cyan">1:11</td>
        <td style="color:cyan">2:12</td>
        <td style="color:cyan">3:13</td>
        <td style="color:cyan">4:14</td>
        <td style="color:cyan">5:15</td>
        <td style="color:cyan">6:16</td>
        <td style="color:cyan">7:17</td>
        <td style="color:cyan">8:18</td>
        <td style="color:cyan">9:19</td>
        <td style="color:cyan">10:20</td>
        <td style="color:cyan">11:21</td>
        <td style="color:cyan">12:22</td>
        <td style="color:cyan">13:23</td>
        <td style="color:cyan">14:24</td>
        <td style="color:cyan">15:25</td>
        <td style="color:cyan">16:26</td>
        <td style="color:cyan">17:27</td>
        <td style="color:cyan">18:28</td>
        <td style="color:cyan">19:29</td>
        <td style="color:cyan">20:30</td>
        <td style="color:cyan">21:31</td>
        <td style="color:cyan">22:32</td>
        <td style="color:cyan">23:33</td>
        <td style="color:cyan">24:34</td>
        <td style="color:yellow">0:35</td>
        <td style="color:yellow">1:36</td>
        <td style="color:yellow">2:37</td>
        <td style="color:yellow">3:38</td>
        <td style="color:yellow">4:39</td>
        <td style="color:green">0:40</td>
        <td style="color:green">1:41</td>
        <td style="color:green">2:42</td>
        <td style="color:green">3:43</td>
        <td style="color:green">4:44</td>
        <td style="color:green">5:45</td>
        <td style="color:green">6:46</td>
        <td style="color:green">7:47</td>
        <td style="color:green">8:48</td>
        <td style="color:orange">0:49</td>
        <td style="color:orange">1:50</td>
        <td style="color:orange">2:51</td>
        <td style="color:cyan">0:52</td>
        <td style="color:cyan">1:53</td>
        <td style="color:cyan">2:54</td>
        <td style="color:cyan">3:55</td>
        <td style="color:cyan">4:56</td>
        <td style="color:cyan">5:57</td>
        <td style="color:yellow">0:58</td>
        <td style="color:yellow">1:59</td>
        <td style="color:yellow">2:60</td>
        <td style="color:yellow">3:61</td>
        <td style="color:yellow">4:62</td>
        <td style="color:yellow">5:63</td>
        <td style="color:green">0:64</td>
        <td style="color:green">1:65</td>
        <td style="color:green">2:66</td>
        <td style="color:green">3:67</td>
        <td style="color:green">4:68</td>
        <td style="color:green">5:69</td>
        <td style="color:orange">0:70</td>
        <td style="color:orange">1:71</td>
        <td style="color:orange">2:72</td>
        <td style="color:orange">3:73</td>
        <td style="color:orange">4:74</td>
        <td style="color:orange">5:75</td>
        <td style="color:cyan">0:76</td>
        <td style="color:cyan">1:77</td>
        <td style="color:cyan">2:78</td>
        <td style="color:cyan">3:79</td>
        <td style="color:cyan">4:80</td>
        <td style="color:cyan">5:81</td>
        <td style="color:yellow">0:82</td>
        <td style="color:yellow">1:83</td>
        <td style="color:yellow">2:84</td>
        <td style="color:yellow">3:85</td>
        <td style="color:yellow">4:86</td>
        <td style="color:yellow">5:87</td>
        <td style="color:yellow">6:88</td>
        <td style="color:green">0:89</td>
        <td style="color:green">1:90</td>
        <td style="color:green">2:91</td>
        <td style="color:green">3:92</td>
        <td style="color:green">4:93</td>
        <td style="color:green">5:94</td>
        <td style="color:green">6:95</td>
        <td style="color:green">7:96</td>
        <td style="color:green">8:97</td>
        <td style="color:green">9:98</td>
        <td style="color:green">10:99</td>
        <td style="color:green">11:100</td>
        <td style="color:green">12:101</td>
        <td style="color:green">13:102</td>
        <td style="color:green">14:103</td>
        <td style="color:green">15:104</td>
        <td style="color:green">16:105</td>
        <td style="color:green">17:106</td>
        <td style="color:green">18:107</td>
        <td style="color:green">19:108</td>
        <td style="color:green">20:109</td>
        <td style="color:green">21:110</td>
        <td style="color:green">22:111</td>
        <td style="color:green">23:112</td>
        <td style="color:green">24:113</td>
        <td style="color:green">25:114</td>
        <td style="color:green">26:115</td>
        <td style="color:green">27:116</td>
        <td style="color:green">28:117</td>
        <td style="color:green">29:118</td>
        <td style="color:green">30:119</td>
        <td style="color:green">31:120</td>
        <td style="color:orange">0:121</td>
        <td style="color:orange">1:122</td>
        <td style="color:orange">2:123</td>
        <td style="color:cyan">NA</td>
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
        <td id="h_3_0_40" style="color:green">0</td>
        <td id="h_3_1_41" style="color:green">0</td>
        <td id="h_3_2_42" style="color:green">0</td>
        <td id="h_3_3_43" style="color:green">1</td>
        <td id="h_3_4_44" style="color:green">0</td>
        <td id="h_3_5_45" style="color:green">1</td>
        <td id="h_3_6_46" style="color:green">1</td>
        <td id="h_3_7_47" style="color:green">0</td>
        <td id="h_3_8_48" style="color:green">0</td>
        <td id="h_4_0_49" style="color:orange">0</td>
        <td id="h_4_1_50" style="color:orange">0</td>
        <td id="h_4_2_51" style="color:orange">0</td>
        <td id="h_5_0_52" style="color:cyan">0</td>
        <td id="h_5_1_53" style="color:cyan">0</td>
        <td id="h_5_2_54" style="color:cyan">0</td>
        <td id="h_5_3_55" style="color:cyan">1</td>
        <td id="h_5_4_56" style="color:cyan">0</td>
        <td id="h_5_5_57" style="color:cyan">0</td>
        <td id="h_6_0_58" style="color:yellow">0</td>
        <td id="h_6_1_59" style="color:yellow">0</td>
        <td id="h_6_2_60" style="color:yellow">0</td>
        <td id="h_6_3_61" style="color:yellow">1</td>
        <td id="h_6_4_62" style="color:yellow">0</td>
        <td id="h_6_5_63" style="color:yellow">0</td>
        <td id="h_7_0_64" style="color:green">0</td>
        <td id="h_7_1_65" style="color:green">0</td>
        <td id="h_7_2_66" style="color:green">0</td>
        <td id="h_7_3_67" style="color:green">1</td>
        <td id="h_7_4_68" style="color:green">0</td>
        <td id="h_7_5_69" style="color:green">0</td>
        <td id="h_8_0_70" style="color:orange">0</td>
        <td id="h_8_1_71" style="color:orange">0</td>
        <td id="h_8_2_72" style="color:orange">0</td>
        <td id="h_8_3_73" style="color:orange">1</td>
        <td id="h_8_4_74" style="color:orange">0</td>
        <td id="h_8_5_75" style="color:orange">0</td>
        <td id="h_9_0_76" style="color:cyan">0</td>
        <td id="h_9_1_77" style="color:cyan">0</td>
        <td id="h_9_2_78" style="color:cyan">0</td>
        <td id="h_9_3_79" style="color:cyan">1</td>
        <td id="h_9_4_80" style="color:cyan">0</td>
        <td id="h_9_5_81" style="color:cyan">0</td>
        <td id="h_10_0_82" style="color:yellow">0</td>
        <td id="h_10_1_83" style="color:yellow">1</td>
        <td id="h_10_2_84" style="color:yellow">0</td>
        <td id="h_10_3_85" style="color:yellow">0</td>
        <td id="h_10_4_86" style="color:yellow">0</td>
        <td id="h_10_5_87" style="color:yellow">0</td>
        <td id="h_10_6_88" style="color:yellow">0</td>
        <td id="h_11_0_89" style="color:green">0</td>
        <td id="h_11_1_91" style="color:green">1</td>
        <td id="h_11_2_91" style="color:green">0</td>
        <td id="h_11_3_92" style="color:green">1</td>
        <td id="h_11_4_93" style="color:green">0</td>
        <td id="h_11_5_94" style="color:green">1</td>
        <td id="h_11_6_95" style="color:green">0</td>
        <td id="h_11_7_96" style="color:green">0</td>
        <td id="h_11_8_97" style="color:green">0</td>
        <td id="h_11_9_98" style="color:green">1</td>
        <td id="h_11_10_99" style="color:green">0</td>
        <td id="h_11_11_100" style="color:green">0</td>
        <td id="h_11_12_101" style="color:green">0</td>
        <td id="h_11_13_102" style="color:green">1</td>
        <td id="h_11_14_103" style="color:green">0</td>
        <td id="h_11_15_104" style="color:green">1</td>
        <td id="h_11_16_105" style="color:green">0</td>
        <td id="h_11_17_106" style="color:green">1</td>
        <td id="h_11_18_107" style="color:green">0</td>
        <td id="h_11_19_108" style="color:green">1</td>
        <td id="h_11_20_109" style="color:green">0</td>
        <td id="h_11_21_110" style="color:green">0</td>
        <td id="h_11_22_111" style="color:green">1</td>
        <td id="h_11_23_112" style="color:green">1</td>
        <td id="h_11_24_113" style="color:green">0</td>
        <td id="h_11_25_114" style="color:green">1</td>
        <td id="h_11_26_115" style="color:green">0</td>
        <td id="h_11_27_116" style="color:green">1</td>
        <td id="h_11_28_117" style="color:green">0</td>
        <td id="h_11_29_118" style="color:green">1</td>
        <td id="h_11_30_119" style="color:green">0</td>
        <td id="h_11_31_120" style="color:green">0</td>
        <td id="h_12_0_121" style="color:orange">0</td>
        <td id="h_12_1_122" style="color:orange">0</td>
        <td id="h_12_2_123" style="color:orange">1</td>
        <td style="color:cyan">NA</td>
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
        <td colspan="4" id="hex_10">1</td>
        <td colspan="4" id="hex_11">6</td>
        <td colspan="4" id="hex_12">0</td>
        <td colspan="4" id="hex_13">1</td>
        <td colspan="4" id="hex_14">0</td>
        <td colspan="4" id="hex_15">4</td>
        <td colspan="4" id="hex_16">1</td>
        <td colspan="4" id="hex_17">0</td>
        <td colspan="4" id="hex_18">4</td>
        <td colspan="4" id="hex_19">1</td>
        <td colspan="4" id="hex_20">1</td>
        <td colspan="4" id="hex_21">0</td>
        <td colspan="4" id="hex_22">2</td>
        <td colspan="4" id="hex_23">A</td>
        <td colspan="4" id="hex_24">2</td>
        <td colspan="4" id="hex_25">2</td>
        <td colspan="4" id="hex_26">A</td>
        <td colspan="4" id="hex_27">9</td>
        <td colspan="4" id="hex_28">A</td>
        <td colspan="4" id="hex_29">A</td>
        <td colspan="4" id="hex_30">1</td>
        <td colspan="4" id="hex_31">C</td>
    </tr>
    <tr><td>Word</td>
        <td colspan="8" id="word_0">00</td>
        <td colspan="8" id="word_1">00</td>
        <td colspan="8" id="word_2">00</td>
        <td colspan="8" id="word_3">07</td>
        <td colspan="8" id="word_4">83</td>
        <td colspan="8" id="word_5">16</td>
        <td colspan="8" id="word_6">01</td>
        <td colspan="8" id="word_7">04</td>
        <td colspan="8" id="word_8">10</td>
        <td colspan="8" id="word_9">41</td>
        <td colspan="8" id="word_10">10</td>
        <td colspan="8" id="word_11">2A</td>
        <td colspan="8" id="word_12">22</td>
        <td colspan="8" id="word_13">A9</td>
        <td colspan="8" id="word_14">AA</td>
        <td colspan="8" id="word_15">1C</td>
    </tr>
</table>
### TwoDatapoints.brclmb contents

OLHCV_BINARYLexical Translation Details:
- TBA
