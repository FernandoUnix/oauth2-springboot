package com.marcosbarbero.lab.sec.oauth.jwt.ds.config.resources;

import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class LLGAuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    @Autowired
    private OAuth2ClientProperties oAuth2ClientProperties;

    @Autowired
    private BaseOAuth2ProtectedResourceDetails baseOAuth2ProtectedResourceDetails;
    private WebResponseExceptionTranslator<?> exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        try {

            //Resolve exceptions, if 401, handle them
            ResponseEntity<?> result = exceptionTranslator.translate(authException);
            if (result.getStatusCode() == HttpStatus.UNAUTHORIZED) {


                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

                String cienteId = "clientId";//oAuth2ClientProperties.getClientId();
                String clienteSecret = "secret";// oAuth2ClientProperties.getClientSecret();

                formData.add("client_id", cienteId);
                formData.add("client_secret", clienteSecret);

                formData.add("grant_type", "refresh_token");
                Enumeration<String> cookies = request.getHeaderNames();
                String bearer = request.getHeader("authorization");

                Cookie[] cookie=request.getCookies();

                for(Cookie coo:cookie){
                    if(coo.getName().equals("refresh_token")){
                        formData.add("refresh_token", coo.getValue());
                    }
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setBasicAuth("clientId","secret");

                String urlRefreshToken = "http://localhost:9000/oauth/token"; //baseOAuth2ProtectedResourceDetails.getAccessTokenUri();

                Map map = restTemplate.exchange(urlRefreshToken, HttpMethod.POST,
                        new HttpEntity<MultiValueMap<String, String>>(formData, headers), Map.class).getBody();

                //If the exception is refreshed, sit back and deal with it further.

                if(map.get("error")!=null){
                    // Returns error information in the specified format
                    response.setStatus(401);
                    response.setHeader("Content-Type", "application/json;charset=utf-8");
                    response.getWriter().print("{\"code\":1,\"message\":\""+map.get("error_description")+"\"}");
                    response.getWriter().flush();
                    //If it's a web page, jump to the landing page
                    //response.sendRedirect("login");
                }else{

                    //If the refresh succeeds, store the cookie and jump to the page you originally wanted to visit

                    String novoToken = "";

                    for(Object key:map.keySet()){

                        if(key.toString() !="scope"){
                            //response.addCookie(new Cookie(key.toString(),map.get(key).toString()));
                        }else{
                            //response.addCookie(new Cookie("scope","read_write"));
                        }

                        if(key.toString() =="access_token"){
                           novoToken = map.get(key).toString();
                        }

                    }
                    //Collection<String>  _headers = response.getHeaderNames();
                    response.addHeader("Authorization", "Bearer "+novoToken);
                    //Collection<String>  _headers2 = response.getHeaderNames();

                    request.getRequestDispatcher(request.getRequestURI()).forward(request,response);
                }
            }else{
                //If it is not a 401 exception, continue processing other exceptions by default
                super.commence(request, response, authException);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}