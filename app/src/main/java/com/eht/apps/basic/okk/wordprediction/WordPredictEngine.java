
package com.eht.apps.basic.okk.wordprediction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eht.apps.basic.okk.MainActivity;

public class WordPredictEngine {

	//private static List<String> EngUkWordList;
	
/*
	public static List<String> getEngUkWordList() {
		return EngUkWordList;
	}

	public static void setEngUkWordList(List<String> engUkWordList) {
		EngUkWordList = engUkWordList;
	}
*/
	public static List<String> putWordsIntoArrayList(InputStream dic) throws IOException {

		List<String> EngUkWordList = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(dic));
		
		String aline ;

		try{
		while ((aline = br.readLine().toString()) != null ) {
		//try {
				EngUkWordList.add(aline + " ");
		//} catch (Exception ex) {
		//	ex.printStackTrace();
		//}
		}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		br.close();		
	
		return EngUkWordList;
	
	}
	
	public static ArrayList<String> findWordsStartingWith(String startStr) {
		
		ArrayList<String> pWords = new ArrayList<String>();
		
		for (String wd : MainActivity.EngUkWordList) {
			if (wd.startsWith(startStr.toLowerCase()))
				pWords.add(wd);
			//if (pWords.size() >= MainActivity.predictedWordsCount)
			//	break;
		}

        Boolean b1 = isAalphabets(startStr);

        if (!pWords.isEmpty() && b1 == true) {
            for (int i = 0; i < pWords.size(); i++) {
                for (int j = 1; j < pWords.size(); j++) {
                    if (pWords.get(i).length() < pWords.get(j).length()) {
                        Collections.swap(pWords, i, j);
                    }
                }
            }

            if (pWords.get(0).length() > pWords.get(1).length()) {
                pWords.add(pWords.get(0));
                pWords.remove(0);
            }

            if (pWords.size() > MainActivity.predictedWordsCount) {
                pWords.subList(MainActivity.predictedWordsCount, pWords.size()).clear();
            }
        }

        return pWords;
	}

    public static boolean isAalphabets(String str) {
        char[] chrseq = str.toCharArray();

        for (char c : chrseq) {
            if (!Character.isLetter(c) == true) {
                    return false;
            }
        }
        return true;
    }

	public static ArrayList<String> getWords(InputStream dic, String startChars) throws IOException {
		
		ArrayList<String> pWords = new ArrayList<String>(8);
		
		//FileReader fr = new FileReader(dic);
		BufferedReader br = new BufferedReader(new InputStreamReader(dic));
		
		String aline ;

		try{
		while ((aline = br.readLine().toString()) != null ) {
		//try {
			if (aline.startsWith(startChars.toLowerCase()) && pWords.size() <= 8)
				pWords.add(aline + " ");
		//} catch (Exception ex) {
		//	ex.printStackTrace();
		//}
		
			if (pWords.size() >= 8)
				break;
		}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		br.close();
		
		return pWords;
	}
}
