package dev.vulcanth.nyel.gerementions.enums.reason;

import dev.vulcanth.nyel.gerementions.enums.punish.PunishType;
import lombok.Getter;

@Getter
public enum ReasonRevogar {
    AJI("Aplicada ao jogador incorreto", PunishType.TEMPMUTE),
    MPI("Motivo de punição incorreto", PunishType.TEMPMUTE),
    PI("Prova incorreta", PunishType.TEMPMUTE),
    PAI("Punição apliacada injustamente", PunishType.TEMPMUTE),
    RA("Revisão aceita", PunishType.TEMPMUTE);

    private final String text;
    private final PunishType punishType;

    ReasonRevogar(String text, PunishType punishType) {
        this.text = text;
        this.punishType = punishType;
    }
}
