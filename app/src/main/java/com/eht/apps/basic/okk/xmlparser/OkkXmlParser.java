package com.eht.apps.basic.okk.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.util.Log;

public class OkkXmlParser {

	private static final String TAG = OkkXmlParser.class.getName();

	private static HashMap<XmlPullParser, Boolean> tagSwitchMap = new HashMap<XmlPullParser, Boolean>();
		
	public static XmlPullParser newOkkXmlParser(InputStream is) throws XmlPullParserException {

		XmlPullParser xpp = null;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
		} catch (XmlPullParserException e) {
			Log.e(TAG, "Error in creating OkkXmlParser: " + e.getMessage());
			throw e;
		}
		
		xpp.setInput(is, null);
		tagSwitchMap.put(xpp, false);
		return xpp;
	}


	public static Entry ControlFlowThroughXmlMethod(XmlPullParser xpp, Entry entry, String TagName)
			throws XmlPullParserException, IOException {

		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_DOCUMENT) {
				Log.d("Blah", TAG + ">> Start document");
			} else if (eventType == XmlPullParser.END_DOCUMENT) {
				Log.d("Blah", TAG + ">> End document");
			} else if (eventType == XmlPullParser.START_TAG) {
				String xppTagName = xpp.getName().toString();
				if (xppTagName.equals("Language")) {
					entry.setLanguage(xpp.getAttributeValue(0).toString());
					entry.setFontFile(xpp.getAttributeValue(2).toString());
					entry.setDictionaryFile(xpp.getAttributeValue(3).toString());
				}
				if (xppTagName.equals(TagName) || tagSwitchMap.get(xpp) == true) {
					tagSwitchMap.put(xpp, true);
					Log.d("Blah", TAG + ">> Start Tag : " + xpp.getName());

					if (xppTagName.equals(TagName)
							|| xppTagName.equals("Include")
							|| xppTagName.equals("Exclude")) {
						ReadEntryValues(entry, xpp, xppTagName);
					}

				}

			} else if (eventType == XmlPullParser.END_TAG) {
				Log.d("Blah", TAG + ">> End Tag : " + xpp.getName());
				if (xpp.getName().toString().equals(TagName)) {
					tagSwitchMap.put(xpp, false);
					break;
				}
				// break;
			} else if (eventType == XmlPullParser.TEXT) {
				Log.d("Blah", TAG + ">> Text = " + xpp.getText());
			}
			eventType = xpp.next();
		}

		return entry;
	}

	public static String getLanguageAttributeFileName(InputStream fname, int fnindex){ 
		
		String filename = "";
		Entry e0 = new Entry();
		try {
			XmlPullParser xmlpp =  newOkkXmlParser(fname);
			e0 = ControlFlowThroughXmlMethod(xmlpp, e0, "Alphabets");
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if (fnindex == 1)			// 1 = font file
			filename = e0.getFontFile();			
		else if (fnindex == 2)		// 2 = dict file
			filename = e0.getDictionaryFile();
		return filename;
	}

	public static List<String> getLanguageAttributeFileName(InputStream fname){ 
		
		List<String> filenames = new ArrayList<String>();
		Entry e0 = new Entry();
		try {
			XmlPullParser xmlpp =  newOkkXmlParser(fname);
			e0 = ControlFlowThroughXmlMethod(xmlpp, e0, "Alphabets");
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
				// 1 = font file
			filenames.add(e0.getFontFile());			
				// 2 = dict file
			filenames.add(e0.getDictionaryFile());
		return filenames;
	}
	

	
	public static void ReadEntryValues(Entry entry, XmlPullParser xpp, String TagName) {

		if (TagName.equals("Alphabets") || TagName.equals("Numbers")
				|| TagName.equals("OtherChars")) {
			entry.setCategoryName(TagName);
		} else if (TagName.equals("Include")) {
			entry.setIncludeRange_Start(Integer.parseInt(xpp.getAttributeValue(
					0).toString()));
			entry.setIncludeRange_End(Integer.parseInt(xpp.getAttributeValue(1)
					.toString()));
		} else if (TagName.equals("Exclude")) {
			int numstart = Integer
					.parseInt(xpp.getAttributeValue(0).toString());
			int numend = Integer.parseInt(xpp.getAttributeValue(1).toString());
			entry.AddExcludeRange(numstart, numend);
		}

	}

/*	public Entry newEntry() {
		// TODO Auto-generated method stub

		return new Entry();

	}
*/
}
