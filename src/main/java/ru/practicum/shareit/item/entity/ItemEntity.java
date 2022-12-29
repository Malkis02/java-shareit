package ru.practicum.shareit.item.entity;

import lombok.*;
import ru.practicum.shareit.user.entity.UserEntity;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "items")
public class ItemEntity {

    @Id
    @SequenceGenerator(name = "pk_sequence",schema = "public",sequenceName = "items_id_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "pk_sequence")
    @Column(name = "id",updatable = false,unique = true)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> comments;

}