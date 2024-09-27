package com.example.demo;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 用 Vaadin 的 view搭配實作 spring-AI:open AI API，及Qdrant vetcor DB的 資料讀取
@Route("/rag")
public class RagView extends VerticalLayout {

    private ChatClient chatClient;

    // pre-prompt
    private final String template = """
             你會根據 DOCUMENTS: 下的文件《驚爆危機》小說回答使用者的問題
                    \s
             Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
             If unsure, simply state that you don't know.
                    \s
             DOCUMENTS:
             {documents}
                        \s
            \s""";

    public RagView(ChatClient.Builder chatClientBuilder, VectorStore vectorStore){
        this.chatClient = chatClientBuilder.build();
        var messageList = new VerticalLayout();
        var messageInput = new MessageInput();

        messageInput.addSubmitListener(e -> {
            messageList.add(new Paragraph("You: " + e.getValue()));

            var listOfSimilarDocuments = vectorStore.similaritySearch(e.getValue());
            var documents = listOfSimilarDocuments
                    .stream()
                    .map(Document::getContent)
                    .collect(Collectors.joining(System.lineSeparator()));
            var ststemMessage = new SystemPromptTemplate(this.template)
                            .createMessage(Map.of("documents", documents));
            var userMessage = new UserMessage(e.getValue());
            var prompt = new Prompt(List.of(ststemMessage, userMessage));

            messageList.add(new Paragraph("AI: " + chatClient.prompt(prompt).call().content()));


        });
        add(messageList,messageInput);
    }
}
