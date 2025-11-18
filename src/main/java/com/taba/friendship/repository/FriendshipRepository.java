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

    @Query("SELECT f FROM Friendship f WHERE ((f.user.id = :userId AND f.friend.id = :friendId) OR (f.user.id = :friendId AND f.friend.id = :userId)) AND f.deletedAt IS NULL")
    List<Friendship> findByUserIdsList(@Param("userId") String userId, @Param("friendId") String friendId);
    
    // 기존 메서드 - List에서 첫 번째만 반환하도록 수정
    default Optional<Friendship> findByUserIds(String userId, String friendId) {
        List<Friendship> friendships = findByUserIdsList(userId, friendId);
        if (friendships.isEmpty()) {
            return Optional.empty();
        }
        // 첫 번째 결과만 반환 (양방향 관계가 있어도 하나만 반환)
        return Optional.of(friendships.get(0));
    }

    boolean existsByUserIdAndFriendIdAndDeletedAtIsNull(String userId, String friendId);
}

