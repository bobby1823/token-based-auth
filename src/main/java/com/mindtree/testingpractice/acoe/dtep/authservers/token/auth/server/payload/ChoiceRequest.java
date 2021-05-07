package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class ChoiceRequest {
    @NotBlank
    @Size(max = 40)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
