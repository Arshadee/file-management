package io.filemanagement.dtos;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class DocumentDto {
	
	private Long id;
	@JsonManagedReference
	private UserDto userDto;
	private String filePath;
	//private List<Post> post;
	
	public DocumentDto() {}
	
	public DocumentDto(UserDto userDto) {
		super();
		this.userDto = userDto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserDto getUserDto() {
		return userDto;
	}

	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return "DocumentDto [id=" + id + ", userDto=" + userDto + ", filePath=" + filePath + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((userDto == null) ? 0 : userDto.hashCode());
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
		DocumentDto other = (DocumentDto) obj;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (userDto == null) {
			if (other.userDto != null)
				return false;
		} else if (!userDto.equals(other.userDto))
			return false;
		return true;
	}

}
