package io.filemanagement.repository;

import org.springframework.data.repository.CrudRepository;

import io.filemanagement.models.Post;

public interface PostRepository extends CrudRepository<Post, Long>{
	
	Post findByUserId(Long userId);
    Post findByTitle(String title);
    
//    @Query("select p from Person p where p.house.id=:houseId and p.carID =:carId")
//    Person findByHouseAndCar(@Param("houseId")Integer houseID, @Param("carId")Integer carID);

}
