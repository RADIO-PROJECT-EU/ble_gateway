package org.atlas.gateway.core.commands;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GwAdvertisment {
	
	private String data;
	private long interval; // In milliseconds
	private long repeats = -1;//Means for ever
	
	@JsonProperty("start_at")
	private Date startAt;
	
	@JsonProperty("stop_at")
	private Date endAt;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public long getRepeats() {
		return repeats;
	}

	public void setRepeats(long repeats) {
		this.repeats = repeats;
	}

	public Date getStartAt() {
		return startAt;
	}

	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}

	public Date getEndAt() {
		return endAt;
	}

	public void setEndAt(Date endAt) {
		this.endAt = endAt;
	}

	@Override
	public String toString() {
		return "GwAdvertisment [data=" + data + ", interval=" + interval
				+ ", repeats=" + repeats + ", startAt=" + startAt + ", endAt="
				+ endAt + "]";
	}
	

}
