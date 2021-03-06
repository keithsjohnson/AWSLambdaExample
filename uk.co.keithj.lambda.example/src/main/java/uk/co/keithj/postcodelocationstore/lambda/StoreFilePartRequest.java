package uk.co.keithj.postcodelocationstore.lambda;

public class StoreFilePartRequest {

	private String bucket;

	private String key;

	private String chunkSize;

	private String regionName;

	private String startPosition;

	private String partIndex;

	public StoreFilePartRequest() {
	}

	public StoreFilePartRequest(String bucket, String key, String chunkSize, String regionName, String startPosition,
			String partIndex) {
		super();
		this.bucket = bucket;
		this.key = key;
		this.chunkSize = chunkSize;
		this.regionName = regionName;
		this.startPosition = startPosition;
		this.partIndex = partIndex;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(String chunkSize) {
		this.chunkSize = chunkSize;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(String startPosition) {
		this.startPosition = startPosition;
	}

	public String getPartIndex() {
		return partIndex;
	}

	public void setPartIndex(String partIndex) {
		this.partIndex = partIndex;
	}

	@Override
	public String toString() {
		return "StoreFilePartRequest [bucket=" + bucket + ", key=" + key + ", chunkSize=" + chunkSize + ", regionName="
				+ regionName + ", startPosition=" + startPosition + ", partIndex=" + partIndex + "]";
	}

}
