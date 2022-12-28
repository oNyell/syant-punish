package dev.vulcanth.nyel.gerementions.punish;

import dev.vulcanth.nyel.gerementions.enums.reason.Reason;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class Punish {

    @Setter
    private String id;
    private final String playerName, stafferName, proof, type, reason;
    private final long date, expire;
    private final Reason reasona;


    public boolean isLocked() {
        if (expire != 0) {
            return (System.currentTimeMillis() < expire);
        }
        return true;
    }
}
