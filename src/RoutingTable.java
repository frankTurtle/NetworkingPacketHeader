/**
 * Created by Barret J. Nobel on 4/30/2016.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RoutingTable {
    private final String ORIGIN = "origin"; //......................... private instance variables for keys
    private final String PORT = "port";
    private final String MTU = "mtu";
    private final String DESTINATION = "destination";
    private static final String MASK = "11111111101110101010101001010101";

    private ArrayList< HashMap<String, int[]> > tableRows; //............ arraylist to hold all routing table info

    public RoutingTable(){
        tableRows = new ArrayList<>(); //................................ instantiate the rows

        int[] origin1  = { 110, 48, 32, 80 }, port1    = { 1 }; //....... hardcoded values for tables
        int[] origin2  = { 110, 32, 32, 80 }, port2    = { 2 };
        int[] origin3  = { 110, 48, 32, 68 }, port3    = { 3 };
        int[] origin4  = { 110, 18, 40, 17 }, port4    = { 4 };
        int[] origin5  = { 110, 18, 40, 21 }, port5    = { 5 };
        int[] origin6  = { 110, 18, 40, 20 }, port6    = { 6 };
        int[] origin7  = { 110, 48, 32, 69 }, port7    = { 7 };
        int[] origin8  = { 110, 48, 32, 64 }, port8    = { 8 };
        int[] origin9  = { 110, 32, 32, 69 }, port9    = { 9 };
        int[] origin10 = { 110, 32, 32, 68 }, port10   = { 10 };

        int[] mtu1   = { 1500 }, dest1  = { 110, 49, 32, 80 };
        int[] mtu2   = { 256 },  dest2  = { 110, 33, 32, 80 };
        int[] mtu3   = { 512 },  dest3  = { 110, 49, 32, 68 };
        int[] mtu4   = { 256 },  dest4  = { 110, 19, 41, 17 };
        int[] mtu5   = { 512 },  dest5  = { 110, 23, 40, 17 };
        int[] mtu6   = { 1500 }, dest6  = { 110, 19, 40, 17 };
        int[] mtu7   = { 256 },  dest7  = { 110, 49, 33, 70 };
        int[] mtu8   = { 512 },  dest8  = { 110, 49, 33, 80 };
        int[] mtu9   = { 256 },  dest9  = { 110, 36, 32, 80 };
        int[] mtu10  = { 512 }, dest10  = { 110, 37, 32, 82 };

        int[][] origin = { origin1, origin2, origin3, origin4, //........ array's to hold everything for easy looping
                           origin5, origin6, origin6, origin7,
                           origin8, origin9, origin10
        };

        int[][] port = { port1, port2, port3, port4, port5, port6,
                         port7, port8, port9, port10
        };

        int[][] mtu = { mtu1, mtu2, mtu3, mtu4, mtu5, mtu6,
                        mtu6, mtu7, mtu8, mtu9, mtu10
        };

        int[][] dest = { dest1, dest2, dest3, dest4, dest5, dest6,
                         dest7, dest8, dest9, dest10
        };

        for( int row = 0; row < 10; row++ ){ //........................... loop through each row
            HashMap< String, int[] > tableInformation = new HashMap<>();

            tableInformation.put( ORIGIN,      origin[row] ); //.......... put each array into the hash
            tableInformation.put( PORT,        port[row] );
            tableInformation.put( MTU,         mtu[row] );
            tableInformation.put( DESTINATION, dest[row] );

            tableRows.add( tableInformation ); //......................... add entire hash to the arraylist
        }
    }

    // Getter method
    // returns the arraylist of hash tables
    public ArrayList< HashMap<String, int[]> > getTableRows(){ return tableRows; }

    // Method to get the MTU from the IP Address
    // checks to see if the IP is valid first and if so, returns the MTU
    public int getMtu( String ipAddress ){
        int returnMTU = 0;
        String convertedIP = createIPString(createIPArray(addMaskToAddress(ipAddress))); //. converts the IP with the MASK

        if( ipExistInTable(ipAddress) ){ //....................................................... if its a valid IP
            for( HashMap<String, int[]> entry : tableRows ){ //................................... loop over the entries
                if( createIPString(entry.get(ORIGIN)).equals(convertedIP) ){ //................... once found
                    return entry.get(MTU)[0]; //.................................................. return the MTU value
                }

            }
        }

        return returnMTU;
    }

    // Method to determine if the IP passed in is part of the list
    public boolean ipExistInTable( String ipAddress ){
        String convertedIP = createIPString(createIPArray(addMaskToAddress(ipAddress))); //... converts the IP with the MASK

        for( HashMap<String, int[]> entry : getTableRows() ) { //............................. checks each entry
            if( createIPString(entry.get(ORIGIN)).equals(convertedIP) ||
                    createIPString(entry.get(DESTINATION)).equals(convertedIP)) return true;
        }

        return false;
    }

    // Method to add the Network Mask to the address
    // reveals it's magical locations!
    public static String addMaskToAddress( String address ){
        String[] binary = address.split( "\\." ); //............................... split the address up into strings
        String updatedAddress = ""; //............................................. variables to hold the addresss
        String addressPart;

        for( int index = 0; index < binary.length; index++ ){ //................... loop through each IP segment
            addressPart =
                    Integer.toBinaryString( Integer.parseInt(binary[index])); //... get the binary converstion
            while( (addressPart.length() % 8) != 0 ){ //........................... if its not 8 chars, add the leading 0's
                addressPart = String.format( "0%s", addressPart );
            }
            updatedAddress += addressPart; //...................................... add onto updated address

        }

        long updatedNum = Long.parseLong( updatedAddress,2 ); //................... get the value of the binary nums
        long maskNum = Long.parseLong( MASK,2 );
        updatedNum = updatedNum & maskNum; //...................................... and them together with the mask
        updatedAddress = Long.toBinaryString( updatedNum ); //..................... output the binary string

        return updatedAddress;
    }

    // Method to create a string in the IP address format from the array passed in
    public static String createIPString( int[] arrayToConvert ){
        String returnString = ""; //...................................... create a string to return
        for( int ip : arrayToConvert ){ returnString += ip + "."; } //.... add a . between each number to return

        return returnString.substring( 0, returnString.length() - 1 ); //. cut off the last . at the end
    }

    // Method to create an array with the IP addresses based off the string passed in
    public static int[] createIPArray( String stringToConvert ){
        int[] returnString = new int[4]; //............................. create an array to return
        while( (stringToConvert.length() % 8) != 0 ){  //............... if it doesnt equal 8 chars add leading 0's
            stringToConvert = String.format( "0%s", stringToConvert);
        }

        for( int i = 0; i < returnString.length; i++ ){ //.............. loop through the string
            String temp = stringToConvert.substring(i*8, i*8+8); //..... get a substring of each ip section
//            System.out.println( temp );
            returnString[ i ] = Integer.parseInt(temp, 2); //........... convert it to an int and put into the return array
        }

        return returnString;
    }

    // Method to return the IP as a string converted
    public static String convertWithMask( String convertMe ){
        String convertedIP = createIPString(createIPArray(addMaskToAddress(convertMe))); //. converts the IP with the MASK

        return convertedIP;
    }
}

