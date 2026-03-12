package dal;

public interface IUserDAO {
    /**
     * Checks whether a user with the given username and hashed password exists.
     *
     * <p>Note: the current implementation uses MD5 via {@link dal.HashCalculator}.
     * For production deployments a stronger, salted algorithm (e.g., bcrypt) should
     * be used instead.
     *
     * @param username     the username to look up
     * @param passwordHash the hash of the password to verify
     * @return {@code true} if the credentials are valid, {@code false} otherwise
     */
    boolean authenticate(String username, String passwordHash);
}
