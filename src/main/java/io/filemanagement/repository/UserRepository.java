package io.filemanagement.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.filemanagement.models.User;



@Repository
public interface UserRepository extends CrudRepository<User, Long>{
	public User findByUsername(String username);
}
