package com.daszczu.workoutexporter.dto;

import com.google.gson.annotations.SerializedName;

public class StravaExchangeTokenResponse {
	@SerializedName("access_token")
	private String accessToken;
	private Object athlete;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Object getAthlete() {
		return athlete;
	}

	public void setAthlete(Object athelete) {
		this.athlete = athelete;
	}
}
