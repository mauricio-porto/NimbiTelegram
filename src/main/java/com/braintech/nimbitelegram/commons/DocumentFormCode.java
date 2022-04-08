package com.braintech.nimbitelegram.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum DocumentFormCode {
    PCNA_COMGER("Não Assistenciais - Compras Gerais"),
    PCNA_PREF_TECN("Não Assistenciais - Preferência Técnica"),
    PCNA_FORM_COMGER("Não Assistenciais - Formalização Compras Gerais"),

    PCASS_MATMED("Assistencial - Compra Mat Med"),
    PCASS_MATMED_URGENTE("Assistencial - Compra Urgente Mat Med"),
    PCASS_LIMINAR("Assistencial - Liminar Mat Med"),
    PCASS_FORM_MATMED("Assistencial - Formalização Mat Med"),

    PCCAF_MATMED("Assistencial - CAF Compra Mat Med"),
    PCCAF_MATMED_URGENTE("Assistencial - CAF Compra Urgente Mat Med"),
    PCCAF_LIMINAR("Assistencial - CAF Liminar Mat Med"),
    PCCAF_FORM_MATMED("Assistencial - CAF Formalização Mat Med"),

    PC_REGULARIZACAO("Assistencial - Regularização"),
    NONE("Nenhum");

    private String description;

    public static DocumentFormCode from(String code) {
        return Arrays.asList(DocumentFormCode.values()).stream().filter(orderStatus -> orderStatus.toString().equalsIgnoreCase(code)).findFirst().orElse(NONE);
    }

    public static void main(String[] args) {
        System.out.println(DocumentFormCode.from("PCASS_FORM_MATMED").getDescription());
    }
}
