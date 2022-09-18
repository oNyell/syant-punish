package com.ayane.nyel.punish.service;

import com.ayane.nyel.other.model.Model;
import com.ayane.nyel.punish.Punish;

import java.util.List;

public interface PunishService extends Model<String, Punish> {

    List<Punish> getPunishes();

}
