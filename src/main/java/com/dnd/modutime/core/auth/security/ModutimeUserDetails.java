package com.dnd.modutime.core.auth.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface ModutimeUserDetails extends UserDetails {

    String getUsername();
}
