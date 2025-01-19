package com.hr.reposetory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hr.entity.CreatePost;

@Repository
public interface CreatePostRepo extends JpaRepository<CreatePost, Integer>{

}
