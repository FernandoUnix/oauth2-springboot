package com.marcosbarbero.lab.sec.oauth.jwt.repository;

import com.marcosbarbero.lab.sec.oauth.jwt.models.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {
    public Usuario findByUsername(String username);
}
