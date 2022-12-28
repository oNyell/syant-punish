package dev.vulcanth.nyel.gerementions.punish.dao;

import dev.vulcanth.nyel.gerementions.Main;
import dev.vulcanth.nyel.gerementions.database.Database;
import dev.vulcanth.nyel.gerementions.enums.punish.PunishType;
import dev.vulcanth.nyel.gerementions.enums.reason.Reason;
import dev.vulcanth.nyel.gerementions.punish.Punish;
import dev.vulcanth.nyel.gerementions.punish.service.PunishService;
import dev.vulcanth.nyel.gerementions.punish.service.impl.PunishServiceImpl;
import dev.vulcanth.nyel.gerementions.thread.PunishThread;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class PunishDao {

    private final PunishThread thread;
    @Getter
    private final PunishService punishService;
    @Getter
    private final List<Punish> lastHourPunishes;

    public PunishDao() {
        this.punishService = new PunishServiceImpl();
        this.lastHourPunishes = new ArrayList<>();
        this.thread = Main.getInstance().getPunishThread();
    }

    public Punish createPunish(String targetName, String stafferName, Reason reason, String proof, String type) {
        Punish punish = Punish.builder().id(UUID.randomUUID().toString().substring(0, 6)).playerName(targetName).stafferName(stafferName).reasona(reason).type(type).proof(proof).date(new Date().getTime()).expire((reason.getTime() != 0 ? (System.currentTimeMillis() + reason.getTime()) : 0)).build();
        CompletableFuture.runAsync(() -> {
            while (getPunishService().getPunishes().stream().anyMatch(p -> p.getId().equals(punish.getId()))) {
                punish.setId(UUID.randomUUID().toString().substring(0, 6));
            }
            punishService.create(punish);
            lastHourPunishes.add(punish);
            ProxiedPlayer target = null;
            Database.getInstance().execute("INSERT INTO syant-punish VALUES (?, ?, ?, ?, ?, ?, ?, ?)", punish.getId(), punish.getPlayerName(), punish.getStafferName(), punish.getReasona().getText(), punish.getType(), punish.getProof(), punish.getDate(), punish.getExpire());
            //ReportManagerBukkit.getReports().stream().filter(report -> report.getTarget().equals(targetName)).forEach(report -> ReportManagerBukkit.deleteReport(target.getName()));
        }, thread);
        return punish;
    }

    public void loadPunishes() {

        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement statement = Database.getInstance().getConnection().prepareStatement("SELECT * FROM `syant-punish`;");
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    punishService.create(Punish.builder().id(resultSet.getString("id")).playerName(resultSet.getString("playerName")).stafferName(resultSet.getString("stafferName")).reasona(Reason.valueOf(resultSet.getString("reason"))).proof(resultSet.getString("proof")).date(resultSet.getLong("date")).expire(resultSet.getLong("expires")).build());
                }
                resultSet.close();
                statement.close();
                Main.getInstance().getLogger().info("§ePunições ativa com sucesso.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            getPunishService().getPunishes().stream().map(Punish::getPlayerName).forEach(this::clearPunishes);
        }, thread);
    }

    public void disablePunish(String id) {
        CompletableFuture.runAsync(() -> {
            Database.getInstance().execute("DELETE FROM syant-punish WHERE id = ?", id);
            punishService.remove(id);
            Main.getInstance().getLogger().info("Punish #" + id + " deletado com sucesso");
        }, thread);
    }

    public void clearPunishes(String player) {
        getPunishService().getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getExpire() > 0 && (System.currentTimeMillis() >= punish.getExpire())).forEach(punish -> disablePunish(punish.getId()));
    }

    public Stream<Punish> isBanned(String player) {
        return punishService.getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getReasona().getPunishType() == PunishType.TEMPBAN || punish.getReasona().getPunishType() == PunishType.BAN).filter(Punish::isLocked);
    }

    public Stream<Punish> isMuted(String player) {
        return punishService.getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getReasona().getPunishType() == PunishType.TEMPMUTE || punish.getReasona().getPunishType() == PunishType.MUTE).filter(Punish::isLocked);
    }
}
