package com.sivalabs.messages;

import com.sivalabs.messages.Exception.OAuth2AuthenticationProcessingException;
import com.sivalabs.messages.OAuth2User.OAuth2UserInfo;
import com.sivalabs.messages.OAuth2User.OAuth2UserInfoFactory;
import com.sivalabs.messages.model.User;
import com.sivalabs.messages.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Service
public class CustomOAuth2UserService  extends DefaultOAuth2UserService {

      @Autowired
      private UserRepository userRepository;


      @Override
      public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException{

          OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

          try{
              return  processOAuthUser(oAuth2UserRequest, oAuth2User);
          }catch(AuthenticationException ex){
              throw ex;
          } catch (Exception e) {
              throw new InternalAuthenticationServiceException(e.getMessage(),e.getCause());
          }
      }

      private OAuth2User processOAuthUser(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User){
          OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                  oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                  oAuth2User.getAttributes()
          );

            if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())){
                throw  new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
            }

          Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());

           User user;

           if(userOptional.isPresent()){
               user =userOptional.get();

               if()
           }


      }



}
