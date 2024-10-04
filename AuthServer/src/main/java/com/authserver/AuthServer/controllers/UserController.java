package com.authserver.AuthServer.controllers;


import com.authserver.AuthServer.dtos.LoginRequestDto;
import com.authserver.AuthServer.dtos.LogoutRequestDto;
import com.authserver.AuthServer.dtos.SignUpRequestDto;
import com.authserver.AuthServer.dtos.UserDto;

import com.authserver.AuthServer.security.services.CustomUserDetailsService;
import com.authserver.AuthServer.services.UserService;

import jakarta.servlet.http.HttpSession;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import okhttp3.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Base64;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    private CustomUserDetailsService customUserDetailsService;

    private  RestTemplate restTemplate;


    private  HttpSession httpSession;


    @Value("${auth.server.revoke.token.url}") // URL of your auth server's revoke endpoint
    private String revokeTokenUrl;


    public UserController(UserService userService,CustomUserDetailsService customUserDetailsService,RestTemplate restTemplate,
                          HttpSession httpSession) {
        this.userService = userService;
        this.customUserDetailsService=customUserDetailsService;
        this.restTemplate = restTemplate;
        this.httpSession = httpSession;
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequestDto request) {
        // check if email and password in db
        // if yes return user
        // else throw some error

        return userService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto request) {
        // no need to hash password for now
        // just store user as is in the db
        // for now no need to have email verification either
        String email = request.getEmail();
        String password = request.getPassword();
        String name = request.getName();;


        return UserDto.from(userService.signUp(name, email, password));
    }

//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
//        // delete token if exists -> 200
//        // if doesn't exist give a 404
//
//        userService.logout(request.getToken());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession httpSession) {
        // Assuming the token is stored in a secure way (like session, etc.)
        String token = (String) httpSession.getAttribute("access_token");

         HttpHeaders headers = new HttpHeaders();
         headers.set("Authorization", "Bearer " + token);

        // Prepare the request for revocation
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Send the request to revoke the token
        ResponseEntity<String> response = restTemplate.exchange(
                revokeTokenUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            // Clear the session or perform any other necessary actions
            return ResponseEntity.ok("Logout successful");
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Logout failed");
        }
    }


    @PostMapping("/validate/{token}")
    public UserDto validateToken(@PathVariable("token") @NonNull String token) {
        return UserDto.from(userService.validateToken(token));
    }

    @GetMapping("/getAccessToken")
    public ResponseEntity<Object> handleOAuth2Response(@RequestParam("code") String code) {
        // Now you have the authorization code and can exchange it for an access token
        return exchangeAuthorizationCodeForAccessToken(code);
       // return ResponseEntity.ok("Authorization code received: " + code);
    }

    private ResponseEntity<Object> exchangeAuthorizationCodeForAccessToken(String authorizationCode) {
        OkHttpClient client = new OkHttpClient();
        URI app = null;
        HttpHeaders httpHeaders = new HttpHeaders();
        okhttp3.RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", authorizationCode)
                .add("redirect_uri", "http://localhost:8181/auth/users/getAccessToken")
                .add("client_id", "oidc-client")
                .add("client_secret", "secret")  // Replace with your actual client secret
                .build();

        String credentials = "oidc-client:secret"; // Use your client_id and client_secret
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        Request request = new Request.Builder()
                .url("http://localhost:8181/auth/oauth2/token") // Token endpoint
                .header("Authorization", basicAuth) // Add the Basic Auth header if required
                .post(formBody)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            System.out.println("Response bodyuiuiuuuuouououou: " + responseBody);
            // return response.body().string();  // Return the response as a string
            try {
                String encodedCode = URLEncoder.encode(responseBody, "UTF-8");
                app = new URI("http://localhost:5173/callback?code="+ encodedCode);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        httpHeaders.setLocation(app);
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

}
