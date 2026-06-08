package com.petvission.notificacion.model;

import com.petvission.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_notificacion_pref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioNotificacionPref {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    /** Interruptor maestro: si false no se envía ningún email. */
    @Column(name = "email_habilitado", nullable = false)
    @Builder.Default
    private Boolean emailHabilitado = true;

    /** Días de anticipación para el recordatorio (por defecto 7). */
    @Column(name = "dias_recordatorio", nullable = false)
    @Builder.Default
    private Integer diasRecordatorio = 7;

    @Column(nullable = false)
    @Builder.Default
    private Boolean recordatorio7dias = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean recordatorio1dia = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean confirmacionReserva = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean cancelacionReserva = true;
}
