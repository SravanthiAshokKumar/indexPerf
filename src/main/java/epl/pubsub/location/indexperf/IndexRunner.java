package epl.pubsub.location.indexperf;

/**
 * Hello world!
 *
 */

import java.util.Random;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class IndexRunner 
{
    private static Logger log = Logger.getLogger(IndexRunner.class);
    public static double testIndex(Index index, double minX, double minY, double maxX, double maxY){
        Random r = new Random();
        StopWatch sw = new StopWatch();
        sw.start();
        for(long i =0; i < 100000; ++i){
            double randomX = minX + (maxX - minX) * r.nextDouble();
            double randomY = minY + (maxY - minY) * r.nextDouble();
            index.getNearestNeighbors(randomX, randomY);
        }
        sw.stop();
        log.info("lookup of 100000 queries took " + sw.getTime() + " ms");
        return sw.getTime();
    }
    public static Index createIndex(Config config, IndexFactory.IndexType type) {
            Properties props = new Properties();
            double minX = config.minX;
            double minY = config.minY;
            double maxY = config.maxY;
            double maxX = config.maxX; 
            double incr = config.incr;
            int precision = config.precision;
            Index index = IndexFactory.getInitializedIndex(minX, minY, maxX,
                maxY, incr, type, props, precision);
            return index; 
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        try{
            ArrayList<List<Double>> readings = new ArrayList<List<Double>>();
            double stepSize = Double.parseDouble(System.getProperty("stepSize"));
            String configFile = System.getProperty("cfgFile");
            double numReadings = Double.parseDouble(System.getProperty("numReadings"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            Config config = mapper.readValue(new File(configFile), Config.class);

             System.out.println("geoHashCreateTime, rtreeCreateTime, geoHashQueryTime, rtreeQueryTime, geohashSize, (double)rtreeSize");
 
            for(int i = 1; i <= numReadings; ++i){
                config.maxX = config.minX + stepSize * i;
                config.maxY = config.minY + stepSize * i;
                StopWatch sw = new StopWatch();
                sw.start(); 
                Index geoHashIndex = createIndex(config,IndexFactory.IndexType.GEOHASH);
                sw.stop();
                double geoHashCreateTime = sw.getTime();
                sw.reset();

                sw.start(); 
                Index rtreeIndex = createIndex(config, IndexFactory.IndexType.RTREE);
                sw.stop();
                double rtreeCreateTime = sw.getTime(); 
                double geoHashQueryTime = testIndex(geoHashIndex, config.minX, config.minY, config.maxX, config.maxY);
                double rtreeQueryTime = testIndex(rtreeIndex, config.minX, config.minY, config.maxX, config.maxY);        
                long rtreeSize = rtreeIndex.getIndexSize();
                long geohashSize = geoHashIndex.getIndexSize();

                //List<Double> vals = new ArrayList<>();
                //vals.add(geoHashCreateTime, rtreeCreateTime, geoHashQueryTime, rtreeQueryTime, (double)geohashSize, (double)rtreeSize);
                //readings.add(vals); 
             
                System.out.println(geoHashCreateTime+ "," +  rtreeCreateTime + "," +  geoHashQueryTime +"," +  rtreeQueryTime + "," +  geohashSize + "," +  rtreeSize);
 
            }
            
        }catch(IOException e){
            log.error(e.getMessage());
        } 
    }
}
