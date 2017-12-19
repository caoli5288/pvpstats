package net.slipcor.pvpstats.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Table(name = "pvpstats")
@Data
@Entity
public class PVPStat {

    @Id
    private int id;
    private String name;
    @Column(unique = true)
    private UUID uid;
    private int kills;
    private int deaths;
    private int streak;
    private int currentstreak;
    private int elo;
    private int score;
    private long time;
}
