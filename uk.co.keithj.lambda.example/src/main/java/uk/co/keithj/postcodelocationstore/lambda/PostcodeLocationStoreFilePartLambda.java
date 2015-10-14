package uk.co.keithj.postcodelocationstore.lambda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	private static final String SEPARATOR = ",";

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

		String partIndexString = storeFilePartRequest.getPartIndex();
		int partIndex = Integer.parseInt(partIndexString);

		context.getLogger().log(
				partIndexString + " -------------------------------------------------------------------------------");
		context.getLogger().log(partIndexString + " bucket=" + bucket + ", key=" + key + ", regionName=" + regionName
				+ ", chunkSize=" + chunkSize + ", startPosition=" + startPosition);

		ObjectData objectData = getObjectData(regionName, bucket, key, startPosition, chunkSize, context,
				partIndexString);

		if (objectData.isCreateSNSMessageToProcessRemainingData()) {
			StoreFilePartRequest nextStoreFilePartRequest = new StoreFilePartRequest(bucket, key, chunkSizeString,
					regionName, Integer.toString(startPosition + objectData.getContentLength()),
					Integer.toString(partIndex + 1));
			context.getLogger()
					.log(partIndexString + " nextStoreFilePartRequest=" + nextStoreFilePartRequest.toString());
			createSNSMessage(nextStoreFilePartRequest, context, partIndexString);
		}

		// Store PostcodeLocation Data in DynamoDB
		storeDataInDynamoDB(objectData);

		String finished = objectData.isCreateSNSMessageToProcessRemainingData() ? "false" : "true";
		StoreFilePartResponse storeFilePartResponse = new StoreFilePartResponse(objectData.toString(),
				Integer.toString(startPosition + objectData.getContentLength()), finished, partIndexString);
		context.getLogger().log(partIndexString + " " + storeFilePartResponse.toString());
		context.getLogger().log(
				partIndexString + " -------------------------------------------------------------------------------");
		return storeFilePartResponse;
	}

	public ObjectData getObjectData(String regionName, String bucket, String key, int startPosition, int chunkSize,
			Context context, String partIndexString) {

		FileObjectData s3OjectData2 = getS3ObjectData(regionName, bucket, key, startPosition, chunkSize, context);
		// FileObjectData s3OjectData2 = getLocalFileObjectData(FILENAME,
		// startPosition, chunkSize, context);
		System.out.println(partIndexString + " s3OjectData2.getContentLength()=" + s3OjectData2.getContentLength());
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

	public class FileObjectData {

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

	public void createSNSMessage(StoreFilePartRequest storeFilePartRequest, Context context, String partIndexString) {

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

		context.getLogger().log(partIndexString + " publish to - topicArn=" + topicArn);
		context.getLogger().log(partIndexString + " publish to - storeFilePartRequestJSONString="
				+ storeFilePartRequestJSONString.toString());
		PublishRequest publishRequest = new PublishRequest(topicArn, storeFilePartRequestJSONString);
		PublishResult publishResult = snsClient.publish(publishRequest);
		// print MessageId of message published to SNS topic
		context.getLogger().log(partIndexString + " MessageId - " + publishResult.getMessageId());
	}

	protected void storeDataInDynamoDB(ObjectData objectData) {
		String[] lines = objectData.getObjectString().split("\n");

		List<String> linesList = Arrays.asList(lines);
		linesList.stream().filter(line -> !line.startsWith("Postcode")).sequential().skip(0).map(mapToNoAndPostcode)
				.collect(Collectors.toList());
	}

	protected Function<String, PostcodeLocationModel> mapToNoAndPostcode = (line) -> {
		// System.out.println(line);

		line = line.replace("\r", "");
		line = line.replace("\n", "");
		String[] lineData = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

		// Postcode,Latitude,Longitude,Easting,Northing,GridRef,County,District,Ward,
		// DistrictCode,WardCode,Country,CountyCode,Constituency,Introduced,Terminated,Parish,NationalPark,Population,Households,Built
		// up area,Built up sub-division,Lower layer super output
		// area,Rural/urban,Region

		String postcode = lineData[0].replace("\"", "");
		String latitude = lineData[1].replace("\"", "");
		String longitude = lineData[2].replace("\"", "");
		String easting = lineData[3].replace("\"", "");
		String northing = lineData[4].replace("\"", "");
		String gridRef = lineData[5].replace("\"", "");
		String county = lineData[6].replace("\"", "");
		String district = lineData[7].replace("\"", "");
		String ward = lineData[8].replace("\"", "");
		String districtCode = lineData[9].replace("\"", "");
		String wardCode = lineData[10].replace("\"", "");
		String country = lineData[11].replace("\"", "");
		String countyCode = lineData[12].replace("\"", "");
		String constituency = lineData[13].replace("\"", "");
		String introduced = lineData[14].replace("\"", "");
		String terminated = lineData[15].replace("\"", "");
		String parish = lineData[16].replace("\"", "");
		String nationalPark = lineData[17].replace("\"", "");
		String population = lineData[18].replace("\"", "");
		String households = lineData[19].replace("\"", "");
		String builtUpArea = lineData[20].replace("\"", "");
		String builtUpSubDivision = lineData[21].replace("\"", "");
		String lowerLayerSuperOutputArea = lineData[22].replace("\"", "");
		String ruralUrban = lineData[23].replace("\"", "");
		String region = lineData[24].replace("\"", "");

		PostcodeLocationModel postcodeLocationModel = new PostcodeLocationModel(postcode, latitude, longitude, easting,
				northing, gridRef, county, district, ward, districtCode, wardCode, country, countyCode, constituency,
				introduced, terminated, parish, nationalPark, population, households, builtUpArea, builtUpSubDivision,
				lowerLayerSuperOutputArea, ruralUrban, region);

		System.out.println(postcodeLocationModel.toString());

		return postcodeLocationModel;
	};

}
