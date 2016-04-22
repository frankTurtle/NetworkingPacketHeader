/**
 * Created by Barret J. Nobel on 4/22/2016.
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Header {
    private static final Map<String, Integer> NUM_OF_BITS = createMap();
    private static final String VERSION        = "Version";
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
    private static final int SIZE = 160;

    private static Map<String, Integer> createMap() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put( VERSION, 4 );
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
        return Collections.unmodifiableMap(result);
    }

    public void printMap(){
        for( String key : NUM_OF_BITS.keySet() ){
            System.out.println( NUM_OF_BITS.get(key) );
        }
    }
}
