package com.example.tttn.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tttn.model.Category;
public interface CategoryRepository extends JpaRepository<Category, Integer> {


	//ds ds cac loại san pham sap xep theo tang dan 
	List<Category> findAllByOrderByCategoryNameAsc();
}
