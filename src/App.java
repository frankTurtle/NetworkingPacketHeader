/**
 * Created by Barret J. Nobel on 4/22/2016.
 */

import java.util.Arrays;

public class App {
    private static final String ORIGIN = "origin";
    private static final String PORT = "port";
    private static final String MTU = "mtu";
    private static final String DESTINATION = "destination";
    private static RoutingTable routingTable = new RoutingTable();

    public static void main( String[] args ){
        Header test = new Header( "test", true );
        System.out.println( test );


        for( int row = 0; row < routingTable.getTableRows().size(); row++ ){
            System.out.println( row+1 + ": "  + Arrays.toString( routingTable.getTableRows().get(row).get(DESTINATION)) );
        }
    }
}
