package io.filemanagement.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class FileManagmentEntityDtoMappingService<E, D> {

	public E fromDto(D d, E e, String... exclList) {
		BeanUtils.copyProperties(d, e, exclList);
		return e;
	}

	public E fromDto(D d, E e) {
		BeanUtils.copyProperties(d, e);
		return e;
	}

	@SuppressWarnings("unchecked")
	public List<E> fromDtoList(List<D> dtos, E newTargetEntity) {
		List<E> entities = new ArrayList<E>();
		for (D dto : dtos) {
			Class<E> clazz =  (Class<E>) newTargetEntity.getClass();
			E entity = createEContents(clazz);
			entities.add(fromDto(dto, entity));
		}
		return entities;
	}

	@SuppressWarnings("unchecked")
	public List<E> fromDtoList(List<D> dtos, E newTargetEntity, String... exclList) {
		List<E> entities = new ArrayList<E>();
		for (D dto : dtos) {
			Class<E> clazz = (Class<E>) newTargetEntity.getClass();
			E entity = createEContents(clazz);
			entities.add(fromDto(dto, entity, exclList));
		}
		return entities;
	}

	public D toDto(D d, E e) {
		BeanUtils.copyProperties(e, d);
		return d;
	}

	public D toDto(D d, E e, String... exclList) {
		BeanUtils.copyProperties(e, d, exclList);
		return d;
	}

	public List<D> toDtoList(List<E> entities, D newTargetDto) {
		List<D> dtos = new ArrayList<D>();
		for (E entity : entities) {
			@SuppressWarnings("unchecked")
			Class<D> clazz = (Class<D>) newTargetDto.getClass();
			D dto = createDContents(clazz);
			dtos.add(toDto(dto,entity));
		}
		return dtos;
	}

	@SuppressWarnings("unchecked")
	public List<D> toDtoList(List<E> entities, D newTargetDto, String... exclList) {
		List<D> dtos = new ArrayList<D>();
		for (E entity : entities) {
			Class<D> clazz = (Class<D>) newTargetDto.getClass();
			D dto = createDContents(clazz);
			dtos.add(toDto(dto, entity, exclList));
		}
		return dtos;
	}

	private E createEContents(Class<E> clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	private D createDContents(Class<D> clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

}
