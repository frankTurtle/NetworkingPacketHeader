/**
 * Created by Barret J. Nobel on 4/22/2016.
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class App {
    private static final String ORIGIN = "origin";
    private static final String PORT = "port";
    private static final String MTU = "mtu";
    private static final String DESTINATION = "destination";

    private static RoutingTable routingTable = new RoutingTable();

    public static void main( String[] args ){
        Header test = new Header( "test", true );
        System.out.println( test );

//        for( int row = 0; row < routingTable.getTableRows().size(); row++ ){
//            System.out.println( row+1 + ": "  + Arrays.toString( routingTable.getTableRows().get(row).get(DESTINATION)) );
//        }

        Options testOptions = new Options( "test", true );
        System.out.println( testOptions );


        System.out.println( testIpAddress( test.getSourceAddress()) );

        writeToFile("");

//        System.out.println( Integer.parseInt(Integer.toBinaryString(110), 2) );
    }

    private static boolean testIpAddress( String ipAddress ){
        return new RoutingTable().ipExistInTable( ipAddress );
    }

    private static void writeToFile( String input ){
        List<String> lines = Arrays.asList("The first line", "The second line");
        Path file = Paths.get("output.txt");
        try{
            Files.write(file, lines, Charset.forName("UTF-8"));
        }
        catch( IOException e ){
            System.out.println( "error" + e.toString() );
        }

//        Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
    }
}
