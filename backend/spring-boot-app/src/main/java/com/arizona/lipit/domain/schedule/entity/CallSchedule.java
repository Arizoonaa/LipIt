package com.arizona.lipit.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "call_schedule", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "scheduled_day"})
})
public class CallSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long callScheduleId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek scheduledDay;

    @Column(nullable = false)
    private String scheduledTime;

    @Enumerated(EnumType.STRING)
    private TopicCategory topicCategory;

    @Column(nullable = false)
    private Integer missedCount;

    @Column(nullable = false)
    private Boolean isCalled;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.missedCount = 0;
        this.isCalled = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}