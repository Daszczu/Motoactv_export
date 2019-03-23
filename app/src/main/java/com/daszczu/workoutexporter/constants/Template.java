package com.daszczu.workoutexporter.constants;

public class Template {

	private Template(){
	}

	private static final String NEW_LINE = "\r\n";
	
	public static String getBegining() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
			.append(NEW_LINE)
			.append("<TrainingCenterDatabase")
			.append(NEW_LINE)
			.append("  xsi:schemaLocation=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2 http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd\"")
			.append(NEW_LINE)
			.append("  xmlns:ns5=\"http://www.garmin.com/xmlschemas/ActivityGoals/v1\"")
			.append(NEW_LINE)
			.append("  xmlns:ns3=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\"")
			.append(NEW_LINE)
			.append("  xmlns:ns2=\"http://www.garmin.com/xmlschemas/UserProfile/v2\"")
			.append(NEW_LINE)
			.append("  xmlns=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2\"")
			.append(NEW_LINE)
			.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ns4=\"http://www.garmin.com/xmlschemas/ProfileExtension/v1\">")
			.append(NEW_LINE)
			.append("  <Activities>");
		return sb.toString();
	}
}
