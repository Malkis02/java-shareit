package ru.practicum.shareit.user.entity;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {


    @Id
    @SequenceGenerator(name = "pk_sequence",schema = "public",sequenceName = "users_id_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "pk_sequence")
    @Column(name = "id",updatable = false,unique = true)
    private Long id;

    @Column(name = "name",length = 255,nullable = false)
    private String name;

    @Column(name = "email",length = 255,nullable = false,unique = true)
    private String email;
}
