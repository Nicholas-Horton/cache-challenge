import java.util.HashMap;
import java.util.LinkedList;

/**
 * Caching object which stores key-value pairs in a map up to a predefined maximum. Should the amount exceed this maximum,
 * the cache begins removing elements at the end of its recent access queue, to make room for the newer element.
 *
 * As such, only frequently accessed elements are stored in the cache.
 *
 * @author Nicholas Horton
 */
public class Cache<K, V> {

    //CONSTANTS
    private static final int DEFAULT_MAX_CACHE_SIZE = 64; //Arbitrary value

    private int currentCacheSize;
    private final int maxCacheSize;

    private HashMap<K, V> hMap;
    private LinkedList<K> orderQueue;

    /**
     * Creates a Cache object using the default maximum cache size.
     */
    public Cache(){
        maxCacheSize      = DEFAULT_MAX_CACHE_SIZE;
        currentCacheSize  = 0;
        hMap              = new HashMap<K, V>(64);
        orderQueue        = new LinkedList<K>();
    }

    /**
     * Constructs a Cache object with the specified maximum size.
     *
     * @param maxCacheSize Number of elements that can be added into the cache before eliminating older elements
     */
    public Cache(int maxCacheSize){
        this.maxCacheSize = maxCacheSize;
        currentCacheSize  = 0;
        hMap              = new HashMap<K, V>(maxCacheSize);
        orderQueue        = new LinkedList<K>();
    }

    /**
     * Looks up a certain key within the cache's hash map and returns true if the element exists. This is not
     * counted as a "access" operation for deciding the order of recently accessed elements.
     *
     * @param key key to search for within the cache's hash map
     * @return true if the cache's hash map contains the specified key
     */
    public boolean containsKey(K key){
       return hMap.containsKey(key);
    }

    /**
     * Looks up a certain value within the cache's hash map and returns true if the element exists. This is not
     * counted as a "access" operation for deciding the order of recently accessed elements.
     *
     * @param value value to search for within the cache's hash map
     * @return true if the cache's hash map contains the specified value
     */
    public boolean containsValue(V value){
        return hMap.containsValue(value);
    }

    /**
     * Looks up the specified key within the cache's hash map and returns its associated value. This counts as an
     * "access" operation and thus identifies the key as now being the most recently accessed element.
     *
     * @param key key to search for within the cache's hash map
     * @return the value found in the hash map associated with the specified key
     */
    public V get(K key){
        /**
         * reorder queue - Should be optimized here by having a separate implementation of a queue, which would give us
         * direct access to the structure. This would allow us to manually move the node in constant time.
         */
        if (hMap.containsKey(key)) {
            orderQueue.remove(key);
            orderQueue.addFirst(key);
        }
        return hMap.get(key);
    }

    /**
     * Adds the specified key-value pair to the cache if the key does not already exist. Should the new element cause
     * the cache to exceed its maximum size, the cache discards the last element in the recent access queue.
     *
     * @param key the key to associate to the specified value
     * @param value the value to be associated to the specified key
     */
    public void put(K key, V value){
        if(!hMap.containsKey(key)) {
            if (currentCacheSize + 1 > maxCacheSize){
                K overflowKey = orderQueue.getLast();
                orderQueue.removeLast();
                hMap.remove(overflowKey);
            }
            else{
                currentCacheSize++;
            }
            // access to the implementation of the queue could give us the ability to simply create a pointer from the
            // hMap to a node in the orderQueue
            orderQueue.addFirst(key);
            hMap.put(key, value);
        }
    }

    /**
     * Removes the specified key from the cache.
     *
     * @param key the key to be removed from the cache
     * @return the value of the element removed from the cache
     */
    public V remove(K key){
        if(hMap.containsKey(key)) {
            // direct access to this element via a pointer from the map could allow us to remove this element in
            // constant time.
            orderQueue.remove(key);
            currentCacheSize--;
            return hMap.remove(key);
        }
        return null;
    }

    /**
     * Gets the number of elements currently stored in the queue.
     *
     * @return the number of elements stored in the queue
     */
    public int size(){
        return hMap.size();
    }

    //// -------------------------------------------------------------------------------------------------------------
    //The below code is for testing purposes only
    public static void main(String[] args){
        Cache<String, Integer> c = new Cache<String, Integer>(5);
        c.put("First",  1);
        c.put("Second", 2);
        c.put("Third",  3);
        c.put("Fourth", 4);
        c.put("Fifth",  5);
        c.put("Sixth",  6);
        //Should contain 2,3,4,5,6
        System.out.println("The cache no longer contains the first key:\t" + c.containsKey("First"));
        System.out.println("The cache no longer contains the first value:\t" + c.containsValue(1));
        System.out.println("The cache still contains the fourth key:\t" + c.containsKey("Fourth"));
        System.out.println("The cache still contains the fourth value:\t" + c.containsValue(4));
        c.get("Fourth");
        c.put("Seventh", 7);
        c.put("Eighth",  8);
        c.put("Ninth",   9);
        c.put("Tenth",   10);
        //Should contain 4,7,8,9,10
        System.out.println("The cache no longer contains the third key:\t" + c.containsKey("Third"));
        System.out.println("The cache no longer contains the third value:\t" + c.containsValue(3));
        System.out.println("The cache still contains the fourth key:\t" + c.containsKey("Fourth"));
        System.out.println("The cache still contains the fourth value:\t" + c.containsValue(4));

        //Note to testers: Stepping through this code with a debugger can show the Cache in action!
    }
}
