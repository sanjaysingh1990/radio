package org.rajmoh.radio.utils;

public enum Colors {
    Color1("#f1948a"),
    Color2("#85c1e9"),
    Color3("#85c1e9"),
    Color4("#f1948a"),
    Color5("#5DADE2"),
    Color6("#16A085"),
    Color7("#1F618D"),
    Color8("#512E5F"),
    Color9("#7D6608"),
    Color10("#D68910"),
    Color11("#F5B041"),
    Color12("#D35400"),
    Color13("#212F3C"),
    Color14("#D32F2F"),
    Color15("#CC3300"),
    Color16("#9933FF"),
    Color17("#CC66CC"),
    Color18("#993366"),
    Color19("#3366CC"),
    Color20("#FF3366"),
    Color21("#00FFFF"),
    Color22("#3399FF"),
    Color23("#0033CC"),
    Color24("#CC00FF"),
    Color25("#FF8A65"),
    Color26("#66FF33"),
    Color27("#006666"),
    Color28("#993366"),
    Color29("#CC9900"),

    Color30("#00FF99");

    private String colorCode;


    private Colors(String colorCode) {
        this.colorCode = colorCode;
    }


    public String getColorCode() {
        return colorCode;
    }

}