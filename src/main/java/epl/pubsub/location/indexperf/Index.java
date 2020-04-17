package epl.pubsub.location.indexperf;

import java.util.List;

interface Index {

    void createIndex(double minX, double minY, double maxX,double maxY, double incr);

    long getIndexSize();
    
    List<String> getNearestNeighbors(double x, double y);    
}
