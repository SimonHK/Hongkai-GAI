package com.graphai.model;

public class RulePressObject {


    //规则公式类别
    public String ruleType = "";

    //规则公式
    public String ruleText = "";
    //对应文本内容
    public String textContent = "";

    //对应类型，是存储还是丢弃或者其他
    public String type = "";

    //内容时间
    public String textTime = "";

    //内容出处
    public String textFrom = "";

    //摘要内容
    public String abstractText = "";

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getTextTime() {
        return textTime;
    }

    public void setTextTime(String textTime) {
        this.textTime = textTime;
    }

    public String getTextFrom() {
        return textFrom;
    }

    public void setTextFrom(String textFrom) {
        this.textFrom = textFrom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRuleText() {
        return ruleText;
    }

    public void setRuleText(String ruleText) {
        this.ruleText = ruleText;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
