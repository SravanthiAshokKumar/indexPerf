package epl.pubsub.location.indexperf;

import java.util.Properties;

public class IndexFactory {

    public enum IndexType{
        GEOHASH,
        RTREE;
    }
    public static Index getInitializedIndex(double minX, double minY,
        double maxX, double maxY, double incr, IndexType indexType,
        Properties props){
        
        if(indexType == IndexType.GEOHASH){
            Index index = new GeoHashIndex(props);
            index.createIndex(minX, minY, maxX, maxY, incr);
            return index;
        }
        if(indexType == IndexType.RTREE){
            Index index = new RTreeIndex(props);
            index.createIndex(minX, minY, maxX, maxY, incr);
            return index;
        }
        return null;
    }
}
