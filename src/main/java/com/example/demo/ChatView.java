package com.example.demo;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.ai.chat.client.ChatClient;


// 用 Vaadin 的 view搭配實作 spring-AI:open AI API的基本對話功能
@Route("")
public class ChatView extends VerticalLayout {

    private final ChatClient chatClient;

    public ChatView(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        var messageList = new VerticalLayout();
        var messageInput = new MessageInput();

        messageInput.addSubmitListener( e ->{
            messageList.add(new Paragraph("You: "+ e.getValue()));
            messageList.add(new Paragraph("AI: " + this.chatClient.prompt()
                    .user(e.getValue())
                    .call()
                    .content()));
        });

        add(messageList,messageInput);
    }
}
