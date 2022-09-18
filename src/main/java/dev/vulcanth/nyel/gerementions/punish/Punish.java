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
    private final String playerName, stafferName, proof, type;
    private final long date, expire;
    private final Reason reason;


    public boolean isLocked() {
        if (expire != 0) {
            return (System.currentTimeMillis() < expire);
        }
        return true;
    }

}
