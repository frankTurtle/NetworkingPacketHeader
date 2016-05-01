/**
 * Created by Barret J. Nobel on 4/22/2016.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Header {
    private static final Map< String, Integer > NUM_OF_BITS = createMap(); //... variable to hold the Packet strings and array lengths
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
    private static final int SIZE = 160; //..................................... variable used to check if header size is legit
    private static final String[][] PACKET_ORDER = createPacketOrder(); //...... variable to have the order of packets align with design

    private Map< String, int[] > data; //....................................... variable to contain the data passed in
    private int totalBytes;

    // Default Constructor
    // initializes data with array's full of 0s
    public Header(){
        data = new HashMap<>(); //..................... instantiate data variable

        for( String row[] : PACKET_ORDER ){ //......... loop through each key
            for( String key : row ){
                int size = NUM_OF_BITS.get( key );
                int[] bitArray = new int[ size ]; //... create an array with the size of the value from HashMap
                data.put( key, bitArray ); //.......... put into data HashMap!
            }
        }

        totalBytes = 0;
    }

    // Constructor with parameters
    // takes a string and a boolean
    // string can be the actual raw data or a file name
    // boolean value determines if its a file name or raw data and acts  accordingly
    public Header( String input, boolean isFilename ){
        this(); //.................................................................................. initializes data array

        if( isFilename ){ //........................................................................ if its a filename and not raw data
            String line = null; //.................................................................. variable to capture the line from the file
            int row = 0; //......................................................................... keeps track of which row its on

            try {
                FileReader fileReader = new FileReader( input + ".txt" ); //........................ open up the file
                BufferedReader bufferedReader = new BufferedReader( fileReader ); //................ pass it along to buffered reader in the event of overflow

                while( (line = bufferedReader.readLine()) != null && row < 5 ) { //................. while there are lines left to read
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

                if( line.length() != 32 ){ totalBytes = Integer.parseInt( line ); }

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

    // Method to return a Map object representing
    // the packet labels and size's for the array's
    private static Map< String, Integer > createMap(){
        Map< String, Integer > result = new HashMap<>(); //.............. HashMap to return
        result.put( VERSION, 4 ); //..................................... put all final key's written above with their values
        result.put( HEADER_LENGTH, 4);
        result.put( SERVICE_TYPE, 8);
        result.put( TOTAL_LENGTH, 16);
        result.put( ID, 16);
        result.put( FLAG, 3);
        result.put( FRAG_OFFSET, 13);
        result.put( TTLIVE, 8);
        result.put( PROTOCOL, 8);
        result.put( CHECKSUM, 16);
        result.put( SOURCE_ADDRESS, 32);
        result.put( DEST_ADDRESS, 32);
        return Collections.unmodifiableMap( result ); //................ make it final
    }

    // Method to return a String array with the proper order
    // inline with the packet header architecture
    private static String[][] createPacketOrder(){
        String[][] returnString = { {VERSION, HEADER_LENGTH, SERVICE_TYPE, TOTAL_LENGTH},
                                    {ID, FLAG, FRAG_OFFSET},
                                    {TTLIVE, PROTOCOL, CHECKSUM},
                                    {SOURCE_ADDRESS},
                                    {DEST_ADDRESS}
                                };

        return returnString;
    }

    // Method to convert an array of binary into a decimal number
    // returns as a String
    public static String binaryToDecimal( int[] binaryArray ){
        String returnString = ""; //.................................... String to return
        int value = 0; //............................................... keep a running total

        for( int i = 0; i < binaryArray.length; i++ ){ //............... loop through each binary in array
            if( binaryArray[i] > 0 ){  //............................... only care if it's a 1
                value += Math.pow( 2,(binaryArray.length - (1+i)) ); //. add it up
            }
        }

        return returnString + value;
    }

    // Method to test out the creation of hte HashMap
    public void printMap(){
        for( String row[] : PACKET_ORDER ){
            for( String key : row ) System.out.println( key + " " + Arrays.toString(data.get(key)) );
        }
    }

    // Method to return the data map
    // NEEDS TO BE UPDATED TO RETURN A DEEP COPY
    public Map< String, int[] > getData(){ return data; }

    public String getDestAddress(){ return binaryIPConvert(data.get(DEST_ADDRESS)); }

    public String getSourceAddress(){ return binaryIPConvert(data.get(SOURCE_ADDRESS)); }

    public int getTotalBytes() { return totalBytes; }

    // Method to return a formatted String representing all data
    // Overridden
    public String toString(){
        String returnString = ""; //......................................................... variable to return

        for( String row[] : PACKET_ORDER ){ //............................................... loop through each row in packet
            for( String key : row ){ //...................................................... loop through each item in that row
                String value = ( key.equals(SOURCE_ADDRESS) || key.equals(DEST_ADDRESS) ) //. if its the source or destination address pass to special method
                        ? this.binaryIPConvert( data.get(key) )
                        : this.binaryToDecimal( data.get(key) ); //.......................... or just get the normal decimal value
                returnString += String.format( "%21s: %s%n", key, value ); //................ build return string
            }
        }

        return returnString;
    }

    // Method to convert an array of binary data representing the IP address into a String
    private String binaryIPConvert( int[] ipAddress ){
        String returnString = ""; //............................................... variable to return

        for( int sections = 0; sections < 4; sections++ ){ //...................... loop through 4 sections for address
            int start = sections * 8; //........................................... starting index for the subarray
            int[] tempArray = Arrays.copyOfRange(ipAddress, start, start + 8); //.. sub array of the next section
            returnString += binaryToDecimal( tempArray ) + "."; //................. build the string to return
        }

        return returnString.substring( 0, returnString.length() - 1 ); //.......... return a substring to cut off the last period
    }
}
