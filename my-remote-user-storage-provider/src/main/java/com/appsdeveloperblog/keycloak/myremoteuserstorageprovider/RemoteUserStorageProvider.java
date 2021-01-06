package com.appsdeveloperblog.keycloak.myremoteuserstorageprovider;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

public class RemoteUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private KeycloakSession keycloakSession;
    private ComponentModel componentModel;
    private UsersApiService usersService;

    public RemoteUserStorageProvider(KeycloakSession keycloakSession, ComponentModel componentModel, UsersApiService usersService) {
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.usersService = usersService;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(String s, RealmModel realmModel) {
        return null;
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        final User user = usersService.getUserDetails(username);

        UserModel returnValue = null;

        if (user != null) {
            returnValue = createUserModel(username, realmModel);
        }

        return returnValue;
    }

    private UserModel createUserModel(String username, RealmModel realmModel) {
        return new AbstractUserAdapter(keycloakSession, realmModel, componentModel) {
            @Override
            public String getUsername() {
                return username;
            }
        };
    }

    @Override
    public UserModel getUserByEmail(String s, RealmModel realmModel) {
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {

        if (!supportsCredentialType(credentialType)) {
            return false;
        }
//        getCredentialStore().getStoredCredentialsByTypeStream(realmModel, userModel, credentialType).count() == 0;
        return !getCredentialStore().getStoredCredentialsByType(realmModel, userModel, credentialType).isEmpty();
    }

    private UserCredentialStore getCredentialStore() {
        return keycloakSession.userCredentialManager();
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        final VerifyPasswordResponse verifyPasswordResponse = usersService.verifyUserPassword(userModel.getUsername(), credentialInput.getChallengeResponse());

        if (verifyPasswordResponse == null) {
            return false;
        }

        return verifyPasswordResponse.getResult();
    }
}
