package ru.practicum.shareit.user.entity;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",updatable = false,unique = true)
    private Long id;

    @Column(name = "name",length = 255,nullable = false)
    private String name;

    @Column(name = "email",length = 255,nullable = false,unique = true)
    private String email;
}
