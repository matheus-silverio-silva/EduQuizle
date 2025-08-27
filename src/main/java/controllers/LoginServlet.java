// LoginServlet.java
package controllers;

import entidades.Usuario;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import services.UsuarioService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UsuarioService usuarioService;

    @Override
    public void init() {
        this.usuarioService = new UsuarioService();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String login = val(req.getParameter("login"));
        String senha = val(req.getParameter("senha"));

        Usuario u = usuarioService.autenticar(login, senha);
        if (u == null) {
            String erro = URLEncoder.encode("Login ou senha inválidos", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath()+"/index.html?erro="+erro+"&login="+enc(login));
            return;
        }
        resp.sendRedirect(req.getContextPath()+"/home.html?ok=Bem-vindo&nome="+enc(u.getNome()));
    }

    private static String val(String s){ return s==null? "": s.trim(); }
    private static String enc(String s){ return URLEncoder.encode(s==null?"":s, StandardCharsets.UTF_8); }
}
