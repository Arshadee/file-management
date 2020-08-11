package io.filemanagement.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.filemanagement.models.Comment;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

	Comment findByIdAndPostId(Long id, Long postId);

	List<Comment> findByPostId(Long postId);
	
	List<Comment> findByBody(String body);

}
