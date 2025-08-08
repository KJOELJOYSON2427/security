package com.sivalabs.messages.OAuth2User;

import java.util.Map;

public abstract class OAuth2UserInfo {


    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> atrributes){
        this.attributes = atrributes;
    }

     public Map<String, Object> getAttributes(){
        return attributes;
     }

     public  abstract String getId();

    public  abstract  String getName();

    public  abstract  String getEmail();

    public  abstract String getImageUrl();
}
