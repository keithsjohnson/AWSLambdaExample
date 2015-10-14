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

		String finshed = "false";

		// When
		while ("false".equalsIgnoreCase(finshed)) {
			StoreFilePartRequest storeFilePartRequest = new StoreFilePartRequest(bucket, key, chunkSize, regionName,
					startPosition);

			StoreFilePartResponse storeFilePartResponse = testSubject
					.handlePostcodeLocationStoreFilePart(storeFilePartRequest, mockContext);

			// Then
			assertNotNull(storeFilePartResponse);
			System.out.println(storeFilePartResponse.toString());
			startPosition = storeFilePartResponse.getNextStartPosition();
			finshed = storeFilePartResponse.getFinished();
		}
	}
}
