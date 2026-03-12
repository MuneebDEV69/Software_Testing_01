package bll;

public interface ILoginBO {
    /**
     * Attempts to authenticate the user with the given credentials.
     *
     * @param username the username
     * @param password the plain-text password (will be hashed internally)
     * @return {@code true} if the credentials are valid, {@code false} otherwise
     */
    boolean login(String username, String password);
}
