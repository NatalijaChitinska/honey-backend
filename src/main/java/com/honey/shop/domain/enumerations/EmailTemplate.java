package com.honey.shop.domain.enumerations;

public enum EmailTemplate {
    USER_ORDER_RECEIPT(
            "user-order-receipt.html",
            "Потврда за нарачка"
    );

    private String htmlTemplate;

    private String subject;

    EmailTemplate(String htmlTemplate, String subject) {
        this.htmlTemplate = htmlTemplate;
        this.subject = subject;
    }


    public String getHtmlTemplate() {
        return htmlTemplate;
    }

    public String getSubject() {
        return subject;
    }
}
