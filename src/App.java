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
    private static final String OPTION_NUMBER  = "Option Number"; //.. private final instance variables for keys
    private static final String HEADER_LENGTH  = "Header Length";
    private static final String TTLIVE         = "Time To Live";
    private static final String POINTER        = "Pointer";
    private static final String LENGTH = "Length";

    private static RoutingTable routingTable = new RoutingTable();

    public static void main( String[] args ){
        Header testHeader = new Header( "noFrag", true ); //............................... get the packet header
        Options testOptions = new Options( "noFrag", true ); //............................ get the packet options
        ArrayList< String > output = new ArrayList<>(); //............................... output array for messages / errors

        if( ableToTransmit(testHeader, testOptions, output) ){ //........................ check if able to transmit
            int mtu = routingTable.getMtu(testHeader.getDestAddress()); //............... get the MTU
            int packetSize = ( testHeader.getTotalBytes() > 0 ) //....................... get packet size
                    ? testHeader.getTotalBytes()
                    : testOptions.getTotalBytes();

            processFragmentation( packetSize, mtu, testHeader, testOptions, output ); //. fragment it!
        }
        writeToFile( output, "output" ); //.............................................. write results to file
    }

    // Method to help with output string
    private static String outputStringHelper( Header testHeader, Options testOptions ){
        int optionNum = Integer.parseInt( Header.binaryToDecimal(testOptions.getData().get(OPTION_NUMBER)) );
        return String.format( "%s%n%s%s",
                getLine(),testHeader.toString(), (optionNum == 7 || optionNum == 9) ? testOptions.toString() : "" );
    }

    // Method to return a line
    private static String getLine(){ return " --------------------"; }

    // Method to test if the IP is valid for the network
    private static boolean testIpAddress( String ipAddress ){
        return new RoutingTable().ipExistInTable( ipAddress );
    }

    // Helper method to fragment packets n jazz
    private static void  processFragmentation( int packetSize, int mtu, Header testHeader,
                                              Options testOptions, ArrayList<String> output ){
        int totalPackets = (int)Math.ceil( packetSize / mtu + 1.5 ); //.......................................... gets the total number of packets we'll have to send
        int ttl = Integer.parseInt(Header.binaryToDecimal(testHeader.getData().get(TTLIVE)) ) - 1; //............ update the TTL
        int dataField; //........................................................................................ instance variables
        int fragOffset = 0;
        int dataLeft = packetSize;

        for( int send = 0; send < totalPackets; send++ ){ //..................................................... loop through each packet
            if( send > 0 ){ //................................................................................... if its not the first packet
                testHeader.setHeaderLength( binaryToArray(Integer.toBinaryString(5))); //........................ set the length to 5
                testHeader.setFragOffset( binaryToArray(Integer.toBinaryString(fragOffset * send ))); //......... set the fragment offsets
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
                incrementPointer( testOptions );
                int num = Integer.parseInt( Header.binaryToDecimal(testOptions.getData().get(OPTION_NUMBER)) );
                if( num == 7 ){ //............................................................................... if its a Record Route
                    String maskedAddress = RoutingTable.convertWithMask(testHeader.getDestAddress());
                    String[] ipArray = createStringIPArray(RoutingTable.getDestinationIPFromAddress(maskedAddress));
                    testOptions.addRecordRouteIPAddress( convertStringArrayIntoIPArray(ipArray) );
                    incrementLength( testOptions );
                }
                else if( num == 9 ){ //.......................................................................... if its a Strict Route
                    String maskedAddress = RoutingTable.convertWithMask(testHeader.getDestAddress());
                    String[] ipArray = createStringIPArray(RoutingTable.getDestinationIPFromAddress(maskedAddress));
                    testOptions.addSourceRouteIPAddress( convertStringArrayIntoIPArray(ipArray) );
                    incrementLength( testOptions );
                }
            }

            testHeader.setTTLIVE( binaryToArray(Integer.toBinaryString(ttl)) ); //............................... add new TTL to header
            dataField = totalLength - headerLength; //........................................................... update values
            dataLeft -= dataField;

            if( send > 0 ){
                if( dataLeft == 0 ) { //......................................................................... if this is the last packet
                    testHeader.setFlag(binaryToArray(Integer.toBinaryString(0))); //............................. set the flag to indicate its the last
                }
            }

            String outputString = ( send == 0 || testOptions.getCopyFlag() == 1 ) //............................. if its the end
                    ? "Fragment " + (send + 1) + outputStringHelper(testHeader, testOptions)  //................. special format output
                        + String.format( "%21s: %s%n", "Data Field", dataField )
                    : "Fragment " + (send + 1) + getLine() + "\n" + testHeader.toString() //..................... normal format output
                        + String.format( "%21s: %s%n", "Data Field", dataField);
            if( !(dataField <= 0) )output.add( outputString ); //................................................ add to output to be printed
        }
    }

    // Helper method to test packet size vs MTU
    private static boolean fragmentPacket( int mtu, int packetSize ){
        return packetSize > mtu;
    }

    // Helper method to convert a string into an binary array
    public static String[] createStringIPArray(String stringToConvert ){
        int[] returnArray = new int[4]; //.................................. create an array to return
        String[] nums = stringToConvert.split("\\.");

        for( int i = 0; i < nums.length; i++ ){
            nums[ i ] = Integer.toBinaryString(Integer.parseInt(nums[i]));
            while( (nums[i].length() % 8) != 0 ){  //....................... if it doesnt equal 8 chars add leading 0's
                nums[ i ] = String.format( "0%s", nums[i]);
            }
        }
        for( int i = 0; i < returnArray.length; i++ ){
            returnArray[i] = Integer.parseInt( nums[i] );
        }

        return nums;
    }

    // Helper method to convert a string array into one long binary array for an ip address
    public static int[] convertStringArrayIntoIPArray( String[] convertMe ){
        int[] returnArray = new int[32];
        int startIndex = 0;

        for( String segment : convertMe ){
            for( int index = 0; index < segment.length(); index++ ){ //...................................... loop through each array in the packet labels
                int length = 8; //........................................................................... determine length of the array with the key
                for( int bitIndex = 0; bitIndex < length; bitIndex++ ){ //................................... loop through each value in the array from the key
                    returnArray[ startIndex ] = Character.getNumericValue( segment.charAt( bitIndex ) ); //.. if it's not a 0 increment the value in the current array to a 1
                    startIndex++;
                }

                segment = segment.substring( length ); //..................................................... cut the data we just processed out of the line
            }
        }

        return returnArray;
    }

    // Helper method to increment the Pointer
    private static void incrementPointer( Options option ){
        int currentPointer =  Integer.parseInt(Header.binaryToDecimal(option.getData().get(POINTER)) ); //. get current pointer
        currentPointer += 4; //............................................................................ add four
        String binaryString = Integer.toBinaryString( currentPointer ); //................................. make into binary string
        option.getData().put(POINTER, binaryToArray(binaryString) ); //.................................... convert to array then put into hash
    }

    // Helper method to increment the Length
    private static void incrementLength( Options option ){
        int currentLength = Integer.parseInt(Header.binaryToDecimal(option.getData().get(LENGTH)) ); //... get current Length

        currentLength += 4; //............................................................................ add four
        String binaryString = Integer.toBinaryString( currentLength ); //................................. make into binary string
        option.getData().put(HEADER_LENGTH, binaryToArray(binaryString) ); //............................. convert to array then put into hash
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
            String address  = ( testIpAddress(testHeader.getDestAddress()) ) //......................................... determine source or dest error
                    ? testHeader.getSourceAddress()
                    : testHeader.getDestAddress();

            output.add( String.format("Unknown %s: %s", outputString, address) ); //.................................... built error string
            writeToFile( output, "output" ); //......................................................................... write error log
            System.out.println( "Error, check log" );
            System.exit(0);
        }
        else { //....................................................................................................... Check to make sure option number is legit
            int optionNumber = Integer.parseInt( Header.binaryToDecimal( testOptions.getData().get(OPTION_NUMBER)) );//. get number
            if( optionNumber != 9 && optionNumber != 7 && optionNumber != 0 ){ //....................................... if its a not a 0,7,9 its not legit
                output.add( String.format("Option Number Error: %d", optionNumber) ); //................................ build error string
                writeToFile( output, "output" ); //..................................................................... write error log
                System.out.println( "Error, check log" );
                System.exit(0);
            }
            if( testHeader.getTTLIVE() == 1 ){
                output.add( "Packet Timeout" ); //...................................................................... build error string
                writeToFile( output, "output" ); //..................................................................... write error log
                System.out.println( "Error, check log" );
                System.exit(0);
            }
            int mtu = routingTable.getMtu(testHeader.getDestAddress());
            int packetSize = ( testHeader.getTotalBytes() > 0 )
                    ? testHeader.getTotalBytes()
                    : testOptions.getTotalBytes();

            if( testHeader.getFlag()[1] == 1 && fragmentPacket(mtu, packetSize) ){
                output.add( "Packet unable to be fragmented and is too large for the network" ); //..................... build error string
                writeToFile( output, "output" ); //..................................................................... write error log
                System.out.println( "Error, check log" );
                System.exit(0);
            }
            else
                System.out.println( "File has been processed, check log." );
        }

        return true;
    }
}
