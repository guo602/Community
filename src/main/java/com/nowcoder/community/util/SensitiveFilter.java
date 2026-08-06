package com.nowcoder.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "**";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(is));
        ) {
            // 读取文件内容...
            String keyword;
            while((keyword = bufferedReader.readLine())!=null){
                //添加到前缀树
                this.addKeyword(keyword);
            }

        } catch (IOException e) { // 如果读取时发生 IOException，可以捕获
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }

    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;
            if (i == keyword.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return 过滤后文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return "";
        }
        //pointer1
        TrieNode tempNode = rootNode;
        //pointer2
        int begin = 0;
        //pointer3
        int position = begin ;

        StringBuilder sb = new StringBuilder();

        while(position<text.length()){
            char c = text.charAt(position);
            if (isSymbol(c)){
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++ ;

                }
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;

            }else if (tempNode.isKeyWordEnd == true){
                sb.append(REPLACEMENT);
                begin = ++ position;
                tempNode = rootNode;
            }else{
                position++;
            }

        }
        sb.append(text.substring(begin));
        return sb.toString();

    }

    private boolean isSymbol(Character c){
        // 0X2E80~0X9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0X2E80 || c > 0X9FFF);
    }
    private class TrieNode {
        private boolean isKeyWordEnd = false;
        private Map<Character,TrieNode> subNodes = new HashMap<>();
        private boolean isKeyWordEnd(){
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        public void setSubNode(Character a,TrieNode node){
            subNodes.put(a,node);
        }

        public TrieNode getSubNode(Character c){
            return subNodes.get(c);

        }

        public void addSubNode(char c,TrieNode node){
            this.subNodes.put(c,node);
        }
    }

}
