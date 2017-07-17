package com.daszczu.workoutexporter;

public class StringUtils {
	
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
	public static boolean areNullOrEmpty(String... ss) {
		boolean empty = false;
		for (String s : ss) {
			empty = isNullOrEmpty(s);
			if (!empty)
				return false;
		}
		return empty;
	}

	public static String prepareActivityType(String activityType){
		return prepareActivityType(activityType, 0);
	}
	
	public static String prepareCadence(String value){
		return prepareCadence(value, 0);
	}
	
	public static String prepareCadence(double value){
		return prepareCadence(String.valueOf(value));
	}
	
	public static String prepareCadence(Long value){
		return prepareCadence(value, 0);
	}

	public static String prepareMaxHeart(Double value){
		return prepareMaxHeart(value, 0);
	}
	
	public static String prepareAvgHeart(Double value){
		return "<AverageHeartRateBpm><Value>" + value + "</Value></AverageHeartRateBpm>";
	}
	
	public static String prepareCalories(Double value){
		return "<Calories>" + value + "</Calories>";
	}
	
	public static String prepareCalories(String value) {
		return "<Calories>" + value + "</Calories>";
	}
	
	public static String prepareMaxSpeed(Double value){
		return "<MaximumSpeed>" + value + "</MaximumSpeed>";
	}

	public static String prepareTime(String value){
		return "<Time>" + value + "</Time>";
	}

	public static String prepareTime(Long value){
		return "<Time>" + value + "</Time>";
	}
	
	public static String preparePosition(String lat, String lon){
		return "<Position>\n\t<LatitudeDegrees>" + lat + "</LatitudeDegrees>\n\t<LongitudeDegrees>" + lon + "</LongitudeDegrees>\n</Position>";
	}
	
	public static String preparePosition(double lat, double lon){
		if (lat == 0.0 && lon == 0.0)
			return "";
		return "<Position>\n\t<LatitudeDegrees>" + lat + "</LatitudeDegrees>\n\t<LongitudeDegrees>" + lon + "</LongitudeDegrees>\n</Position>";
	}
	
	public static String prepareAltitude(String value){
		return "<AltitudeMeters>" + value + "</AltitudeMeters>";
	}

	public static String prepareAltitude(double value){
		if (value == 0.0)
			return "";
		return "<AltitudeMeters>" + value + "</AltitudeMeters>";
	}
	
	public static String prepareDistance(String value){
		return "<DistanceMeters>" + value + "</DistanceMeters>";
	}
	
	public static String prepareDistance(double value){
		return "<DistanceMeters>" + value + "</DistanceMeters>";
	}
	
	public static String prepareHeart(String value){
		return "<HeartRateBpm><Value>" + value + "</Value></HeartRateBpm>";
	}
	
	public static String prepareHeart(double value){
		return "<HeartRateBpm><Value>" + value + "</Value></HeartRateBpm>";
	}
	
	public static String prepareSpeed(String value){
		return "<Extensions><TPX xmlns=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\"><Speed>" + value + "</Speed></TPX></Extensions>";
	}

	public static String prepareSpeed(double value){
		return "<Extensions><TPX xmlns=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\"><Speed>" + value + "</Speed></TPX></Extensions>";
	}
	
	public static String prepareId(String value){
		return "<Id>" + value + "</Id>";
	}
	
	public static String prepareTotalTime(double value){
		return prepareTotalTime(value, 0);
	}

	public static String prepareActivityType(String activityType, int tabs){
		return prepareIndent("<Activity Sport=\"" + activityType + "\">", tabs);
	}

	public static String prepareActivityType(int activityTypeId, int tabs){
		return prepareActivityType(getActivityType(activityTypeId), tabs);
	}

	public static String prepareCadence(String value, int tabs){
		return prepareIndent("<Cadence>" + value + "</Cadence>", tabs);
	}
	
	public static String prepareCadence(Long value, int tabs){
		return prepareIndent("<Cadence>" + value + "</Cadence>", tabs);
	}

	public static String prepareMaxHeart(Double value, int tabs){
		return prepareIndent("<MaximumHeartRateBpm><Value>" + value + "</Value></MaximumHeartRateBpm>", tabs);
	}
	
	public static String prepareAvgHeart(Double value, int tabs){
		return prepareIndent("<AverageHeartRateBpm><Value>" + value + "</Value></AverageHeartRateBpm>", tabs);
	}
	
	public static String prepareCalories(Double value, int tabs){
		return prepareIndent("<Calories>" + value + "</Calories>", tabs);
	}
	
	public static String prepareCalories(String value, int tabs) {
		return prepareIndent("<Calories>" + value + "</Calories>", tabs);
	}
	
	public static String prepareMaxSpeed(Double value, int tabs){
		return prepareIndent("<MaximumSpeed>" + value + "</MaximumSpeed>", tabs);
	}

	public static String prepareTime(String value, int tabs){
		return prepareIndent("<Time>" + value + "</Time>", tabs);
	}
	
	public static String preparePosition(String lat, String lon, int tabs){
		return prepareIndent("<Position>\n\t<LatitudeDegrees>" + lat + "</LatitudeDegrees>\n\t<LongitudeDegrees>" + lon + "</LongitudeDegrees>\n</Position>", tabs);
	}
	
	public static String prepareAltitude(String value, int tabs){
		return prepareIndent("<AltitudeMeters>" + value + "</AltitudeMeters>", tabs);
	}
	
	public static String prepareDistance(String value, int tabs){
		return prepareIndent("<DistanceMeters>" + value + "</DistanceMeters>", tabs);
	}
	
	public static String prepareHeart(String value, int tabs){
		return prepareIndent("<HeartRateBpm><Value>" + value + "</Value></HeartRateBpm>", tabs);
	}
	
	public static String prepareSpeed(String value, int tabs){
		return prepareIndent("<Extensions><TPX xmlns=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\"><Speed>" + value + "</Speed></TPX></Extensions>", tabs);
	}
	
	public static String prepareId(String value, int tabs){
		return prepareIndent("<Id>" + value + "</Id>", tabs);
	}
	
	public static String prepareTotalTime(double value, int tabs){
		return prepareIndent("<TotalTimeSeconds>" + value + "</TotalTimeSeconds>", tabs);
	}
	
	public static String prepareLapTime(String lapTime) {
		return "<Lap StartTime=\"" + lapTime + "\">";
	}
	
	private static String prepareIndent(String string, int tabs){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tabs; i++)
			sb.append("\t");
		sb.append(string);
		return sb.toString();
	}

	public static String getActivityType(int activityTypeId) {
		switch(activityTypeId) {
			case 1:
			case 2:
			case 8:
			case 16:
				return "Running";
			case 4:
				return "Biking";
			default:
				return "Other";
		}
	}
}
