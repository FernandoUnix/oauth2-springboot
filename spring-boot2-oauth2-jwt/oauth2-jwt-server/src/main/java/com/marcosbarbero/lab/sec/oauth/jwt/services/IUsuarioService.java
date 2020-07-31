package com.marcosbarbero.lab.sec.oauth.jwt.services;

import com.marcosbarbero.lab.sec.oauth.jwt.models.entity.Usuario;

public interface IUsuarioService {

    public Usuario findByUsername(String username);

}
