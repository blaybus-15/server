package com.blaybus.server.domain;

import com.blaybus.server.domain.senior.Senior;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Center")
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "center_id")
    public Long id;

    @Column(name = "center_name", nullable = false)
    private String centerName; // 센터 이름

    @Column(name = "has_bath_vehicle")
    private boolean hasBathVehicle; // 목욕 차량 소유 여부

    @Column(name = "created_at")
    private String createdAt; // 설립년도(이걸로 운영기간 확인)

    @Column(name = "address", nullable = false)
    private String address; // 주소

    @Column(name = "center_rating")
    private double centerRating; // 선택 입력: 센터 등급

    @OneToMany(mappedBy = "center")
    private List<Senior> seniors; // 센터에 소속된 시니어들

    public static Center createCenter(String centerName,
                                      boolean hasBathVehicle,
                                      String createdAt,
                                      double centerRating,
                                      String address) {
        return Center.builder()
                .centerName(centerName)
                .hasBathVehicle(hasBathVehicle)
                .createdAt(createdAt)
                .address(address)
                .centerRating(centerRating)
                .build();
    }

}
