package org.example.petvission.Mascotas.Repository;

import org.example.petvission.Mascotas.Model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * REPOSITORIO DE MASCOTAS
 * PERMITE REALIZAR OPERACIONES CRUD
 * Y CONSULTAS PERSONALIZADAS
 * SOBRE LA ENTIDAD MASCOTA.
 */

@Repository
public interface MascotaRepository
        extends JpaRepository<Mascota, Long> {

    /*
     * MÉTODO PARA LISTAR LAS MASCOTAS
     * ASOCIADAS A UN USUARIO
     */
    List<Mascota> findByUsuario_IdUsuario(
            Long idUsuario
    );

    /*
     * MÉTODO PARA BUSCAR MASCOTAS
     * SEGÚN SU ESPECIE
     */
    List<Mascota> findByEspecie(
            String especie
    );

    /*
     * MÉTODO PARA LISTAR
     * LAS MASCOTAS ACTIVAS
     */
    List<Mascota> findByEstadoTrue();
}