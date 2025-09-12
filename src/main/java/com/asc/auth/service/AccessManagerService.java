package com.asc.auth.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.asc.auth.dto.CampaignInfoDto;
import com.asc.auth.dto.FiltersDto;
import com.asc.auth.dto.ModulesDto;
import com.asc.auth.dto.ModulesDto.SubModuleDto;
import com.asc.auth.dto.RoleMasterDto;
import com.asc.auth.dto.RoleMasterDto.ModulePermissionDto;
import com.asc.auth.dto.RoleMasterDto.ModulePermissionDto.SubModulesDto;
import com.asc.auth.dto.RolesDto;
import com.asc.auth.dto.RolesDto.RolePermissionsDto;
import com.asc.auth.exception.EntityAlreadyExistsException;
import com.asc.auth.exception.RecordNotFoundException;
import com.asc.auth.model.CampaignInfo;
import com.asc.auth.model.ModuleMaster;
import com.asc.auth.model.ModulePermissions;
import com.asc.auth.model.RoleMaster;
import com.asc.auth.model.SubModuleMaster;
import com.asc.auth.model.SubmodulePermissions;
import com.asc.auth.model.enums.UserRoles;
import com.asc.auth.repository.ModuleMasterRepository;
import com.asc.auth.repository.ModulePermissionsRepository;
import com.asc.auth.repository.RoleMasterRepository;
import com.asc.auth.repository.SubModuleMasterRepository;
import com.asc.auth.transformer.CampaignInfoFiltersTransformer;
import com.asc.auth.transformer.RoleMasterFiltersTransformer;
import com.asc.auth.utils.Constants;
import com.asc.auth.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccessManagerService {
	@Autowired
	RoleMasterRepository roleMasterRepository;
	@Autowired
	ModuleMasterRepository moduleMasterRepository;
	@Autowired
	SubModuleMasterRepository subModuleMasterRepository;

	@Autowired
	ModulePermissionsRepository modulePermissionsRepository;

	@Transactional
	public List<RolesDto> getRoleList() {
		List<RoleMaster> roles = roleMasterRepository.findAll();
		List<RolesDto> rolesDto = roles.stream().map(obj -> copyRoleEntityToDto(obj, true)).toList();
		return rolesDto;
	}

	private RolesDto copyRoleEntityToDto(RoleMaster role, Boolean flag) {
		RolesDto roleDto = new RolesDto();
		Utils.copyProperties(role, roleDto);
		if (flag) {
			List<RolePermissionsDto> rolePermissionsDtoList = role.getModulePermissions().stream().map(obj -> {
				RolePermissionsDto permissionsDto = new RolePermissionsDto();
				Utils.copyProperties(obj, permissionsDto);
				return permissionsDto;
			}).collect(Collectors.toList());
			roleDto.setRolePermissions(rolePermissionsDtoList);
		}
		return roleDto;
	}

	public List<ModulesDto> getModuleList() {
		List<ModuleMaster> moduleMaster = moduleMasterRepository.findAll();
		log.info("moduleMaster: {}", moduleMaster);
		return moduleMaster.stream().sorted(Comparator.comparing(ModuleMaster::getSortSequence)).map(module -> {
			ModulesDto moduleDto = new ModulesDto();
			Utils.copyProperties(module, moduleDto);
			List<SubModuleDto> subModuleDtoList = module.getSubModules().stream()
					.sorted(Comparator.comparing(SubModuleMaster::getStatus)).map(subModule -> {
						SubModuleDto subModuleDto = new SubModuleDto();
						Utils.copyProperties(subModule, subModuleDto);
						subModuleDto.setModuleId(module.getId());
						log.info("subModuleDto: {}", subModuleDto);
						return subModuleDto;
					}).toList();
			moduleDto.setSubModuleMapping(subModuleDtoList);
			return moduleDto;
		}).toList();
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public List<ModulesDto> updateModules(List<ModulesDto> modulesDtoList) {
		return modulesDtoList.stream().map(modulesDto -> {
			ModuleMaster moduleMaster = Optional.ofNullable(modulesDto.getId())
					.flatMap(moduleMasterRepository::findById).orElse(new ModuleMaster());
			Utils.copyProperties(modulesDto, moduleMaster);
			List<SubModuleMaster> updatedSubModules = Optional.ofNullable(modulesDto.getSubModuleMapping())
					.orElse(Collections.emptyList()).stream().map(subModuleDto -> {
						SubModuleMaster subModuleMaster = Optional.ofNullable(subModuleDto.getId())
								.flatMap(subModuleMasterRepository::findById).orElse(new SubModuleMaster());
						Utils.copyProperties(subModuleDto, subModuleMaster);
						subModuleMaster.setModuleMaster(moduleMaster);
						return subModuleMaster;
					}).collect(Collectors.toList());
			moduleMaster.setSubModules(updatedSubModules);
			moduleMasterRepository.save(moduleMaster);
			return modulesDto;
		}).collect(Collectors.toList());
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public RoleMasterDto updateRolePermissionsNew(RoleMasterDto roleMasterDto) {
		RoleMaster roleMaster = roleMasterRepository.findById(roleMasterDto.getRoleId())
				.orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleMasterDto.getRoleId()));
		log.info("roleMasterDto: {}", roleMasterDto);
		Map<Long, ModulePermissions> existingModuleMap = roleMaster.getModulePermissions().stream()
				.collect(Collectors.toMap(ModulePermissions::getId, Function.identity()));
		List<ModulePermissions> newModulePermissions = new ArrayList<>();
		for (ModulePermissionDto moduleDto : roleMasterDto.getModulePermissions()) {
			if (moduleDto.getId() != null && existingModuleMap.containsKey(moduleDto.getId())) {
				updateExistingModulePermissions(existingModuleMap.get(moduleDto.getId()), moduleDto);
			} else if (hasPermission(moduleDto)) {
				ModulePermissions newModule = createNewModulePermission(roleMaster, moduleDto);
				newModulePermissions.add(newModule);
			}
		}
		roleMaster.getModulePermissions().addAll(newModulePermissions);
		roleMasterRepository.save(roleMaster);
		return roleMasterDto;
	}

	private void updateExistingModulePermissions(ModulePermissions existingModule, ModulePermissionDto moduleDto) {
		log.info("Existing Module : {} ", existingModule);
		Utils.copyProperties(moduleDto, existingModule);
		Map<Long, SubmodulePermissions> existingSubMap = existingModule.getSubmodulePermission().stream()
				.collect(Collectors.toMap(SubmodulePermissions::getId, Function.identity()));
		for (SubModulesDto subDto : moduleDto.getSubmodulePermissions()) {
			if (subDto.getId() != null && existingSubMap.containsKey(subDto.getId())) {
				Utils.copyProperties(subDto, existingSubMap.get(subDto.getId()));
			} else if (hasPermission(subDto)) {
				SubmodulePermissions newSub = createNewSubmodulePermission(existingModule, subDto);
				existingModule.getSubmodulePermission().add(newSub);
			}
		}
	}

	private ModulePermissions createNewModulePermission(RoleMaster roleMaster, ModulePermissionDto moduleDto) {
		ModuleMaster moduleMaster = moduleMasterRepository.findById(moduleDto.getModuleId())
				.orElseThrow(() -> new RecordNotFoundException("Module not found"));
		ModulePermissions newModule = new ModulePermissions();
		Utils.copyProperties(moduleDto, newModule);
		newModule.setRole(roleMaster);
		newModule.setModuleMaster(moduleMaster);
		for (SubModulesDto subDto : moduleDto.getSubmodulePermissions()) {
			if (hasPermission(subDto)) {
				SubmodulePermissions subPermission = createNewSubmodulePermission(newModule, subDto);
				newModule.getSubmodulePermission().add(subPermission);
			}
		}
		return newModule;
	}

	private SubmodulePermissions createNewSubmodulePermission(ModulePermissions parentModule, SubModulesDto subDto) {
		SubModuleMaster submoduleMaster = subModuleMasterRepository.findById(subDto.getSubModuleId())
				.orElseThrow(() -> new RecordNotFoundException("Submodule not found"));
		SubmodulePermissions subPermission = new SubmodulePermissions();
		Utils.copyProperties(subDto, subPermission);
		subPermission.setModulePermission(parentModule);
		subPermission.setSubmoduleMaster(submoduleMaster);
		return subPermission;
	}

	private boolean hasPermission(ModulePermissionDto moduleDto) {
		return Boolean.TRUE.equals(moduleDto.getRead()) || Boolean.TRUE.equals(moduleDto.getWrite())
				|| Boolean.TRUE.equals(moduleDto.getUpdate());
	}

	private boolean hasPermission(SubModulesDto subDto) {
		return Boolean.TRUE.equals(subDto.getRead()) || Boolean.TRUE.equals(subDto.getWrite())
				|| Boolean.TRUE.equals(subDto.getUpdate());
	}

	public RoleMasterDto addRole(RoleMasterDto roleMasterDto) {
		Optional<RoleMaster> roleMaster = roleMasterRepository.findByName(roleMasterDto.getName());
		if (roleMaster.isPresent()) {
			throw new EntityAlreadyExistsException("Role Already Exist " + roleMasterDto.getName());
		}
		RoleMaster roleMaster1 = new RoleMaster();
		Utils.copyProperties(roleMasterDto, roleMaster1);
		roleMaster1 = roleMasterRepository.save(roleMaster1);
		Utils.copyProperties(roleMaster1, roleMasterDto);
		return roleMasterDto;
	}

	public List<ModulePermissionDto> getSideMenuPermisions(Long roleId) {
		List<ModulesDto> moduleList = getModuleList();
		if (UserRoles.ROLE_SUPERUSER.equals(UserRoles.fromId(roleId.intValue()))) {
			return moduleList.stream()
					.map(module -> toPermissionDto(module, true, true, true, false, module.getId(), null, true))
					.collect(Collectors.toList());
		}

		List<ModulePermissions> modulePermissions = modulePermissionsRepository.findAllByRoleId(roleId);
		if (modulePermissions.isEmpty()) {
			return moduleList.stream()
					.map(module -> toPermissionDto(module, false, false, false, false, null, null, false))
					.collect(Collectors.toList());
		}

		Map<Long, ModulePermissions> permissionMap = modulePermissions.stream().collect(
				Collectors.toMap(ModulePermissions::getModuleId, permission -> permission, (existing, replacement) -> {
					existing.setRead(existing.getRead() || replacement.getRead());
					existing.setWrite(existing.getWrite() || replacement.getWrite());
					existing.setUpdate(existing.getUpdate() || replacement.getUpdate());
					existing.setIsDefault(existing.getIsDefault() || replacement.getIsDefault());
					return existing;
				}));
		return moduleList.stream().map(module -> {
			ModulePermissions permission = permissionMap.get(module.getId());
			if (permission != null) {
				return toPermissionDto(module, permission.getRead(), permission.getWrite(), permission.getUpdate(),
						permission.getIsDefault(), permission.getId(), permission.getSubmodulePermission(), false);
			} else {
				return toPermissionDto(module, false, false, false, false, null, null, false);
			}
		}).collect(Collectors.toList());

	}

	private ModulePermissionDto toPermissionDto(ModulesDto module, boolean read, boolean write, boolean update,
			boolean isDefault, Long id, List<SubmodulePermissions> submodulePermissions, boolean flag) {
		ModulePermissionDto dto = new ModulePermissionDto();
		Utils.copyProperties(module, dto);
		dto.setId(id);
		dto.setModuleId(module.getId());
		dto.setStatus(module.getStatus());
		dto.setActive(module.getStatus() != null && module.getStatus() == 1);
		dto.setRead(read);
		dto.setWrite(write);
		dto.setUpdate(update);
		dto.setIsDefault(isDefault);
		dto.setSortSequence(module.getSortSequence());
		Map<Long, SubmodulePermissions> permissionMap = (submodulePermissions != null)
				? submodulePermissions.stream().collect(Collectors.toMap(SubmodulePermissions::getSubModuleId,
						permission -> permission, (existing, replacement) -> {
							existing.setRead(existing.getRead() || replacement.getRead());
							existing.setWrite(existing.getWrite() || replacement.getWrite());
							existing.setUpdate(existing.getUpdate() || replacement.getUpdate());
							existing.setIsDefault(existing.getIsDefault() || replacement.getIsDefault());
							return existing;
						}))
				: Collections.emptyMap();
		List<SubModulesDto> subModulesDtos = module.getSubModuleMapping().stream().map(subModule -> {
			SubModulesDto subModulesDto = new SubModulesDto();
			Utils.copyProperties(subModule, subModulesDto);
			SubmodulePermissions permission = permissionMap.get(subModule.getId());
			if (permission != null) {
				Utils.copyProperties(permission, subModulesDto);
			} else {
				subModulesDto.setId(flag ? subModule.getId() : null);
				subModulesDto.setSubModuleId(subModule.getId());
				subModulesDto.setRead(flag);
				subModulesDto.setWrite(flag);
				subModulesDto.setUpdate(flag);
				subModulesDto.setIsDefault(false);
			}
			return subModulesDto;
		}).collect(Collectors.toList());
		dto.setSubmodulePermissions(subModulesDtos);
		return dto;
	}

	public Page<RolesDto> getRoles(List<FiltersDto> filters, Integer pageNumber, Integer pageSize, String sortingColumn,
			Direction direction) {
		Sort sorting = (Boolean.TRUE.equals(Objects.nonNull(sortingColumn))
				&& Boolean.TRUE.equals(Objects.nonNull(direction))) ? Sort.by(direction, sortingColumn)
						: Sort.by(Direction.ASC, "id");
		filters = (Boolean.FALSE.equals(filters.isEmpty())) ? filters : new ArrayList<>();
		pageSize = (Boolean.FALSE.equals(pageSize < 1)) ? pageSize : 5;
		Page<RoleMaster> pageList = roleMasterRepository.findAll(RoleMasterFiltersTransformer.buildCriteria(filters),
				PageRequest.of(pageNumber, pageSize, sorting));
		if (!pageList.isEmpty()) {
			List<RolesDto> userDetail = pageList.stream().map(obj -> copyRoleEntityToDto(obj, true))
					.collect(Collectors.toList());
			Page<RolesDto> pageData = new PageImpl<>(userDetail, PageRequest.of(pageNumber, pageSize, sorting),
					pageList.getTotalElements());
			return pageData;

		}
		return null;
	}

}
