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
        <td colspan="4" style="color:magenta">D[0].UTC</td>
        <td colspan="4" style="color:teal">D[0].OW</td>
        <td colspan="4" style="color:pink">D[0].OF</td>
        <td colspan="4" style="color:teal">D[0].HW</td>
        <td colspan="4" style="color:pink">D[0].HF</td>
        <td colspan="4" style="color:teal">D[0].LW</td>
        <td colspan="4" style="color:pink">D[0].LF</td>
        <td colspan="4" style="color:teal">D[0].CW</td>
        <td colspan="4" style="color:pink">D[0].CF</td>
        <td colspan="4" style="color:teal">D[0].VW</td>
        <td colspan="4" style="color:pink">D[0].VF</td>
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
        <td style="color:magenta">0:124</td>
        <td style="color:magenta">1:125</td>
        <td style="color:magenta">2:126</td>
        <td style="color:magenta">3:127</td>
        <td style="color:teal">0:128</td>
        <td style="color:teal">1:129</td>
        <td style="color:teal">2:130</td>
        <td style="color:teal">3:131</td>
        <td style="color:pink">0:132</td>
        <td style="color:pink">1:133</td>
        <td style="color:pink">2:134</td>
        <td style="color:pink">3:135</td>
        <td style="color:teal">0:136</td>
        <td style="color:teal">1:137</td>
        <td style="color:teal">2:138</td>
        <td style="color:teal">3:139</td>
        <td style="color:pink">0:140</td>
        <td style="color:pink">1:141</td>
        <td style="color:pink">2:142</td>
        <td style="color:pink">3:143</td>
        <td style="color:teal">0:144</td>
        <td style="color:teal">1:145</td>
        <td style="color:teal">2:146</td>
        <td style="color:teal">3:147</td>
        <td style="color:pink">0:148</td>
        <td style="color:pink">1:149</td>
        <td style="color:pink">2:150</td>
        <td style="color:pink">3:151</td>
        <td style="color:teal">0:152</td>
        <td style="color:teal">1:153</td>
        <td style="color:teal">2:154</td>
        <td style="color:teal">3:155</td>
        <td style="color:pink">0:156</td>
        <td style="color:pink">1:157</td>
        <td style="color:pink">2:158</td>
        <td style="color:pink">3:159</td>
        <td style="color:teal">0:160</td>
        <td style="color:teal">1:161</td>
        <td style="color:teal">2:162</td>
        <td style="color:teal">3:163</td>
        <td style="color:pink">0:164</td>
        <td style="color:pink">1:165</td>
        <td style="color:pink">2:166</td>
        <td style="color:pink">3:167</td>
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
        <td id="d_0_UTC_0_124" style="color:magenta">1</td>
        <td id="d_0_UTC_1_125" style="color:magenta">1</td>
        <td id="d_0_UTC_2_126" style="color:magenta">0</td>
        <td id="d_0_UTC_3_127" style="color:magenta">0</td>
        <td id="d_0_OW_0_128" style="color:teal">0</td>
        <td id="d_0_OW_1_129" style="color:teal">1</td>
        <td id="d_0_OW_2_130" style="color:teal">0</td>
        <td id="d_0_OW_3_131" style="color:teal">0</td>
        <td id="d_0_OF_0_132" style="color:pink">0</td>
        <td id="d_0_OF_1_133" style="color:pink">0</td>
        <td id="d_0_OF_2_134" style="color:pink">0</td>
        <td id="d_0_OF_3_135" style="color:pink">0</td>
        <td id="d_0_HW_0_136" style="color:teal">1</td>
        <td id="d_0_HW_1_137" style="color:teal">0</td>
        <td id="d_0_HW_2_138" style="color:teal">0</td>
        <td id="d_0_HW_3_139" style="color:teal">1</td>
        <td id="d_0_HF_0_140" style="color:pink">0</td>
        <td id="d_0_HF_1_141" style="color:pink">0</td>
        <td id="d_0_HF_2_142" style="color:pink">0</td>
        <td id="d_0_HF_3_143" style="color:pink">0</td>
        <td id="d_0_LW_0_144" style="color:teal">0</td>
        <td id="d_0_LW_1_145" style="color:teal">0</td>
        <td id="d_0_LW_2_146" style="color:teal">1</td>
        <td id="d_0_LW_3_147" style="color:teal">0</td>
        <td id="d_0_LF_0_148" style="color:pink">0</td>
        <td id="d_0_LF_1_149" style="color:pink">0</td>
        <td id="d_0_LF_2_150" style="color:pink">0</td>
        <td id="d_0_LF_3_151" style="color:pink">0</td>
        <td id="d_0_CW_0_152" style="color:teal">0</td>
        <td id="d_0_CW_1_153" style="color:teal">1</td>
        <td id="d_0_CW_2_154" style="color:teal">0</td>
        <td id="d_0_CW_3_155" style="color:teal">1</td>
        <td id="d_0_CF_0_156" style="color:pink">0</td>
        <td id="d_0_CF_1_157" style="color:pink">0</td>
        <td id="d_0_CF_2_158" style="color:pink">0</td>
        <td id="d_0_CF_3_159" style="color:pink">0</td>
        <td id="d_0_VW_0_160" style="color:teal">1</td>
        <td id="d_0_VW_1_161" style="color:teal">0</td>
        <td id="d_0_VW_2_162" style="color:teal">1</td>
        <td id="d_0_VW_3_163" style="color:teal">0</td>
        <td id="d_0_VF_0_164" style="color:pink">0</td>
        <td id="d_0_VF_1_165" style="color:pink">1</td>
        <td id="d_0_VF_2_166" style="color:pink">0</td>
        <td id="d_0_VF_3_167" style="color:pink">1</td>
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
        <td colspan="4" id="hex_32">4</td>
        <td colspan="4" id="hex_33">0</td>
        <td colspan="4" id="hex_34">9</td>
        <td colspan="4" id="hex_35">0</td>
        <td colspan="4" id="hex_36">2</td>
        <td colspan="4" id="hex_37">0</td>
        <td colspan="4" id="hex_38">5</td>
        <td colspan="4" id="hex_39">0</td>
        <td colspan="4" id="hex_40">A</td>
        <td colspan="4" id="hex_41">5</td>
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
        <td colspan="8" id="word_16">40</td>
        <td colspan="8" id="word_17">90</td>
        <td colspan="8" id="word_18">20</td>
        <td colspan="8" id="word_19">50</td>
        <td colspan="8" id="word_20">A5</td>
    </tr>
</table>

### TwoDatapoints.brclmb contents

OLHCV_BINARYLexical Translation Details:

00 00 00 07 83 16 01 04 10 41 10 2A 22 A9 AA 2C 40 90 20 50 A5 D4 19 72 25 0F 60

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

Encoded Data point format:
| UTC | UTC bits | Open | Open Whole bits | Open Fraction bits | High | High Whole bits| High Fraction bits | Low | Low Whole bits | Low Fraction bits | Close | Close Whole bits | Close Fraction bits | Volume | Volume Whole  bits| Volume Fraction bits |
|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
| 12 | 1100 | 4 | 0100 | 0000 | 9 | 1001 | 0000 | 2 | 0010 | 0000 | 5 | 0101 | 0000 | 10.5 | 1010 | 0101 |
| 13 | 1101 | 4.1 | 0100 | 0001 | 9.7 | 1001 | 0111 | 2.2 | 0010 | 0010 | 5 | 0101 | 0000 | 15.6 | 1111 | 0110 |

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
        <td colspan="4" style="color:magenta">D[0].UTC</td>
        <td colspan="4" style="color:teal">D[0].OW</td>
        <td colspan="4" style="color:pink">D[0].OF</td>
        <td colspan="4" style="color:teal">D[0].HW</td>
        <td colspan="4" style="color:pink">D[0].HF</td>
        <td colspan="4" style="color:teal">D[0].LW</td>
        <td colspan="4" style="color:pink">D[0].LF</td>
        <td colspan="4" style="color:teal">D[0].CW</td>
        <td colspan="4" style="color:pink">D[0].CF</td>
        <td colspan="4" style="color:teal">D[0].VW</td>
        <td colspan="4" style="color:pink">D[0].VF</td>
        <td colspan="4" style="color:magenta">D[1].UTC</td>
        <td colspan="4" style="color:teal">D[1].OW</td>
        <td colspan="4" style="color:pink">D[1].OF</td>
        <td colspan="4" style="color:teal">D[1].HW</td>
        <td colspan="4" style="color:pink">D[1].HF</td>
        <td colspan="4" style="color:teal">D[1].LW</td>
        <td colspan="4" style="color:pink">D[1].LF</td>
        <td colspan="4" style="color:teal">D[1].CW</td>
        <td colspan="4" style="color:pink">D[1].CF</td>
        <td colspan="4" style="color:teal">D[1].VW</td>
        <td colspan="4" style="color:pink">D[1].VF</td>
        <td colspan="4" style="color:red">Excess bits</td>
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
        <td style="color:magenta">0:124</td>
        <td style="color:magenta">1:125</td>
        <td style="color:magenta">2:126</td>
        <td style="color:magenta">3:127</td>
        <td style="color:teal">0:128</td>
        <td style="color:teal">1:129</td>
        <td style="color:teal">2:130</td>
        <td style="color:teal">3:131</td>
        <td style="color:pink">0:132</td>
        <td style="color:pink">1:133</td>
        <td style="color:pink">2:134</td>
        <td style="color:pink">3:135</td>
        <td style="color:teal">0:136</td>
        <td style="color:teal">1:137</td>
        <td style="color:teal">2:138</td>
        <td style="color:teal">3:139</td>
        <td style="color:pink">0:140</td>
        <td style="color:pink">1:141</td>
        <td style="color:pink">2:142</td>
        <td style="color:pink">3:143</td>
        <td style="color:teal">0:144</td>
        <td style="color:teal">1:145</td>
        <td style="color:teal">2:146</td>
        <td style="color:teal">3:147</td>
        <td style="color:pink">0:148</td>
        <td style="color:pink">1:149</td>
        <td style="color:pink">2:150</td>
        <td style="color:pink">3:151</td>
        <td style="color:teal">0:152</td>
        <td style="color:teal">1:153</td>
        <td style="color:teal">2:154</td>
        <td style="color:teal">3:155</td>
        <td style="color:pink">0:156</td>
        <td style="color:pink">1:157</td>
        <td style="color:pink">2:158</td>
        <td style="color:pink">3:159</td>
        <td style="color:teal">0:160</td>
        <td style="color:teal">1:161</td>
        <td style="color:teal">2:162</td>
        <td style="color:teal">3:163</td>
        <td style="color:pink">0:164</td>
        <td style="color:pink">1:165</td>
        <td style="color:pink">2:166</td>
        <td style="color:pink">3:167</td>
        <td style="color:magenta">0:168</td>
        <td style="color:magenta">1:169</td>
        <td style="color:magenta">2:170</td>
        <td style="color:magenta">3:171</td>
        <td style="color:teal">0:172</td>
        <td style="color:teal">1:173</td>
        <td style="color:teal">2:174</td>
        <td style="color:teal">3:175</td>
        <td style="color:pink">0:176</td>
        <td style="color:pink">1:177</td>
        <td style="color:pink">2:178</td>
        <td style="color:pink">3:179</td>
        <td style="color:teal">0:180</td>
        <td style="color:teal">1:181</td>
        <td style="color:teal">2:182</td>
        <td style="color:teal">3:183</td>
        <td style="color:pink">0:184</td>
        <td style="color:pink">1:185</td>
        <td style="color:pink">2:186</td>
        <td style="color:pink">3:187</td>
        <td style="color:teal">0:188</td>
        <td style="color:teal">1:189</td>
        <td style="color:teal">2:190</td>
        <td style="color:teal">3:191</td>
        <td style="color:pink">0:192</td>
        <td style="color:pink">1:193</td>
        <td style="color:pink">2:194</td>
        <td style="color:pink">3:195</td>
        <td style="color:teal">0:196</td>
        <td style="color:teal">1:197</td>
        <td style="color:teal">2:198</td>
        <td style="color:teal">3:199</td>
        <td style="color:pink">0:200</td>
        <td style="color:pink">1:201</td>
        <td style="color:pink">2:202</td>
        <td style="color:pink">3:203</td>
        <td style="color:teal">0:204</td>
        <td style="color:teal">1:205</td>
        <td style="color:teal">2:206</td>
        <td style="color:teal">3:207</td>
        <td style="color:pink">0:208</td>
        <td style="color:pink">0:209</td>
        <td style="color:pink">0:210</td>
        <td style="color:pink">0:211</td>
        <td style="color:red">0:212</td>
        <td style="color:red">1:213</td>
        <td style="color:red">2:214</td>
        <td style="color:red">3:215</td>
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
        <td id="d_0_UTC_0_124" style="color:magenta">1</td>
        <td id="d_0_UTC_1_125" style="color:magenta">1</td>
        <td id="d_0_UTC_2_126" style="color:magenta">0</td>
        <td id="d_0_UTC_3_127" style="color:magenta">0</td>
        <td id="d_0_OW_0_128" style="color:teal">0</td>
        <td id="d_0_OW_1_129" style="color:teal">1</td>
        <td id="d_0_OW_2_130" style="color:teal">0</td>
        <td id="d_0_OW_3_131" style="color:teal">0</td>
        <td id="d_0_OF_0_132" style="color:pink">0</td>
        <td id="d_0_OF_1_133" style="color:pink">0</td>
        <td id="d_0_OF_2_134" style="color:pink">0</td>
        <td id="d_0_OF_3_135" style="color:pink">0</td>
        <td id="d_0_HW_0_136" style="color:teal">1</td>
        <td id="d_0_HW_1_137" style="color:teal">0</td>
        <td id="d_0_HW_2_138" style="color:teal">0</td>
        <td id="d_0_HW_3_139" style="color:teal">1</td>
        <td id="d_0_HF_0_140" style="color:pink">0</td>
        <td id="d_0_HF_1_141" style="color:pink">0</td>
        <td id="d_0_HF_2_142" style="color:pink">0</td>
        <td id="d_0_HF_3_143" style="color:pink">0</td>
        <td id="d_0_LW_0_144" style="color:teal">0</td>
        <td id="d_0_LW_1_145" style="color:teal">0</td>
        <td id="d_0_LW_2_146" style="color:teal">1</td>
        <td id="d_0_LW_3_147" style="color:teal">0</td>
        <td id="d_0_LF_0_148" style="color:pink">0</td>
        <td id="d_0_LF_1_149" style="color:pink">0</td>
        <td id="d_0_LF_2_150" style="color:pink">0</td>
        <td id="d_0_LF_3_151" style="color:pink">0</td>
        <td id="d_0_CW_0_152" style="color:teal">0</td>
        <td id="d_0_CW_1_153" style="color:teal">1</td>
        <td id="d_0_CW_2_154" style="color:teal">0</td>
        <td id="d_0_CW_3_155" style="color:teal">1</td>
        <td id="d_0_CF_0_156" style="color:pink">0</td>
        <td id="d_0_CF_1_157" style="color:pink">0</td>
        <td id="d_0_CF_2_158" style="color:pink">0</td>
        <td id="d_0_CF_3_159" style="color:pink">0</td>
        <td id="d_0_VW_0_160" style="color:teal">1</td>
        <td id="d_0_VW_1_161" style="color:teal">0</td>
        <td id="d_0_VW_2_162" style="color:teal">1</td>
        <td id="d_0_VW_3_163" style="color:teal">0</td>
        <td id="d_0_VF_0_164" style="color:pink">0</td>
        <td id="d_0_VF_1_165" style="color:pink">1</td>
        <td id="d_0_VF_2_166" style="color:pink">0</td>
        <td id="d_0_VF_3_167" style="color:pink">1</td>
        <td id="d_1_UTC_0_168" style="color:magenta">1</td>
        <td id="d_1_UTC_1_169" style="color:magenta">1</td>
        <td id="d_1_UTC_2_170" style="color:magenta">0</td>
        <td id="d_1_UTC_3_171" style="color:magenta">1</td>
        <td id="d_1_OW_0_172" style="color:teal">0</td>
        <td id="d_1_OW_1_173" style="color:teal">1</td>
        <td id="d_1_OW_2_174" style="color:teal">0</td>
        <td id="d_1_OW_3_175" style="color:teal">0</td>
        <td id="d_1_OF_0_176" style="color:pink">0</td>
        <td id="d_1_OF_1_177" style="color:pink">0</td>
        <td id="d_1_OF_2_178" style="color:pink">0</td>
        <td id="d_1_OF_3_179" style="color:pink">1</td>
        <td id="d_1_HW_0_180" style="color:teal">1</td>
        <td id="d_1_HW_1_181" style="color:teal">0</td>
        <td id="d_1_HW_2_182" style="color:teal">0</td>
        <td id="d_1_HW_3_183" style="color:teal">1</td>
        <td id="d_1_HF_0_184" style="color:pink">0</td>
        <td id="d_1_HF_1_185" style="color:pink">1</td>
        <td id="d_1_HF_2_186" style="color:pink">1</td>
        <td id="d_1_HF_3_187" style="color:pink">1</td>
        <td id="d_1_LW_0_188" style="color:teal">0</td>
        <td id="d_1_LW_1_189" style="color:teal">0</td>
        <td id="d_1_LW_2_190" style="color:teal">1</td>
        <td id="d_1_LW_3_191" style="color:teal">0</td>
        <td id="d_1_LF_0_192" style="color:pink">0</td>
        <td id="d_1_LF_1_193" style="color:pink">0</td>
        <td id="d_1_LF_2_194" style="color:pink">1</td>
        <td id="d_1_LF_3_195" style="color:pink">0</td>
        <td id="d_1_CW_0_196" style="color:teal">0</td>
        <td id="d_1_CW_1_197" style="color:teal">1</td>
        <td id="d_1_CW_2_198" style="color:teal">0</td>
        <td id="d_1_CW_3_199" style="color:teal">1</td>
        <td id="d_1_CF_0_200" style="color:pink">0</td>
        <td id="d_1_CF_1_201" style="color:pink">0</td>
        <td id="d_1_CF_2_202" style="color:pink">0</td>
        <td id="d_1_CF_3_203" style="color:pink">0</td>
        <td id="d_1_VW_0_204" style="color:teal">1</td>
        <td id="d_1_VW_1_205" style="color:teal">0</td>
        <td id="d_1_VW_2_206" style="color:teal">1</td>
        <td id="d_1_VW_3_207" style="color:teal">0</td>
        <td id="d_1_VF_0_208" style="color:pink">0</td>
        <td id="d_1_VF_1_209" style="color:pink">1</td>
        <td id="d_1_VF_2_210" style="color:pink">1</td>
        <td id="d_1_VF_3_211" style="color:pink">0</td>
        <td id="excess_0" style="color:red">0</td>
        <td id="excess_1" style="color:red">0</td>
        <td id="excess_2" style="color:red">0</td>
        <td id="excess_3" style="color:red">0</td>
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
        <td colspan="4" id="hex_30">2</td>
        <td colspan="4" id="hex_31">C</td>
        <td colspan="4" id="hex_32">4</td>
        <td colspan="4" id="hex_33">0</td>
        <td colspan="4" id="hex_34">9</td>
        <td colspan="4" id="hex_35">0</td>
        <td colspan="4" id="hex_36">2</td>
        <td colspan="4" id="hex_37">0</td>
        <td colspan="4" id="hex_38">5</td>
        <td colspan="4" id="hex_39">0</td>
        <td colspan="4" id="hex_40">A</td>
        <td colspan="4" id="hex_41">5</td>
        <td colspan="4" id="hex_42">D</td>
        <td colspan="4" id="hex_43">4</td>
        <td colspan="4" id="hex_44">1</td>
        <td colspan="4" id="hex_45">9</td>
        <td colspan="4" id="hex_46">7</td>
        <td colspan="4" id="hex_47">2</td>
        <td colspan="4" id="hex_48">2</td>
        <td colspan="4" id="hex_49">5</td>
        <td colspan="4" id="hex_50">0</td>
        <td colspan="4" id="hex_51">F</td>
        <td colspan="4" id="hex_52">6</td>
        <td colspan="4" id="hex_53">0</td>
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
        <td colspan="8" id="word_15">2C</td>
        <td colspan="8" id="word_16">40</td>
        <td colspan="8" id="word_17">90</td>
        <td colspan="8" id="word_18">20</td>
        <td colspan="8" id="word_19">50</td>
        <td colspan="8" id="word_20">A5</td>
        <td colspan="8" id="word_21">D4</td>
        <td colspan="8" id="word_22">19</td>
        <td colspan="8" id="word_23">72</td>
        <td colspan="8" id="word_24">25</td>
        <td colspan="8" id="word_25">0F</td>
        <td colspan="8" id="word_26">60</td>
    </tr>
</table>
