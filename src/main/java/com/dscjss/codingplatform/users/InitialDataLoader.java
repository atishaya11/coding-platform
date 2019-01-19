package com.dscjss.codingplatform.users;

import com.dscjss.codingplatform.users.model.Privilege;
import com.dscjss.codingplatform.users.model.Role;
import com.dscjss.codingplatform.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class InitialDataLoader implements CommandLineRunner {


    private RoleRepository roleRepository;

    private PrivilegeRepository privilegeRepository;

    private UserService userService;

    @Autowired
    public InitialDataLoader(RoleRepository roleRepository, PrivilegeRepository privilegeRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.userService = userService;
    }

    @Transactional
    public void init() {
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        Set<Privilege> adminPrivileges = new HashSet<>(Arrays.asList(readPrivilege, writePrivilege));
        Set<Privilege> userPrivileges = new HashSet<>(Arrays.asList(readPrivilege));

        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", userPrivileges);
        userService.createAdmin();
    }

    private Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    private Role createRoleIfNotFound(String name, Set<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

    @Override
    public void run(String... args) throws Exception {
        init();
    }
}
