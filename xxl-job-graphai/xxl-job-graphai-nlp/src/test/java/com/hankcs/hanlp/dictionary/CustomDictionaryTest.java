package com.hankcs.hanlp.dictionary;

import junit.framework.TestCase;

public class CustomDictionaryTest extends TestCase
{
    public void testReload() throws Exception
    {
        CustomDictionary.loadDat("/Users/hongkai/Development/GitHubSource/GraphAI/Hongkai-GAI/data/dictionary/CoreNatureDictionary.mini.txt.bin");
        assertEquals(true, CustomDictionary.reload());
        assertEquals(true, CustomDictionary.contains("中华白海豚"));
    }
}