package com.ogl.MarketInfo.controller;

import com.ogl.MarketInfo.model.Role;
import com.ogl.MarketInfo.model.Usuario;
import com.ogl.MarketInfo.repository.RoleRepository;
import com.ogl.MarketInfo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    public static class LoginRequest {
        private String username;
        private String senha;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }

    @GetMapping("/login")
    public String login() {
        return "/login/login";
    }
    @GetMapping("/registro")
    public String registro() {
        return "/login/registro";
    }


    @PostMapping("/logar")
    public String logar(@RequestParam("username") String username,
                       @RequestParam("senha") String senha, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        try {
            if (usuario.isPresent()) {
                if (passwordEncoder.matches(senha, usuario.get().getPassword())) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, senha);

                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    if (authentication != null && authentication.isAuthenticated()) {
                        return "/home/home";
                    }
                } else {
                    redirectAttributes.addFlashAttribute("mensagem", "Usuário/senha incorretos.");
                    return "redirect:/login";
                }
            } else {
                redirectAttributes.addFlashAttribute("mensagem", "Usuário/senha incorretos.");
                return "redirect:/login";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Ocorreu um erro.");
            return "/login/login";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Ocorreu um erro.");
        return "redirect:/login";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam String username,
                               @RequestParam String senha,
                               @RequestParam String email,
                               RedirectAttributes redirectAttributes) {

        if (usuarioRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("mensagem", "Este username já existe!");
            return "redirect:/registro";

        }

        if(usuarioRepository.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("mensagem", "Email já cadastrado!");
            return "redirect:/registro";
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(senha));
        usuario.setEnabled(true);

        Role roleUser = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new IllegalArgumentException("Role USER não encontrada"));
        usuario.setRoles(Collections.singleton(roleUser));

        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário criado com sucesso!");
        return "redirect:/login";
    }
}
