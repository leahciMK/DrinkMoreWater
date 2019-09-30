package com.mj.drinkmorewater.components;

public enum DrinkType {
    WATER("WATER"),
    TEA("TEA"),
    GREEN_TEA("GREEN_TEA"),
    COFFE("COFFE"),
    SOFT_DRINK("SOFT_DRINK"),
    BEER("BEER"),
    JUICE("JUICE"),
    CUSTOM("CUSTOM");

    String value;

    DrinkType(String drinkType) {
        this.value = drinkType;
    }
}
