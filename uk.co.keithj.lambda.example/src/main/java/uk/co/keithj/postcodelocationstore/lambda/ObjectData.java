package uk.co.keithj.postcodelocationstore.lambda;

public class ObjectData {

	private final int contentLength;

	private final int nextStartPosition;

	private final boolean createSNSMessageToProcessRemainingData;

	private final String objectString;

	public ObjectData(int contentLength, int nextStartPosition, boolean createSNSMessageToProcessRemainingData,
			String objectString) {
		super();
		this.contentLength = contentLength;
		this.nextStartPosition = nextStartPosition;
		this.createSNSMessageToProcessRemainingData = createSNSMessageToProcessRemainingData;
		this.objectString = objectString;
	}

	public int getContentLength() {
		return contentLength;
	}

	public int getNextStartPosition() {
		return nextStartPosition;
	}

	public boolean isCreateSNSMessageToProcessRemainingData() {
		return createSNSMessageToProcessRemainingData;
	}

	public String getObjectString() {
		return objectString;
	}

	@Override
	public String toString() {
		return "ObjectData [contentLength=" + contentLength + ", nextStartPosition=" + nextStartPosition
				+ ", createSNSMessageToProcessRemainingData=" + createSNSMessageToProcessRemainingData
				+ ", objectString=" + objectString + "]";
	}

}
