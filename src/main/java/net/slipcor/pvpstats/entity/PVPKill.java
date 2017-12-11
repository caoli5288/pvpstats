package net.slipcor.pvpstats.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Table(name = "pvpkillstats")
@Data
@Entity
public class PVPKill {

    @Id
    private int id;
    private UUID uid;
    private String name;
    @Column(name = "`kill`")
    private int kill;
    private long time;
}
