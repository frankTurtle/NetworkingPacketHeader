/**
 * Created by Barret J. Nobel on 4/30/2016.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Options {
    private static final Map< String, Integer > NUM_OF_BITS = createMap(); //... variable to hold the Packet strings and array lengths
    private static final String[][] PACKET_ORDER = createPacketOrder(); //...... variable to have the order of packets align with design
    private static final String COPY_FLAG = "Copy Flag";
    private static final String OPTION_CLASS = "Option Class";
    private static final String OPTION_NUMBER = "Option Number";
    private static final String LENGTH = "Length"; //........******** INCLUDES THE FIRST THREE OCtets
    private static final String POINTER = "Pointer";
    private static final String PADDING = "Padding";

    private Map< String, int[] > data; //....................................... variable to contain the data passed in

    public Options(){
        data = new HashMap<>(); //..................... instantiate data variable

        for( String row[] : PACKET_ORDER ){ //......... loop through each key
            for( String key : row ){
                int size = NUM_OF_BITS.get( key );
                int[] bitArray = new int[ size ]; //... create an array with the size of the value from HashMap
                data.put( key, bitArray ); //.......... put into data HashMap!
            }
        }
    }

    // Constructor with parameters
    // takes a string and a boolean
    // string can be the actual raw data or a file name
    // boolean value determines if its a file name or raw data and acts  accordingly
    public Options( String input, boolean isFilename ){
        this(); //.................................................................................. initializes data array

        if( isFilename ){ //........................................................................ if its a filename and not raw data
            String line = null; //.................................................................. variable to capture the line from the file
            int row = 0; //......................................................................... keeps track of which row its on
            int header = 5;

            try {
                FileReader fileReader = new FileReader( input + ".txt" ); //........................ open up the file
                BufferedReader bufferedReader = new BufferedReader( fileReader ); //................ pass it along to buffered reader in the event of overflow

                while( (line = bufferedReader.readLine()) != null && row < 1 ) { //................. while there are lines left to read
                    if( header > 0 ){ header--; continue; } //...................................... ignores all the header lines in the packet
                    for( int index = 0; index < PACKET_ORDER[row].length; index++ ){ //............. loop through each array in the packet labels
                        String key = PACKET_ORDER[row][index]; //................................... get the key
                        int length = NUM_OF_BITS.get( key ); //..................................... determine length of the array with the key
                        int[] dataArray = data.get( key ); //....................................... pointer to that array so we can manipulate

                        for( int bitIndex = 0; bitIndex < length; bitIndex++ ){ //.................. loop through each value in the array from the key
                            if( line.charAt(bitIndex) != '0' ){ dataArray[ bitIndex ]++; } //...... if it's not a 0 increment the value in the current array to a 1
                        }

                        line = line.substring( length ); //......................................... cut the data we just processed out of the line
                    }

                    row++; //....................................................................... once the line is finished go to the next one
                }

                bufferedReader.close(); //.......................................................... close the file connection
            }
            catch( FileNotFoundException ex ){ //................................................... if the file doesnt exist
                System.out.println( "Unable to open file '" + input + "'" );
                ex.printStackTrace();
            }
            catch( IOException ex ){ //............................................................. if were unable to read the file
                System.out.println( "Error reading file '" + input + "'" );
            }
        }
    }

    // Method to map all the options to the bits associated with each
    private static Map< String, Integer > createMap(){
        Map< String, Integer > result = new HashMap<>(); //. HashMap to return
        result.put( COPY_FLAG, 1 ); //...................... put all final key's written above with their values
        result.put( OPTION_CLASS, 2 );
        result.put( OPTION_NUMBER, 5 );
        result.put( LENGTH, 8 );
        result.put( POINTER, 8 );
        result.put( PADDING, 8 );
        return Collections.unmodifiableMap( result ); //.... make it final
    }

    // Method to return a String array with the proper order
    // inline with the packet header architecture
    private static String[][] createPacketOrder(){
        String[][] returnString = { {COPY_FLAG, OPTION_CLASS, OPTION_NUMBER, LENGTH, POINTER, PADDING}, };

        return returnString;
    }

    // Method to return a formatted String representing all data
    // Overridden
    public String toString(){
        String returnString = ""; //......................................................... variable to return

        for( String row[] : PACKET_ORDER ){ //............................................... loop through each row in packet
            for( String key : row ){ //...................................................... loop through each item in that row
                if( key.equals(PADDING) ) continue;
                String value = Header.binaryToDecimal( data.get(key) );
                returnString += String.format( "%21s: %s%n", key, value ); //................ build return string
            }
        }

        return returnString;
    }
}
