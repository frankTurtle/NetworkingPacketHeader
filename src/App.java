/**
 * Created by Barret J. Nobel on 4/22/2016.
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class App {
    private static final String ORIGIN         = "origin";
    private static final String PORT           = "port";
    private static final String MTU            = "mtu";
    private static final String DESTINATION    = "destination";
    private static final String OPTION_NUMBER  = "Option Number";
    private static final String VERSION        = "Version"; //.................. final variables to be used for the HashMap keys
    private static final String HEADER_LENGTH  = "Header Length";
    private static final String SERVICE_TYPE   = "Service Type";
    private static final String TOTAL_LENGTH   = "Total Length";
    private static final String ID             = "Identification";
    private static final String FLAG           = "Flags";
    private static final String FRAG_OFFSET    = "Fragmentation Offset";
    private static final String TTLIVE         = "Time To Live";
    private static final String PROTOCOL       = "Protocol";
    private static final String CHECKSUM       = "Checksum";
    private static final String SOURCE_ADDRESS = "Source Address";
    private static final String DEST_ADDRESS   = "Destination Address";

    private static RoutingTable routingTable = new RoutingTable();

    public static void main( String[] args ){
        Header testHeader = new Header( "test", true );
        Options testOptions = new Options( "test", true );
        ArrayList< String > output = new ArrayList<>();

        if( ableToTransmit(testHeader, testOptions, output) ){
            int mtu = routingTable.getMtu(testHeader.getDestAddress());
            int packetSize = ( testHeader.getTotalBytes() > 0 )
                    ? testHeader.getTotalBytes()
                    : testOptions.getTotalBytes();

            if( fragmentPacket(mtu, packetSize) ){ //........................................ if the packet needs to be fragmented
                System.out.println( "File is fragmented, check log." );
                processFragmentation( packetSize, mtu, testHeader, testOptions, output ); //. fragment it!
            }
        }
        writeToFile( output, "output" ); //.................................................. write results to file
    }

    // Method to help with output string
    private static String outputStringHelper( Header testHeader, Options testOptions ){
        return String.format( "%s%n%s%s", getLine(),testHeader.toString(), testOptions.toString() );
    }

    // Method to return a line
    private static String getLine(){ return " --------------------"; }

    // Method to test if the IP is valid for the network
    private static boolean testIpAddress( String ipAddress ){
        return new RoutingTable().ipExistInTable( ipAddress );
    }

    // Helper method to fragment packets n jazz
    private static void processFragmentation( int packetSize, int mtu, Header testHeader,
                                              Options testOptions, ArrayList<String> output ){
        int totalPackets = (int)Math.ceil( packetSize / mtu + 1.5 ); //.......................................... gets the total number of packets we'll have to send
        int ttl = Integer.parseInt(Header.binaryToDecimal(testHeader.getData().get(TTLIVE)) ) - 1; //............ update the TTL
        int dataField = 0; //........................................................................................ instance variables
        int fragOffset = 0;
        int dataLeft = packetSize;

        for( int send = 0; send < totalPackets; send++ ){ //..................................................... loop through each packet
            if( send > 0 ){ //................................................................................... if its not the first packet
                testHeader.setHeaderLength( binaryToArray(Integer.toBinaryString(5))); //........................ set the length to 5
                testHeader.setFragOffset( binaryToArray(Integer.toBinaryString(fragOffset))); //................. set the fragment offsets
                fragOffset += fragOffset; //..................................................................... update offsets to new value for next round
            }

            int totalLength = mtu; //............................................................................ get total length of this network
            int headerLength =
                    Integer.parseInt(Header.binaryToDecimal(testHeader.getData().get(HEADER_LENGTH)) ) * 4; //... get the header length
            int remainder = ((mtu - headerLength) % 8 ); //...................................................... if there's anything left over after mod 8

            if( totalLength > (dataLeft + headerLength) ){ //.................................................... if the length is more than dataLeft + header
                totalLength = dataLeft + headerLength; //........................................................ it means it still needs to be split up
                testHeader.setTotalLength( binaryToArray(Integer.toBinaryString(totalLength))); //............... set the new total length
            }
            else if( remainder > 0 ){ //......................................................................... check if theres a remainder
                totalLength -= remainder; //..................................................................... subtract from total length to make / by 8
                testHeader.setTotalLength( binaryToArray(Integer.toBinaryString(totalLength))); //............... set the new total length
            }

            if( send == 0 ){ //.................................................................................. if its the first packet
                testHeader.setFlag( binaryToArray(Integer.toBinaryString(1))); //................................ set flag to 1
                fragOffset = (totalLength - headerLength) / 8; //................................................ calculate the offset
            }

            testHeader.setTTLIVE( binaryToArray(Integer.toBinaryString(ttl)) ); //............................... add new TTL to header
            dataField = totalLength - headerLength; //........................................................... update values
            dataLeft -= dataField;

            if( send > 0 ){
                if( dataLeft == 0 ) { //......................................................................... if this is the last packet
                    testHeader.setFlag(binaryToArray(Integer.toBinaryString(0))); //............................. set the flag to indicate its the last
                }
            }

            String outputString = ( send == 0 ) //............................................................... if its the end
                    ? "Fragment " + (send + 1) + outputStringHelper(testHeader, testOptions)  //................. special format output
                        + String.format( "%21s: %s%n", "Data Field", dataField )
                    : "Fragment " + (send + 1) + getLine() + "\n" + testHeader.toString() //..................... normal format output
                        + String.format( "%21s: %s%n", "Data Field", dataField);
            if( !(dataField <= 0) )output.add( outputString ); //................................................ add to output to be printed
//            output.add( outputString );
        }
    }

    // Helper method to test packet size vs MTU
    private static boolean fragmentPacket( int mtu, int packetSize ){
        return packetSize > mtu;
    }

    // Method to convert a binary string to an array
    private static int[] binaryToArray( String number ){
        int[] returnArray = new int[ number.length() ];
        for( int i = 0; i < number.length(); i++ ){
            returnArray[ i ] = Character.getNumericValue( number.charAt(i) );
        }

        return returnArray;
    }

    // Method to write the array list input to a file
    private static void writeToFile( ArrayList<String> input, String name ){
        List< String > lines = input; //.............................. makes input a list
        Path file = Paths.get( name + ".txt" ); //..................... create a path to the file
        try{
            Files.write( file, lines, Charset.forName("UTF-8") ); //.. write all lines to the file
        }
        catch( IOException e ){
            System.out.println( "error" + e.toString() );
        }
    }

    // Method to be called in the beginning
    // does basic packet checking to make sure it's able to be transmitted
    private static boolean ableToTransmit( Header testHeader, Options testOptions, ArrayList< String > output ){
        if( !(testIpAddress(testHeader.getDestAddress()) &&
                testIpAddress(testHeader.getSourceAddress())) ){ //..................................................... if there's an error in either address
            String outputString = ( testIpAddress(testHeader.getDestAddress()) ) //..................................... determine source or dest error
                    ? "Source"
                    : "Destination";

            output.add( String.format("Unknown %s: %s", outputString, testHeader.getDestAddress()) ); //................ built error string
            writeToFile( output, "output" ); //................................................................................... write error log
            System.out.println( "Error, check log" );
            System.exit(0);
        }
        else { //....................................................................................................... Check to make sure option number is legit
            int optionNumber = Integer.parseInt( Header.binaryToDecimal( testOptions.getData().get(OPTION_NUMBER)) );//. get number
            if( optionNumber != 9 && optionNumber != 7 && optionNumber != 0 ){ //....................................... if its a not a 0,7,9 its not legit
                output.add( String.format("Option Number Error: %d", optionNumber) ); //................................ build error string
                writeToFile( output, "output" ); //............................................................................... write error log
                System.out.println( "Error, check log" );
                System.exit(0);
            }
        }

        return true;
    }
}
