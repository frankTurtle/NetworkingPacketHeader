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
    private static final String ORIGIN = "origin";
    private static final String PORT = "port";
    private static final String MTU = "mtu";
    private static final String DESTINATION = "destination";
    private static final String OPTION_NUMBER = "Option Number";

    private static RoutingTable routingTable = new RoutingTable();

    public static void main( String[] args ){
        Header testHeader = new Header( "testEmpty", true );
        Options testOptions = new Options( "testEmpty", true );
        ArrayList< String > output = new ArrayList<>();

        if( ableToTransmit(testHeader, testOptions, output) ){
            System.out.println( "lets begin" );
            System.out.println( routingTable.getMtu(testHeader.getDestAddress()) );
            System.out.println(testOptions.getTotalBytes());
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
    }

    // Method to test if the IP is valid for the network
    private static boolean testIpAddress( String ipAddress ){
        return new RoutingTable().ipExistInTable( ipAddress );
    }

    // Method to write the array list input to a file
    private static void writeToFile( ArrayList<String> input ){
        List< String > lines = input; //.............................. makes input a list
        Path file = Paths.get( "output.txt" ); //..................... create a path to the file
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
            writeToFile( output ); //................................................................................... write error log
            System.out.println( "Error, check log" );
            System.exit(0);
        }
        else { //....................................................................................................... Check to make sure option number is legit
            int optionNumber = Integer.parseInt( Header.binaryToDecimal( testOptions.getData().get(OPTION_NUMBER)) );//. get number
            if( optionNumber != 9 && optionNumber != 7 && optionNumber != 0 ){ //....................................... if its a not a 0,7,9 its not legit
                output.add( String.format("Option Number Error: %d", optionNumber) ); //................................ build error string
                writeToFile( output ); //............................................................................... write error log
                System.out.println( "Error, check log" );
                System.exit(0);
            }
        }

        return true;
    }
}
