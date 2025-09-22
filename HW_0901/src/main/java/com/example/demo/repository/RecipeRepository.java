package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long>{

}
