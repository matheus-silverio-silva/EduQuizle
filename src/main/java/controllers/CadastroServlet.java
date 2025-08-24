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

        String nome  = request.getParameter("nome");
        String login = request.getParameter("login");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try {
            Usuario u = new Usuario(login, nome, email, senha);
            Long idGerado = usuarioService.registrarUsuario(u);

            String ok = URLEncoder.encode("cadastrado", StandardCharsets.UTF_8);
            String url = request.getContextPath() + "/index.html?ok=" + ok + "&id=" + idGerado;
            response.sendRedirect(url);

        } catch (IllegalArgumentException e) {
            String err = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/cadastro.html?erro=" + err);

        } catch (Exception e) {
            String err = URLEncoder.encode("interno", StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/cadastro.html?erro=" + err);
        }
    }
}