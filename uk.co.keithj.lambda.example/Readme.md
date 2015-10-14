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

csv Data
--------

Postcode,  Latitude,  Longitude, Easting, Northing, GridRef,  County,           District,        Ward,             DistrictCode, WardCode,
SM1 1AA,   51.359919, -0.193024, 525907,  163866,   TQ259638, "Greater London", "Sutton",        "Sutton Central", E09000029,    E05000564,
ST7 2YB,   53.096855, -2.294545, 380374,  355557,   SJ803555, "Cheshire",       "Cheshire East", "Alsager",        E06000049,    E05008611,

Country, CountyCode, Constituency,       Introduced, Terminated,Parish,   NationalPark,Population,Households,Built up area,   Built up sub-division,Lower layer super output area,Rural/urban,              Region
England, E11000009,  "Sutton and Cheam", 1990-02-01, ,          "",       "",          ,          ,          "Greater London","Sutton",             "Sutton 012D",                "Urban major conurbation",London
England, E11000004,  "Congleton",        1990-07-01, ,          "Alsager","",          180,65,               "Alsager",       "Alsager",            "Cheshire East 040C",         "Urban city and town",    North West

Postcode,Latitude,Longitude,Easting,Northing,GridRef,County,District,Ward,DistrictCode,WardCode,
Country,CountyCode,Constituency,Introduced,Terminated,Parish,NationalPark,Population,Households,Built up area,Built up sub-division,Lower layer super output area,Rural/urban,Region

Postcode,Latitude,Longitude,Easting,Northing,GridRef,County,District,Ward,DistrictCode,WardCode,
Country,CountyCode,Constituency,Introduced,Terminated,Parish,NationalPark,Population,Households,Built up area,Built up sub-division,Lower layer super output area,Rural/urban,Region

Tables
------
Postcode
BuiltUpArea
BuiltUpSubDivision
Constituency
Country
County
District
LowerLayerSuperOutputArea
NationalPark
Parish
Region
RuralUrban
Ward
UK

Tables & Columns
----------------
Postcode
Postcode, Latitude, Longitude, Easting, Northing, GridRef, County, District, Ward, Country, CountyCode, Constituency, Introduced, Terminated, Parish,   NationalPark, Population, Households, BuiltUpArea, BuiltUpSubDivision,LowerLayerSuperOutputArea ,RuralUrban, Region

BuiltUpArea
BuiltUpArea, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

BuiltUpSubDivision
BuiltUpSubDivision, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

Constituency
Constituency, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

Country
Country, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

County
County, CountyCode, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

District
District, DistrictCode, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

LowerLayerSuperOutputArea
LowerLayerSuperOutputArea, DistrictCode, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

NationalPark
NationalPark, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

Parish
Parish, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

Region
Region, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

RuralUrban
RuralUrban, Population, Households

Ward
Ward, WardCode, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude

UK
UK, Population, Households, MaxLatitude, MaxLongitude, MinLatitude, MinLongitude, MiddleLatitude, MiddleLongitude
