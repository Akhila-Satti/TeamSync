package com.projects.teamsync.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.repository.SkillRepository;
import java.util.List;

@Configuration
public class SkillConfig {

    @Bean
    CommandLineRunner skillAdder(SkillRepository skillRepository){
        return args->{
            List<String> skills = List.of(
    "Java",
    "Python",
    "C++",
    "JavaScript",
    "TypeScript",
    "Spring Boot",
    "React",
    "Node.js",
    "Angular",
    "SQL",
    "MySQL",
    "MongoDB",
    "Git",
    "Docker",
    "Machine Learning",
    "Deep Learning",
    "HTML",
    "CSS"
);

for (String skillName:skills){
    if(!skillRepository.existsByName(skillName)){
        Skill skill = new Skill();
        skill.setName(skillName);
        skillRepository.save(skill);
    }
   

}

        }; // in general it is run(String ..args)
    }
    
}
