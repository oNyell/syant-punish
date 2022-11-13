package dev.vulcanth.nyel.gerementions.enums.reason;

import dev.vulcanth.nyel.gerementions.enums.punish.PunishType;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum Reason {
    AC("Zeus detection", PunishType.BAN, 0),
    HACK("Hack", PunishType.BAN, 0),
    ABUSODEBUGS("Abuso de bugs", PunishType.TEMPBAN, TimeUnit.DAYS.toMillis(30)),
    ANTIJOGOGAME("Anti jogo (Game)", PunishType.TEMPBAN, TimeUnit.DAYS.toMillis(3)),
    INVASAODECONTA("Invasão de conta", PunishType.BAN, 0),
    SKININAPROPRIADA("Skin inapropriada", PunishType.TEMPBAN, TimeUnit.DAYS.toMillis(1)),
    NICKNAMEINADEQUADO("Nickname inadequado", PunishType.BAN, 0),
    AMEACA("Ameaça", PunishType.TEMPBAN, TimeUnit.DAYS.toMillis(7)),
    AMEACAAOSERVIDOR("Ameaça ao servidor", PunishType.BAN, 0),
    DESINFORMACAO("Desinformação", PunishType.TEMPBAN, TimeUnit.DAYS.toMillis(3)),
    ASSEDIOABUSOVERBAL("Assédio/abuso verbal", PunishType.TEMPBAN, TimeUnit.DAYS.toMillis(5)),
    CHANTAGEM("Chantagem", PunishType.TEMPBAN, TimeUnit.DAYS.toMillis(7)),
    INCENTIVARUSODEPROGRAMASILEGAIS("Incentivar uso de programas ilegais", PunishType.TEMPBAN, TimeUnit.DAYS.toMillis(5)),
    ANTIJOGOCHAT("Anti jogo (chat)", PunishType.TEMPMUTE, TimeUnit.HOURS.toMillis(5)),
    DISCRIMINACAO("Discriminação", PunishType.TEMPMUTE, TimeUnit.DAYS.toMillis(7)),
    DIVULGACAOGRAVE("Divulgação grave", PunishType.MUTE, 0),
    DIVULGACAOSIMPLES("Divulgação simples", PunishType.TEMPMUTE, TimeUnit.DAYS.toMillis(1)),
    INCENTIVARFLOOD("Incentivar flood", PunishType.TEMPMUTE, TimeUnit.HOURS.toMillis(3)),
    OFENSAAJOGADOR("Ofensa a jogador", PunishType.TEMPMUTE, TimeUnit.HOURS.toMillis(3)),
    OFENSAAOSERVIDORSTAFF("Ofensa ao servidor/staff", PunishType.TEMPMUTE, TimeUnit.DAYS.toMillis(5)),
    COMERCIO("Comércio", PunishType.TEMPMUTE, TimeUnit.DAYS.toMillis(3)),
    SPAM("Spam", PunishType.TEMPMUTE, TimeUnit.HOURS.toMillis(1)),
    FLOOD("Flood", PunishType.TEMPMUTE, TimeUnit.HOURS.toMillis(5)),
    CONVERSAEXPLICITA("Conversa explícita", PunishType.TEMPMUTE, TimeUnit.DAYS.toMillis(3)),
    NENHUM("Não foi informado", PunishType.BAN, 0);

    private final String text;
    private final PunishType punishType;
    private final long time;

    Reason(String text, PunishType punishType, long time) {
        this.text = text;
        this.punishType = punishType;
        this.time = time;
    }
}
