package com.example.demo;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.security.auth.message.MessageInfo;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.Arrays;
import java.util.List;

// 用 Vaadin 的 view搭配實作 spring-AI:open AI API 的文字 embedding 轉換(only) 功能
@Route("embedding")
public class EmbeddingView extends VerticalLayout {

    public EmbeddingView(EmbeddingModel embeddingClient) {
        var messageList = new VerticalLayout();
        var messageInput = new MessageInput();

        messageInput.addSubmitListener(e ->{
            messageList.add(new Paragraph("You: " + e.getValue()));

            EmbeddingResponse embeddingResponse = embeddingClient.embedForResponse(List.of(e.getValue()));
            messageList.add(new Paragraph("AI: " + Arrays.toString(embeddingResponse.getResult().getOutput())));
        });

        add(messageList,messageInput);
    }
}
