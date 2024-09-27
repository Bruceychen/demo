package com.example.demo;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.ai.chat.client.ChatClient;
import org.vaadin.firitin.components.messagelist.MarkdownMessage;

// 用 Vaadin 的 view搭配實作 spring-AI:open AI API的進階串流對話功能
@Route("/stream")
public class StreamView extends VerticalLayout {

    private final ChatClient chatClient;

    public StreamView(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        var messageList = new VerticalLayout();
        var messageInput = new MessageInput();

        messageInput.addSubmitListener( e ->{
            var question = e.getValue();
            var userMessage = new MarkdownMessage(question, "You",
                    MarkdownMessage.Color.AVATAR_PRESETS[1]);
            var assistantMessage = new MarkdownMessage("Assistant",
                    MarkdownMessage.Color.AVATAR_PRESETS[2]);

            messageList.add(userMessage, assistantMessage);

            chatClient.prompt().user(question)
                    .stream().content().subscribe(assistantMessage::appendMarkdownAsync);
        });

        add(messageList,messageInput);
    }
}
