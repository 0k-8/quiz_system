package com.kizlyak.service;

import com.kizlyak.dao.UserDao;
import com.kizlyak.dao.TeamDao;
import com.kizlyak.entity.Role;
import com.kizlyak.entity.User;
import com.kizlyak.entity.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuthService {
    private final UserDao userDao;
    private final TeamDao teamDao;

    public AuthService(UserDao userDao, TeamDao teamDao) {
        this.userDao = userDao;
        this.teamDao = teamDao;
    }

    public void register(String firstName, String lastName, String email, String password) {
        // ... (existing validation code)
        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Ім'я та прізвище не можуть бути порожніми!");
        }
        
        String nameRegex = "^[A-ZА-ЩЬЮЯҐЄІЇ][a-zа-щьюяґєії']{1,20}$";
        if (!firstName.matches(nameRegex)) {
            throw new IllegalArgumentException("Невірний формат імені! (Має починатися з великої літери)");
        }
        if (!lastName.matches(nameRegex)) {
            throw new IllegalArgumentException("Невірний формат прізвища! (Має починатися з великої літери)");
        }

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пошта та пароль не можуть бути порожніми!");
        }
        if (!email.matches("[A-Za-z0-9._%+-]+@(.+)$"))
            throw new IllegalArgumentException("Невірний формат пошти!");

        if(password.length() < 6)
            throw new IllegalArgumentException("Пароль повинен мати щонайменше 6 символів!");

        if (userDao.findByEmail(email).isPresent())
            throw new RuntimeException("Користувач з такою поштою вже існує!");

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPasswordHash(password);
        newUser.setRole(Role.USER);
        userDao.saveUser(newUser);
    }

    public Optional<User> login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }
        return userDao.findByEmail(email)
                .filter(user -> user.getPasswordHash().equals(password));
    }

    public void leaveTeam(User user) {
        user.setTeamsId(null);
        userDao.saveUser(user);
    }


    public void createTeam(String teamName, User creator) {
        if (teamName == null || teamName.isBlank()) {
            throw new IllegalArgumentException("Назва команди не може бути порожньою!");
        }
        if (teamName.length() < 3 || teamName.length() > 30) {
            throw new IllegalArgumentException("Назва команди має бути від 3 до 30 символів!");
        }
        if (teamDao.findByName(teamName).isPresent()) {
            throw new RuntimeException("Команда з такою назвою вже існує!");
        }

        Team team = new Team();
        team.setId(UUID.randomUUID());
        team.setTeamName(teamName);
        team.setCreatedAt(java.time.LocalDateTime.now());
        teamDao.saveTeam(team);

        creator.setTeamsId(team.getId());
        userDao.saveUser(creator); 
    }

    public void joinTeam(String teamName, User user) {
        Team team = teamDao.findByName(teamName)
                .orElseThrow(() -> new RuntimeException("Команду не знайдено!"));
        
        user.setTeamsId(team.getId());
        userDao.saveUser(user); 
    }

    public List<User> getTeamMembers(UUID teamId) {
        return userDao.findAll().stream()
                .filter(u -> teamId.equals(u.getTeamsId()))
                .collect(Collectors.toList());
    }

    public List<Team> getAllTeams() {
        return teamDao.getAllTeams();
    }
}
