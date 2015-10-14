package uk.co.keithj.postcodelocationstore.lambda;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.amazonaws.services.lambda.runtime.Context;

@RunWith(MockitoJUnitRunner.class)
public class PostcodeLocationStoreFilePartLambdaTest {

	private PostcodeLocationStoreFilePartLambda testSubject;

	@Mock
	private Context mockContext;

	@Before
	public void setUp() {
		testSubject = new PostcodeLocationStoreFilePartLambda();
	}

	@Test

	@Ignore
	public void shouldHandlePostcodeLocationStoreFilePartLambda() {

		// Given
		String bucket = "bucket";
		String key = "key";
		String chunkSize = "10000";
		String regionName = "regionName";
		String startPosition = "0";
		String partIndexString = "0";

		String finshed = "false";

		// When
		while ("false".equalsIgnoreCase(finshed)) {
			StoreFilePartRequest storeFilePartRequest = new StoreFilePartRequest(bucket, key, chunkSize, regionName,
					startPosition, partIndexString);

			StoreFilePartResponse storeFilePartResponse = testSubject
					.handlePostcodeLocationStoreFilePart(storeFilePartRequest, mockContext);

			// Then
			assertNotNull(storeFilePartResponse);
			System.out.println(storeFilePartResponse.toString());
			startPosition = storeFilePartResponse.getNextStartPosition();
			finshed = storeFilePartResponse.getFinished();
		}
	}

	@Test
	public void shouldStoreDataInDynamoDB() {
		// Given
		String data = data();
		ObjectData objectData = new ObjectData(data.length(), data.length(), true, data);

		// when
		testSubject.storeDataInDynamoDB(objectData);

		// Then
	}

	private String data() {
		StringBuilder sb = new StringBuilder(1000);
		sb.append(
				"Postcode,Latitude,Longitude,Easting,Northing,GridRef,County,District,Ward,DistrictCode,WardCode,Country,CountyCode,Constituency,Introduced,Terminated,Parish,NationalPark,Population,Households,Built up area,Built up sub-division,Lower layer super output area,Rural/urban,Region\r\n");

		sb.append(
				"ST7 2YB,53.096855,-2.294545,380374,355557,SJ803555,\"Cheshire\",\"Cheshire East\",\"Alsager\", E06000049,E05008611,England,E11000004,\"Congleton\",1990-07-01,,\"Alsager\",\"\",180,65,\"Alsager\",\"Alsager\",\"Cheshire East 040C\",\"Urban city and town\",North West\r\n");
		sb.append(
				"ST7 2YD,53.095001,-2.313019,379136,355356,SJ791553,\"Cheshire\",\"Cheshire East\",\"Alsager\", E06000049,E05008611,England,E11000004,\"Congleton\",1990-07-01,,\"Alsager\",\"\",37,36,\"Alsager\",\"Alsager\",\"Cheshire East 042B\",\"Urban city and town\",North West\r\n");
		sb.append(
				"ST7 2YE,53.08589,-2.259071,382745,354328,SJ827543,\"Staffordshire\",\"Newcastle-under-Lyme\",\"Butt Lane\", E07000195,E05006966,England,E10000028,\"Stoke-on-Trent North\",2007-12-01,2008-01-01,\"Kidsgrove\",\"\",,,\"Stoke-on-Trent\",\"Kidsgrove\",\"Newcastle-under-Lyme 003C\",\"Urban city and town\",West Midlands\r\n");
		return sb.toString();
	}

}
