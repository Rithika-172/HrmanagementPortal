package com.hr.reposetory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hr.entity.Compose;

@Repository
public interface ComposeRepo extends JpaRepository<Compose, Integer>{

	public List<Compose> findByParentUkid(Integer parentUkid);
}
