package com.example.demo;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;

// 用 Vaadin 的 view搭配實作 spring-AI:open AI API的 產圖 功能
@Route("/image")
public class ImageView extends VerticalLayout {

    public ImageView (OpenAiImageModel imageClient) {
        var messageList = new VerticalLayout();
        var messageInput = new MessageInput();

        messageInput.addSubmitListener(e -> {
            messageList.add(new Paragraph("You: " + e.getValue()));

            ImageResponse response = imageClient.call(
                    new ImagePrompt(e.getValue(),
                    OpenAiImageOptions.builder()
                            .withQuality("hd")
                            .withN(1)
                            .withHeight(1024)
                            .withWidth(1024).build()));

            messageList.add(new Image(response.getResult().getOutput().getUrl(), "Generated Image"));
        });
        add(messageList, messageInput);
    }
}
