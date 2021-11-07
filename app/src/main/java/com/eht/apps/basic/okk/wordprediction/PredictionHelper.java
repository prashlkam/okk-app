package com.eht.apps.basic.okk.wordprediction;

import java.util.ArrayList;
import java.util.List;

public class PredictionHelper {

    List<Word> listofwords;
    int predictedWordsCount;

    public List<Word> getListofwords() {
        return listofwords;
    }

    public void setListofwords(List<Word> listofwords) {
        this.listofwords = listofwords;
    }

    public int getPredictedWordsCount() {
        return predictedWordsCount;
    }

    public void setPredictedWordsCount(int predictedWordsCount) {
        this.predictedWordsCount = predictedWordsCount;
    }

    public void ResetWordList() {
        this.listofwords = new ArrayList<Word>();
    }

    public void AddWordToList(String wd) {
        this.listofwords.add(GetWordFromString(wd));
    }

    private Word GetWordFromString(String wd) {
        // TODO Auto-generated method stub
        Word cw = new Word();
        cw.setName(wd);
        cw.setLen(wd.length());

        return cw;
    }

}
