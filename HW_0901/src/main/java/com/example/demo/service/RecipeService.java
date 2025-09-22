package com.example.demo.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.LabUser;
import com.example.demo.domain.Recipe;
import com.example.demo.repository.LabUserRepository;
import com.example.demo.repository.RecipeRepository;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {
	
	private final RecipeRepository recipeRepository;
	private final LabUserRepository labUserRepository;

	public List<Recipe> findAll() {
		return recipeRepository.findAll();
	}

	@Transactional
	public void save(Recipe recipe, String username) {
		LabUser labUser = labUserRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		Recipe newRecipe = Recipe.builder()
				.name(recipe.getName())
				.description(recipe.getDescription())
				.labUser(labUser)
				.build();
		recipeRepository.save(newRecipe);
	}

	public Recipe findById(Long id) {
		// TODO Auto-generated method stub
		return recipeRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다."));
	}
	
    @Transactional
    public void update(Long id, Recipe recipeUpdateInfo, String username) {
        Recipe recipe = findById(id);
        if (!recipe.getLabUser().getUsername().equals(username)) {
            throw new AccessDeniedException("수정할 권한이 없습니다.");
        }
        recipe.update(recipeUpdateInfo.getName(), recipeUpdateInfo.getDescription());
    }
    
    @Transactional
	public void delete(Long id, String username) {
		// TODO Auto-generated method stub
		Recipe recipe = findById(id);
		if(!recipe.getLabUser().getUsername().equals(username)) {
			throw new AccessDeniedException("삭제할 권한이 없습니다.");
		}
		recipeRepository.delete(recipe);
	}




}
