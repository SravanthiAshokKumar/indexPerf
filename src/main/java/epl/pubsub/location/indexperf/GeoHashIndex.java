package epl.pubsub.location.indexperf;

import ch.hsr.geohash.GeoHash;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.Map;
import java.util.SortedMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
 
class GeoHashIndex implements Index {
    Logger log = Logger.getLogger(GeoHashIndex.class);
    
    Trie<String, List<String>> spatialIndex;    
    int hashPrecision;
    public GeoHashIndex(Properties properties){
        spatialIndex = new PatriciaTrie<>();
        hashPrecision = 8;
        if(properties.containsKey("maxCharPrecision")){
            hashPrecision = (int)properties.get("maxCharPrecision");
        }
        
    }
    
    @Override
    public void createIndex(double minX, double minY, double maxX, double maxY, double incr){
    
        for(double x = minX; x <= maxX; x += incr){
            for(double y = minY; y <= maxY; y += incr){
                GeoHash gh = GeoHash.withCharacterPrecision(x, y, hashPrecision);
                String key = gh.toBase32();
                
                GeoHash[] neighbors = gh.getAdjacent();
                List<String> vals = new ArrayList<>();
                for(int i =0; i < neighbors.length; ++i){
                    vals.add(neighbors[i].toBase32());
                }
                spatialIndex.put(key, vals);
            }
        }
//        System.out.println("index size = " + spatialIndex);
        log.info("index size = " + spatialIndex.size());
    }


    @Override 
    public String getStringValue(double x, double y){
        return GeoHash.withCharacterPrecision(x, y, hashPrecision).toBase32();
    }

    @Override
    public List<String> getNearestNeighbors(double x, double y){
        GeoHash gh = GeoHash.withCharacterPrecision(x, y, hashPrecision);
        String prefix = gh.toBase32().substring(0, hashPrecision - 1);
        List<String> nearestNeighbors = spatialIndex.get(gh.toBase32());
        // if (nearestNeighbors != null) {
        //     nearestNeighbors.forEach(s -> s = s+"_topic");
        // }
        return nearestNeighbors;
    }

    @Override
    public long getIndexSize(){
        return spatialIndex.size();
    }
}
