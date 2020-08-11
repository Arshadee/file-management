package io.filemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.filemanagement.models.Document;
import io.filemanagement.models.User;


@Repository
public interface DocumentRepository extends CrudRepository<Document, Long>{
	Long deleteByFilePath(String filePath);
	List<Document> deleteByUser(User user);
	List<Document> findByUser(User user);
	Document findByUserAndFilePathIgnoreCase(User user, String filePath);
	
  @Query("select d from Document d where d.user.username=:username and lower(d.filePath) = lower(:filePath)")
  Document findByUsernameAndFilePath(@Param("username")String username, @Param("filePath")String filePath);

}
