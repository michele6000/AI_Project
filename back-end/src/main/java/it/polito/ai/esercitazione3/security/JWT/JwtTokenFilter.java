package it.polito.ai.esercitazione3.security.JWT;

import io.jsonwebtoken.JwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JwtTokenFilter extends GenericFilterBean {
  private JwtTokenProvider jwtTokenProvider;

  public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public void doFilter(
    ServletRequest req,
    ServletResponse res,
    FilterChain filterChain
  )
    throws IOException, ServletException {
    try {
      String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
      if (token != null && jwtTokenProvider.validateToken(token)) {
        Authentication auth = jwtTokenProvider.getAuthentication(token);
        if (auth != null) {
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }
      filterChain.doFilter(req, res);
    } catch (JwtException e) {
      res.getWriter().println(e.getMessage());
    }
  }
}
