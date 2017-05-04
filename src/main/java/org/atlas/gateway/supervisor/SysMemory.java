package org.atlas.gateway.supervisor;

public class SysMemory {
	
	private MemoryType type;
	private long total;
	private long used;
	private long free;
	private long shared;
	private long cached;
	private long available;
	
	public SysMemory(MemoryType type){
		this.type = type;
	}
	
	public SysMemory(String info, MemoryType type){
		this.type = type;
		this.parse(info);
	}

	public MemoryType getType() {
		return type;
	}

	public void setType(MemoryType type) {
		this.type = type;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getUsed() {
		return used;
	}

	public void setUsed(long used) {
		this.used = used;
	}

	public long getFree() {
		return free;
	}

	public void setFree(long free) {
		this.free = free;
	}

	public long getShared() {
		return shared;
	}

	public void setShared(long shared) {
		this.shared = shared;
	}

	public long getCached() {
		return cached;
	}

	public void setCached(long cached) {
		this.cached = cached;
	}

	public long getAvailable() {
		return available;
	}

	public void setAvailable(long available) {
		this.available = available;
	}
	
	private void parse(String info){
		String trimmedInfo = info.trim().replaceAll(" +", " ");
		String[] temp = trimmedInfo.split(" ");
		this.total = Integer.parseInt(temp[1]);
		this.used = Integer.parseInt(temp[2]);
		this.free = Integer.parseInt(temp[3]);
		if( this.type == MemoryType.RAM ){
			this.shared = Integer.parseInt(temp[4]);
			this.cached = Integer.parseInt(temp[5]);
			this.available = Integer.parseInt(temp[6]);
		}
	}

}
