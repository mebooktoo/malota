package com.mebooktoo.malota;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class MalotaObjectPool<V> {
    
    private int remainingObjectPool = 0;

    private ArrayBlockingQueue<MalotaObjectDecorator<V>> corePool = null;

    private ArrayBlockingQueue<MalotaObjectDecorator<V>> overflowPool = null;

    private IScannerTask reaperTask;

    private IScannerTask scannerTask;

    //verificare che serva davvero nello scanning degli oggetti
    private ReentrantLock lock = new ReentrantLock();

    private Timer reaper;

    private MalotaObjectFactory<V> factory;
    
    private boolean started = false;

    public MalotaObjectPool(int coreSize) {
	this(coreSize, coreSize, null, null);
    }

    public MalotaObjectPool(int coreSize, int maxSize) {
	this(coreSize, maxSize, null, null);
    }

    public MalotaObjectPool(int coreSize, int maxSize, IScannerTask reaperTask,
	    IScannerTask scannerTask) {
	super();
	corePool = new ArrayBlockingQueue<MalotaObjectDecorator<V>>(coreSize);
	this.remainingObjectPool = coreSize;
	if (maxSize != coreSize) {
	    overflowPool = new ArrayBlockingQueue<MalotaObjectDecorator<V>>(
		    maxSize - coreSize);
	    this.remainingObjectPool += (maxSize - coreSize);
	}

	this.reaperTask = reaperTask;
	this.scannerTask = scannerTask;	
    }

    /**
     * Recupera il primo oggetto libero dal pool
     * 
     * 
     * @return
     * @throws InterruptedException
     */
    public MalotaObjectDecorator<V> getObject() throws InterruptedException {
	
	if (corePool.isEmpty())
	    populatePool();
	    
	// recupero oggetto, restando eventualmente in attesa
	MalotaObjectDecorator<V> obj = corePool.take();
	return obj;
    }

    public void releaseObject(MalotaObjectDecorator<V> obj)
	    throws InterruptedException {
	if (corePool.remainingCapacity() > 0) { // inserisco l'oggetto nel core
						// pool
	    corePool.offer(obj);
	    return;
	}
	if (overflowPool != null && overflowPool.offer(obj)) { // altrimenti
							       // vuol dire che
							       // il core pool
							       // e'
							       // inutilizzato(nessuno
							       // lo consuma)
							       // percio' metto
							       // gli oggetti in
							       // idle
	    obj.idle(); 
	} else {
	    // se non riesco a inserire nulla nemmeno nel idle pool vuol dire
	    // che il pool e' pieno
	}
    }

    private void populatePool() {
	if (overflowPool != null) {
	    MalotaObjectDecorator<V> idle = overflowPool.poll(); // recupero
								 // oggetti
								 // inutilizzati
	    if (idle != null) {// e li inserisco nel core pool
		idle.active();
		corePool.offer(idle);
		return;
	    }
	}
	// se non ci sono oggetti inutilizzati ne creo uno nuovo se possibile
	if (corePool.remainingCapacity() > 0 && this.remainingObjectPool > 0) {
	    corePool.offer(newObjectForPool());
	}
    }

    @Override
    public String toString() {
	if (this.overflowPool != null)
	    return this.getClass().getName() + " maxSize:" + ( this.corePool.size() + this.overflowPool.size() )
		+ " coreSize: " + this.corePool.size() + "idlesize: "
		+ this.overflowPool.size();
	else
	    return this.getClass().getName() + " coreSize: " + this.corePool.size();
    }

    public void start() {
	this.start(5000, 5000);
    }    

    public void start(int reaperIntervall, int scannerIntervall) {
	if (this.isStarted())
	    return;
	    	    
	reaper = new Timer(true);
	if (this.scannerTask != null) // avvio lo scanner per gli oggetti
				      // contenuti nel core pool
	    reaper.schedule(new TimerTask() {
	        
	        @Override
	        public void run() {
	         
	    	    lock.lock();
	    	    try {
	    		Iterator<MalotaObjectDecorator<V>> iter = corePool.iterator();
	    		while (iter.hasNext()) {
	    		    scannerTask.scanObject(iter.next());
	    		}
	    	    } finally {
	    		lock.unlock();
	    	    }
	    	}	    	
	        
	    }, scannerIntervall,
		    scannerIntervall);

	if (this.reaperTask != null) // avvio lo scanner per gli oggetti
				     // inattivi
	    reaper.schedule(new TimerTask() {
	        
	        @Override
	        public void run() {
	            lock.lock();
		    try {
			Iterator<MalotaObjectDecorator<V>> iter = overflowPool.iterator();
			while (iter.hasNext()) {
			    reaperTask.scanObject(iter.next());
			    iter.remove();
			    remainingObjectPool++;
			}
		    } finally {
			lock.unlock();
		    }	    	
	        }
	    }, reaperIntervall,
	       reaperIntervall);
	
	this.started = true;
    }
    
    public boolean isStarted() {
	return started;
    }
    
    protected MalotaObjectFactory<V> getObjectFactory() {
	return factory;
    }

    public void setObjectFactory(MalotaObjectFactory<V> factory) {
	this.factory = factory;
    }

    private MalotaObjectDecorator<V> newObjectForPool() {	
	V obj = getObjectFactory().newObjectForPool(); // creo nuovo oggetto da
						       // inserire nel pool
	if (obj == null) {
	    throw new IllegalArgumentException(
		    "Non e' stato possibile creare l'oggetto per il pool");
	}
	// inserisco l'oggetto semplice nel contenitore
	MalotaObjectDecorator<V> bod = new MalotaObjectDecorator<V>(obj);
	this.remainingObjectPool--;
	return bod;
    }

}