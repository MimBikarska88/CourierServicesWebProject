package com.example.orders.Courier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;

public interface CourierRepository extends CrudRepository<Courier, Integer> {
    @Query("select c from Courier c where c.phoneNumber = ?1")
    Courier findByPhoneNumber(String phoneNumber);

    @Query("select (count(c) > 0) from Courier c where c.phoneNumber = ?1")
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("select (count(c) > 0) from Courier c where c.phoneNumber = ?1 and c.loginDetails = ?2")
    boolean existsByPhoneNumberAndLoginDetails(String phoneNumber, String LoginDetails);

    @Query("select c from Courier c where c.phoneNumber = ?1 and c.loginDetails = ?2")
    Courier findCourierByPhoneNumberAndLoginDetails(String phoneNumber, String loginDetails);
}
