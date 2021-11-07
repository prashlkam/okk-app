
package com.eht.apps.basic.okk.wordprediction;

import android.util.Log;

public class Word {

	public Word() {
		// TODO Auto-generated constructor stub
		name = new String();
		wordType = new String();
		spelling = new String();
		len = new Integer(0);
		currpos = new Integer(0);
	}
	String name, wordType;
	CharSequence spelling;
	int len, currpos;
	
	public CharSequence getSpelling() {
		return spelling;
	}
	public void setSpelling(CharSequence spelling) {
		this.spelling = spelling;
	}
	public void AddCharToSpelling(String chr) {
		this.spelling = this.spelling + chr.toString();
	}

	public void RemoveLastCharFromSpelling() {
		if (this.spelling.length() > 0 )
			this.spelling = this.spelling.subSequence(0, this.spelling.length()-1);
		Log.d("tag", this.spelling.toString());
	}
	
	public String getName() {
		return name;
	}
	public void setName(String wd) {
		this.name = wd;
	}
	public String getWordType() {
		return wordType;
	}
	public void setWordType(String wordType) {
		this.wordType = wordType;
	}
	public int getLen() {
		return len;
	}
	
	public int getCurrpos() {
		return currpos;
	}
	public void setCurrpos(int currpos) {
		this.currpos = currpos;
	}
	public void setLen(int length) {
		// TODO Auto-generated method stub
	    this.len = length;	
	}
	
	
}
