# NetworkingPacketHeader
Implement in software the IP Protocol for Router 1

# Test Data:
01000111000000000000000111000100

00000000000110010000000000000000

00000111000001100000000000000000

01101110001100010010000101001100

01101110011101010111010111101110‬

1024


# Output:
Fragment 1 --------------------

              Version: 4
        Header Length: 7
         Service Type: 0
         Total Length: 508
       Identification: 25
                Flags: 1
 Fragmentation Offset: 0
         Time To Live: 6
             Protocol: 6
             Checksum: 0
       Source Address: 110.49.33.76
  Destination Address: 110.117.117.238
            Copy Flag: 0
         Option Class: 0
        Option Number: 0
               Length: 0
              Pointer: 4
           Data Field: 480

Fragment 2 --------------------

              Version: 4
        Header Length: 5
         Service Type: 0
         Total Length: 508
       Identification: 25
                Flags: 1
 Fragmentation Offset: 60
         Time To Live: 6
             Protocol: 6
             Checksum: 0
       Source Address: 110.49.33.76
  Destination Address: 110.117.117.238
           Data Field: 488

Fragment 3 --------------------

              Version: 4
        Header Length: 5
         Service Type: 0
         Total Length: 76
       Identification: 25
                Flags: 0
 Fragmentation Offset: 120
         Time To Live: 6
             Protocol: 6
             Checksum: 0
       Source Address: 110.49.33.76
  Destination Address: 110.117.117.238
           Data Field: 56
