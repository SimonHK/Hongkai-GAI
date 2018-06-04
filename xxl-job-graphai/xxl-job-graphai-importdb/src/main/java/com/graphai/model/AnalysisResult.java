package com.graphai.model;

public class AnalysisResult {

    //分析字段
    public String AnalysisWord;

    //分析词性
    public String AnalysisNature;

    //分析的句子
    public String AnalysisSentence;

    //句子中如果有人名则摘出来并以逗号隔开
    public String SentencePersonName;

    //时间摘出来
    public String SentenceTimes;

    //摘录出来的实体ID
    public String locationids;


    public String getLocationids() {
        return locationids;
    }

    public void setLocationids(String locationids) {
        this.locationids = locationids;
    }

    public String getSentenceTimes() {
        return SentenceTimes;
    }

    public void setSentenceTimes(String sentenceTimes) {
        SentenceTimes = sentenceTimes;
    }

    public String getSentencePersonName() {
        return SentencePersonName;
    }

    public void setSentencePersonName(String sentencePersonName) {
        SentencePersonName = sentencePersonName;
    }

    public String getAnalysisWord() {
        return AnalysisWord;
    }

    public void setAnalysisWord(String analysisWord) {
        AnalysisWord = analysisWord;
    }

    public String getAnalysisNature() {
        return AnalysisNature;
    }

    public void setAnalysisNature(String analysisNature) {
        AnalysisNature = analysisNature;
    }

    public String getAnalysisSentence() {
        return AnalysisSentence;
    }

    public void setAnalysisSentence(String analysisSentence) {
        AnalysisSentence = analysisSentence;
    }
}
