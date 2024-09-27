package com.example.demo;


import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

// 用 Vaadin 的 view搭配實作 spring-AI:open AI API 及 Qdrant vector DB，做文件讀取(doc.txt)，及轉換embedding後送入遠端 vector db的流程

@Component
public class DocumentLoader {

    @Value("classpath:doc.txt")
    private Resource resource;

    @Value("${spring.ai.vectorstore.qdrant.host}")
    private String host;

    @Value("${spring.ai.vectorstore.qdrant.port}")
    private int port;

    @Value("${spring.ai.vectorstore.qdrant.api-key}")
    private String apiKey;

    @Value("${spring.ai.vectorstore.qdrant.collection-name}")
    private String collectionName;

    @Value("${spring.ai.vectorstore.qdrant.use-tls}")
    private boolean useTls;

    private final VectorStore vectorStore;

    public DocumentLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        QdrantClient client = new QdrantClient(QdrantGrpcClient.newBuilder(host, port, useTls)
                .withApiKey(apiKey).build());

        Collections.CollectionInfo workshop = client.getCollectionInfoAsync(collectionName).get();
        System.out.println("PointsCount: " + workshop.getPointsCount());
        if(workshop.getPointsCount()>0){
            System.out.println("Collection not empty.");
            return;
        }

        var textSplitter = new TokenTextSplitter();
        List<Document> documents = textSplitter.apply(loadText());
        System.out.println("Document: " + documents.size());
        System.out.println(documents);

        vectorStore.add(documents);
    }

    List<Document> loadText(){
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName", "doc.txt");
        return textReader.get();
    }
}
