package com.taba.friendship.repository;

import com.taba.friendship.entity.SharedFlower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedFlowerRepository extends JpaRepository<SharedFlower, String> {
    
    @Query("SELECT sf FROM SharedFlower sf WHERE sf.friendship.id = :friendshipId ORDER BY sf.sentAt DESC")
    Page<SharedFlower> findByFriendshipId(@Param("friendshipId") String friendshipId, Pageable pageable);

    @Query("SELECT COUNT(sf) FROM SharedFlower sf WHERE sf.friendship.id = :friendshipId AND sf.isRead = false")
    long countUnreadByFriendshipId(@Param("friendshipId") String friendshipId);
}

