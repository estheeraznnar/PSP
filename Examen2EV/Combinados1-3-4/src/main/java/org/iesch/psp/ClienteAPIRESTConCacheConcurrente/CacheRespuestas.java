package org.iesch.psp.ClienteAPIRESTConCacheConcurrente;// --- CacheRespuestas.java (ConcurrentHashMap + ReadWriteLock) ---
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheRespuestas {

    private ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> hashes = new ConcurrentHashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private int hits = 0;
    private int misses = 0;

    public String obtener(String url) {
        lock.readLock().lock();
        try {
            if (cache.containsKey(url)) {
                hits++;
                String datos = cache.get(url);
                String hashAlmacenado = hashes.get(url);

                // Verificar integridad
                if (HashUtil.checkHash(datos, hashAlmacenado)) {
                    System.out.println("[CACHE HIT] " + url + " (integridad OK)");
                    return datos;
                } else {
                    System.out.println("[CACHE HIT] " + url + " (integridad FALLIDA - descartando)");
                    // Eliminar datos corruptos
                    cache.remove(url);
                    hashes.remove(url);
                    misses++;
                    return null;
                }
            }
            misses++;
            System.out.println("[CACHE MISS] " + url);
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void guardar(String url, String datos) {
        lock.writeLock().lock();
        try {
            String hash = HashUtil.getHash(datos);
            cache.put(url, datos);
            hashes.put(url, hash);
            System.out.println("[CACHE SAVE] " + url);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public synchronized int getHits() { return hits; }
    public synchronized int getMisses() { return misses; }
}