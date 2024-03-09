package com.project.shop.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider",length = 20,nullable = false)
    private String provider;

    @Column(name = "provider_id",length = 50)
    private String providerId;

    @Column(name = "name",length = 150)
    private String name;

    @Column(name = "email",length = 150)
    private String email;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
