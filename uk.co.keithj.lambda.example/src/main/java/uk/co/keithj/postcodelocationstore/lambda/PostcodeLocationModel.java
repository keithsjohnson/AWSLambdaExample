package uk.co.keithj.postcodelocationstore.lambda;

public class PostcodeLocationModel {

	private String postcode;
	private String latitude;
	private String longitude;
	private String easting;
	private String northing;
	private String gridRef;
	private String county;
	private String district;
	private String ward;
	private String districtCode;
	private String wardCode;
	private String country;
	private String countyCode;
	private String constituency;
	private String introduced;
	private String terminated;
	private String parish;
	private String nationalPark;
	private String population;
	private String households;
	private String builtUpArea;
	private String builtUpSubDivision;
	private String lowerLayerSuperOutputArea;
	private String ruralUrban;
	private String region;

	public PostcodeLocationModel(String postcode, String latitude, String longitude, String easting, String northing,
			String gridRef, String county, String district, String ward, String districtCode, String wardCode,
			String country, String countyCode, String constituency, String introduced, String terminated, String parish,
			String nationalPark, String population, String households, String builtUpArea, String builtUpSubDivision,
			String lowerLayerSuperOutputArea, String ruralUrban, String region) {
		super();
		this.postcode = postcode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.easting = easting;
		this.northing = northing;
		this.gridRef = gridRef;
		this.county = county;
		this.district = district;
		this.ward = ward;
		this.districtCode = districtCode;
		this.wardCode = wardCode;
		this.country = country;
		this.countyCode = countyCode;
		this.constituency = constituency;
		this.introduced = introduced;
		this.terminated = terminated;
		this.parish = parish;
		this.nationalPark = nationalPark;
		this.population = population;
		this.households = households;
		this.builtUpArea = builtUpArea;
		this.builtUpSubDivision = builtUpSubDivision;
		this.lowerLayerSuperOutputArea = lowerLayerSuperOutputArea;
		this.ruralUrban = ruralUrban;
		this.region = region;
	}

	public String getPostcode() {
		return postcode;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getEasting() {
		return easting;
	}

	public String getNorthing() {
		return northing;
	}

	public String getGridRef() {
		return gridRef;
	}

	public String getCounty() {
		return county;
	}

	public String getDistrict() {
		return district;
	}

	public String getWard() {
		return ward;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public String getWardCode() {
		return wardCode;
	}

	public String getCountry() {
		return country;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public String getConstituency() {
		return constituency;
	}

	public String getIntroduced() {
		return introduced;
	}

	public String getTerminated() {
		return terminated;
	}

	public String getParish() {
		return parish;
	}

	public String getNationalPark() {
		return nationalPark;
	}

	public String getPopulation() {
		return population;
	}

	public String getHouseholds() {
		return households;
	}

	public String getBuiltUpArea() {
		return builtUpArea;
	}

	public String getBuiltUpSubDivision() {
		return builtUpSubDivision;
	}

	public String getLowerLayerSuperOutputArea() {
		return lowerLayerSuperOutputArea;
	}

	public String getRuralUrban() {
		return ruralUrban;
	}

	public String getRegion() {
		return region;
	}

	@Override
	public String toString() {
		return "PostcodeLocationModel [postcode=" + postcode + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", easting=" + easting + ", northing=" + northing + ", gridRef=" + gridRef + ", county=" + county
				+ ", district=" + district + ", ward=" + ward + ", districtCode=" + districtCode + ", wardCode="
				+ wardCode + ", country=" + country + ", countyCode=" + countyCode + ", constituency=" + constituency
				+ ", introduced=" + introduced + ", terminated=" + terminated + ", parish=" + parish + ", nationalPark="
				+ nationalPark + ", population=" + population + ", households=" + households + ", builtUpArea="
				+ builtUpArea + ", builtUpSubDivision=" + builtUpSubDivision + ", lowerLayerSuperOutputArea="
				+ lowerLayerSuperOutputArea + ", ruralUrban=" + ruralUrban + ", region=" + region + "]";
	}

}
