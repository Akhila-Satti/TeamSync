package com.projects.teamsync.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.RecommendationResponse;
import com.projects.teamsync.dto.SkillRecommendationResponse;
import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectSkill;
import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.entity.StudentSkill;
import com.projects.teamsync.enums.Availability;
import com.projects.teamsync.enums.ProjectMemberStatus;
import com.projects.teamsync.enums.ProjectStatus;
import com.projects.teamsync.exception.AccessDeniedException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.repository.ApplicationRepository;
import com.projects.teamsync.repository.InvitationRepository;
import com.projects.teamsync.repository.ProjectMemberRepository;
import com.projects.teamsync.repository.ProjectRepository;
import com.projects.teamsync.repository.ProjectSkillRepository;
import com.projects.teamsync.repository.StudentRepository;
import com.projects.teamsync.repository.StudentSkillRepository;

@Service
public class RecommendationService {

    private StudentRepository studentRepository;

    private ProjectRepository projectRepository;

    private ProjectMemberRepository projectMemberRepository;

    private ProjectSkillRepository projectSkillRepository;

    private StudentSkillRepository studentSkillRepository;

    private InvitationRepository invitationRepository;

    private ApplicationRepository applicationRepository;


    public RecommendationService(
            StudentRepository studentRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            ProjectSkillRepository projectSkillRepository,
            StudentSkillRepository studentSkillRepository,
            InvitationRepository invitationRepository,
            ApplicationRepository applicationRepository) {

        this.studentRepository = studentRepository;

        this.projectRepository = projectRepository;

        this.projectMemberRepository =
                projectMemberRepository;

        this.projectSkillRepository =
                projectSkillRepository;

        this.studentSkillRepository =
                studentSkillRepository;

        this.applicationRepository =
                applicationRepository;

        this.invitationRepository =
                invitationRepository;
    }


    public List<RecommendationResponse>
            generateRecommendations(
                    Integer projectId) {


        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();


        Student student =
                studentRepository.findByEmail(email);


        if (student == null) {

            throw new UnauthorizedException(
                    "Unauthorized user");
        }


        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Project not found"));


        if (project.getStatus()
                == ProjectStatus.CLOSED) {

            throw new AccessDeniedException(
                    "Cannot view recommendations for a closed project");
        }


        boolean isMember =
                projectMemberRepository
                        .existsByProjectAndStudentAndStatus(
                                project,
                                student,
                                ProjectMemberStatus.ACTIVE);


        if (!isMember) {

            throw new AccessDeniedException(
                    "You do not have permission to view recommendations");
        }


        List<ProjectSkill> projectSkills =
                projectSkillRepository
                        .findByProject(project);


        HashSet<Student> students =
                new HashSet<>();


        /*
         * Find candidate students.
         *
         * A student must:
         *
         * 1. Have at least one required skill
         * 2. Be AVAILABLE
         * 3. Not already be a project member
         * 4. Not have already applied
         * 5. Not have already been invited
         */


        for (ProjectSkill projectSkill
                : projectSkills) {


            Skill skill =
                    projectSkill.getSkill();


            List<StudentSkill> studentSkills =
                    studentSkillRepository
                            .findBySkill(skill);


            for (StudentSkill studentSkill
                    : studentSkills) {


                Student currentStudent =
                        studentSkill.getStudent();


                /*
                 * Already a project member.
                 */

                boolean alreadyMember =
                        projectMemberRepository
                                .existsByProjectAndStudent(
                                        project,
                                        currentStudent);


                if (alreadyMember) {

                    continue;
                }


                /*
                 * Already applied.
                 */

                boolean alreadyApplied =
                        applicationRepository
                                .existsByStudentAndProject(
                                        currentStudent,
                                        project);


                if (alreadyApplied) {

                    continue;
                }


                /*
                 * Already invited.
                 */

                boolean alreadyInvited =
                        invitationRepository
                                .existsByProjectAndSentTo(
                                        project,
                                        currentStudent);


                if (alreadyInvited) {

                    continue;
                }


                /*
                 * Student must be available.
                 */

                if (currentStudent.getAvailability()
                        == Availability.AVAILABLE) {

                    students.add(currentStudent);
                }
            }
        }


        List<RecommendationResponse>
                recommendations =
                        new ArrayList<>();


        /*
         * Calculate recommendation score
         * for every candidate.
         */


        for (Student candidate : students) {


            double weightedScore = 0;

            double maximumPossibleScore = 0;


            List<SkillRecommendationResponse>
                    matchedSkills =
                            new ArrayList<>();


            List<SkillRecommendationResponse>
                    missingSkills =
                            new ArrayList<>();


            List<StudentSkill>
                    candidateSkills =
                            studentSkillRepository
                                    .findByStudent(candidate);


            /*
             * Check every required project skill.
             */


            for (ProjectSkill projectSkill
                    : projectSkills) {


                StudentSkill matchedStudentSkill =
                        null;


                /*
                 * Find whether candidate
                 * has this required skill.
                 */


                for (StudentSkill studentSkill
                        : candidateSkills) {


                    if (studentSkill.getSkill()
                            .getId()
                            .equals(
                                    projectSkill
                                            .getSkill()
                                            .getId())) {


                        matchedStudentSkill =
                                studentSkill;

                        break;
                    }
                }


                int importance =
                        projectSkill.getImportance();


                /*
                 * Maximum possible contribution
                 *
                 * importance × maximum proficiency
                 *
                 * Assuming maximum proficiency = 5.
                 */


                maximumPossibleScore +=
                        importance * 5;


                SkillRecommendationResponse
                        skillResponse =
                                new SkillRecommendationResponse();


                skillResponse.setSkillName(
                        projectSkill
                                .getSkill()
                                .getName());


                /*
                 * Student has the skill.
                 */


                if (matchedStudentSkill != null) {


                    int proficiency =
                            matchedStudentSkill
                                    .getProficiency();


                    /*
                     * Weighted contribution.
                     */


                    weightedScore +=
                            importance * proficiency;


                    skillResponse.setProficiency(
                            proficiency);


                    matchedSkills.add(
                            skillResponse);
                }


                /*
                 * Student does not have
                 * the required skill.
                 */


                else {

                    missingSkills.add(
                            skillResponse);
                }
            }


            /*
             * Calculate final percentage.
             */


            double score = 0;


            if (maximumPossibleScore > 0) {

                score =
                        (weightedScore
                                / maximumPossibleScore)
                                * 100;
            }


            RecommendationResponse response =
                    new RecommendationResponse();


            response.setStudentId(
                    candidate.getId());


            response.setUserName(
                    candidate.getUserName());


            response.setExperience(
                    candidate.getExperience());


            response.setMatchedSkills(
                    matchedSkills);


            response.setMissingSkills(
                    missingSkills);


            response.setMatchingScore(
                    score);


            recommendations.add(
                    response);
        }


        /*
         * Sort by highest matching score first.
         */


        recommendations.sort(
                (a, b) ->
                        Double.compare(
                                b.getMatchingScore(),
                                a.getMatchingScore()));


        /*
         * Return maximum 10 recommendations.
         */


        return recommendations.subList(
                0,
                Math.min(
                        10,
                        recommendations.size()));
    }
}