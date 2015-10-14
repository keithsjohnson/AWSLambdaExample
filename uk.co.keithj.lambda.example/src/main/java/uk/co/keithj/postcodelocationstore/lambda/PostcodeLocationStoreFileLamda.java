package uk.co.keithj.postcodelocationstore.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class PostcodeLocationStoreFileLamda {

	private static final String CR_LN_REQEX = "\\r?\\n";

	public String handlePostcodeLocationStoreFile(S3Event s3event, Context context) throws IOException {

		context.getLogger().log("-----------------------------------");
		context.getLogger().log(s3event.toJson());
		context.getLogger().log("-----------------------------------");
		context.getLogger().log(s3event.toString());
		context.getLogger().log("-----------------------------------");
		S3EventNotificationRecord record = s3event.getRecords().get(0);

		String srcBucket = record.getS3().getBucket().getName();

		String srcKey = record.getS3().getObject().getKey();

		AmazonS3 s3Client = new AmazonS3Client();
		s3Client.setRegion(Region.getRegion(Regions.fromName("eu-west-1")));

		S3Object s3Object = s3Client.getObject(new GetObjectRequest(srcBucket, srcKey));

		context.getLogger().log("srcBucket=" + srcBucket + ", srcKey=" + srcKey + ", " + s3Object.toString());
		context.getLogger().log("-----------------------------------");

		getFileFromS3InChunks(s3Client, srcBucket, srcKey, context);

		s3Client.deleteObject(srcBucket, srcKey);

		return "OK";
	}

	public void getFileFromS3InChunks(AmazonS3 s3Client, String bucket, String key, Context context) {
		GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucket, key);

		int chunkSize = 10000;
		int startPosition = 0;
		boolean moreFileData = true;
		String remainingLine = "";
		while (moreFileData) {

			context.getLogger().log("startPosition: " + startPosition + ", remainingLine: " + remainingLine);

			// Get a range of bytes from an object.
			rangeObjectRequest.setRange(startPosition, startPosition + chunkSize - 1);
			S3Object objectPortion = s3Client.getObject(rangeObjectRequest);

			int contentLength = (int) objectPortion.getObjectMetadata().getContentLength();
			context.getLogger().log("contentLength: " + contentLength);

			if (contentLength < chunkSize) {
				moreFileData = false;
			}

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(objectPortion.getObjectContent()));

			remainingLine = processInputStreamIntoTextLines(bufferedReader, 0, contentLength, remainingLine, context);

			startPosition += chunkSize;
		}
	}

	public String processInputStreamIntoTextLines(BufferedReader reader, int startPosition, int contentLength,
			String remainingLine, Context context) {

		context.getLogger().log("startPosition: " + startPosition + ", contentLength: " + contentLength
				+ ", remainingLine: " + remainingLine);

		char[] dataChar = getDataCharArray(reader, startPosition, contentLength, context);
		if (dataChar == null) {
			return "";
		}

		String dataString = remainingLine + new String(dataChar);
		// context.getLogger().log("dataString: '" + dataString + "'");

		String[] lines = dataString.split(CR_LN_REQEX);
		context.getLogger().log("lines.length: " + lines.length);

		String lastLine = lines[lines.length - 1];
		lines = removeLastLine(lines);

		// context.getLogger().log("lastLine: '" + lastLine + "'");

		List<String> postcodesList = Arrays.asList(lines);

		// processPostcodes(postcodesList, context);

		context.getLogger().log("------------------------------------------------------------");
		return lastLine;
	}

	private String[] removeLastLine(String[] lines) {
		String[] newLines = new String[lines.length - 1];
		for (int i = 0; i < newLines.length; i++) {
			newLines[i] = lines[i];
		}
		return newLines;
	}

	protected char[] getDataCharArray(BufferedReader reader, int startPosition, int contentLength, Context context) {
		char[] dataChar = new char[contentLength];
		try {
			int length = reader.read(dataChar, startPosition, contentLength);
			if (length == -1) {
				return null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return dataChar;
	}

	public void processPostcodes(List<String> postcodes, Context context) {
		System.out.println("Chunk:");
		postcodes.stream().forEach(System.out::println);
	}

}
