package dev.vulcanth.nyel.gerementions.punish.service;

import dev.vulcanth.nyel.gerementions.model.Model;
import dev.vulcanth.nyel.gerementions.punish.Punish;

import java.util.List;

public interface PunishService extends Model<String, Punish> {

    List<Punish> getPunishes();

}
