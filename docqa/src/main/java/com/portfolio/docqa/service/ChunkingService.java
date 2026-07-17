package com.portfolio.docqa.service;



//This File is responsible  for the chunking of data but remember this chunking concept is used for this project and file and it is not universal sweetspot every time!

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ChunkingService {

    private  static  final int CHUNK_SIZE=500;
    private static  final int OVERLAP_SIZE=50;

    public List<String> chunkText(String text){
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();

        }

        String[] words=text.split("\\s+");
        List<String> chunks=new ArrayList<>();


        int start=0;

        while(start<words.length){
            int end=Math.min(start+CHUNK_SIZE, words.length);

            String chunk=String.join(" ", Arrays.copyOfRange(words,start,end));

            if (chunk.trim().length() > 50) {
                chunks.add(chunk.trim());
            }

            start += (CHUNK_SIZE - OVERLAP_SIZE);

        }
        return chunks;
    }
}
