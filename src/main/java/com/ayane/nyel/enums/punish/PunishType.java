package com.ayane.nyel.enums.punish;

import lombok.Getter;

public enum PunishType {

    BAN("Banimento permanente"),
    TEMPBAN("Banimento temporário"),
    MUTE("Mute permanente"),
    TEMPMUTE("Mute temporário"),
    KICK("Kick");

    @Getter
    private final String text;

    PunishType(String text){
        this.text = text;
    }

}
