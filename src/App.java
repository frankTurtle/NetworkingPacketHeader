/**
 * Created by Barret J. Nobel on 4/22/2016.
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
            System.out.println( "Lets begin" );
            int mtu = routingTable.getMtu(testHeader.getDestAddress());
            int packetSize = ( testHeader.getTotalBytes() > 0 )
                    ? testHeader.getTotalBytes()
                    : testOptions.getTotalBytes();

            if( fragmentPacket(mtu, packetSize) ){
                System.out.println( "Fragment" );
                int totalPackets = (int)((double)packetSize / mtu + .5); //.. gets the total number of packets we'll have to send
                int ttl = Integer.parseInt(Header.binaryToDecimal(testHeader.getData().get(TTLIVE)) ) - 1; //................ update the TTL
                int dataField;

                for( int send = 0; send < totalPackets; send++ ){
                    int totalLength = mtu;
                    int headerLength = Integer.parseInt(Header.binaryToDecimal(testHeader.getData().get(HEADER_LENGTH)) ) * 4;
                    int remainder = ((mtu - headerLength) % 8 );

                    if( remainder > 0 ){
                        totalLength -= remainder;
                        testHeader.setTotalLength( binaryToArray(Integer.toBinaryString(totalLength)));
                    }

                    testHeader.setTTLIVE( binaryToArray(Integer.toBinaryString(ttl)) ); //........................................ add new TTL to header
                    dataField = totalLength - headerLength;

                    output.add( testHeader.toString() + String.format( "%21s: %s%n", "Data Field", dataField) );
                }

            }
        }

//        System.out.println( testIpAddress(testHeader.getSourceAddress()) );

//        for( int row = 0; row < routingTable.getTableRows().size(); row++ ){
//            System.out.println( row+1 + ": "  + Arrays.toString( routingTable.getTableRows().get(row).get(DESTINATION)) );
//        }

//        Options testOptions = new Options( "test", true );
//        Options test2Options = new Options( "test2", true );
//        Options test3Options = new Options( "test3", true );
//        Options test4Options = new Options( "testEmpty", true );

//        System.out.println( test4Options );

//        for( int[] num : test2Options.getRecordRoute() ){
//            System.out.println(Arrays.toString(num));
//        }

//        for( int[] num : test4Options.getSourceRoute() ){
//            System.out.println(Arrays.toString(num));
//        }


//        System.out.println( testIpAddress( test.getSourceAddress()) );

//        output.add( "Success" );
//        output.add( "hooray" );

//        writeToFile( output );

//        System.out.println( Integer.parseInt(Integer.toBinaryString(110), 2) );
        writeToFile( output, "output" );
    }

    // Method to test if the IP is valid for the network
    private static boolean testIpAddress( String ipAddress ){
        return new RoutingTable().ipExistInTable( ipAddress );
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
