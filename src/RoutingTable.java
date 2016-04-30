/**
 * Created by Barret J. Nobel on 4/30/2016.
 */

import java.util.ArrayList;
import java.util.HashMap;

public class RoutingTable {
    private final String ORIGIN = "origin"; //......................... private instance variables for keys
    private final String PORT = "port";
    private final String MTU = "mtu";
    private final String DESTINATION = "destination";

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

}

