package by.itacademy.pisarev.credential;

import credentials.Credentials;

import java.util.Set;

public interface CredentialRepository {
    //Create
    boolean createCredential(Credentials credentials);

    //Read
    Credentials getCredentialById(int id);
    Credentials getCredentialByLoginAndPassword(String login, String password);
    Set<Credentials> getAllCredentials();

    //Update
    boolean updateCredential(Credentials credentials);

    //Delete
    boolean deleteCredentialById(int id);
    boolean deleteCredentialByLoginAndPassword(String login, String password);
}
