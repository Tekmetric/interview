package com.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.interview.data.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

	Optional<Shop> findById(Long id);
	List<Shop> findAllByOrderByNameAsc();

	@Query("select s from shop s where s.name like :name")
	List<Shop> findAllByNameOrderByNameAsc(String name);

}