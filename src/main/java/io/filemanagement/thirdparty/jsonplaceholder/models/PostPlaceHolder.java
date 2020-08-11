package io.filemanagement.thirdparty.jsonplaceholder.models;

import java.io.Serializable;

public class PostPlaceHolder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long userId;
	private String title;
	private String body;

	public PostPlaceHolder() {}
	
	public PostPlaceHolder(Long userId, Long id, String title, String body) {
		super();
		this.userId = userId;
		this.id = id;
		this.title = title;
		this.body = body;
	}
	
	public PostPlaceHolder(Long userId, String title, String body) {
		super();
		this.userId = userId;
		this.title = title;
		this.body = body;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
