package org.example.petvission.cita.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.petvission.usuario.model.Usuario;
import org.example.petvission.usuario.model.UsuarioVeterinario;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "cita")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long idCita;

    /*
     * CLIENTE (USUARIO QUE RESERVA LA CITA)
     */
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /*
     * VETERINARIO (USUARIO VETERINARIO ASIGNADO)
     */
    @ManyToOne
    @JoinColumn(name = "id_veterinario", nullable = false)
    private UsuarioVeterinario veterinario;

    /*
     * FECHA DE LA CITA
     */
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    /*
     * HORA DE LA CITA
     */
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    /*
     * MOTIVO DE LA CONSULTA
     */
    @Column(name = "motivo", nullable = false)
    private String motivo;

    /*
     * ESTADO ACTUAL DE LA CITA
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCita estado;

    // Recordatorio
    // @Column(name = "recordatorio_enviado", nullable = false)
    // private Boolean recordatorioEnviado = false;
}