package io.filemanagement.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="User")
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id", unique=true, nullable=false)
	private Long id;
	@Column(unique=true, nullable=false) 
	private String username;
	@OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private Set<Document> documents;
	
	public User() {
		documents = new HashSet<>();
	}
	
	public User(String username, Set<Document> documents) {
		super();
		this.username = username;
		this.documents = documents;
	}
	
	public User(String username) {
		super();
		this.username = username;
		this.documents = new HashSet<>();
	}

	
	public User(Long id, String username) {
		super();
		this.id = id;
		this.username = username;
		this.documents = new HashSet<>();
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

	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", documents=" + documents + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documents == null) ? 0 : documents.hashCode());
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
		User other = (User) obj;
		if (documents == null) {
			if (other.documents != null)
				return false;
		} else if (!documents.equals(other.documents))
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
