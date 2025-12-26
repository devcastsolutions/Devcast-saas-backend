package com.devcast.saas.security;


import com.devcast.saas.model.Users;
import com.devcast.saas.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepo userRepository;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
            log.error("❌ Cannot load user: emailOrUsername is null or empty");
            throw new UsernameNotFoundException("Email or username is required");
        }

        String sanitizedIdentifier = emailOrUsername.trim();
        Users user = null;
        boolean databaseAttempted = false;

        try{
            if (user == null) {
                try {
                    log.debug("🔍 User not found in cache, checking database: {}", sanitizedIdentifier);
                    user = userRepository.findByEmailOrUsername(sanitizedIdentifier).orElse(null);
                    databaseAttempted = true;

                    if (user == null) {
                        log.error("❌ User not found in database with email/username: {}", sanitizedIdentifier);
                        logUserNotFoundDetails(sanitizedIdentifier);
                        throw new UsernameNotFoundException("User not found: " + sanitizedIdentifier);
                    }

                    log.debug("✅ User found in database: {}", sanitizedIdentifier);

                    // Validate database user data
                    if (!isUserDataValid(user)) {
                        log.error("❌ Database user data is invalid for: {}", sanitizedIdentifier);
                        throw new UsernameNotFoundException("User data is corrupted: " + sanitizedIdentifier);
                    }

                } catch (UsernameNotFoundException e) {
                    throw e; // Re-throw as-is
                } catch (Exception dbException) {
                    log.error("❌ Database error while loading user: {} - {}", sanitizedIdentifier, dbException.getMessage());
                    throw new UsernameNotFoundException("Database error loading user: " + sanitizedIdentifier, dbException);
                }
            }

            // Step 3: Try to warm up cache for future requests (non-critical)


            // Step 4: Create and return UserPrincipal
            return createUserPrincipal(user, sanitizedIdentifier);

        } catch (UsernameNotFoundException e) {
            // Re-throw UsernameNotFoundException as-is
            throw e;
        } catch (Exception e) {
            log.error("❌ Unexpected error loading user: {} - {}", sanitizedIdentifier, e.getMessage(), e);

            // Comprehensive fallback: try database one more time
            try {
                if (!databaseAttempted) {
                    log.info("🔄 Attempting final database fallback for user: {}", sanitizedIdentifier);
                    user = userRepository.findByEmailOrUsername(sanitizedIdentifier).orElse(null);

                    if (user != null && isUserDataValid(user)) {
                        log.info("✅ Successfully recovered user from database: {}", sanitizedIdentifier);
                        return createUserPrincipal(user, sanitizedIdentifier);
                    }
                }
            } catch (Exception fallbackException) {
                log.error("❌ Final database fallback also failed for user: {} - {}", sanitizedIdentifier, fallbackException.getMessage());
            }

            throw new UsernameNotFoundException("Error loading user: " + sanitizedIdentifier, e);
        }
    }

    /**
     * Validate user data integrity
     */
    private boolean isUserDataValid(Users user) {
        try {
            if (user == null) {
                return false;
            }

            // Check essential fields
            if (user.getUserId() == null || user.getUserId() <= 0) {
                log.warn("⚠️ User data invalid: userId is null or invalid");
                return false;
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                log.warn("⚠️ User data invalid: email is null or empty");
                return false;
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                log.warn("⚠️ User data invalid: password is null or empty");
                return false;
            }

            if (user.getRole() == null) {
                log.warn("⚠️ User data invalid: role is null");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("❌ Error validating user data: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Create UserPrincipal with error handling
     */
    private UserDetails createUserPrincipal(Users user, String identifier) {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }

            CustomUserPrincipal principal = CustomUserPrincipal.create(user);

            if (principal == null) {
                throw new RuntimeException("Failed to create UserPrincipal");
            }

            log.debug("✅ Successfully loaded user: {} (ID: {}, Status: {})",
                    user.getEmail(), user.getUserId(), user.getStatus());

            return principal;
        } catch (Exception e) {
            log.error("❌ Error creating UserPrincipal for user: {} - {}", identifier, e.getMessage());
            throw new UsernameNotFoundException("Failed to create user principal: " + identifier, e);
        }
    }


    /**
     * Log detailed information when user is not found
     */
    private void logUserNotFoundDetails(String identifier) {
        try {
            log.error("🔍 User not found analysis for: {}", identifier);
            log.error("This could indicate:");
            log.error("1. User account was deleted");
            log.error("2. Email/username was changed");
            log.error("3. JWT token contains outdated user information");
            log.error("4. Database connectivity issues");
            log.error("5. Data corruption or migration issues");

            // Additional diagnostic information
            try {
                long totalUsers = userRepository.count();
                log.error("📊 Total users in database: {}", totalUsers);
            } catch (Exception e) {
                log.error("❌ Could not get user count: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("❌ Error logging user not found details: {}", e.getMessage());
        }
    }

    /**
     * Health check method to verify service functionality
     */
    public boolean isServiceHealthy() {
        try {
            // Test database connectivity
            long userCount = userRepository.count();
            log.debug("✅ UserDetailsService health check passed. Users: {}", userCount);
            return true;
        } catch (Exception e) {
            log.error("❌ UserDetailsService health check failed: {}", e.getMessage());
            return false;
        }
    }


}
