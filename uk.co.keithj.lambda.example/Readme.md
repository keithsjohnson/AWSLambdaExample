Test Data
---------
{ "firstName":"Keith", "lastName":"Johnson" }

Links
-----
http://docs.aws.amazon.com/lambda/latest/dg/java-handler-io-type-pojo.html

Handler
-------
example.Hello::myHandler

PostcodeLocationStoreFileLamda
uk.co.keithj.postcodelocationstore.lambda.PostcodeLocationStoreFileLamda::handlePostcodeLocationStoreFile

PostcodeLocationStoreFilePartLambda
uk.co.keithj.postcodelocationstore.lambda.PostcodeLocationStoreFilePartLambda::handlePostcodeLocationStoreFilePart

PostcodeLocationStoreFilePartSNSEvent
uk.co.keithj.postcodelocationstore.lambda.PostcodeLocationStoreFilePartLambda::handlePostcodeLocationStoreFilePartSNSEvent

{
  "bucket": "postcodelocationstorefiles",
  "key": "SMpostcodes.csv",
  "chunkSize": "10000",
  "regionName": "eu-west-1",
  "startPosition": "0"
}

{
  "bucket": "postcodelocationstorefiles",
  "key": "SMpostcodes.csv",
  "chunkSize": "10000",
  "regionName": "eu-west-1",
  "startPosition": "9785"
}

{
  "bucket": "postcodelocationstorefiles",
  "key": "SMpostcodes.csv",
  "chunkSize": "10000",
  "regionName": "eu-west-1",
  "startPosition": "19573"
}

{
  "bucket": "postcodelocationstorefiles",
  "key": "SMpostcodes.csv",
  "chunkSize": "10000",
  "regionName": "eu-west-1",
  "startPosition": "29334"
}

29334


	private String bucket;

	private String key;

	private String chunkSize;

	private String regionName;

	private String startPosition;
