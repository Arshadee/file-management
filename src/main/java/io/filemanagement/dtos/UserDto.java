package io.filemanagement.dtos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;


public class UserDto {
	
	private Long id;
	private String username;
	@JsonBackReference
	private List<DocumentDto> documentDtos;
	
	public UserDto() {
		documentDtos = new ArrayList<>();
	}
	
	public UserDto(String username, List<DocumentDto> documentDtos) {
		super();
		this.username = username;
		this.documentDtos = documentDtos;
	}
	
	public UserDto(Long id, String username) {
		super();
		this.id = id;
		this.username = username;
		this.documentDtos = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<DocumentDto> getDocumentDtos() {
		return documentDtos;
	}

	public void setDocuments(List<DocumentDto> documentDtos) {
		this.documentDtos = documentDtos;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", documentDtos=" + documentDtos + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentDtos == null) ? 0 : documentDtos.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDto other = (UserDto) obj;
		if (documentDtos == null) {
			if (other.documentDtos != null)
				return false;
		} else if (!documentDtos.equals(other.documentDtos))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
}
