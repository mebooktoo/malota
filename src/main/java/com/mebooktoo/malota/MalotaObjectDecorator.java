package com.mebooktoo.malota;

import java.util.Date;

public class MalotaObjectDecorator<V> {
    
	public static final int ACTIVE = 1;

	public static final int IDLE = 0;

	private long idleTime;
	
	private long creationTime;

	private int status;

	private V internalObject;
	
	protected MalotaObjectDecorator(V internalObject) {
		super();
		this.internalObject = internalObject;
		this.creationTime = System.currentTimeMillis();
		this.status = MalotaObjectDecorator.ACTIVE;
	}
	
	protected MalotaObjectDecorator() {
		this.creationTime = System.currentTimeMillis();
		this.status = MalotaObjectDecorator.ACTIVE;
	}

	public long getIdleTime() {
		return idleTime;
	}

	protected void setIdleTime(long idleTime) {
		this.idleTime = idleTime;
	}

	protected void setIdleTime(Date idleTime) {
		this.idleTime = idleTime.getTime();
	}

	public V getInternalObject() {
		return internalObject;
	}

	protected void setInternalObject(V internalObject) {
		this.internalObject = internalObject;
	}

	public int getStatus() {
		return status;
	}

	protected void setStatus(int status) {
		this.status = status;
	}

	public long getCreationTime() {
		return creationTime;
	}
	
	protected void idle() {
	    this.setStatus(MalotaObjectDecorator.IDLE);
	    this.setIdleTime(System.currentTimeMillis());
	}
	
	protected void active() {
	    this.setStatus(MalotaObjectDecorator.ACTIVE);
	}
			
}
