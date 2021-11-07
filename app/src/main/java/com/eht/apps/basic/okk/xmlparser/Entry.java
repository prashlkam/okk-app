package com.eht.apps.basic.okk.xmlparser;

import java.util.ArrayList;

public class Entry implements Cloneable {

	/**
	 * @param categoryName
	 * @param includeRange_Start
	 * @param includeRange_End
	 * @param exculdeRange_Start
	 * @param exculdeRange_End
	 */
	public Entry() {
		super();
		
		Language = new String();
		FontFile = new String();
		DictionaryFile = new String();
		
		CategoryName = new String();
		IncludeRange_Start = new Integer(0);		
		IncludeRange_End = new Integer(0);
		ExculdeRange_Start = new ArrayList<Integer>();
		ExculdeRange_End = new ArrayList<Integer>();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {

		return super.clone();
		
	}

	private String Language;
	private String FontFile;
	private String DictionaryFile;
	public String getLanguage() {
		return Language;
	}

	public void setLanguage(String language) {
		Language = language;
	}

	public String getFontFile() {
		return FontFile;
	}

	public void setFontFile(String fontFile) {
		FontFile = fontFile;
	}

	public String getDictionaryFile() {
		return DictionaryFile;
	}

	public void setDictionaryFile(String dictionaryFile) {
		DictionaryFile = dictionaryFile;
	}

	private String CategoryName;
	private int IncludeRange_Start;
	private int IncludeRange_End;
	private ArrayList<Integer> ExculdeRange_Start; 
	private ArrayList<Integer> ExculdeRange_End;
	
	
	
	public String getCategoryName() {
		return CategoryName;
	}



	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}



	public int getIncludeRange_Start() {
		return IncludeRange_Start;
	}



	public void setIncludeRange_Start(int includeRange_Start) {
		IncludeRange_Start = includeRange_Start;
	}



	public int getIncludeRange_End() {
		return IncludeRange_End;
	}



	public void setIncludeRange_End(int includeRange_End) {
		IncludeRange_End = includeRange_End;
	}



	public ArrayList<Integer> getExculdeRange_Start() {
		return ExculdeRange_Start;
	}



	public void setExculdeRange_Start(ArrayList<Integer> exculdeRange_Start) {
		ExculdeRange_Start = exculdeRange_Start;
	}



	public ArrayList<Integer> getExculdeRange_End() {
		return ExculdeRange_End;
	}



	public void setExculdeRange_End(ArrayList<Integer> exculdeRange_End) {
		ExculdeRange_End = exculdeRange_End;
	}



	public void AddExcludeRange(int st, int en) {
		// TODO Auto-generated method stub
		
		this.ExculdeRange_Start.add(st);
		this.ExculdeRange_End.add(en);
		
	}
	
}
