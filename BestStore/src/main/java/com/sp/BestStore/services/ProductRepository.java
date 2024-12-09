package com.sp.BestStore.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sp.BestStore.models.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

}
