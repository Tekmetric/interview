package com.interview._infrastructure.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "revisions")
@Getter
@Setter
@RevisionEntity
public class Revision {

    @Id
    @RevisionNumber
    //h2 is not generating the unique id, so this is a work-around.  It does produce a warning message on startup
    //the fix would be to use a different h2 version
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rev_seq")
    @SequenceGenerator(
            name = "rev_seq",
            sequenceName = "revisions_seq",
            allocationSize = 1
    )
    private Integer id;

    @RevisionTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_stamp")
    private Date timeStamp;
}
