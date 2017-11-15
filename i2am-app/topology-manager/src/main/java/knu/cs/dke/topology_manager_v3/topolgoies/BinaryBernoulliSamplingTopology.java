package knu.cs.dke.topology_manager_v3.topolgoies;

public class BinaryBernoulliSamplingTopology extends ASamplingFilteringTopology {

	private int sampleSize;
	private int windowSize;
	
	public BinaryBernoulliSamplingTopology(int sampleSize, int windowSize) {
		
		this.sampleSize = sampleSize;
		this.windowSize = windowSize;
	}	
	
	@Override
	public void submitTopology() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void killTopology() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activeTopology() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactiveTopology() {
		// TODO Auto-generated method stub
		
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

}
