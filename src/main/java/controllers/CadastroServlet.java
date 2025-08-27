package controllers;

import entidades.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.UsuarioService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private UsuarioService usuarioService;

    @Override
    public void init() throws ServletException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        request.getParameterMap().forEach((k, v) ->
                System.out.println("param " + k + " = " + java.util.Arrays.toString(v)));

        String nome  = get(request, "nome");
        String login = get(request, "login");
        String email = get(request, "email");
        String senha = get(request, "senha");
        String confirma = get(request, "confirma_senha");
        boolean termos = request.getParameter("termos") != null;

        try {
            if (!termos) throw new IllegalArgumentException("É preciso aceitar os termos.");
            if (!senha.equals(confirma)) throw new IllegalArgumentException("Senhas não conferem.");

            Usuario u = new Usuario();
            u.setLogin(login);
            u.setNome(nome);
            u.setEmail(email);
            u.setSenha(senha);
            Long idGerado = usuarioService.registrarUsuario(u);

            String ok = java.net.URLEncoder.encode("cadastrado", java.nio.charset.StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath()+"/index.html?ok="+ok+"&id="+idGerado);

        } catch (IllegalArgumentException e) {
            String err = java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
            String qs = "erro="+err+"&nome="+url(nome)+"&login="+url(login)+"&email="+url(email);
            response.sendRedirect(request.getContextPath()+"/cadastro.html?"+qs);

        } catch (Exception e) {
            String err = java.net.URLEncoder.encode("interno", java.nio.charset.StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath()+"/cadastro.html?erro="+err);
        }
    }

    private static String get(HttpServletRequest req, String name){
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
    private static String url(String s){
        return java.net.URLEncoder.encode(s == null ? "" : s, java.nio.charset.StandardCharsets.UTF_8);
    }
}