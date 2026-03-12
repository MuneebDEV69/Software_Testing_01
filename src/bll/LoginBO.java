package bll;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dal.HashCalculator;
import dal.IUserDAO;

public class LoginBO implements ILoginBO {

    private static final Logger LOGGER = LogManager.getLogger(LoginBO.class);

    private final IUserDAO userDAO;

    public LoginBO(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.isEmpty()) {
            return false;
        }
        try {
            String passwordHash = HashCalculator.calculateHash(password);
            return userDAO.authenticate(username, passwordHash);
        } catch (Exception e) {
            LOGGER.debug("Login attempt failed", e);
            return false;
        }
    }
}
