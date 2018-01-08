package com.daszczu.workoutexporter.mapsimporter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.daszczu.workoutexporter.managers.DatabaseManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MapsImporter {
  
  public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException, XmlPullParserException {
    File file = new File(Environment.getExternalStorageDirectory(), "m81.gpx");
    List<MapTrackPoint> tracks = getMapTracks(file);

    for (MapTrackPoint track2 : tracks) {
      System.out.println(String.format("%s lat, %s lon, %s alt", track2.getLat(), track2.getLon(), track2.getAlt()));
    }
  }
  
  public static List<MapTrackPoint> getMapTracks(File file) throws SAXException, ParserConfigurationException, IOException, XmlPullParserException {
    XmlPullParserFactory  factory = XmlPullParserFactory.newInstance();
    factory.setNamespaceAware(true);
    XmlPullParser xpp = factory.newPullParser();
    xpp.setInput(new InputStreamReader(new FileInputStream(file)));

    List<MapTrackPoint> tracks = new ArrayList<>();
    MapTrackPoint track = null;

    int event = xpp.getEventType();

    boolean insideTag = false;
    while (event != XmlPullParser.END_DOCUMENT) {
      switch (event) {
        case XmlPullParser.START_TAG:
          if ("trkpt".equals(xpp.getName())) {
            track = new MapTrackPoint();
            insideTag = true;
            track.setLat(Double.valueOf(xpp.getAttributeValue(0)));
            track.setLon(Double.valueOf(xpp.getAttributeValue(1)));
          }
          break;

        case XmlPullParser.TEXT:
          if (insideTag) {
            String content = xpp.getText();
            if (content != null) {
              content = content.replace("\n", "").replace(" ", "");
              if (!content.equals(""))
                track.setAlt(Double.valueOf(content));
            }
          }
          break;

        case XmlPullParser.END_TAG:
          if ("trkpt".equals(xpp.getName())) {
              insideTag = false;
              tracks.add(track);
              break;
          }
          break;
      }
      event = xpp.next();
    }

    return tracks;
  }

  public static void getEventsDataFromCalendar(Context ctx) {
    DatabaseManager dbClient = new DatabaseManager(ctx.getContentResolver());
    for (String row : dbClient.getCalendarEvents()) {
      Log.d("EVENT", row);
    }

  }

/*  public static void getRoutes(Context ctx) {
    DatabaseManager dbClient = new DatabaseManager(ctx.getContentResolver());
    for (String row : dbClient.getGPX())
      Log.d("EVENT", row);
  }*/
}