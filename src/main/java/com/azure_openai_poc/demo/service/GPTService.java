package com.azure_openai_poc.demo.service;

import com.openai.client.OpenAIClient;

import com.openai.client.okhttp.OpenAIOkHttpClient;

import com.openai.models.ChatModel;

import com.openai.models.chat.completions.ChatCompletion;

import com.openai.models.chat.completions.ChatCompletionCreateParams;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

@Service
public class GPTService {

    @Value("${openai.key}")
    private String key;

    /**
     * Create OpenAI client
     */
    private OpenAIClient getClient() {

        return OpenAIOkHttpClient.builder()
                .apiKey(key)
                .build();
    }

    /**
     * Generate summary from document text
     */
    public String summarize(String text) {

        try {

            ChatCompletionCreateParams params =

                    ChatCompletionCreateParams.builder()

                            .model(ChatModel.GPT_3_5_TURBO)

                            .addUserMessage(
                                    "Summarize this document in simple points:\n\n"
                                            +  limitText(text)
                            )

                            .build();

            ChatCompletion completion =

                    getClient()
                            .chat()
                            .completions()
                            .create(params);

            return completion
                    .choices()
                    .get(0)
                    .message()
                    .content()
                    .orElse("No summary generated");

        } catch (Exception e) {

            e.printStackTrace();

            return "Failed to generate summary: "
                    + e.getMessage();
        }
    }

    /**
     * Ask AI question from document
     */
    public String askQuestion(
            String document,
            String question
    ) {

        try {

            String prompt =

                    "Answer ONLY using this document.\n\n"

                            + "DOCUMENT:\n"

                            + limitText(document)

                            + "\n\nQUESTION:\n"

                            + question;

            ChatCompletionCreateParams params =

                    ChatCompletionCreateParams.builder()

                            .model(ChatModel.GPT_3_5_TURBO)

                            .addUserMessage(prompt)

                            .build();

            ChatCompletion completion =

                    getClient()
                            .chat()
                            .completions()
                            .create(params);

            return completion
                    .choices()
                    .get(0)
                    .message()
                    .content()
                    .orElse("No response");

        } catch (Exception e) {

            e.printStackTrace();

            return "Failed to process question: "
                    + e.getMessage();
        }
    }
    private String limitText(String text) {

        int maxLength = 12000;

        if(text.length() > maxLength) {

            return text.substring(0, maxLength);
        }

        return text;
    }
}