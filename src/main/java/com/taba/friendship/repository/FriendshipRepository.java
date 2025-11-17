package com.taba.friendship.repository;

import com.taba.friendship.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, String> {
    
    @Query("SELECT f FROM Friendship f WHERE (f.user.id = :userId OR f.friend.id = :userId) AND f.deletedAt IS NULL")
    List<Friendship> findAllByUserId(@Param("userId") String userId);

    @Query("SELECT f FROM Friendship f WHERE f.user.id = :userId AND f.deletedAt IS NULL")
    List<Friendship> findByUserId(@Param("userId") String userId);

    @Query("SELECT f FROM Friendship f WHERE (f.user.id = :userId AND f.friend.id = :friendId) OR (f.user.id = :friendId AND f.friend.id = :userId) AND f.deletedAt IS NULL")
    Optional<Friendship> findByUserIds(@Param("userId") String userId, @Param("friendId") String friendId);

    boolean existsByUserIdAndFriendIdAndDeletedAtIsNull(String userId, String friendId);
}

