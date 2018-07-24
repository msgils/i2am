package knu.cs.dke.topology_manager.topolgoies;

import java.io.IOException;

import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.NotAliveException;
import org.apache.storm.thrift.TException;
import org.apache.storm.thrift.transport.TTransportException;

public class BinaryBernoulliSamplingTopology extends ASamplingFilteringTopology {

	private int sampleSize;
	private int windowSize;
	
	private String preSampleKey;
	
	public BinaryBernoulliSamplingTopology(String createdTime, String plan, int index, String topologyType, int sampleSize, int windowSize) throws TTransportException {
		
		super(createdTime, plan, index, topologyType);
		this.sampleSize = sampleSize;
		this.windowSize = windowSize;				
		
		this.preSampleKey = super.getRedisKey() + "preSample";		
	}
	
	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public String getPreSampleKey() {
		return preSampleKey;
	}

	public void setPreSampleKey(String preSampleKey) {
		this.preSampleKey = preSampleKey;
	}
}
