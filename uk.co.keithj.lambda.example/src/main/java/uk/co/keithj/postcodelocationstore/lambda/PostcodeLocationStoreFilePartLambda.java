package uk.co.keithj.postcodelocationstore.lambda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostcodeLocationStoreFilePartLambda {

	private static final String FILENAME = "E:/dev/git/uk.co.keithj.lambda.example/uk.co.keithj.lambda.example/src/test/resources/SMpostcodes.csv";
	private static final String CR_LN_REQEX = "\\r?\\n";

	public StoreFilePartResponse handlePostcodeLocationStoreFilePart(StoreFilePartRequest storeFilePartRequest,
			Context context) {

		return processStoreFilePartRequest(context, storeFilePartRequest);

	}

	public StoreFilePartResponse handlePostcodeLocationStoreFilePartSNSEvent(SNSEvent snsEvent, Context context) {

		context.getLogger().log("-----------------------------------");
		List<SNSRecord> snsRecords = snsEvent.getRecords();
		context.getLogger().log("snsRecords.size()=" + snsRecords.size());

		StoreFilePartRequest storeFilePartRequest = null;

		for (SNSRecord snsRecord : snsRecords) {
			SNS snsMessage = snsRecord.getSNS();
			context.getLogger().log("snsMessage=" + snsMessage.getMessage());

			ObjectMapper mapper = new ObjectMapper();

			try {
				storeFilePartRequest = mapper.readValue(snsMessage.getMessage(), StoreFilePartRequest.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return processStoreFilePartRequest(context, storeFilePartRequest);
	}

	private StoreFilePartResponse processStoreFilePartRequest(Context context,
			StoreFilePartRequest storeFilePartRequest) {

		String bucket = storeFilePartRequest.getBucket();
		String key = storeFilePartRequest.getKey();
		String regionName = storeFilePartRequest.getRegionName();

		String chunkSizeString = storeFilePartRequest.getChunkSize();
		int chunkSize = Integer.parseInt(chunkSizeString);

		String startPositionString = storeFilePartRequest.getStartPosition();
		int startPosition = Integer.parseInt(startPositionString);
		context.getLogger().log("-------------------------------------------------------------------------------");
		context.getLogger().log("bucket=" + bucket + ", key=" + key + ", regionName=" + regionName + ", chunkSize="
				+ chunkSize + ", startPosition=" + startPosition);

		ObjectData objectData = getObjectData(regionName, bucket, key, startPosition, chunkSize, context);

		if (objectData.isCreateSNSMessageToProcessRemainingData()) {
			StoreFilePartRequest nextStoreFilePartRequest = new StoreFilePartRequest(bucket, key, chunkSizeString,
					regionName, Integer.toString(startPosition + objectData.getContentLength()));
			context.getLogger().log("nextStoreFilePartRequest=" + nextStoreFilePartRequest.toString());
			createSNSMessage(nextStoreFilePartRequest, context);
		}

		String finished = objectData.isCreateSNSMessageToProcessRemainingData() ? "false" : "true";
		StoreFilePartResponse storeFilePartResponse = new StoreFilePartResponse(objectData.toString(),
				Integer.toString(startPosition + objectData.getContentLength()), finished);
		context.getLogger().log(storeFilePartResponse.toString());
		context.getLogger().log("-------------------------------------------------------------------------------");
		return storeFilePartResponse;
	}

	public ObjectData getObjectData(String regionName, String bucket, String key, int startPosition, int chunkSize,
			Context context) {

		FileObjectData s3OjectData2 = getS3ObjectData(regionName, bucket, key, startPosition, chunkSize, context);
		// FileObjectData s3OjectData2 = getLocalFileObjectData(FILENAME,
		// startPosition, chunkSize, context);
		System.out.println("s3OjectData2.getContentLength()=" + s3OjectData2.getContentLength());
		// context.getLogger().log("contentLength: " +
		// s3OjectData2.getContentLength());

		boolean createSNSMessageToProcessRemainingData = true;
		if (s3OjectData2.getContentLength() < chunkSize || s3OjectData2.getContentLength() == 0) {
			createSNSMessageToProcessRemainingData = false;
		}

		int nextStartPosition = s3OjectData2.getObjectString().lastIndexOf('\n');

		String dataString = s3OjectData2.getObjectString().substring(0, nextStartPosition);

		return new ObjectData(dataString.length(), nextStartPosition, createSNSMessageToProcessRemainingData,
				dataString);
	}

	public FileObjectData getS3ObjectData(String regionName, String bucket, String key, int startPosition,
			int chunkSize, Context context) {
		AmazonS3 s3Client = new AmazonS3Client();
		s3Client.setRegion(Region.getRegion(Regions.fromName(regionName)));

		GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucket, key);
		rangeObjectRequest.setRange(startPosition, startPosition + chunkSize - 1);

		S3Object objectPortion = s3Client.getObject(rangeObjectRequest);
		int contentLength = (int) objectPortion.getObjectMetadata().getContentLength();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(objectPortion.getObjectContent()));
		String objectDataString = getDataCharArray(bufferedReader, 0, contentLength, context);

		try {
			bufferedReader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new FileObjectData(contentLength, objectDataString);
	}

	public FileObjectData getLocalFileObjectData(String filename, int startPosition, int chunkSize, Context context) {

		File file = new File(filename);
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String objectDataString = getDataCharArray(bufferedReader, startPosition, chunkSize, context);

		return new FileObjectData(objectDataString.length(), objectDataString);
	}

	private class ObjectData {

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

	private class FileObjectData {

		private final int contentLength;

		private final String objectString;

		public FileObjectData(int contentLength, String objectString) {
			super();
			this.contentLength = contentLength;
			this.objectString = objectString;
		}

		public int getContentLength() {
			return contentLength;
		}

		public String getObjectString() {
			return objectString;
		}

		@Override
		public String toString() {
			return "ObjectData [contentLength=" + contentLength + ", objectString=" + objectString + "]";
		}

	}

	public String processInputStreamIntoTextLines(BufferedReader reader, int startPosition, int contentLength,
			Context context) {

		// context.getLogger().log("startPosition: " + startPosition + ",
		// contentLength: " + contentLength);

		String dataString = getDataCharArray(reader, startPosition, contentLength, context);

		dataString.lastIndexOf(CR_LN_REQEX);
		// context.getLogger().log("dataString: '" + dataString + "'");

		String[] lines = dataString.split(CR_LN_REQEX);
		context.getLogger().log("lines.length: " + lines.length);

		// String lastLine = lines[lines.length - 1];
		// lines = removeLastLine(lines);

		// context.getLogger().log("lastLine: '" + lastLine + "'");

		// List<String> postcodesList = Arrays.asList(lines);

		// processPostcodes(postcodesList, context);

		// context.getLogger().log("------------------------------------------------------------");
		return "";
	}

	private String[] removeLastLine(String[] lines) {
		String[] newLines = new String[lines.length - 1];
		for (int i = 0; i < newLines.length; i++) {
			newLines[i] = lines[i];
		}
		return newLines;
	}

	protected String getDataCharArray(BufferedReader reader, int startPosition, int contentLength, Context context) {
		char[] dataChar = new char[contentLength];
		int length = -1;
		try {
			if (startPosition > 0) {
				reader.skip(startPosition - 1);
			}
			length = reader.read(dataChar, 0, contentLength);
			if (length == -1) {
				return "";
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new String(dataChar).substring(0, length);
	}

	public void processPostcodes(List<String> postcodes, Context context) {
		System.out.println("Chunk:");
		postcodes.stream().forEach(System.out::println);
	}

	public void createSNSMessage(StoreFilePartRequest storeFilePartRequest, Context context) {

		ObjectMapper mapper = new ObjectMapper();
		String storeFilePartRequestJSONString = null;
		try {
			storeFilePartRequestJSONString = mapper.writeValueAsString(storeFilePartRequest);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Cannot convert to StoreFilePartRequest to String: " + e.getMessage(), e);
		}
		EnvironmentVariableCredentialsProvider environmentVariableCredentialsProvider = new EnvironmentVariableCredentialsProvider();
		AmazonSNSClient snsClient = new AmazonSNSClient(environmentVariableCredentialsProvider);
		snsClient.setRegion(Region.getRegion(Regions.EU_WEST_1));

		String topicArn = "arn:aws:sns:eu-west-1:656423721434:postcodeFileUploadNotifications";

		context.getLogger().log("publish to - topicArn=" + topicArn);
		context.getLogger()
				.log("publish to - storeFilePartRequestJSONString=" + storeFilePartRequestJSONString.toString());
		PublishRequest publishRequest = new PublishRequest(topicArn, storeFilePartRequestJSONString);
		PublishResult publishResult = snsClient.publish(publishRequest);
		// print MessageId of message published to SNS topic
		context.getLogger().log("MessageId - " + publishResult.getMessageId());
	}
}
