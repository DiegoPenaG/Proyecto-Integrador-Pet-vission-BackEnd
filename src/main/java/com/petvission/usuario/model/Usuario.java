// src/main/java/com/petvission/usuario/model/Usuario.java

package com.petvission.usuario.model;

import com.petvission.usuario.model.Rol;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    private String telefono;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "totp_secret")
    private String totpSecret;

    @Column(name = "totp_enabled", nullable = false)
    @Builder.Default
    private Boolean totpEnabled = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estado = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Relación con Rol
    // EAGER porque Spring Security lo necesita en cada petición
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    // Relación con UsuarioVeterinario
    // Solo existe si el rol es VETERINARIO
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "usuario",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = true)
    private UsuarioVeterinario datosVeterinario;

    // ============================================
    // Métodos requeridos por Spring Security
    // ============================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + rol.getNombreRol().name()
                )
        );
    }

    @Override
    public String getPassword()              { return contrasena; }

    @Override
    public String getUsername()              { return correo; }

    @Override
    public boolean isAccountNonExpired()     { return true; }

    @Override
    public boolean isAccountNonLocked()      { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled()               { return estado; }
}