package com.gof.ICNBack.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.DataSources.User.UserDao;
import com.gof.ICNBack.Entity.User;

@Component
public class UserService {
    @Autowired
    UserDao userDao;

    public User loginUser(String email, String password) {
        return userDao.getUserByPair(email, password);
    }

    public boolean updateUser(User user) {
        return userDao.update(user.toEntity());
    }

    public boolean createUser(UserEntity user) {
        return userDao.create(user);
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    // Bookmark management methods

    /**
     * Add a company to user's bookmarked companies list
     * 
     * @param userId    The user ID
     * @param companyId The company ID to bookmark
     * @return true if successfully added, false otherwise
     */
    public boolean addBookmark(String userId, String companyId) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            return false;
        }

        List<String> bookmarks = user.getBookmarkedCompanies();
        if (bookmarks == null) {
            bookmarks = new ArrayList<>();
        }

        // Avoid duplicates
        if (!bookmarks.contains(companyId)) {
            bookmarks.add(companyId);
            user.setBookmarkedCompanies(bookmarks);
            return userDao.update(user.toEntity());
        }

        return true; // Already bookmarked
    }

    /**
     * Remove a company from user's bookmarked companies list
     * 
     * @param userId    The user ID
     * @param companyId The company ID to remove
     * @return true if successfully removed, false otherwise
     */
    public boolean removeBookmark(String userId, String companyId) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            return false;
        }

        List<String> bookmarks = user.getBookmarkedCompanies();
        if (bookmarks == null) {
            return true; // Nothing to remove
        }

        bookmarks.remove(companyId);
        user.setBookmarkedCompanies(bookmarks);
        return userDao.update(user.toEntity());
    }

    /**
     * Get all bookmarked companies for a user
     * 
     * @param userId The user ID
     * @return List of company IDs, or empty list if none
     */
    public List<String> getBookmarks(String userId) {
        User user = userDao.getUserById(userId);
        if (user == null || user.getBookmarkedCompanies() == null) {
            return new ArrayList<>();
        }
        return user.getBookmarkedCompanies();
    }

    /**
     * Sync bookmarks - replace user's bookmarks with provided list
     * Used for offline sync when user comes back online
     * 
     * @param userId    The user ID
     * @param bookmarks The list of company IDs to sync
     * @return true if successfully synced, false otherwise
     */
    public boolean syncBookmarks(String userId, List<String> bookmarks) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            return false;
        }

        user.setBookmarkedCompanies(bookmarks != null ? bookmarks : new ArrayList<>());
        return userDao.update(user.toEntity());
    }
}
