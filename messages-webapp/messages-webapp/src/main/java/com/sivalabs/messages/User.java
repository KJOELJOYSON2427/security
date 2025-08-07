package com.sivalabs.messages;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private  String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
     private  Role role;

    private String image;
    @Column(nullable = false)
    private String Provider;
}
