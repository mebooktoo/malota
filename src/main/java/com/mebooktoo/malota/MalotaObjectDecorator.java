/*
    Copyright (c) 2012-2015 Mebooktoo S.R.L 
    Copyright (c) 2016 Stefano Rocca <stefano.rocca@gmail.com>, Michele Zuccalà <ardut@gmail.com>

    This file is part of Malota.

    Malota is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Malota is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser Public License for more details.

    You should have received a copy of the GNU Lesser Public License
    along with Malota.  If not, see <http://www.gnu.org/licenses/>.

*/

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
