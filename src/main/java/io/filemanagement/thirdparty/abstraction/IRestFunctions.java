package io.filemanagement.thirdparty.abstraction;

import java.sql.SQLException;
import java.util.List;

import javax.naming.ServiceUnavailableException;

import org.apache.http.ConnectionClosedException;
import org.springframework.http.ResponseEntity;

import io.filemanagement.dtos.CommentDto;
import io.filemanagement.exceptions.FileManagementExtServerDownException;


public interface IRestFunctions<PlaceHolderEntity> {
	
	public  <K> ResponseEntity<List<PlaceHolderEntity>> getList(K key)throws Exception;
	
	public  <K> ResponseEntity<PlaceHolderEntity> get(K key)throws Exception;
	
	public  ResponseEntity<PlaceHolderEntity> post(PlaceHolderEntity placeHolderEntity) throws SQLException, ConnectionClosedException, ServiceUnavailableException;
	
	public  ResponseEntity<PlaceHolderEntity> put(PlaceHolderEntity placeHolderEntity)throws  SQLException, ConnectionClosedException, ServiceUnavailableException;
	
	public  ResponseEntity<PlaceHolderEntity> delete(PlaceHolderEntity placeHolderEntity)throws SQLException, ConnectionClosedException, ServiceUnavailableException;
	
	public ResponseEntity<CommentDto> recover(ConnectionClosedException exception) throws FileManagementExtServerDownException;
	
	public ResponseEntity<CommentDto> recover(ServiceUnavailableException exception) throws FileManagementExtServerDownException;

}
