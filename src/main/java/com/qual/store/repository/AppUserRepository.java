package com.qual.store.repository;


import com.qual.store.model.AppUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface AppUserRepository extends ShopRepository<AppUser, Long> {

    @EntityGraph(value = "userWithOrders", type = EntityGraph.EntityGraphType.LOAD)
    AppUser findUserByUsername(String username);

    // print user with orders
    @Query("select distinct u from AppUser u")
    @EntityGraph(value = "userWithOrders", type = EntityGraph.EntityGraphType.LOAD)
    List<AppUser> findAllWithOrders();
}
