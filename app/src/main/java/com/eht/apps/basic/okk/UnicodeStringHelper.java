package com.eht.apps.basic.okk;
import java.util.List;


public class UnicodeStringHelper {

	public int[] CodePoints;
	
	public String ConvertIntToUnicodeString(int codePoint) {
		//int codePoint = 128149;

		char[] charPair = Character.toChars(codePoint);

		String symbol = new String(charPair);
		
		return symbol;
	}
	
	@SuppressWarnings("null")
	public List<String> PopulateCharGroup(int[] UniCodePoints) {
		
		List<String> charGroup = null;
		
		for(int i : UniCodePoints)
		{
			String c = ConvertIntToUnicodeString(i);
			charGroup.add(c);
		}
		
		
		return charGroup;
	}
	
	public void GetCodePointsFromXMLFile(String filename) {
		
		
	}
	
}
